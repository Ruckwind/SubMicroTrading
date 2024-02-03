/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.state;

import com.rr.core.model.Event;
import com.rr.core.session.socket.SessionStateException;

public interface SessionState {

    /**
     * socket is now connected, take appropriate action for state
     */
    void connected();

    void handle( Event msg ) throws SessionStateException;
}
