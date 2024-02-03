/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.events.perf;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ClockFactory;
import com.rr.core.utils.Utils;
import org.junit.Test;

public class PerfTestTime extends BaseTestCase {

    @Test
    public void testNanoMillis() {

        int runs       = 5;
        int iterations = 1000000000;

        doRun( runs, iterations );
    }

    private void doRun( int runs, int iterations ) {
        for ( int idx = 0; idx < runs; idx++ ) {

            long nano  = nano( iterations );
            long milli = milli( iterations );

            System.out.println( "Run " + idx + " nanoTime=" + nano + " (avg nanoseconds), milli=" + milli + " (avg nanoseconds)" );
        }
    }

    private long milli( int iterations ) {
        long startTime = ClockFactory.get().currentTimeMillis();

        for ( int i = 0; i < iterations; ++i ) {
            ClockFactory.get().currentTimeMillis();
        }

        long endTime = ClockFactory.get().currentTimeMillis();

        return endTime - startTime;
    }

    private long nano( int iterations ) {
        long startTime = Utils.nanoTime();

        for ( int i = 0; i < iterations; ++i ) {

            Utils.nanoTime();
        }

        long endTime = Utils.nanoTime();

        return (endTime - startTime) / iterations;
    }
}
