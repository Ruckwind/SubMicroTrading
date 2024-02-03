/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.dispatch.EventDispatcher;

public interface MultiSessionDispatcher extends EventDispatcher {

    /**
     * @param session - a non blocking session ie an NIO one that wont block if socket cant read/write
     */

    void addSession( NonBlockingSession session );
}
