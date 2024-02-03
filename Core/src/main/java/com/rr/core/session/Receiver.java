/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.component.SMTComponent;

public interface Receiver extends SMTComponent {

    /**
     * @return true if the receiver has been started
     */
    boolean isStarted();

    void setStopping( boolean stopping );

    /**
     * start the Receiver IF not already running, if already running ignore
     */
    void start();
}
