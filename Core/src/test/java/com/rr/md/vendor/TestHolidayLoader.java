package com.rr.md.vendor;

import com.rr.core.hols.HolidayLoader;
import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.Env;
import com.rr.core.logger.Level;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.ExchangeCode;
import com.rr.core.model.HolidayEntry;
import com.rr.core.time.BackTestClock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class TestHolidayLoader extends BaseTestCase {

    @Before public void setUp() throws Exception {
        LoggerFactory.forceLevel( Level.debug );

        backTestReset( Env.BACKTEST );

        final BackTestClock clock = (BackTestClock) ClockFactory.get();

        SimpleDateFormat df = new SimpleDateFormat( "yyyyMMdd-HH:mm:ss.SSS" );
        df.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
        long unixTime = df.parse( "20201215-00:00:00.001" ).getTime();
        clock.setCurrentTimeMillis( unixTime );
    }

    @After
    public void tearDown() throws Exception {
        backTestReset( Env.TEST );
    }

    @Test public void testCME() {

        HolidayLoader holidayLoader = new HolidayLoader();

        LinkedHashMap<Integer, HolidayEntry> hols = holidayLoader.getHolidays( ExchangeCode.XCME );

        int yyyymmdd = 20210118;

        HolidayEntry h = hols.get( yyyymmdd );

        assertTrue( HolidayLoader.instance().isHoliday( ExchangeCode.XCME, yyyymmdd ) );
    }

    @Test public void testHols() {
        HolidayLoader holidayLoader = new HolidayLoader();

        LinkedHashMap<Integer, HolidayEntry> hols = holidayLoader.getHolidays( ExchangeCode.XLON );

        HolidayEntry h = hols.get( 20211224 );

        assertEquals( "HolidayEntryImpl , date=20211224, holidayName=Half day, market=London, openTime=20211224-08:00:00.000 (UTC), closeTime=20211224-12:30:00.000 (UTC), mic=XLON", h.toString() );
        assertEquals( 20211224, h.getDate() );
        assertEquals( ExchangeCode.XLON, h.getMic() );
    }

    @Test
    public void testInvalidDate() {
        try {
            HolidayLoader.getDate( "2020-1-01" );
            fail( "Invalid date" );
        } catch( Exception e ) {
            assertEquals( "HolidayLoader getDate str too small [2020-1-01]", e.getMessage() );
        }
        // no validation on validity of result
        assertEquals( 20202501, HolidayLoader.getDate( "2020-25-01" ) );
        assertEquals( 170202501, HolidayLoader.getDate( "A020-25-01" ) );
        assertEquals( 455542120, HolidayLoader.getDate( "YYYY-MM-DD" ) );
    }

    @Test
    public void testParseDate() {
        assertEquals( 20201001, HolidayLoader.getDate( "2020-10-01" ) );
        assertEquals( 19990101, HolidayLoader.getDate( "1999-01-01" ) );
        assertEquals( 20231229, HolidayLoader.getDate( "2023-12-29" ) );
    }

    @Test public void testSynthHols() {

        HolidayLoader holidayLoader = new HolidayLoader();

        LinkedHashMap<Integer, HolidayEntry> hols = holidayLoader.getHolidays( ExchangeCode.XCME );

        Calendar c = Calendar.getInstance();

        c.setTimeInMillis( ClockFactory.get().currentTimeMillis() );

        int year = c.get( Calendar.YEAR );

        int dayOfYear = c.get( Calendar.DAY_OF_YEAR );

        if ( dayOfYear > 359 ) {
            ++year;
        }

        int xmas = year * 10000 + 1225;

        HolidayEntry h = hols.get( xmas );

        assertEquals( "HolidayEntryImpl , date=" + year + "1225, holidayName=HOL" + year + "1225, market=XCME, mic=XCME", h.toString() );
        assertEquals( xmas, h.getDate() );
        assertEquals( ExchangeCode.XCME, h.getMic() );

        assertTrue( HolidayLoader.instance().isHoliday( ExchangeCode.XCME, xmas ) );
    }

    @Test
    public void timeBombToRefreshMarketHolidaysCsv() {
        LocalDate now                        = LocalDate.now( Clock.systemUTC() );
        LocalDate refreshedHolidayDatesUntil = LocalDate.of( 2024, 12, 1 );
        assertTrue( now.isBefore( refreshedHolidayDatesUntil ) );
    }
}
