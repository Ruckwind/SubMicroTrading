/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.thread;

import com.rr.core.component.SMTControllableComponent;

public interface ExecutableElement extends SMTControllableComponent {

    /**
     * @return true if executor has work it can do
     */
    boolean checkReady();

    /**
     * run execution unit (assumes element is ready)
     * <p>
     * if execution unit is no longer ready it should throw an exception to force control thread to use ready before next execute invoke
     *
     * @throws Exception
     */
    void execute() throws Exception;

    /**
     * handle caught exception thrown during execute, MUST be called on exception before execute is invoked again
     *
     * @param ex
     */
    void handleExecutionException( Exception ex );

    String info();

    /**
     * invoked when element not ready (as opposed to execute)
     *
     * @NOTE must never wait/sleep
     */
    void notReady();

    /**
     * stop the executable element freeing any resources, should only be invoked once
     */
    void stop();

    /**
     * invoked within the thread of control from the owning ControlThread
     * <p>
     * occurs AFTER GC so be aware of object creation
     */
    void threadedInit();
}
