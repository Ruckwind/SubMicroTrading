/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session.socket;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.Encoder;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.session.AbstractSession;
import com.rr.core.session.BadMessageSize;
import com.rr.core.session.EventRouter;
import com.rr.core.session.RecoverableSession;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.Utils;

import java.io.IOException;

/**
 * a socket session implementation
 * <p>
 * protocol for all messages is
 *
 * <int><bytes>
 * <p>
 * where number of bytes is denoted by the leading 4 byte integer
 */
public class SocketSession extends AbstractSocketSession {

    private static final Logger _log = LoggerFactory.create( SocketSession.class );

    private static final int NUM_BYTES_IN_INT = 4;

    private static final byte[] BLANK_INT = "    ".getBytes();

    private final int _initialReadLimit;

    private final int _outBlank1;
    private final int _outBlank2;
    private final int _outBlank3;
    private final int _outBlank4;

    public static int getDataOffset( String name, boolean isInbound ) {
        return AbstractSession.getLogHdrLen( name, isInbound ) + NUM_BYTES_IN_INT - 1;
    }

    public SocketSession( String name,
                          EventRouter inboundRouter,
                          SocketConfig socketConfig,
                          EventDispatcher dispatcher,
                          Encoder encoder,
                          Decoder decoder,
                          Decoder fullDecoder,
                          ThreadPriority receiverPriority ) {

        super( name, inboundRouter, socketConfig, dispatcher, encoder, decoder, fullDecoder, receiverPriority );

        _initialReadLimit = _inHdrLen + NUM_BYTES_IN_INT;

        _outBlank1 = _outHdrLen;
        _outBlank2 = _outHdrLen + 1;
        _outBlank3 = _outHdrLen + 2;
        _outBlank4 = _outHdrLen + 3;
    }

    @Override
    public boolean canHandle() {
        return isConnected();
    }

    @Override
    public void processNextInbound() throws Exception {

        int   preBuffered = _inPreBuffered;
        int   bytesRead;
        int   msgLen;
        Event msg;

        _inPreBuffered = 0;                                                         // reset the preBuffer .. incase of exception
        final int startPos = _inHdrLen + preBuffered;
        _inByteBuffer.limit( _inByteBuffer.capacity() );
        _inByteBuffer.position( startPos );
        bytesRead = initialChannelRead( preBuffered, NUM_BYTES_IN_INT );
        if ( bytesRead == 0 ) {
            _inPreBuffered = preBuffered;
            return;
        }

        final int curPos = _inByteBuffer.position();
        _inByteBuffer.position( _inHdrLen );
        msgLen = _inByteBuffer.getInt();

        if ( msgLen <= 0 || msgLen > _inBuffer.length ) {
            throw new BadMessageSize( "Bad message length : " + msgLen );
        }

        _inByteBuffer.position( curPos ); // restore position to end of read lva

        final int hdrLenPlusMsgLen = _initialReadLimit + msgLen;
        final int fullMsgLen       = NUM_BYTES_IN_INT + msgLen;
        bytesRead = readFixedExpectedBytes( bytesRead, fullMsgLen );

        // time starts from when we have read the full message off the socket
        if ( _logStats ) _decoder.setReceived( Utils.nanoTime() );

        _inByteBuffer.position( _inHdrLen );
        _inByteBuffer.put( BLANK_INT );
        _inByteBuffer.limit( hdrLenPlusMsgLen );

        if ( _stopping ) return;

        _inLogBuf.setLength( hdrLenPlusMsgLen );

        logInEvent( _inLogBuf );

        msg = decode( _initialReadLimit, msgLen );

        try {
            _inPersister.persist( _inBuffer, _initialReadLimit, msgLen );
        } catch( Exception e ) {
            _log.error( ERR_PERSIST_IN, "", e );
        }

        // message decoded, shift left any extra bytes before invoke controller as that can throw exception .. avoids extra try-finally
        final int extraBytes = bytesRead - fullMsgLen;
        if ( extraBytes > 0 ) {
            _inPreBuffered = extraBytes;
            System.arraycopy( _inBuffer, hdrLenPlusMsgLen, _inBuffer, _inHdrLen, extraBytes ); // dont need zero out bytes out on the right
        }

        dispatchInbound( msg );
    }

    @Override
    public void handleForSync( Event msg ) {
        // @TODO push up into StatefulSession interface from Session
    }

    @Override
    public boolean discardOnDisconnect( Event msg ) {
        return false;
    }

    @Override
    public void persistLastInboundMesssage() {
        // @TODO push up into StatefulSession interface from Session
    }

    @Override
    protected void sendNow( Event msg ) throws IOException {

        _encoder.encode( msg );

        final int encodedLen = _encoder.getLength();

        if ( encodedLen > 0 ) {
            long persistedKey = -1;

            int startEncodeIdx = _encoder.getOffset();

            try {
                persistedKey = _outPersister.persist( _outBuffer, startEncodeIdx, encodedLen );
            } catch( Exception e ) {
                _log.error( ERR_PERSIST_OUT, "", e );
            }

            final int     totLimit = encodedLen + startEncodeIdx;
            final boolean logStats = _logStats;

            _outLogBuf.setLength( totLimit );

            _outByteBuffer.clear();
            _outByteBuffer.position( _outHdrLen );
            _outByteBuffer.putInt( encodedLen );
            _outByteBuffer.limit( totLimit );
            _outByteBuffer.position( _outHdrLen );

            long time = 0L;

            if ( logStats ) time = Utils.nanoTime();

            blockingWriteSocket();

            if ( _stopping ) return;

            if ( _markConfirmationEnabled ) {
                try {
                    _outPersister.setUpperFlags( persistedKey, RecoverableSession.PERSIST_FLAG_CONFIRM_SENT );
                } catch( Exception e ) {
                    _log.error( ERR_PERSIST_MKR, "", e );
                }
            }

            // blank out the length 4 bytes so message logs without binary

            _outBuffer[ _outBlank1 ] = ' ';
            _outBuffer[ _outBlank2 ] = ' ';
            _outBuffer[ _outBlank3 ] = ' ';
            _outBuffer[ _outBlank4 ] = ' ';

            _outLogBuf.setLength( totLimit );

            if ( logStats ) _encoder.addStats( _outLogBuf, msg, time );

            logOutEvent( _outLogBuf );
        }
    }

    @Override
    protected final void sendChain( Event msg, boolean canRecycle ) {
        if ( _chainSession != null && _chainSession.isConnected() ) {
            _chainSession.handle( msg );
        } else if ( canRecycle ) {
            outboundRecycle( msg );
        }
    }

    @Override
    protected final void logInEvent( ZString event ) {
        if ( _logEvents ) {
            _log.infoLarge( event );
        }
    }

    @Override
    protected final void logOutEvent( ZString event ) {
        if ( _logEvents ) {
            _log.infoLarge( event );
        }
    }

    @Override
    protected synchronized Event recoveryDecode( byte[] buf, int offset, int len, boolean inBound ) {

        // dont need decode the length as persister knows the record size

        _decoder.parseHeader( buf, offset + NUM_BYTES_IN_INT, len - NUM_BYTES_IN_INT );

        Event msg = _decoder.postHeaderDecode();

        return msg;
    }

    @Override
    protected Event recoveryDecodeWithContext( byte[] buf, int offset, int len, byte[] opt, int optOffset, int optLen, boolean inBound ) {
        return recoveryDecode( buf, offset, len, inBound );
    }

    @Override
    protected void persistIntegrityCheck( boolean inbound, long key, Event msg ) {
        // nothing
    }
}
