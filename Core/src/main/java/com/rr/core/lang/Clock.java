/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

/**
 * @Note java 1.8 Clock and Instants are not used as they are Immutable and contribute to GC
 */
public interface Clock {

    /**
     * @return the current time in internal time format ... at some point this will change to epoch time in micros
     */
    long currentInternalTime();

    long currentTimeMillis();

    long nanoTime();

    /**
     * @return current time in nanoseconds using CLOCK_MONOTONIC_RAW OR if not supported on OS then nanos based on RDTSC
     */
    long nanoTimeMonotonicRaw();
}
