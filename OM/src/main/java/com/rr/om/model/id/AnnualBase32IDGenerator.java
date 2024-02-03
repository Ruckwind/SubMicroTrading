/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.model.id;

import com.rr.core.idgen.AbstractBase32IDGenerator;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ZString;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * in effect the counter will be the number of millisends from start of year to the last midnight.
 * this gives 24 * 60 * 60 * 1000 = 86,400,000 ids per day unique over the year
 * max id is 365 * 86,400,000 = 31,536,000,000
 * in base 32, a length of seven is enough for 34,359,738,368 combinations
 */
public class AnnualBase32IDGenerator extends AbstractBase32IDGenerator {

    public AnnualBase32IDGenerator() {
        super();
    }

    /**
     * @param processInstanceSeed
     * @param len                 length of non seed component
     */
    public AnnualBase32IDGenerator( ZString processInstanceSeed, int len ) {
        super( processInstanceSeed, len );
    }

    @Override
    protected long seed() {
        final long now = ClockFactory.get().currentTimeMillis();

        Calendar c = new GregorianCalendar( TimeZone.getTimeZone( "UTC" ) );

        c.setTimeInMillis( now );

        c.set( Calendar.HOUR_OF_DAY, 0 );
        c.set( Calendar.MINUTE, 0 );
        c.set( Calendar.SECOND, 0 );
        c.set( Calendar.MILLISECOND, 0 );
        c.set( Calendar.DAY_OF_MONTH, 1 );
        c.set( Calendar.MONTH, 0 );

        return now - c.getTimeInMillis();
    }
}
