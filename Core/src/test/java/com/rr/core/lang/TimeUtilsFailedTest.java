package com.rr.core.lang;

import com.rr.core.time.BackTestClock;
import com.rr.core.time.StandardTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class TimeUtilsFailedTest extends BaseTestCase {

    private DateFormat _dateFormatUTC   = new SimpleDateFormat( "yyyyMMdd-HH:mm:ss.SSS" );
    private DateFormat _dateFormatBST   = new SimpleDateFormat( "yyyyMMdd-HH:mm:ss.SSS" );
    private DateFormat _dateFormatLA    = new SimpleDateFormat( "yyyyMMdd-HH:mm:ss.SSS" );
    private DateFormat _dateFormatTokyo = new SimpleDateFormat( "yyyyMMdd-HH:mm:ss.SSS" );

    private TimeUtils _standardTimeUtilsUTC;

    @Test public void failedSingleUnixTimeToLocalTS() {

        final ReusableString dest      = new ReusableString();
        final TimeZone       chicagoTZ = TimeZone.getTimeZone( "America/Chicago" );

        Calendar cal = Calendar.getInstance( TimeZone.getTimeZone( "Europe/London" ) );

        cal.set( 2008, 3 - 1, 10, 4, 59, 59 );
        doUnixTimeToLocalTS( dest, chicagoTZ, cal, "20080309-23:59:59.000 (CDT)" );
    }

    @Test public void failedTimeA() {

        long now = 1531900165959L;

        BackTestClock c = (BackTestClock) ClockFactory.get();
        c.setCurrentTimeMillis( now );

        setUp();

        String utc = getDateTime( now, TimeZone.getTimeZone( "UTC" ) );
        String uk  = getDateTime( now, TimeZone.getTimeZone( "Europe/London" ) );
        String la  = getDateTime( now, TimeZone.getTimeZone( "America/Los_Angeles" ) );

        assertEquals( "2018/07/18 07:49", utc );
        assertEquals( "2018/07/18 08:49", uk );
        assertEquals( "2018/07/18 00:49", la );

        final ReusableString str = new ReusableString( 30 );
        final ByteBuffer     buf = ByteBuffer.wrap( str.getBytes() );

        Calendar cal = Calendar.getInstance( _standardTimeUtilsUTC.getLocalTimeZone() );
        cal.setTimeInMillis( now );
        cal.set( Calendar.HOUR_OF_DAY, 1 );

        final long unixTime = cal.getTimeInMillis();

        utc = getDateTime( unixTime, TimeZone.getTimeZone( "UTC" ) );
        uk  = getDateTime( unixTime, TimeZone.getTimeZone( "Europe/London" ) );
        la  = getDateTime( unixTime, TimeZone.getTimeZone( "America/Los_Angeles" ) );

        assertEquals( "2018/07/18 08:49", utc );
        assertEquals( "2018/07/18 09:49", uk );
        assertEquals( "2018/07/18 01:49", la );

        SimpleDateFormat s = new SimpleDateFormat( "HH:mm:ss.SSS (z)" );
        s.setTimeZone( _standardTimeUtilsUTC.getLocalTimeZone() );

        String expected = s.format( unixTime );

        _standardTimeUtilsUTC.unixTimeToLocalStr( buf, unixTime );
        str.setLength( buf.position() );

        assertEquals( "Failed on hour " + 1, expected, str.toString() );
    }

    @Test public void failedTimeB() {

        long now = 1532137774093L;

        BackTestClock c = (BackTestClock) ClockFactory.get();
        c.setCurrentTimeMillis( now );

        setUp();

        String utc = getDateTime( now, TimeZone.getTimeZone( "UTC" ) );
        String uk  = getDateTime( now, TimeZone.getTimeZone( "Europe/London" ) );
        String la  = getDateTime( now, TimeZone.getTimeZone( "America/Los_Angeles" ) );

        assertEquals( "2018/07/21 01:49", utc );
        assertEquals( "2018/07/21 02:49", uk );
        assertEquals( "2018/07/20 18:49", la );

        final ReusableString str = new ReusableString( 30 );
        final ByteBuffer     buf = ByteBuffer.wrap( str.getBytes() );

        Calendar cal = Calendar.getInstance( _standardTimeUtilsUTC.getLocalTimeZone() );
        cal.setTimeInMillis( now );
        cal.set( Calendar.HOUR_OF_DAY, 1 );

        final long unixTime = cal.getTimeInMillis();

        utc = getDateTime( unixTime, TimeZone.getTimeZone( "UTC" ) );
        uk  = getDateTime( unixTime, TimeZone.getTimeZone( "Europe/London" ) );
        la  = getDateTime( unixTime, TimeZone.getTimeZone( "America/Los_Angeles" ) );

        assertEquals( "2018/07/20 08:49", utc );
        assertEquals( "2018/07/20 09:49", uk );
        assertEquals( "2018/07/20 01:49", la );

        SimpleDateFormat s = new SimpleDateFormat( "HH:mm:ss.SSS (z)" );
        s.setTimeZone( _standardTimeUtilsUTC.getLocalTimeZone() );

        String expected = s.format( unixTime );

        _standardTimeUtilsUTC.unixTimeToLocalStr( buf, unixTime );
        str.setLength( buf.position() );

        assertEquals( "Failed on hour " + 1, expected, str.toString() );
    }

    @Test public void failedUnixTimeToLocalTS() {

        final ReusableString dest      = new ReusableString();
        final TimeZone       chicagoTZ = TimeZone.getTimeZone( "America/Chicago" );

        Calendar cal = Calendar.getInstance( TimeZone.getTimeZone( "Europe/London" ) );

        cal.set( 2008, 3 - 1, 10, 5, 30, 0 );
        doUnixTimeToLocalTS( dest, chicagoTZ, cal, "20080310-00:30:00.000 (CDT)" );

        cal.set( 2008, 3 - 1, 10, 5, 00, 0 );
        doUnixTimeToLocalTS( dest, chicagoTZ, cal, "20080310-00:00:00.000 (CDT)" );

        cal.set( 2008, 3 - 1, 10, 4, 59, 59 );
        doUnixTimeToLocalTS( dest, chicagoTZ, cal, "20080309-23:59:59.000 (CDT)" );

        cal.set( 2008, 3 - 1, 10, 3, 30, 0 );
        doUnixTimeToLocalTS( dest, chicagoTZ, cal, "20080309-22:30:00.000 (CDT)" );
    }

    @Before public void setUp() {
        _standardTimeUtilsUTC = new StandardTimeUtils( TimeZone.getTimeZone( "UTC" ) );

        _standardTimeUtilsUTC.setLocalTimezone( TimeZone.getTimeZone( "America/Los_Angeles" ) );

        _dateFormatUTC.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
        _dateFormatLA.setTimeZone( TimeZone.getTimeZone( "America/Los_Angeles" ) );
        _dateFormatBST.setTimeZone( TimeZone.getTimeZone( "Europe/London" ) );
        _dateFormatTokyo.setTimeZone( TimeZone.getTimeZone( "Asia/Tokyo" ) );

        backTestReset( Env.BACKTEST );
    }

    @After public void tearDown() {
        backTestReset( Env.TEST );
    }

    @Test public void testLAYesterday() throws Exception {

        final long     unixTime   = 1532137774093L;
        final Format   dateFormat = _dateFormatLA;
        final TimeZone timeZone   = _standardTimeUtilsUTC.getLocalTimeZone();

        String todayStr = dateFormat.format( unixTime );

        _standardTimeUtilsUTC.setLocalTimezone( timeZone );
        _standardTimeUtilsUTC.setTodayFromLocalStr( todayStr );

        long internalTime = CommonTimeUtils.unixTimeToInternalTime( unixTime );

        assertEquals( unixTime, internalTime ); // for StandardTimeUtils the internalTime is same as UnixTime
        assertEquals( unixTime, _standardTimeUtilsUTC.internalTimeToUnixTime( internalTime ) );
        assertEquals( unixTime, CommonTimeUtils.unixTimeToInternalTime( unixTime ) );

        final long unixLocalTime = getLocalTime( unixTime, timeZone );
        assertEquals( unixLocalTime, _standardTimeUtilsUTC.internalTimeToLocalMSFromStartDay( internalTime ) );
    }

    @Test public void testZStrToUTC() throws ParseException {

        checkZStrToUTC( "20150331-23:59:00", 1427846340000L );
        checkZStrToUTC( "20150401-00:00:00", 1427846400000L );
        checkZStrToUTC( "20150401-00:01:00", 1427846460000L );
    }

    protected void doUnixTimeToLocalTS( final ReusableString dest, final TimeZone chicagoTZ, final Calendar cal, String expTS ) {
        dest.reset();
        cal.set( Calendar.MILLISECOND, 0 );
        long          now = cal.getTimeInMillis();
        BackTestClock c   = (BackTestClock) ClockFactory.get();
        c.setCurrentTimeMillis( now );
        setUp();
        long unixTime = CommonTimeUtils.internalTimeToUnixTime( now );
        TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( dest, chicagoTZ, unixTime );
        assertEquals( expTS, dest.toString() );
    }

    private void checkZStrToUTC( final String ts, long exp ) throws ParseException {

        final ReusableString zts = new ReusableString( ts );

        long res1 = _standardTimeUtilsUTC.parseUTCStringToInternalTime( zts.getBytes(), 0, zts.length() );

        DateFormat dateFormatUTC = new SimpleDateFormat( "yyyyMMdd-HH:mm:ss" );
        dateFormatUTC.setTimeZone( TimeZone.getTimeZone( "UTC" ) );

        long exp2 = dateFormatUTC.parse( ts ).getTime();

        assertEquals( exp2, res1 );
    }

    private long convStrToUTC( final String dateA ) throws Exception {

        Date d = _dateFormatUTC.parse( dateA );

        return d.getTime();
    }

    private String getDateTime( final long now, final TimeZone tz ) {
        SimpleDateFormat df = new SimpleDateFormat( "yyyy/MM/dd HH:mm" );

        df.setTimeZone( tz );
        return df.format( new Date( now ) );
    }

    private long getLocalTime( final long unixTime, final TimeZone timeZone ) {
        Calendar c = Calendar.getInstance( timeZone );
        c.setTimeInMillis( unixTime );
        long hh = c.get( Calendar.HOUR_OF_DAY );
        long mi = c.get( Calendar.MINUTE );
        long se = c.get( Calendar.SECOND );
        long ms = c.get( Calendar.MILLISECOND );

        return ((hh * 60 + mi) * 60 + se) * 1000 + ms;
    }
}
