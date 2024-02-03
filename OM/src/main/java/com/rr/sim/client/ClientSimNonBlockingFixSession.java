/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.sim.client;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.FixDecoder;
import com.rr.core.codec.FixEncoder;
import com.rr.core.collections.EventQueue;
import com.rr.core.model.Event;
import com.rr.core.persister.PersisterException;
import com.rr.core.session.EventRouter;
import com.rr.core.session.MultiSessionThreadedDispatcher;
import com.rr.core.session.MultiSessionThreadedReceiver;
import com.rr.core.session.SessionException;
import com.rr.om.session.fixsocket.FixSocketConfig;
import com.rr.om.session.fixsocket.NonBlockingFixSocketSession;

public class ClientSimNonBlockingFixSession extends NonBlockingFixSocketSession {

    public interface EventListener {

        void sent( Event msg, long sent );
    }

    private EventListener _listener = null;

    public ClientSimNonBlockingFixSession( String name,
                                           EventRouter inboundRouter,
                                           FixSocketConfig fixConfig,
                                           MultiSessionThreadedDispatcher dispatcher,
                                           MultiSessionThreadedReceiver receiver,
                                           FixEncoder encoder,
                                           FixDecoder decoder,
                                           Decoder fullDecoder,
                                           EventQueue dispatchQueue ) throws SessionException,
                                                                             PersisterException {
        super( name, inboundRouter, fixConfig, dispatcher, receiver, encoder, decoder, fullDecoder, dispatchQueue );
    }

    @Override
    protected void postSocketWriteActions( final Event msg, int startEncodeIdx, final int totLimit ) {
        super.postSocketWriteActions( msg, startEncodeIdx, totLimit );

        if ( _listener != null ) {
            _listener.sent( msg, getLastSent() );
        }
    }

    public EventListener getListener() {
        return _listener;
    }

    public void setListener( EventListener listener ) {
        _listener = listener;
    }

}
