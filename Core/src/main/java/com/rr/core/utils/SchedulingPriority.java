/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

public interface SchedulingPriority {

    int UNKNOWN_MASK = -1;

    enum CPU {
        ANY, MAIN, SECONDARY, THIRD, FOURTH
    }

    enum CoreThread {
        ANY, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT
    }

    CPU getCPU();

    CoreThread getCoreThread();

    int getMask();

    /**
     * set the mask (from config file  cpumasks.cfg)
     *
     * @param mask
     */
    void setMask( int mask );

    int getPriority();

    /**
     * allow the priority to be set from config
     *
     * @param priority
     */
    void setPriority( int priority );
}
