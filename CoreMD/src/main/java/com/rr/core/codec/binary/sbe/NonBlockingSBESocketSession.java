/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.sbe;

import com.rr.core.codec.DummyDecoder;
import com.rr.core.collections.BlockingSyncQueue;
import com.rr.core.collections.EventQueue;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.session.*;
import com.rr.core.session.socket.AbstractSocketSession;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.Utils;

import java.io.IOException;

/**
 * a multicast socket session implementation for SBE
 *
 * @WARN CHANGES TO THIS CLASS SHOULD BE CHECKED AGAINST FastFixSocketSession
 */
public abstract class NonBlockingSBESocketSession extends AbstractSocketSession implements NonBlockingSession {

    protected static final Logger _log = LoggerFactory.create( NonBlockingSBESocketSession.class );

    protected static final ZString FAILED_TO_RESYNC = new ViewString( "Invalid mcast header, failed to skip to next valid header " );
    private final EventQueue _sendQueue;
    private final EventQueue _sendSyncQueue = new BlockingSyncQueue();
    private int   _outStandingWriteBytes = 0;  // bytes left of current message to write out
    private Event _partialSentMsg;
    private SBEPacketHeader _outPktHdr;

    public NonBlockingSBESocketSession( String name,
                                        EventRouter inboundRouter,
                                        SocketConfig config,
                                        MultiSessionDispatcher dispatcher,
                                        MultiSessionReceiver receiver,
                                        SBEEncoder encoder,
                                        SBEDecoder decoder,
                                        EventQueue dispatchQueue ) {

        // note pass Other as receiver priority to super as NonBlocking sessions do NOT own the receive thread
        super( name, inboundRouter, config, dispatcher, encoder, decoder, new DummyDecoder(), ThreadPriority.Other );

        _sendQueue = dispatchQueue;

        if ( !config.isNIO() ) {
            _log.warn( "Session " + name + " override and enable NIO which is required for non blocking" );
            config.setUseNIO( true );
        }

        attachReceiver( receiver );
        receiver.addSession( this );
    }

    @Override
    public final boolean canHandle() {
        return isConnected();
    }

    @Override
    public synchronized final void connect() {
        _log.info( "NonBlockingSBESocketSession: " + getComponentId() + " connect ignored, connection will be started via MultiSessionThreadedReceiver" );
    }

    @Override
    protected final void disconnected() {
        super.disconnected();
        _inPreBuffered = 0;
        clearPartialMsg();
    }

    @Override
    public final void handleForSync( Event msg ) {
        // TODO Auto-generated method stub

    }

    @Override
    public final boolean discardOnDisconnect( Event msg ) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public final void persistLastInboundMesssage() {
        // TODO Auto-generated method stub

    }

    @Override
    public final boolean isMsgPendingWrite() {
        return _outStandingWriteBytes > 0;
    }

    @Override
    public final void retryCompleteWrite() throws IOException {
        final int startEncodeIdx = _encoder.getOffset();
        final int encodedLen     = _encoder.getLength();
        final int totLimit       = encodedLen + startEncodeIdx;

        if ( _partialSentMsg != null ) {
            final Event echoMsg = _partialSentMsg;

            tryWriteRemaining( _partialSentMsg, startEncodeIdx, totLimit );

            if ( _partialSentMsg == null ) {
                sendChain( echoMsg, true );
            }
        }
    }

    @Override
    public final EventQueue getSendQueue() {
        return _sendQueue;
    }

    @Override
    public final EventQueue getSendSyncQueue() {
        return _sendSyncQueue;
    }

    @Override
    public void logInboundError( Exception e ) {
        super.logInboundError( e );
        ((SBEDecoder) _decoder).logLastMsg();
    }

    @Override
    protected final void handleOutboundError( IOException e, Event msg ) {
        super.handleOutboundError( e, msg );
        clearPartialMsg();
    }

    @Override
    protected final void logInEvent( ZString event ) {
        if ( _logEvents ) {
            ((SBEDecoder) _decoder).logLastMsg();
        }
    }

    @Override
    protected final void logOutEvent( ZString event ) {
        if ( _logEvents ) {
            ((SBEEncoder) _encoder).logLastMsg();
        }
    }

    @Override
    protected final void postSend( Event msg ) {
        // if message has been fully sent then can send to echo session now
        // otherwise echo must be done on retryCompleteWrite

        if ( _partialSentMsg == null ) {
            sendChain( msg, true );
        }
    }

