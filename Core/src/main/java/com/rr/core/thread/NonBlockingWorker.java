/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.thread;

import com.rr.core.component.SMTActiveWorker;
import com.rr.core.component.SMTComponent;

public interface NonBlockingWorker extends SMTComponent, SMTActiveWorker {

    interface StatusChanged {

        void stateChange( RunState state );
    }

    /**
     * do a unit of work .... important that work units are fast as possible as will hold up other workers
     * <p>
     * if no work pending then just return
     */
    void doWorkUnit();

    void registerListener( StatusChanged callback );

    void stop();

    /**
     * init specific to the message handler which is requied to be
     * within the thread the handler will run in
     * all timers must be set in the threadedInit not init method for them to work in backtest
     */
    void threadedInit();
}
