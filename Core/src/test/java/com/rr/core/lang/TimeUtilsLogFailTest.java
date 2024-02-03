package com.rr.core.lang;

import com.rr.core.time.BackTestClock;
import com.rr.core.time.StandardTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class TimeUtilsLogFailTest extends BaseTestCase {

    private DateFormat _dateFormatUTC     = new SimpleDateFormat( "yyyyMMdd-HH:mm:ss.SSS" );
    private DateFormat _dateFormatChicago = new SimpleDateFormat( "yyyyMMdd-HH:mm:ss.SSS" );

    private TimeUtils _standardTimeUtilsUTC;

    @Before public void setUp() {
        _standardTimeUtilsUTC = new StandardTimeUtils( TimeZone.getTimeZone( "UTC" ) );

        _standardTimeUtilsUTC.setLocalTimezone( TimeZone.getTimeZone( "America/Chicago" ) );

        _dateFormatUTC.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
        _dateFormatChicago.setTimeZone( TimeZone.getTimeZone( "America/Chicago" ) );

        backTestReset( Env.BACKTEST );
    }

    @After public void tearDown() {
        backTestReset( Env.TEST );
    }

    // 20030101-23:58:00.000 (CDT) [info]  marketDataChanged:  Bar secDes=TY1C, exSym=ZN, , sym=ZN, MIC=XCBT, timeStamp 20030101-23:57:00.001 (CST) to 20030101-23:58:00.000 (CST), open = 213.125, close = 213.125, tradeVolume = 0
    // 20030101-00:12:00.000 (CDT) [info]  marketDataChanged:  Bar secDes=TY1C, exSym=ZN, , sym=ZN, MIC=XCBT, timeStamp 20030102-00:11:00.001 (CST) to 20030102-00:12:00.000 (CST), open = 213.125, close = 213.125, tradeVolume = 1340

    // 20030101-15:01:00.000 (CDT) [info]  marketDataChanged:  Bar secDes=SP1C, exSym=SP, , sym=SP, MIC=XCME, timeStamp 20030102-15:00:00.001 (CST) to 20030102-15:01:00.000 (CST), open = 449.0, close = 449.6, tradeVolume = 0

    // 20030101-17:59:00.000 (CDT) [info]  marketDataChanged:  Bar secDes=SP1C, exSym=SP, , sym=SP, MIC=XCME, timeStamp 20030102-17:58:00.001 (CST) to 20030102-17:59:00.000 (CST), open = 448.8, close = 448.8, tradeVolume = 6
    // 20030102-18:00:00.000 (CDT) [info]  marketDataChanged:  Bar secDes=SP1C, exSym=SP, , sym=SP, MIC=XCME, timeStamp 20030102-17:59:00.001 (CST) to 20030102-18:00:00.000 (CST), open = 448.9, close = 448.8, tradeVolume = 18

    @Test public void testChicagoDST() throws Exception {

        checkTimestamp( "20170311-00:59:00.001" );

        checkTimestamp( "20170312-00:59:00.001" );
        checkTimestamp( "20170312-01:59:00.001" );
        checkTimestamp( "20170312-02:00:00.001" );
        checkTimestamp( "20170312-03:00:00.001" );
        checkTimestamp( "20170312-05:00:00.001" );
        checkTimestamp( "20171105-01:59:00.001" );
        checkTimestamp( "20171105-02:00:00.001" );

        checkTimestamp( "20180311-01:59:00.001" );
        checkTimestamp( "20180311-02:00:00.001" );
        checkTimestamp( "20181104-01:59:00.001" );
        checkTimestamp( "20181104-02:00:00.001" );
    }

    @Test public void testChicagoLocalDateTime() throws Exception {

        checkTimestamp( "20030101-23:57:00.001" );
        checkTimestamp( "20030102-00:11:00.001" );
        checkTimestamp( "20030102-15:00:00.001" );
        checkTimestamp( "20030102-17:58:00.001" );
        checkTimestamp( "20030102-17:59:00.001" );
    }

    @Test public void testStartChicagoDST() throws Exception {
        checkTimestamp( "20171105-01:59:00.001" );
    }

    private void checkTimestamp( final String timeChicago ) throws ParseException {

        long now = _dateFormatChicago.parse( timeChicago ).getTime();

        Instant       i  = Instant.ofEpochMilli( now );
        ZonedDateTime i2 = ZonedDateTime.ofInstant( i, _standardTimeUtilsUTC.getLocalTimeZone().toZoneId() );

        String expVal = DateTimeFormatter.ofPattern( "yyyyMMdd-HH:mm:ss.SSS" ).format( i2 );

        BackTestClock c = (BackTestClock) ClockFactory.get();
        c.setCurrentTimeMillis( now );

        ByteBuffer buf = ByteBuffer.allocate( 100 );
        _standardTimeUtilsUTC.unixTimeToLocalTimestamp( buf, now );
        ReusableString str = new ReusableString( buf.array(), 0, buf.position() );

        assertEquals( expVal, str.toString().substring( 0, expVal.length() ) );
    }
}
