/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.ets;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.Encoder;
import com.rr.core.collections.EventQueue;
import com.rr.core.lang.ZString;
import com.rr.core.model.Event;
import com.rr.core.persister.PersisterException;
import com.rr.core.session.EventRouter;
import com.rr.core.session.MultiSessionThreadedDispatcher;
import com.rr.core.session.MultiSessionThreadedReceiver;
import com.rr.core.session.SessionException;
import com.rr.core.session.socket.SessionStateException;
import com.rr.om.session.AbstractNonBlockingSocketSession;

/**
 * on disconnect its possible to loose the buffered messages, its up to Fix or Exchange protocol to re-request
 */
public class ETSNonBlockingSocketSession extends AbstractNonBlockingSocketSession<ETSController, ETSSocketConfig> {

    private static final int INITIAL_READ_BYTES = 8;

    public ETSNonBlockingSocketSession( String name,
                                        EventRouter inboundRouter,
                                        ETSSocketConfig fixConfig,
                                        MultiSessionThreadedDispatcher dispatcher,
                                        MultiSessionThreadedReceiver receiver,
                                        Encoder encoder,
                                        Decoder decoder,
                                        Decoder fullDecoder,
                                        EventQueue dispatchQueue ) throws SessionException, PersisterException {

        super( name, inboundRouter, fixConfig, dispatcher, receiver, encoder, decoder, fullDecoder, INITIAL_READ_BYTES, dispatchQueue );
    }

    @Override
    protected final ETSController createSessionContoller() {
        return new ETSController( this, _config );
    }

    @Override
    protected void invokeController( Event msg ) throws SessionStateException {
        logInEventPojo( msg );                          // must logger event BEFORE controller called
        super.invokeController( msg );
    }

    @Override
    protected void postSocketWriteActions( final Event msg, int startEncodeIdx, final int totLimit ) {
        super.postSocketWriteActions( msg, startEncodeIdx, totLimit );
        logOutEventPojo( msg );
    }

    @Override
    protected final int setOutSeqNum( final Event msg ) {
        return ETSCommonSessionUtils.setOutSeqNum( _controller, msg );
    }

    @Override public int getLastSeqNumProcessed() { return 0; }

    @Override
    public final boolean isSessionMessage( Event msg ) {
        return ETSCommonSessionUtils.isSessionMessage( msg );
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
