/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

public interface MultiSessionReceiver extends Receiver {

    void addSession( NonBlockingSession nonBlockingSession );

    int getNumSessions();
}
