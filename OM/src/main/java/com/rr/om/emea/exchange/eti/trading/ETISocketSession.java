/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.eti.trading;

import com.rr.codec.emea.exchange.eti.ETIDecodeContext;
import com.rr.codec.emea.exchange.eti.ETIDecoder;
import com.rr.codec.emea.exchange.eti.ETIEncoder;
import com.rr.core.codec.Decoder;
import com.rr.core.codec.Encoder;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;
import com.rr.core.model.Event;
import com.rr.core.persister.PersisterException;
import com.rr.core.session.EventRouter;
import com.rr.core.session.RecoverableSession;
import com.rr.core.session.SessionException;
import com.rr.core.session.socket.SessionStateException;
import com.rr.core.utils.ThreadPriority;
import com.rr.om.session.state.AbstractStatefulSocketSession;

/**
 * ETI socket session
 * note ETI has a gateway socket and trading socket
 * to simplify generic socket handling, the gateway instance must control/instruct the trading session
 */

public class ETISocketSession extends AbstractStatefulSocketSession<ETIController, ETISocketConfig> implements ETISession {

    private static final int INITIAL_READ_BYTES = 8;

    private final ETIDecodeContext _lastDecodeContext;

    private RecoverableSession _gwySess;

    public ETISocketSession( String name,
                             EventRouter inboundRouter,
                             ETISocketConfig etiConfig,
                             EventDispatcher dispatcher,
                             Encoder encoder,
                             Decoder decoder,
                             Decoder fullDecoder,
                             ThreadPriority receiverPriority ) throws SessionException, PersisterException {

        super( name, inboundRouter, etiConfig, dispatcher, encoder, decoder, fullDecoder, receiverPriority, INITIAL_READ_BYTES );

        _lastDecodeContext = new ETIDecodeContext( etiConfig.getExpectedRequests() );

        ((ETIEncoder) encoder).setSenderSubID( etiConfig.getUserId() );
        ((ETIEncoder) encoder).setLocationId( etiConfig.getLocationId() );
        ((ETIEncoder) encoder).setUniqueClientCode( etiConfig.getUniqueClientCode() );

        if ( etiConfig.isServer() ) { // emulating exchange
            ((ETIEncoder) encoder).setExchangeEmulationOn();
            ((ETIDecoder) decoder).setExchangeEmulationOn();
        }
    }

    @Override
    protected final ETIController createSessionContoller() {
        return ETICommonSessionUtils.createSessionController( this, _config );
    }

    @Override
    protected void invokeController( Event msg ) throws SessionStateException {
        logInEventPojo( msg );                          // must logger event BEFORE controller called
        ((ETIDecoder) _decoder).getLastContext( _lastDecodeContext );
        _controller.handle( msg );
    }

    @Override
    protected synchronized Event recoveryDecode( byte[] buf, int offset, int len, boolean inBound ) {
        return ETICommonSessionUtils.recoveryDecode( _controller, (ETIDecoder) _fullDecoder, _lastDecodeContext, buf, offset, len, inBound );
    }

    @Override
    protected Event recoveryDecodeWithContext( byte[] buf, int offset, int len, byte[] opt, int optOffset, int optLen, boolean inBound ) {
        final Event msg = recoveryDecode( buf, offset, len, inBound );
        ETICommonSessionUtils.enrichRecoveredContext( msg, opt, optOffset, optLen );
        return msg;
    }

    /**
     * specialisation to persist linkId as ETI doesnt have anywhere to store it, the srcLinkId is stored for mktNOS
     */
    @Override
    protected long persistOutRec( int nextOut, final int encodedLen, final int startEncodeIdx, Event msg ) throws PersisterException {
        ReusableString linkId = _lastDecodeContext.getParentClOrdIdForUpdate();
        ETICommonSessionUtils.getContextForOutPersist( msg, linkId );
        return getOutboundPersister().persistIdxAndRec( nextOut, _outBuffer, startEncodeIdx, encodedLen, linkId.getBytes(), 0, linkId.length() );
    }

    @Override
    protected final int setOutSeqNum( final Event msg ) {
        return ETICommonSessionUtils.setOutSeqNum( _controller, _lastDecodeContext, msg );
    }

    @Override
    protected void postSocketWriteActions( final Event msg, int startEncodeIdx, final int totLimit ) {
        super.postSocketWriteActions( msg, startEncodeIdx, totLimit );
        logOutEventPojo( msg );
    }

    @Override
    public ETIDecodeContext getDecodeContext() {
        return _lastDecodeContext;
    }

    @Override
    public RecoverableSession getGatewaySession() {
        return (_gwySess != null) ? _gwySess : this;
    }

    public void setGatewaySession( RecoverableSession gwySess ) {
        _gwySess = gwySess;
    }

    @Override
    public final boolean isSessionMessage( Event msg ) {
        return ETICommonSessionUtils.isSessionMessage( msg );
    }

    // @NOTE ideally decoder should support enrichment of a generic context type, for Millenium this would hold the appId 

    @Override public int getLastSeqNumProcessed() { return 0; }

    @Override
    public void startWork() {
        _log.info( "Start Work for ETI session delegated to gateway session" );
        getGatewaySession().connect();
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
