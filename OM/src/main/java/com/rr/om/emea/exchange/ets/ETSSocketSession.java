/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.ets;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.Encoder;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.ZString;
import com.rr.core.model.Event;
import com.rr.core.persister.PersisterException;
import com.rr.core.session.EventRouter;
import com.rr.core.session.SessionException;
import com.rr.core.session.socket.SessionStateException;
import com.rr.core.utils.ThreadPriority;
import com.rr.om.session.state.AbstractStatefulSocketSession;

public class ETSSocketSession extends AbstractStatefulSocketSession<ETSController, ETSSocketConfig> {

    private static final int INITIAL_READ_BYTES = 8;

    public ETSSocketSession( String name,
                             EventRouter inboundRouter,
                             ETSSocketConfig fixConfig,
                             EventDispatcher dispatcher,
                             Encoder encoder,
                             Decoder decoder,
                             Decoder fullDecoder,
                             ThreadPriority receiverPriority ) throws SessionException, PersisterException {

        super( name, inboundRouter, fixConfig, dispatcher, encoder, decoder, fullDecoder, receiverPriority, INITIAL_READ_BYTES );

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
