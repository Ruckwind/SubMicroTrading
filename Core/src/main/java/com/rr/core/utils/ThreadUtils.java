/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

import com.rr.core.lang.Procedure;

public interface ThreadUtils {

    String DEFAULT_THREAD_CONFIG = "./config/cpumasks.cfg";

    void delayedRun( String id, int millisToDelay, Procedure procToRun );

    void init( String fileName, boolean isDebug );

    void init( String fileName );

    void setPriority( Thread thread, int priority, int mask );

    void setPriority( Thread thread, ThreadPriority priority );

    /**
     * pause thread for specified milliseconds
     *
     * @param ms milliseconds
     */
    void sleep( int ms );

    /**
     * pause thread for specified microseconds
     *
     * @param micros
     */
    void sleepMicros( int micros );

    /**
     * wait for upto delayInterval milliseconds on the delayLock object
     * will return early if any interruptions occur
     *
     * @param delayLock
     * @param delayIntervalMS
     */
    void waitFor( Object delayLock, int delayIntervalMS );
}
