/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.os;

public interface NativeHooks {

    int MAX_PRIORITY = 10;

    long nanoTimeMonotonicRaw();

    long nanoTimeRDTSC();

    /**
     * set priority 1 to 10 with 10 highest
     *
     * @param priority
     */
    void setPriority( Thread thread, int mask, int priority );

    void setProcessMaxPriority();

    void sleep( int ms );

    void sleepMicros( int micros );
}
