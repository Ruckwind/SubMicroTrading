/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.model.EventHandler;

/**
 * Router for messages, given a message route it to a destination
 */
public interface EventRouter extends EventHandler {

    /**
     * allows users of the router to get status events for destinations
     * only applicable where the destinations support SessionStatus event generation
     *
     * @param listener
     */
    void addSessionStatusListener( SessionStatusListener listener );
}
