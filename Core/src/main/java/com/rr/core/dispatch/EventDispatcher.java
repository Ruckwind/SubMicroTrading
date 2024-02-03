/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.dispatch;

import com.rr.core.component.SMTControllableComponent;
import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;

/**
 * @NOTE avoid adding more methods, create new interfaces and implement where appropriate to avoid plethora of empty methods where not wanted
 */
public interface EventDispatcher extends SMTControllableComponent {

    /**
     * @return true if when disconnected an enqueue and replay later
     */
    boolean canQueue();

    void dispatch( Event msg );

    /**
     * dispatch message required for synchronisation ... i.e. before full logOn can be achieved
     *
     * @param msg
     */
    void dispatchForSync( Event msg );

    /**
     * clear any enqueued events IF possible
     * <p>
     * default is no implementation, must be specialised as appropriate
     */
    default void forceStop() { }

    /**
     * some routers may need different behaviour if the delegate is disconnected ie unable to
     *
     * @param isOk
     */
    void handleStatusChange( EventHandler handler, boolean isOk );

    /**
     * @return string of key properties
     */
    String info();

    void setHandler( EventHandler handler );

    void setStopping();

    /**
     * start the dispatcher, also invoke init on the handler, if invoked more than once ignore
     */
    void start();

    ;
}
