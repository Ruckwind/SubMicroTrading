/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.component.SMTComponent;

public interface ThreadedInit extends SMTComponent {

    /**
     * init specific to the message handler which is requied to be
     * within the thread the handler will run in
     */
    void threadedInit();
}
