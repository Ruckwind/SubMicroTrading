/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.events.perf;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ClockFactory;
import com.rr.core.utils.NumberFormatUtils;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * test the performance difference between using ReusableStrings in a NOS and ViewStrings
 *
 * @author Richard Rose
 */
// TODO check impact of replacing all the small (<=8 byte) with SmallString
public class PerfTestNums extends BaseTestCase {

    private static final int  TEN           = 10;
    private              long _dontOptimise = 0;

    @Test
    public void testIncrementTen() {

        int runs       = 5;
        int iterations = 100000000;

        doRunInc( runs, iterations );
    }

    @Test
    public void testLongToString() {

        int runs       = 5;
        int iterations = 100000000;

        doLongToString( runs, iterations, 987654321L );
        doLongToString( runs, iterations, 10 );
        doLongToString( runs, iterations, 100 );
        doLongToString( runs, iterations, 1000 );
        doLongToString( runs, iterations, 5000 );
        doLongToString( runs, iterations, 1234567891234567L );
        doLongToString( runs, iterations, 5 );
    }

    @Test
    public void testMultTen() {

        int runs       = 5;
        int iterations = 100000000;

        doRunTen( runs, iterations, 10 );
        doRunTen( runs, iterations, 100 );
        doRunTen( runs, iterations, 1000 );
        doRunTen( runs, iterations, 5000 );
        doRunTen( runs, iterations, 1 );

        doRunTen( runs, iterations, 10 );
        doRunTen( runs, iterations, 100 );
        doRunTen( runs, iterations, 1000 );
        doRunTen( runs, iterations, 5000 );
        doRunTen( runs, iterations, 1 );
    }

    private long dblShift10( int iterations, int baseVal, int ten ) {
        long tmpTot = 0;
        int  tmp;

        long startTime = ClockFactory.get().currentTimeMillis();

        for ( int i = 0; i < iterations; ++i ) {

            tmp = (baseVal << 3) + (baseVal << 1);

            tmpTot += tmp;
        }

        long endTime = ClockFactory.get().currentTimeMillis();

        _dontOptimise += tmpTot;

        return endTime - startTime;
    }

    private void doLongToString( int runs, int iterations, long baseVal ) {
        for ( int idx = 0; idx < runs; idx++ ) {

            long time = longToString( iterations, baseVal );

            System.out.println( "Run " + idx + " val=" + baseVal + ", longToString old=" + time );
        }
    }

    private void doRunInc( int runs, int iterations ) {
        for ( int idx = 0; idx < runs; idx++ ) {

            long pre  = pre( iterations );
            long post = post( iterations );

            System.out.println( "Run INC " + idx + " pre=" + pre + ", post=" + post );
        }
    }

    private void doRunTen( int runs, int iterations, int baseVal ) {
        for ( int idx = 0; idx < runs; idx++ ) {

            long normalMult = mult10( iterations, baseVal, 10 );
            long shiftMult  = shift10( iterations, baseVal, 10 );
            long dblShift   = dblShift10( iterations, baseVal, 10 );

            System.out.println( "Run " + idx + " *10=" + normalMult + ", shiftAndAdd=" + shiftMult +
                                ", dblShift=" + dblShift + ", base=" + baseVal );
        }
        assertTrue( _dontOptimise > 0 );
    }

    private long longToString( int iterations, long baseVal ) {
        long startTime = ClockFactory.get().currentTimeMillis();

        final int    len  = NumberFormatUtils.getLongLen( baseVal );
        final byte[] buff = new byte[ len + 1 ];

        for ( int i = 0; i < iterations; ++i ) {
            NumberFormatUtils.addPositiveLongFixedLength( buff, 0, baseVal, len );
        }

        long endTime = ClockFactory.get().currentTimeMillis();

        return endTime - startTime;
    }

    private long mult10( int iterations, int baseVal, int ten ) {
        long tmpTot = 0;
        int  tmp;

        long startTime = ClockFactory.get().currentTimeMillis();

        for ( int i = 0; i < iterations; ++i ) {

            tmp = baseVal * ten;

            tmpTot += tmp;
        }

        long endTime = ClockFactory.get().currentTimeMillis();

        _dontOptimise += tmpTot;

        return endTime - startTime;
    }

    private long post( int iterations ) {

        long tmpTot = 0;

        long startTime = ClockFactory.get().currentTimeMillis();

        for ( int i = 0; i < iterations; ++i ) {

            tmpTot += i;
        }

        long endTime = ClockFactory.get().currentTimeMillis();

        _dontOptimise += tmpTot;

        return endTime - startTime;
    }

    private long pre( int iterations ) {
        long tmpTot = 0;

        long startTime = ClockFactory.get().currentTimeMillis();

        int i = 0;

        while( i < iterations ) {

            tmpTot += ++i;
        }

        long endTime = ClockFactory.get().currentTimeMillis();

        _dontOptimise += tmpTot;

        return endTime - startTime;
    }

    private long shift10( int iterations, int baseVal, int ten ) {

        long tmpTot = 0;
        int  tmp;

        long startTime = ClockFactory.get().currentTimeMillis();

        for ( int i = 0; i < iterations; ++i ) {

            tmp = (baseVal << 3) + baseVal + TEN;

            tmpTot += tmp;
        }

        long endTime = ClockFactory.get().currentTimeMillis();

        _dontOptimise += tmpTot;

        return endTime - startTime;
    }

}
