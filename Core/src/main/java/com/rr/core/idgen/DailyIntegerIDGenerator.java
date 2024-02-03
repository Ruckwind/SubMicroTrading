/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.idgen;

import com.rr.core.idgen.IDGenerator;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ReusableString;

// NON THREADSAFE NUMERIC COUNTER

public class DailyIntegerIDGenerator implements IDGenerator {

    private static final long MS_IN_DAY = 24 * 60 * 60 * 1000;  // 86400000

    private static final int MAX_SEED = 20;
    private static final int MIN_SIZE = 6;

    private final int _instanceSeed;
    private final int _totalFieldLen;

    private int _counter;

    public DailyIntegerIDGenerator( int processInstanceSeed, int totalFieldLen ) {
        _instanceSeed  = processInstanceSeed;
        _totalFieldLen = totalFieldLen;

        // int can hold max  2,147,483,647
        // this allows seeds of 1 to 20

        if ( _instanceSeed <= 0 || _instanceSeed > MAX_SEED ) {
            throw new RuntimeException( "DailyIntegerIDGenerator seed=" + _instanceSeed + ", is invalid, max=" + MAX_SEED );
        }

        if ( _totalFieldLen < MIN_SIZE ) {
            throw new RuntimeException( "DailyIntegerIDGenerator fieldLen=" + _totalFieldLen + ", is too small, min=" + MIN_SIZE );
        }

        //worst case, if 1 ms before midnight there will be room for 13,600,000 unique ids before any chance of overlap 

        seed();
    }

    @Override
    public void genID( ReusableString id ) {

        _counter++;

        id.reset();
        id.append( _counter, _totalFieldLen );
    }

    private void seed() {

        long now = ClockFactory.get().currentTimeMillis();

        int msecsSinceStartOFDay = (int) (now % MS_IN_DAY);

        _counter = _instanceSeed * 100000000 + msecsSinceStartOFDay;
    }
}
