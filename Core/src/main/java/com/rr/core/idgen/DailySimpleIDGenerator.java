/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.idgen;

import com.rr.core.idgen.IDGenerator;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;

// NON THREADSAFE NUMERIC COUNTER WITH STRING PREFIX

public class DailySimpleIDGenerator implements IDGenerator {

    private static final long MS_IN_DAY = 24 * 60 * 60 * 1000;  // 86,400,000

    private final ReusableString _base = new ReusableString();
    private       long           _counter;

    public DailySimpleIDGenerator( ZString processInstanceSeed ) {
        _base.copy( processInstanceSeed );

        // int can hold max  2,147,483,647
        // this allows seeds of 1 to 20

        //worst case, if 1 ms before midnight there will be room for 13,600,000 unique ids before any chance of overlap 

        seed();
    }

    @Override
    public void genID( ReusableString id ) {

        _counter++;

        id.copy( _base ).append( _counter );
    }

    private void seed() {

        long now = ClockFactory.get().currentTimeMillis();

        int msecsSinceStartOFDay = (int) (now % MS_IN_DAY);

        // so a restart of 1min would cater for (1 * 60 * 1000 * 100) = 1,500,000 orders

        _counter = msecsSinceStartOFDay * 25;
    }
}
