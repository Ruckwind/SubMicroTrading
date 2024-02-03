/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

public interface EventHandler extends ThreadedInit {

    /**
     * @return true if can handle messages at the  moment
     */
    boolean canHandle();

    /**
     * messge handler, message may optionally be enqueued for ordered processing on background thread
     * <p>
     * make sure you understand the event recycling requirements for the handler
     * in general the handler will own the event and recycle it
     *
     * @param msg
     */
    void handle( Event msg );

    /**
     * handle the message in the current thread of control
     *
     * @param msg
     * @WARNING will recycle the msg/event
     * @NOTE BE CAREFUL, THIS IS ONLY INTENDED FOR USE BY THREADED DISPATCHER
     */
    void handleNow( Event msg );
}
