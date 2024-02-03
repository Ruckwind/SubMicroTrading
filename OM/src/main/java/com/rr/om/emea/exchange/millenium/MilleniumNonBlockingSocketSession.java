/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.millenium;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.Encoder;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.collections.EventQueue;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;
import com.rr.core.model.Event;
import com.rr.core.persister.PersisterException;
import com.rr.core.session.EventRouter;
import com.rr.core.session.MultiSessionThreadedDispatcher;
import com.rr.core.session.MultiSessionThreadedReceiver;
import com.rr.core.session.SessionException;
import com.rr.core.session.socket.SessionStateException;
import com.rr.model.generated.codec.MilleniumLSEDecoder;
import com.rr.om.session.AbstractNonBlockingSocketSession;

public class MilleniumNonBlockingSocketSession extends AbstractNonBlockingSocketSession<MilleniumController, MilleniumSocketConfig> {

    private static final int INITIAL_READ_BYTES = 4;

    private final ReusableString msgContext = new ReusableString();

    public MilleniumNonBlockingSocketSession( String name,
                                              EventRouter inboundRouter,
                                              MilleniumSocketConfig millConfig,
                                              MultiSessionThreadedDispatcher dispatcher,
                                              MultiSessionThreadedReceiver receiver,
                                              Encoder encoder,
                                              Decoder decoder,
                                              Decoder fullDecoder,
                                              EventQueue dispatchQueue ) throws SessionException, PersisterException {

        super( name, inboundRouter, millConfig, dispatcher, receiver, encoder, decoder, fullDecoder, INITIAL_READ_BYTES, dispatchQueue );
    }

    @Override
    protected final MilleniumController createSessionContoller() {
        return MilleniumCommonSessionUtils.createSessionController( this, _config );
    }

    @Override
    protected void errorDumpMsg( ReusableString logInMsg, RuntimeDecodingException rde ) {
        _logInMsg.append( ' ' ).appendHEX( rde.getFixMsg() );
    }

    @Override
    protected void invokeController( Event msg ) throws SessionStateException {
        logInEventPojo( msg );                          // must logger event BEFORE controller called
        _controller.handle( msg, ((MilleniumLSEDecoder) _decoder).getAppId() );
    }

    @Override
    protected synchronized Event recoveryDecode( byte[] buf, int offset, int len, boolean inBound ) {
        return MilleniumCommonSessionUtils.recoveryDecode( _controller, (MilleniumLSEDecoder) _fullDecoder, buf, offset, len, inBound );
    }

    /**
     * for Millenium the only message with extra context is the mkt NOS which stores the srcLinkId
     */
    @Override
    protected Event recoveryDecodeWithContext( byte[] buf, int offset, int len, byte[] opt, int optOffset, int optLen, boolean inBound ) {
        final Event msg = recoveryDecode( buf, offset, len, inBound );
        MilleniumCommonSessionUtils.enrichRecoveredContext( msg, opt, optOffset, optLen );
        return msg;
    }

    /**
     * specialisation to persist linkId as millenium doesnt have anywhere the srcLinkId can be stored for mktNOS
     */
    @Override
    protected long persistOutRec( int nextOut, final int encodedLen, final int startEncodeIdx, Event msg ) throws PersisterException {
        MilleniumCommonSessionUtils.getContextForOutPersist( msg, msgContext );
        return getOutboundPersister().persistIdxAndRec( nextOut, _outBuffer, startEncodeIdx, encodedLen, msgContext.getBytes(), 0, msgContext.length() );
    }

    @Override
    protected final int setOutSeqNum( final Event msg ) {
        return 0;
    }

    @Override
    protected void postSocketWriteActions( final Event msg, int startEncodeIdx, final int totLimit ) {
        super.postSocketWriteActions( msg, startEncodeIdx, totLimit );
        logOutEventPojo( msg );
    }

    @Override public int getLastSeqNumProcessed() { return 0; }

    @Override
    public final boolean isSessionMessage( Event msg ) {
        return MilleniumCommonSessionUtils.isSessionMessage( msg );
    }

    @Override
    protected final void logInEvent( ZString event ) {
        if ( _logEvents ) {
            _log.infoLargeAsHex( event, _inHdrLen );
        }
    }

    @Override
    protected final void logOutEvent( ZString event ) {
        if ( _logEvents ) {
            _log.infoLargeAsHex( event, _outHdrLen );
        }
    }
}
