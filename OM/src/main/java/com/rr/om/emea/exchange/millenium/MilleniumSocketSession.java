/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.millenium;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.Encoder;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;
import com.rr.core.model.Event;
import com.rr.core.persister.PersisterException;
import com.rr.core.session.EventRouter;
import com.rr.core.session.SessionException;
import com.rr.core.session.socket.SessionStateException;
import com.rr.core.utils.ThreadPriority;
import com.rr.model.generated.codec.MilleniumLSEDecoder;
import com.rr.om.session.state.AbstractStatefulSocketSession;

// @TODO detangle index based seqnum persistence from base class into persist helper .. not relevant for millenium

public class MilleniumSocketSession extends AbstractStatefulSocketSession<MilleniumController, MilleniumSocketConfig> {

    private static final int INITIAL_READ_BYTES = 4;

    private final ReusableString _msgContext = new ReusableString();

    public MilleniumSocketSession( String name,
                                   EventRouter inboundRouter,
                                   MilleniumSocketConfig millConfig,
                                   EventDispatcher dispatcher,
                                   Encoder encoder,
                                   Decoder decoder,
                                   Decoder fullDecoder,
                                   ThreadPriority receiverPriority ) throws SessionException, PersisterException {

        super( name, inboundRouter, millConfig, dispatcher, encoder, decoder, fullDecoder, receiverPriority, INITIAL_READ_BYTES );

        if ( _inPersister.getClass() != SequentialPersister.class ) {
            throw new SessionException( "inbound Persister must be of type SequentialPersister" );
        }
    }

    @Override
    protected final MilleniumController createSessionContoller() {
        return MilleniumCommonSessionUtils.createSessionController( this, _config );
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
        MilleniumCommonSessionUtils.getContextForOutPersist( msg, _msgContext );
        return getOutboundPersister().persistIdxAndRec( nextOut, _outBuffer, startEncodeIdx, encodedLen, _msgContext.getBytes(), 0, _msgContext.length() );
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

    // @NOTE ideally decoder should support enrichment of a generic context type, for Millenium this would hold the appId 

    @Override
    public final boolean isSessionMessage( Event msg ) {
        return MilleniumCommonSessionUtils.isSessionMessage( msg );
    }

    @Override public int getLastSeqNumProcessed() { return 0; }

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
