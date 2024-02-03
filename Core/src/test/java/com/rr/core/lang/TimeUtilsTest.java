package com.rr.core.lang;

import com.rr.core.time.StandardTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class TimeUtilsTest extends BaseTestCase {

    private DateFormat _dateFormatUTC     = new SimpleDateFormat( "yyyyMMdd-HH:mm:ss.SSS" );
    private DateFormat _dateFormatBST     = new SimpleDateFormat( "yyyyMMdd-HH:mm:ss.SSS" );
    private DateFormat _dateFormatLA      = new SimpleDateFormat( "yyyyMMdd-HH:mm:ss.SSS" );
    private DateFormat _dateFormatChicago = new SimpleDateFormat( "yyyyMMdd-HH:mm:ss.SSS" );
    private DateFormat _dateFormatTokyo   = new SimpleDateFormat( "yyyyMMdd-HH:mm:ss.SSS" );

    private TimeUtils _standardTimeUtilsUTC;

    @Before public void setUp() {
        _standardTimeUtilsUTC = new StandardTimeUtils( TimeZone.getTimeZone( "UTC" ) );

        _standardTimeUtilsUTC.setLocalTimezone( TimeZone.getTimeZone( "America/Los_Angeles" ) );

        _dateFormatUTC.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
        _dateFormatLA.setTimeZone( TimeZone.getTimeZone( "America/Los_Angeles" ) );
        _dateFormatChicago.setTimeZone( TimeZone.getTimeZone( "America/Chicago" ) );
        _dateFormatBST.setTimeZone( TimeZone.getTimeZone( "Europe/London" ) );
        _dateFormatTokyo.setTimeZone( TimeZone.getTimeZone( "Asia/Tokyo" ) );
    }

    @After public void tearDown() {
        backTestReset( Env.TEST );
    }

    @Test public void testFailedBST() throws Exception {

        StandardTimeUtils timeUtilsLdn = new StandardTimeUtils( TimeZone.getTimeZone( "Europe/London" ) );

        long timeInternal;

        timeUtilsLdn.setTodayFromLocalStr( "2015-10-25", "yyyy-MM-dd" );
        timeInternal = timeUtilsLdn.localMSFromMidnightToInternalTimeToday( 2 * Constants.MS_IN_HOUR + 1 );
        assertEquals( "20151025-02:00:00.001 (UTC)", timeUtilsLdn.unixTimeToUTCTimestamp( timeInternal ) );

        timeUtilsLdn.setTodayFromLocalStr( "2015-03-30", "yyyy-MM-dd" );
        timeInternal = timeUtilsLdn.localMSFromMidnightToInternalTimeToday( 1 );
        assertEquals( "20150329-23:00:00.001 (UTC)", timeUtilsLdn.unixTimeToUTCTimestamp( timeInternal ) );

        timeUtilsLdn.setTodayFromLocalStr( "2015-03-29", "yyyy-MM-dd" );
        timeInternal = timeUtilsLdn.localMSFromMidnightToInternalTimeToday( 1 );
        assertEquals( "20150329-00:00:00.001 (UTC)", timeUtilsLdn.unixTimeToUTCTimestamp( timeInternal ) );

        timeUtilsLdn.setTodayFromLocalStr( "2015-03-29", "yyyy-MM-dd" );
        timeInternal = timeUtilsLdn.localMSFromMidnightToInternalTimeToday( 2 * Constants.MS_IN_HOUR + 1 );
        assertEquals( "20150329-01:00:00.001 (UTC)", timeUtilsLdn.unixTimeToUTCTimestamp( timeInternal ) );

        timeUtilsLdn.setTodayFromLocalStr( "2015-10-25", "yyyy-MM-dd" );
        timeInternal = timeUtilsLdn.localMSFromMidnightToInternalTimeToday( 2 * Constants.MS_IN_HOUR );
        assertEquals( "20151025-01:00:00.000 (UTC)", timeUtilsLdn.unixTimeToUTCTimestamp( timeInternal ) );

        timeUtilsLdn.setTodayFromLocalStr( "2015-10-25", "yyyy-MM-dd" );
        timeInternal = timeUtilsLdn.localMSFromMidnightToInternalTimeToday( 3 * Constants.MS_IN_HOUR + 1 );
        assertEquals( "20151025-03:00:00.001 (UTC)", timeUtilsLdn.unixTimeToUTCTimestamp( timeInternal ) );

        timeUtilsLdn.setTodayFromLocalStr( "2015-10-25", "yyyy-MM-dd" );
        timeInternal = timeUtilsLdn.localMSFromMidnightToInternalTimeToday( 1 * Constants.MS_IN_HOUR + 1 );
        assertEquals( "20151025-00:00:00.001 (UTC)", timeUtilsLdn.unixTimeToUTCTimestamp( timeInternal ) );

        timeUtilsLdn.setTodayFromLocalStr( "2012-06-01", "yyyy-MM-dd" );
        timeInternal = timeUtilsLdn.localMSFromMidnightToInternalTimeToday( 28802525L );
        assertEquals( "20120601-07:00:02.525 (UTC)", timeUtilsLdn.unixTimeToUTCTimestamp( timeInternal ) );
    }

    @Test public void testFailedChicagoDate() throws Exception {

        final long unixTime = 877930140000L;
        System.out.println( "UTC=" + _dateFormatUTC.format( new Date( unixTime ) ) );
        System.out.println( "CHICAGO=" + _dateFormatChicago.format( new Date( unixTime ) ) );

        /**
         *     UTC=19971027-05:29:00.000
         * CHICAGO=19971026-23:29:00.000
         */
        TimeZone tz = TimeZone.getTimeZone( "America/Chicago" );

        final ReusableString dest = new ReusableString();
        TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( dest, tz, unixTime );

        assertEquals( "19971026-23:29:00.000 (CST)", dest.toString() );

        long decodedMS = TimeUtilsFactory.safeTimeUtils().localTimeStampToUnixTime( dest );

        assertEquals( unixTime, decodedMS );
    }

    @Test public void testFailedUnixTimeToLocalStrB() throws Exception {
        String dateA           = "20180501-00:15:25.123";
        long   expDateAinUTCMs = convStrToUTC( dateA );

        final StandardTimeUtils standardTimeUtilsBST = new StandardTimeUtils( TimeZone.getTimeZone( "Europe/London" ) );
        doTestUnixTimeToLocalStr( 0, standardTimeUtilsBST, expDateAinUTCMs );
    }

    @Test public void testFrankfurtLocal2ToHHMMSSzzz() throws Exception {

        String dateA           = "20050103-16:15:25.123";
        long   expDateAinUTCMs = convStrToUTC( dateA );

        TimeZone tz = TimeZone.getTimeZone( "CET" );

        _standardTimeUtilsUTC.setLocalTimezone( tz );

        int hhmmsszzz = _standardTimeUtilsUTC.unixTimeToIntLocalHHMMSSzzz( expDateAinUTCMs );

        assertEquals( 171525123, hhmmsszzz );
    }

    @Test public void testFrankfurtLocalToHHMMSSzzz() throws Exception {

        String dateA           = "20180131-16:15:25.123";
        long   expDateAinUTCMs = convStrToUTC( dateA );

        TimeZone tz = TimeZone.getTimeZone( "Europe/Frankfurt" );

        _standardTimeUtilsUTC.setLocalTimezone( tz );

        int hhmmsszzz = _standardTimeUtilsUTC.unixTimeToIntLocalHHMMSSzzz( expDateAinUTCMs );

        assertEquals( 161525123, hhmmsszzz );
    }

    @Test public void testIsAtOrAfterLA() throws Exception {

        String dateA           = "20180131-06:15:25.123";
        long   expDateAinUTCMs = convStrToUTC( dateA );

        TimeZone tz = TimeZone.getTimeZone( "America/Los_Angeles" );

        _standardTimeUtilsUTC.setLocalTimezone( tz );

        int hhmmsszzz = _standardTimeUtilsUTC.unixTimeToIntLocalHHMMSSzzz( expDateAinUTCMs );

        assertEquals( 221525123, hhmmsszzz );

        assertTrue( CommonTimeUtils.isAtOrAfter( _standardTimeUtilsUTC, expDateAinUTCMs, hhmmsszzz ) );

        assertTrue( CommonTimeUtils.isAtOrAfter( _standardTimeUtilsUTC, expDateAinUTCMs + 1, hhmmsszzz ) );

        assertFalse( CommonTimeUtils.isAtOrAfter( _standardTimeUtilsUTC, expDateAinUTCMs - 1, hhmmsszzz ) );

        // adding 2 hours pushes time into next day
        assertFalse( CommonTimeUtils.isAtOrAfter( _standardTimeUtilsUTC, expDateAinUTCMs + 2 * Constants.MS_IN_HOUR, hhmmsszzz ) );
    }

    @Test public void testLALocalToHHMMSSzzz() throws Exception {

        String dateA           = "20180131-22:15:25.123";
        long   expDateAinUTCMs = convStrToUTC( dateA );

        TimeZone tz = TimeZone.getTimeZone( "America/Los_Angeles" );

        _standardTimeUtilsUTC.setLocalTimezone( tz );

        int hhmmsszzz = _standardTimeUtilsUTC.unixTimeToIntLocalHHMMSSzzz( expDateAinUTCMs );

        assertEquals( 141525123, hhmmsszzz );
    }

    @Test public void testLAPrevDayLocalToHHMMSSzzz() throws Exception {

        String dateA           = "20180131-06:15:25.123";
        long   expDateAinUTCMs = convStrToUTC( dateA );

        TimeZone tz = TimeZone.getTimeZone( "America/Los_Angeles" );

        _standardTimeUtilsUTC.setLocalTimezone( tz );

        int hhmmsszzz = _standardTimeUtilsUTC.unixTimeToIntLocalHHMMSSzzz( expDateAinUTCMs );

        assertEquals( 221525123, hhmmsszzz );
    }

    @Test public void testLongConversions() throws Exception {
        String dateA           = "20180131-02:15:25.123";
        long   expDateAinUTCMs = convStrToUTC( dateA );

        doTestLongConversions( 1532137774093L, _dateFormatLA, _standardTimeUtilsUTC.getLocalTimeZone() ); // previous failed test
        doTestLongConversions( 1517361325123L, _dateFormatLA, _standardTimeUtilsUTC.getLocalTimeZone() ); // previous failed test

        doTestLongConversions( ClockFactory.get().currentTimeMillis(), _dateFormatLA, _standardTimeUtilsUTC.getLocalTimeZone() );
        doTestLongConversions( expDateAinUTCMs, _dateFormatLA, _standardTimeUtilsUTC.getLocalTimeZone() );

        dateA           = "20180131-23:15:25.123";
        expDateAinUTCMs = convStrToUTC( dateA );
        doTestLongConversions( expDateAinUTCMs, _dateFormatLA, _standardTimeUtilsUTC.getLocalTimeZone() );
    }

    @Test public void testLongConversionsTokyo() throws Exception {
        String dateA           = "20180131-02:15:25.123";
        long   expDateAinUTCMs = convStrToUTC( dateA );

        TimeZone tz = TimeZone.getTimeZone( "Asia/Tokyo" );

        doTestLongConversions( 1517361325123L, _dateFormatTokyo, tz ); // previous failed test

        doTestLongConversions( ClockFactory.get().currentTimeMillis(), _dateFormatTokyo, tz );
        doTestLongConversions( expDateAinUTCMs, _dateFormatTokyo, tz );

        dateA           = "20180131-22:15:25.123";
        expDateAinUTCMs = convStrToUTC( dateA );
        doTestLongConversions( expDateAinUTCMs, _dateFormatTokyo, tz );
    }

    @Test public void testOldLA() throws Exception {
        String dateA           = "20180131-09:15:25.123";
        long   expDateAinUTCMs = convStrToUTC( dateA );

        expDateAinUTCMs = expDateAinUTCMs + _standardTimeUtilsUTC.getLocalTimeZone().getOffset( expDateAinUTCMs ); // YYYMMDD=1517356800000, fullDateTime=1517390125123

        checkStrToLocalTimeMSandBack( _standardTimeUtilsUTC, dateA, expDateAinUTCMs );

        // dont bother testing HFTTTimeUtils the non UTC stuff has bugs
    }

    @Test public void testOldUTC() throws Exception {
        String dateA           = "20180131-09:15:25.123";
        long   expDateAinUTCMs = convStrToUTC( dateA ); // YYYMMDD=1517356800000, fullDateTime=1517390125123

        checkStrToUnixTimeMSandBack( _standardTimeUtilsUTC, dateA, expDateAinUTCMs );
    }

    @Test public void testParisLocal2ToHHMMSSzzz() throws Exception {

        String dateA           = "20050103-16:15:25.123";
        long   expDateAinUTCMs = convStrToUTC( dateA );

        TimeZone tz = TimeZone.getTimeZone( "Europe/Paris" );

        _standardTimeUtilsUTC.setLocalTimezone( tz );

        int hhmmsszzz = _standardTimeUtilsUTC.unixTimeToIntLocalHHMMSSzzz( expDateAinUTCMs );

        assertEquals( 171525123, hhmmsszzz );
    }

    @Test public void testParisLocalToHHMMSSzzz() throws Exception {

        String dateA           = "20180131-16:15:25.123";
        long   expDateAinUTCMs = convStrToUTC( dateA );

        TimeZone tz = TimeZone.getTimeZone( "Europe/Paris" );

        _standardTimeUtilsUTC.setLocalTimezone( tz );

        int hhmmsszzz = _standardTimeUtilsUTC.unixTimeToIntLocalHHMMSSzzz( expDateAinUTCMs );

        assertEquals( 171525123, hhmmsszzz );
    }

    @Test public void testParseUTCStringToInternalTime() throws Exception {

        StandardTimeUtils timeUtilsLdn = new StandardTimeUtils( TimeZone.getTimeZone( "Europe/London" ) );

        timeUtilsLdn.setTodayFromLocalStr( "2020-03-24", "yyyy-MM-dd" );

        String date = "          20200324-18:21:00                                   ";

        long timeInternal = timeUtilsLdn.parseUTCStringToInternalTime( date.getBytes(), 10, 17 );

        assertEquals( "20200324-18:21:00.000 (GMT)", timeUtilsLdn.internalTimeToLocalTimestamp( timeInternal ) );
    }

    @Test public void testTodayLA() {
        Date now = new Date();

        String dateA = _dateFormatUTC.format( now );

        long expDateAinLAMs = (now.getTime() + _standardTimeUtilsUTC.getLocalTimeZone().getOffset( now.getTime() ));

        checkStrToLocalTimeMSandBack( _standardTimeUtilsUTC, dateA, expDateAinLAMs );

        // dont bother testing HFTTTimeUtils the non UTC stuff has bugs
    }

    @Test public void testTodayUTC() {
        Date now = new Date();

        String dateA = _dateFormatUTC.format( now );

        long expDateAinUTCMs = now.getTime();

        checkStrToUnixTimeMSandBack( _standardTimeUtilsUTC, dateA, expDateAinUTCMs );
    }

    @Test public void testTokyoLocalToHHMMSSzzz() throws Exception {

        String dateA           = "20180131-22:15:25.123";
        long   expDateAinUTCMs = convStrToUTC( dateA );

        TimeZone tz = TimeZone.getTimeZone( "Asia/Tokyo" );

        _standardTimeUtilsUTC.setLocalTimezone( tz );

        int hhmmsszzz = _standardTimeUtilsUTC.unixTimeToIntLocalHHMMSSzzz( expDateAinUTCMs );

        assertEquals( 71525123, hhmmsszzz );
    }

    @Test public void testUnixTimeToLocalStr() throws Exception {

        for ( int hour = 0; hour < 24; hour++ ) {
            long now = System.currentTimeMillis();

            doTestUnixTimeToLocalStr( hour, _standardTimeUtilsUTC, now );

            String dateA           = "20180501-00:15:25.123";
            long   expDateAinUTCMs = convStrToUTC( dateA );

            doTestUnixTimeToLocalStr( hour, _standardTimeUtilsUTC, expDateAinUTCMs );

            final StandardTimeUtils standardTimeUtilsLDN = new StandardTimeUtils( TimeZone.getTimeZone( "Europe/London" ) );
            doTestUnixTimeToLocalStr( hour, standardTimeUtilsLDN, expDateAinUTCMs );
        }
    }

    private void checkStrToLocalTimeMSandBack( final TimeUtils timeUtilsUTC, final String dateStr, final long expDateAinLAMs ) {
        byte[] dateBuf = dateStr.getBytes();

        final TimeUtils.DateParseResults out = new TimeUtils.DateParseResults();
        timeUtilsUTC.parseUTCStringToInternalTime( dateBuf, 0, out );

        long internalTime  = out._internalTime;
        long unixTime      = timeUtilsUTC.internalTimeToUnixTime( internalTime );
        long unixTimeLocal = timeUtilsUTC.internalTimeToLocalMSFromStartDay( internalTime );

        long laAdjust = timeUtilsUTC.getLocalTimeZone().getOffset( unixTime );

        assertEquals( expDateAinLAMs - laAdjust, out._internalTime );
        assertEquals( 21, out._nextIdx );

        assertEquals( expDateAinLAMs - laAdjust, unixTime );
        assertEquals( getLocalTime( unixTime, timeUtilsUTC.getLocalTimeZone() ), unixTimeLocal );

        byte[] tmpStr    = new byte[ 20 ];
        byte[] expHHMMSS = new byte[ 20 ];

        expHHMMSS[ 0 ] = (byte) dateStr.charAt( 9 ); // "20180131-09:15:25.123";
        expHHMMSS[ 1 ] = (byte) dateStr.charAt( 10 );
        expHHMMSS[ 2 ] = (byte) dateStr.charAt( 12 );
        expHHMMSS[ 3 ] = (byte) dateStr.charAt( 13 );
        expHHMMSS[ 4 ] = (byte) dateStr.charAt( 15 );
        expHHMMSS[ 5 ] = (byte) dateStr.charAt( 16 );

        int nextIdx = timeUtilsUTC.internalTimeToHHMMSS( tmpStr, 0, out._internalTime );

        assertEquals( new String( expHHMMSS, 0, 6 ), new String( tmpStr, 0, nextIdx ) );
        assertEquals( 6, nextIdx );
    }

    private void checkStrToUnixTimeMSandBack( final TimeUtils timeUtilsUTC, final String dateStr, final long expDateinUTCMillis ) {
        byte[] dateBuf = dateStr.getBytes();

        final TimeUtils.DateParseResults out = new TimeUtils.DateParseResults();
        timeUtilsUTC.parseUTCStringToInternalTime( dateBuf, 0, out );

        assertEquals( expDateinUTCMillis, out._internalTime );
        assertEquals( 21, out._nextIdx );

        byte[] tmpStr    = new byte[ 20 ];
        byte[] expHHMMSS = new byte[ 20 ];

        expHHMMSS[ 0 ] = (byte) dateStr.charAt( 9 ); // "20180131-09:15:25.123";
        expHHMMSS[ 1 ] = (byte) dateStr.charAt( 10 );
        expHHMMSS[ 2 ] = (byte) dateStr.charAt( 12 );
        expHHMMSS[ 3 ] = (byte) dateStr.charAt( 13 );
        expHHMMSS[ 4 ] = (byte) dateStr.charAt( 15 );
        expHHMMSS[ 5 ] = (byte) dateStr.charAt( 16 );

        int nextIdx = timeUtilsUTC.internalTimeToHHMMSS( tmpStr, 0, out._internalTime );

        assertEquals( new String( expHHMMSS, 0, 6 ), new String( tmpStr, 0, nextIdx ) );
        assertEquals( 6, nextIdx );
    }

    private long convStrToUTC( final String dateA ) throws Exception {

        Date d = _dateFormatUTC.parse( dateA );

        return d.getTime();
    }

    private void doTestLongConversions( final long unixTime, final Format dateFormat, final TimeZone timeZone ) {
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

    private void doTestUnixTimeToLocalStr( final int hour, final TimeUtils timeUtilsUTC, final long timeIn ) {
        final ReusableString str = new ReusableString( 30 );
        final ByteBuffer     buf = ByteBuffer.wrap( str.getBytes() );

        Instant       i  = Instant.ofEpochMilli( timeIn );
        ZonedDateTime i2 = ZonedDateTime.ofInstant( i, timeUtilsUTC.getLocalTimeZone().toZoneId() );
        i2 = i2.withHour( hour );
        long   unixTime = i2.toInstant().toEpochMilli();
        String expected = DateTimeFormatter.ofPattern( "HH:mm:ss.SSS (z)" ).format( i2 );

        timeUtilsUTC.unixTimeToLocalStr( buf, unixTime );
        str.setLength( buf.position() );

        assertEquals( "Failed on hour " + hour, expected, str.toString() );
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
