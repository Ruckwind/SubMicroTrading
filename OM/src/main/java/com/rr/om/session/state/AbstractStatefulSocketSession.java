/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.state;

import com.rr.core.codec.*;
import com.rr.core.codec.Decoder.ResyncCode;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TLC;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.persister.IndexPersister;
import com.rr.core.persister.PersisterException;
import com.rr.core.session.*;
import com.rr.core.session.socket.*;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.Utils;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class AbstractStatefulSocketSession<T_CONTROLLER extends SessionController<?>,
        T_CFG extends SocketConfig & SessionControllerConfig>
        extends AbstractSocketSession implements SeqNumSession {

    protected static final ZString FAILED_TO_RESYNC   = new ViewString( "Invalid header, failed to skip to next valid header " );
    private static final   ZString FAILED_TO_RETRIEVE = new ViewString( "Failed to reconstitute sent message seqNo=" );

    protected final Logger _log = LoggerFactory.create( AbstractStatefulSocketSession.class );
    protected final T_CONTROLLER _controller;
    protected final T_CFG        _config;
    protected final byte[]     _tmpBuffer;
    protected final ByteBuffer _tmpByteBuffer;
    protected final int _initialBytesToRead;
    private final int _outBufStartMsgIdx;
    private final IndexPersister _inIdxPersister;     // avoid cast cost per write
    private final IndexPersister _outIdxPersister;
    protected       int          _inMsgLen;                                   // length of current inbound message
    private         int          _inMsgSeqNo;
    private boolean _assignSeqNumAndPersist = true;
    private       long           _persistedOutKey;    // persisted key for current out message

    public static int getDataOffset( String name, boolean isInbound ) {
        return AbstractSession.getLogHdrLen( name, isInbound );
    }

    public AbstractStatefulSocketSession( String name,
                                          EventRouter inboundRouter,
                                          T_CFG fixConfig,
                                          EventDispatcher dispatcher,
                                          Encoder encoder,
                                          Decoder decoder,
                                          Decoder fullDecoder,
                                          ThreadPriority receiverPriority,
                                          int initialBytesToRead ) throws SessionException, PersisterException {

        super( name, inboundRouter, fixConfig, dispatcher, encoder, decoder, fullDecoder, receiverPriority );

        _tmpBuffer     = new byte[ fixConfig.getCodecBufferSize() ];
        _tmpByteBuffer = ByteBuffer.wrap( _tmpBuffer );

        _initialBytesToRead = initialBytesToRead;
        _outBufStartMsgIdx  = _outHdrLen;

        _config = fixConfig;

        if ( !(_inPersister instanceof IndexPersister) ) {
            throw new SessionException( "Stateful inbound Persister must be instance of IndexPersister" );
        }

        _inIdxPersister = (IndexPersister) _inPersister;

        if ( !(_outPersister instanceof IndexPersister) ) {
            throw new SessionException( "Stateful outbound Persister must be instance of IndexPersister" );
        }

        _outIdxPersister = (IndexPersister) _outPersister;

        _controller = createSessionContoller();

        _inIdxPersister.open();
        _outIdxPersister.open();
    }

    @Override
    public final boolean canHandle() {
        return !_controller.isLoggedOut();
    }

    @Override
    public final void gapExtendOutbound( int fromSeqNum, int toSeqNum ) {
        ((IndexPersister) _outPersister).addIndexEntries( fromSeqNum, toSeqNum );
    }

    @Override
    public final void gapFillInbound( int fromSeqNum, int uptoSeqNum ) throws SessionStateException {

        boolean ok = _inIdxPersister.addIndexEntries( fromSeqNum, uptoSeqNum );

        if ( !ok ) {
            throw new SessionStateException( "SequenceReset failure: nextExpInSeqNum=" + fromSeqNum + ", NewInSeqNum=" + uptoSeqNum );
        }
    }

    @Override
    public final T_CFG getStateConfig() {
        return _config;
    }

    /**
     * @NOTE MUST ONLY BE CALLED IN THE RECEIVERS THREAD OF CONTROL
     */
    @Override
    public final Event retrieve( int curMsgSeqNo ) {
        Event msg = null;

        boolean isFixDecoder = _fullDecoder instanceof FixDecoder;

        if ( isFixDecoder ) ((FixDecoder) _fullDecoder).setVerifyHdrVals( false );

        try {
            int numBytes = ((IndexPersister) _outPersister).readFromIndex( curMsgSeqNo, _inBuffer, _inHdrLen );

            if ( numBytes > 0 ) {

                _fullDecoder.parseHeader( _inBuffer, _inHdrLen, numBytes );

                msg = _fullDecoder.postHeaderDecode();

                if ( msg instanceof BaseReject<?> ) {
                    BaseReject<?> rej = (BaseReject<?>) msg;

                    _logInMsg.copy( FAILED_TO_RETRIEVE ).append( curMsgSeqNo ).append( ' ' ).append( rej.getMessage() );

                    Throwable t = rej.getThrowable();
                    if ( t instanceof RuntimeDecodingException ) {
                        RuntimeDecodingException rde = (RuntimeDecodingException) t;
                        errorDumpMsg( _logInMsg, rde );
                    }
                    _log.warn( _logInMsg );

                    return null;
                }
            }

        } catch( PersisterException e ) {
            _logInMsg.copy( FAILED_TO_RETRIEVE ).append( curMsgSeqNo ).append( ' ' ).append( e.getMessage() );
            _log.warn( _logInMsg );
        } finally {
            if ( isFixDecoder ) ((FixDecoder) _fullDecoder).setVerifyHdrVals( true );
        }

        return msg;
    }

    @Override
    public final void truncateInboundIndexDown( int fromSeqNum, int toSeqNum ) throws SessionStateException {

        _log.warn( "FixSocketSession.truncateInboundIndexDown() erasing index entries from=" + toSeqNum + " to=" + fromSeqNum );

        boolean ok = _inIdxPersister.removeIndexEntries( fromSeqNum, toSeqNum );

        if ( !ok ) {
            throw new SessionStateException( "Failed to truncate inbound index entries: fromSeqNum=" + toSeqNum + ", downtoSeqNum=" + fromSeqNum );
        }
    }

    @Override
    public final void truncateOutboundIndexDown( int fromSeqNum, int toSeqNum ) throws SessionStateException {

        _log.warn( "FixSocketSession.truncateOutboundIndexDown() erasing index entries from=" + toSeqNum + " to=" + fromSeqNum );

        boolean ok = _outIdxPersister.removeIndexEntries( fromSeqNum, toSeqNum );

        if ( !ok ) {
            throw new SessionStateException( "Failed to truncate outbound index entries: fromSeqNum=" + toSeqNum + ", downtoSeqNum=" + fromSeqNum );
        }
    }

    @Override
    public String info() {
        ReusableString m = TLC.instance().getString();

        m.append( super.info() ).append( " " );

        if ( _controller != null ) m.append( _controller.info() );

        if ( _socketChannel != null ) _socketChannel.info( m );

        String s = m.toString();

        TLC.instance().recycle( m );

        return s;
    }

    @Override
    public boolean isLoggedIn() {
        return _controller.isLoggedIn();
    }

    @Override
    protected final void connected() {
        super.connected();
        _controller.connected();
    }

    @Override
    protected void handleOutboundError( IOException e, Event msg ) {

        if ( _assignSeqNumAndPersist ) {
            _controller.outboundError();
        }

        super.handleOutboundError( e, msg );
    }

    @Override
    protected final void persistIntegrityCheck( final boolean inbound, final long key, final Event msg ) {
        final IndexPersister p = (inbound) ? _inIdxPersister : _outIdxPersister;

        if ( msg != null ) {
            final int seqNum = msg.getMsgSeqNum();

            if ( seqNum > 0 ) {
                p.verifyIndex( key, seqNum );
            }
        }
    }

    /**
     * needs to be syncronised OR have a seperate decoder for in/out reccovery
     */
    @Override
    protected synchronized Event recoveryDecode( byte[] buf, int offset, int len, boolean inBound ) {

        _fullDecoder.parseHeader( buf, offset, len );
        Event msg = _fullDecoder.postHeaderDecode();

        if ( msg != null ) {
            _controller.recoverContext( msg, inBound );
        }

        return msg;
    }

    @Override
    protected Event recoveryDecodeWithContext( byte[] buf, int offset, int len, byte[] opt, int optOffset, int optLen, boolean inBound ) {
        return recoveryDecode( buf, offset, len, inBound );
    }

    @Override
    protected final void sendChain( Event msg, boolean canRecycle ) {
        if ( _assignSeqNumAndPersist && _chainSession != null && _chainSession.isConnected() ) {
            _chainSession.handle( msg );
        } else if ( canRecycle ) {
            outboundRecycle( msg );
        }
    }

    @Override
    protected void sendNow( Event msg ) throws IOException {

        final int encodedLen = encodeForWrite( msg );

        if ( encodedLen > 0 ) {

            final int startEncodeIdx = _encoder.getOffset();
            final int totLimit       = encodedLen + startEncodeIdx;

            _outByteBuffer.clear();
            _outByteBuffer.limit( totLimit );
            _outByteBuffer.position( startEncodeIdx );

            blockingWriteSocket();

            if ( isStopping() ) return;

            postSocketWriteActions( msg, startEncodeIdx, totLimit );
        }
    }

    @Override
    protected final void setInboundRecoveryFinished( boolean finished ) {
        super.setInboundRecoveryFinished( finished );

        if ( finished ) _log.info( getComponentId() + " INBOUND recovery finished nextExpectedInSeqNum=" + _controller.logInboundRecoveryFinished() );
    }

    @Override
    protected final void setOutboundRecoveryFinished( boolean finished ) {
        super.setOutboundRecoveryFinished( finished );

        if ( finished ) _log.info( getComponentId() + " OUTBOUND recovery finished " + _controller.logOutboundRecoveryFinished() );
    }

    @Override public void waitForRecoveryToComplete() {
        super.waitForRecoveryToComplete();

        _log.info( getComponentId() + " recovery complete, comtroller info=" + _controller.info() );
    }

    @Override
    public void processNextInbound() throws Exception {

        final int preBuffered = prepareForReadMessage();

        int bytesRead = initialChannelRead( preBuffered, _initialBytesToRead );
        if ( bytesRead == 0 ) {
            _inPreBuffered = preBuffered;
            return;
        }

        _inMsgLen = _decoder.parseHeader( _inBuffer, _inHdrLen, bytesRead );  // bytesRead is at least INITIAL_READ_BYTES

        if ( _inMsgLen < 0 ) {
            bytesRead = resyncToNextHeader( bytesRead );
        }

        final int hdrLenPlusMsgLen  = _inHdrLen + _inMsgLen;
        final int remainingMsgBytes = _inMsgLen - bytesRead;

        if ( remainingMsgBytes > 0 ) { // in general remainingBytes will be zero as client will disable Nagle
            bytesRead = readFixedExpectedBytes( bytesRead, _inMsgLen );
        }

        processReadMessage( bytesRead, hdrLenPlusMsgLen );
    }

    /**
     * if unable to send the message as session disconnected then if allowed simply persist the event in local persistence
     * allowing event to be recycled and ease memory pressure
     * <p>
     * only possible where session has stateful recovery via gap fill replay, eg FIX
     *
     * @param msg
     * @throws IOException
     */
    @Override public final boolean handleDisconnectedNow( Event msg ) {

        final boolean persistMsg = _config.isLocalPersistWhenDisconnected();

        if ( persistMsg && msg != null ) {

            try {

                if ( persistMsg ) {
                    final int encodedLen = encodeForWrite( msg );

                    if ( encodedLen > 0 ) {

                        final int startEncodeIdx = _encoder.getOffset();
                        final int totLimit       = encodedLen + startEncodeIdx;

                        _outByteBuffer.clear();
                        _outByteBuffer.limit( totLimit );
                        _outByteBuffer.position( startEncodeIdx );

                        if ( _logEvents ) {
                            _logOutMsg.copy( getComponentId() ).append( " session disconnected, enqueued event of type " ).append( msg.getClass().getName() ).append( " with assigned seqNum" ).append( msg.getMsgSeqNum() );
                            _log.info( _logOutMsg );
                        }
                    }
                }

                postSend( msg );

            } catch( SMTRuntimeException e ) {

                handleSendSMTException( e, msg );

            } catch( Exception e ) {
                // not a socket error dont drop socket
                logOutboundError( e, msg );
            }

        }

        return persistMsg;
    }

    @Override
    public final void handleForSync( Event msg ) {
        _outboundDispatcher.dispatchForSync( msg );
    }

    @Override
    public final boolean discardOnDisconnect( Event msg ) {     // discard SESSION messages
        return isSessionMessage( msg );
    }

    // persistence will be invoked by the controller/fixEngine
    @Override
    public final void persistLastInboundMesssage() {
        if ( _inMsgSeqNo > 0 ) {
            try {
                _inIdxPersister.persistIdxAndRec( _inMsgSeqNo, _inBuffer, _inHdrLen, _inMsgLen );
            } catch( Exception e ) {
                _logInErrMsg.setValue( e.getMessage() );
                _logInErrMsg.append( ", inMsgSeqNo=" ).append( _inMsgSeqNo ).append( ", offset=" ).append( _inHdrLen )
                            .append( ", len=" ).append( _inMsgLen );

                _inIdxPersister.appendState( _logOutMsg ).append( ' ' );

                _log.error( ERR_PERSIST_IN, _logInErrMsg, e );
            }
        }
    }

    @Override
    public synchronized void stop() {
        if ( isStopping() ) return;

        super.stop();
        _controller.stop();
    }

    @Override
    protected void disconnected() {
        super.disconnected();
        _controller.changeState( _controller.getStateLoggedOut() );
    }

    /**
     * @return the fix controller, only for use by admin commands
     */
    public final T_CONTROLLER getController() {
        return _controller;
    }

    protected abstract T_CONTROLLER createSessionContoller();

    /**
     * @param msg
     * @return bytes encoded, 0 for no message to send
     */
    protected final int encodeForWrite( final Event msg ) {
        // is Replay or its a message we previously tried but failed to send

        _assignSeqNumAndPersist = (msg.getMsgSeqNum() <= 0);

        int nextOut;

        if ( _assignSeqNumAndPersist ) {
            nextOut = setOutSeqNum( msg );
        } else {
            nextOut = msg.getMsgSeqNum();

            if ( nextOut < 0 ) {
                nextOut = 0;
                msg.setMsgSeqNum( 0 );
            }
        }

        _encoder.encode( msg );

        final int encodedLen     = _encoder.getLength();
        final int startEncodeIdx = _encoder.getOffset();

        if ( encodedLen > 0 ) {
            _persistedOutKey = -1;

            if ( _assignSeqNumAndPersist ) {
                if ( nextOut > 0 ) {
                    try {
                        _persistedOutKey = persistOutRec( nextOut, encodedLen, startEncodeIdx, msg );
                    } catch( Exception e ) {
                        _logOutMsg.setValue( e.getMessage() );
                        _logOutMsg.append( ", outMsgSeqNo=" ).append( nextOut ).append( ", offset=" ).append( startEncodeIdx )
                                  .append( ", len=" ).append( encodedLen );

                        _outIdxPersister.appendState( _logOutMsg ).append( ' ' );

                        _log.error( ERR_PERSIST_OUT, _logOutMsg, e );
                    }
                }
            }
        }

        return encodedLen;
    }

    protected void errorDumpMsg( ReusableString logInMsg, RuntimeDecodingException rde ) {
        _logInMsg.append( ' ' ).append( rde.getFixMsg() );
//        _logInMsg.append( ' ' ).appendHEX( rde.getFixMsg() );
    }

    protected IndexPersister getOutboundPersister() {
        return _outIdxPersister;
    }

    protected void invokeController( Event msg ) throws SessionStateException {
        _controller.handle( msg );
    }

    protected long persistOutRec( int nextOut, final int encodedLen, final int startEncodeIdx, Event msg ) throws PersisterException {
        return _outIdxPersister.persistIdxAndRec( nextOut, _outBuffer, startEncodeIdx, encodedLen );
    }

    protected void postSocketWriteActions( final Event msg, int startEncodeIdx, final int totLimit ) {
        if ( _assignSeqNumAndPersist && _markConfirmationEnabled ) {
            try {
                _outPersister.setUpperFlags( _persistedOutKey, RecoverableSession.PERSIST_FLAG_CONFIRM_SENT );
            } catch( Exception e ) {
                _log.error( ERR_PERSIST_MKR, "", e );
            }
        }

        while( --startEncodeIdx >= _outBufStartMsgIdx ) {
            _outBuffer[ startEncodeIdx ] = ' ';
        }

        _outLogBuf.setLength( totLimit );

        if ( _logStats ) {
            _encoder.addStats( _outLogBuf, msg, getLastSent() );
        }

        logOutEvent( _outLogBuf );
    }

    protected final void processReadMessage( final int bytesRead, final int hdrLenPlusMsgLen ) throws SessionStateException {
        // time starts from when we have read the full message off the socket
        if ( _logStats ) _decoder.setReceived( Utils.nanoTime() );

        _inByteBuffer.position( _inHdrLen );

        if ( isStopping() ) return;

        _inLogBuf.setLength( hdrLenPlusMsgLen );

        final int extraBytes = bytesRead - _inMsgLen;
        _inPreBuffered = extraBytes;                    // set preBuffered here incase decoder throws exception

        logInEvent( _inLogBuf );

        Event msg = _decoder.postHeaderDecode();

        shiftInBufferLeft( extraBytes, hdrLenPlusMsgLen );

        if ( msg != null ) {
            _inMsgSeqNo = msg.getMsgSeqNum();

            invokeController( msg );
        }
    }

    protected abstract int setOutSeqNum( final Event msg );

    /**
     * resync stream IF a header can be found in current buffered lva
     * If find a partial buffer at end of the stream, then shift left and read more
     *
     * @param bytesRead
     * @return bytesRead
     * @throws Exception
     */
    private int resyncToNextHeader( int bytesRead ) throws Exception {

        ResyncCode code = _decoder.resync( _inBuffer, _inHdrLen, bytesRead );

        final int skippedBytes = _decoder.getSkipBytes();

        bytesRead -= skippedBytes;

        shiftInBufferLeft( bytesRead, _inHdrLen + skippedBytes );

        if ( code == ResyncCode.FOUND_PARTIAL_HEADER_NEED_MORE_DATA ) {
            bytesRead = readFixedExpectedBytes( bytesRead, _initialBytesToRead ); // now should have enough for header
        }

        _inMsgLen = _decoder.parseHeader( _inBuffer, _inHdrLen, bytesRead );  // bytesRead is at least INITIAL_READ_BYTES

        if ( _inMsgLen < 0 ) {
            _logInMsg.copy( FAILED_TO_RESYNC ).append( _inBuffer, _inHdrLen, _initialBytesToRead );
            _log.info( _logInMsg );
            throw new BadMessageSize( _logInMsg.toString() );
        }

        return bytesRead;
    }
}
