/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.thread;

import com.rr.core.component.SMTActiveWorker;
import com.rr.core.utils.ThreadPriority;

/**
 * ControlThread is a long running (generally daemon) thread, load balances the thread across the registered executable streams
 * For best performance the control thread should spin and not wait/notify
 * Note implememtations can restrict the number of allowed ExecutableElements / streams
 */
public interface ControlThread extends SMTActiveWorker {

    Thread getThread();

    boolean isStarted();

    boolean isStopping();

    void setStopping( boolean stopping );

    void register( ExecutableElement ex );

    void setPriority( ThreadPriority priority );

    void start();

    /**
     * allows executable elements to notify control thread of a change of state, control thread can then wake if it was sleeping
     */
    void statusChange();
}
