/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.idgen;

import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ZString;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * in effect the counter will be the number of millisends from one week ago.
 */
public class WeeklyBase32IDGenerator extends AbstractBase32IDGenerator {

    public WeeklyBase32IDGenerator() {
        super();
    }

    /**
     * @param processInstanceSeed
     * @param len                 length of non seed component
     */
    public WeeklyBase32IDGenerator( ZString processInstanceSeed, int len ) {
        super( processInstanceSeed, len );

        seed();
    }

    @Override
    protected long seed() {
        Calendar c = new GregorianCalendar( TimeZone.getTimeZone( "UTC" ) );

        final long now = ClockFactory.get().currentTimeMillis();

        c.setTimeInMillis( now );

        c.set( Calendar.HOUR_OF_DAY, 0 );
        c.set( Calendar.MINUTE, 0 );
        c.set( Calendar.SECOND, 0 );
        c.set( Calendar.MILLISECOND, 0 );
        c.set( Calendar.MONTH, 0 );

        c.add( Calendar.DAY_OF_MONTH, -7 );

        return now - c.getTimeInMillis();
    }
}