    @Override
    protected final Event recoveryDecode( byte[] buf, int offset, int len, boolean inBound ) {
        return null;
    }

    @Override
    protected final Event recoveryDecodeWithContext( byte[] buf, int offset, int len, byte[] opt, int optOffset, int optLen, boolean inBound ) {
        return null;
    }

    @Override
    protected final void sendChain( final Event msg, final boolean canRecycle ) {
        if ( _chainSession != null && _chainSession.isConnected() ) {
            _chainSession.handle( msg );
        } else if ( canRecycle ) {
            outboundRecycle( msg );
        }
    }

    @Override
    protected final void sendNow( Event msg ) throws IOException {

        final int encodedLen     = encodeForWrite( msg );
        int       startEncodeIdx = _encoder.getOffset();

        if ( encodedLen > 0 ) {

            final int totLimit = encodedLen + startEncodeIdx;

            _outByteBuffer.clear();
            _outByteBuffer.limit( totLimit );
            _outByteBuffer.position( startEncodeIdx );

            _outNativeByteBuffer.clear();
            _outNativeByteBuffer.put( _outByteBuffer );
            _outNativeByteBuffer.flip();

            if ( _logStats ) lastSent( Utils.nanoTime() );

            tryWriteRemaining( msg, startEncodeIdx, totLimit );
        }
    }

    /**
     * @param channelId
     * @param lastSeqNum
     * @param seqNum     upper bound of gap or 0 if book seq num should be reset to 0 ... eg failover at exchange
     */
    protected abstract void dispatchMsgGap( int channelId, int lastSeqNum, int seqNum );

    protected final int nonBlockingRead( final int preBuffered, int requiredBytes ) throws Exception {
        final int maxBytes  = _inByteBuffer.remaining();
        int       totalRead = preBuffered;
        int       curRead;

        if ( requiredBytes - preBuffered <= 0 ) return preBuffered;

        _inNativeByteBuffer.position( 0 );
        _inNativeByteBuffer.limit( maxBytes );

        curRead = _socketChannel.read();

        if ( curRead == -1 ) throw new DisconnectedException( "Detected socket disconnect" );

        // spurious wakeup

        if ( curRead == 0 ) {
            return totalRead;
        }

        totalRead += curRead;

        _inNativeByteBuffer.flip();

        // @TODO bypass the soft ref creation in IOUtil.read for heap based buffers
        _inByteBuffer.put( _inNativeByteBuffer );

        return totalRead;
    }

    protected final void postSocketWriteActions( final Event msg, int startEncodeIdx, final int totLimit ) {

        logOutEvent( null );
        logOutEventPojo( msg );
    }

    private void clearPartialMsg() {
        _outStandingWriteBytes = 0;
        _partialSentMsg        = null;
    }

    private int encodeForWrite( final Event msg ) {

        // dont bother setting nano timestamp
        ++_outPktHdr._packetSeqNum;

        ((SBEEncoder) _encoder).encodeStartPacket( _outPktHdr );
        _encoder.encode( msg );

        return _encoder.getLength();
    }

    /**
     * non blocking socket write returns remaining bytes to be written
     *
     * @return
     * @throws IOException
     */
    private int nonBlockingWriteSocket() throws IOException {

        do {
            if ( _socketChannel.write() == 0 ) {
                if ( _delayedWrites++ == 0 ) {
                    _log.warn( getComponentId() + " Delayed Write : possible slow consumer" );
                }
                break; // DONT BLOCK ON FAILED WRITE
            }
        } while( _outNativeByteBuffer.hasRemaining() && !_stopping ); // allow write to be split into multiple calls

        return (_outNativeByteBuffer.remaining());
    }

    private void tryWriteRemaining( Event msg, int startEncodeIdx, final int totLimit ) throws IOException {
        _outStandingWriteBytes = nonBlockingWriteSocket();

        if ( _stopping ) return;

        if ( _outStandingWriteBytes <= 0 ) {
            final long sendStart = getLastSent();
            if ( sendStart > 0 ) {
                final long duration = Math.abs( Utils.nanoTime() - sendStart );

                if ( duration > _socketConfig.getLogDelayedWriteNanos() ) {
                    _outMessage.reset();
                    _outMessage.append( getComponentId() );
                    _outMessage.append( DELAYED_WRITE ).append( duration / 1000 ).append( MICROS );
                    _log.info( _outMessage );
                }
            }

            postSocketWriteActions( msg, startEncodeIdx, totLimit );
            clearPartialMsg();
        } else {
            _partialSentMsg = msg;
        }
    }
}
