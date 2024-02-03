/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.utils.Utils;

/**
 * Router for messages, given a message route it to a destination
 */
public abstract class AbstractEventRouter implements EventRouter {

    private final String _id;
    private SessionStatusListener[] _listeners = new SessionStatusListener[ 0 ];

    public AbstractEventRouter( final String id ) {
        _id = id;
    }

    /**
     * allows users of the router to get status events for destinations
     * only applicable where the destinations support SessionStatus event generation
     *
     * @param listener
     */
    @Override public final synchronized void addSessionStatusListener( SessionStatusListener listener ) {
        _listeners = Utils.arrayCopyAndAddEntry( _listeners, listener );
    }

    @Override public final String getComponentId() { return _id; }

    /**
     * @param msg
     * @WARNING SessionStatusEvent is sent to multiple destinations and must not be
     */
    protected final void handleSessionStatus( final SessionStatusEvent msg ) {
        for ( SessionStatusListener l : _listeners ) {
            l.handle( msg );
        }
    }
}
