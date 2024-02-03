/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

import com.rr.core.properties.AppProps;
import com.rr.core.tasks.ZLocalDateTime;

import java.nio.ByteBuffer;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.TimeZone;

/**
 * SMT initially held all timestamps internally as int's being number of milliseconds from last midnight to the timestamp time
 * <p>
 * Now all timestamps are held in internal time format known as InternalTime
 * Functions exist to convert between
 * <p>
 * unixTime          -   unix time, number of millis since 1 Jan 1970 (same as System.currentTimeMillis)
 * internalTime      -   internal format for a UTC time
 *
 * @DEPRECATED unixTimeLocal     -   unix time, adjusted to represent the local timezone that the TimeZoneCalculator is currently using
 * @DEPRECATED internalTimeLocal -   internal format for a local time
 * <p>
 * The application will either run using StandardTimeZoneCalculator where internal time is same as unix time or as
 * TimeZoneCalculatorHFTTodayOpt where a long holds the number of millis since the last midnight
 */
public interface TimeUtils {

    long DATE_1971_MICROS = 31536000000000L;
    int    DATE_STR_LEN          = 9;
    int    SECS_TIMESTAMP_FACTOR = 1000; // while internal time timestamps are in milliseconds, when change to nano then multiply by 1000000
    String TIME_FMT_MS           = "HH:mm:ss.SSS";    // 07:00:00.000

    final class DateParseResults {

        public long _internalTime;
        public int  _nextIdx;
    }

    static long toMicro( final long timeMS ) {
        if ( timeMS == 0 ) return Constants.UNSET_LONG;

        if ( timeMS < DATE_1971_MICROS ) {
            return timeMS * 1000;
        }

        return timeMS;
    }

    static long toMS( final long timeUSEC ) {
        if ( timeUSEC == 0 ) return Constants.UNSET_LONG;

        if ( timeUSEC < DATE_1971_MICROS ) {
            return timeUSEC;
        }

        return timeUSEC / 1000;
    }

    static EnumSet<DayOfWeek> getWorkingDays() {
        if ( AppProps.instance().getBooleanProperty( "TRADE_WEEKENDS", false, false ) ) {
            return EnumSet.range( DayOfWeek.MONDAY, DayOfWeek.SUNDAY );
        }

        return EnumSet.range( DayOfWeek.MONDAY, DayOfWeek.FRIDAY );
    }

    /**
     * apply offset to cal
     *
     * @param cal
     * @param offset eg +00:30:00
     * @return
     */
    Calendar adjust( Calendar cal, String offset );

    /**
     * @return hook around ClockFactory.get().currentTimeMillis()
     * @NOTE THIS ROUTINE MUST BE THREADSAFE
     */
    long currentTimeMillis();

    Calendar getCalendar();

    Calendar getCalendar( TimeZone tz );

    byte[] getDateLocal();

    TimeZone getLocalTimeZone();

    /**
     * @return now as local timestamp
     * @WARNING CONTRIBUTES TO GC
     */
    String getLocalTimestamp();

    /**
     * @return ClockFactory.get().currentTimeMillis() converted into UTC internal time
     */
    long getNowAsInternalTime();

    /**
     * @return an OPTIONAL timezone offset ... currently hard coded to zero
     */
    int getOffset();

    /**
     * @param time sample in 24 hour clock "07:00:00"
     * @param tz
     * @return calendar for specified timezone and time based on today
     */
    Calendar getTimeAsToday( String time, TimeZone tz );

    /**
     * get the date string representing today
     *
     * @param today
     */
    void getToday( byte[] today );

    /**
     * encode internalTime as UTC date time in supplied string buffer
     */
    ZString internalTimeToFixStrMillis( ReusableString str, long internalTime );

    /**
     * encode internalTime as UTC date time in supplied byte array
     *
     * @return length of date time encoding .. ie number of bytes written to buf
     * @NOTE the buffer must be big enough to hold the date or runtime exception will be thrown
     */
    int internalTimeToFixStrMillis( byte[] buf, int idx, long internalTime );

    ZString internalTimeToFixStrSecs( ReusableString str, long internalTime );

    int internalTimeToFixStrSecs( byte[] buf, int idx, long internalTime );

    int internalTimeToHHMMSS( byte[] dest, int idx, long internalTime );

    /**
     * formats the internal time into the local time based string
     *
     * @return nextIdx for continuing to parse after the date
     */
    int internalTimeToLocalHHMMSS( byte[] dest, int idx, long internalTime );

    // ==========================171000========================================================================
    // DateTime decoding

    long internalTimeToLocalMSFromStartDay( final long internalTime );

    String internalTimeToLocalTimestamp( long internalTime );

    long internalTimeToUnixTime( final long internalTime );

    boolean isToday( long dateUTC );

    // ==================================================================================================
    // DateTime encoding

    /**
     * @return a unix local time converted into UTC internal time as of today !
     * @DEPRECATED - safer use calendar as threshold DST switchover only to hour .... wont work some places
     * @WARNING CURRENT STATUS IS SOMEWHAT UNSAFE, RELIES ON TODAY BEING SET CORRECTLY, ONLY FOR USE BY MARKET DATA DECODERS
     **/
    long localMSFromMidnightToInternalTimeToday( final long unixTimeLocal );

    /**
     * convert a fix timestamp in local timezone to unix time
     *
     * @return
     */
    long localTimeStampToUnixTime( ZLocalDateTime localTime );

    /**
     * parse a timestamp in format yyyyMMdd-HH:mm:ss.SSS (z)
     * <p>
     * USES SimpleDateFormat .. SLOW AND CREATES TEMP OBJS
     */
    long localTimeStampToUnixTime( ZString localTime );

    String nowToLocalTimestamp();

    /**
     * @param src      - date timestamp im fix format eg 20180131-09:15:25.123
     * @param startIdx
     * @param out
     */
    void parseUTCStringToInternalTime( byte[] src, int startIdx, DateParseResults out );

    /**
     * @param src      - date timestamp im fix format eg 20180131-09:15:25.123
     * @param startIdx
     * @return the time in internal time
     */
    long parseUTCStringToInternalTime( byte[] src, int startIdx, int len );

    /**
     * set the local timezone for use in all conversions of "local" time
     *
     * @param t
     */
    void setLocalTimezone( TimeZone t );

    /**
     * sets the local and UTC Today date strings as from now
     */
    void setTodayAsNow();

    /**
     * sets the today run date using a date/time in local time .. used to optimise procedding of date strings ... assumption is that by default
     * most dates will be today ... if la171000tency is concern this will be true ... if backtesting then it wont
     * but overhead is small so non issue
     *
     * @param date
     */
    void setTodayFromLocalStr( String date );

    /**
     * set today given date str and custom date format pattern
     *
     * @param date
     * @param ptn
     */
    void setTodayFromLocalStr( String date, String ptn );

    /**
     * sets the today run date using a UTC String
     *
     * @param date
     */
    void setTodayFromUTCStr( String date );

    // ==================================================================================================
    // DateTime conversion long to int
    int unixTimeToIntLocalHHMMSSzzz( long unixTime );

    /**
     * convert a standard timestamp of num millis from 1970 ie unixTime  and encode it to the supplied buf in local time format HH:mm:ss.SSS (localTZ)
     *
     * @param buf      - buffer to encode the string into
     * @param unixTime
     * @NOTE the local timezone is the current timezone and NOT the one for the unixTime
     */
    void unixTimeToLocalStr( ByteBuffer buf, long unixTime );

    /**
     * @param unixTime
     * @return time string in local time
     * @WARNING creates temp objects, only for use in DEBUG/TRACE
     */
    String unixTimeToLocalStr( long unixTime );

    /**
     * convert a standard timestamp of num millis from 1970 ie unixTime  and encode it to the supplied buf in local timestamp yyyyMMdd-HH:mm:ss.SSS (z)
     * <p>
     * if timezone is not UTC then log local / UTC
     *
     * @param buf      - buffer to encode the string into
     * @param unixTime
     */
    void unixTimeToLocalTimestamp( ByteBuffer buf, long unixTime );

    void unixTimeToLocalTimestamp( ByteBuffer buf, TimeZone local, long unixTime );

    /**
     * convert a standard timestamp of num millis from 1970 ie unixTime  and encode it to the supplied buf in specified timestamp yyyyMMdd-HH:mm:ss.SSS (z)
     *
     * @param buf      - buffer to encode the string into
     * @param unixTime
     * @return the buffer passed into the function
     * @NOTE this routine is currently threadsafe in all letters, if implementing new letter check the ThreadSafeTimeUtils and change if needed
     */
    ReusableString unixTimeToLocalTimestamp( ReusableString buf, TimeZone local, long unixTime );

    ReusableString unixTimeToLocalTimestamp( ReusableString buf, TimeZone local, long unixTime, boolean appendUTC );

    /**
     * convert a standard timestamp of num millis from 1970 ie unixTime  and encode it to the supplied buf in the local timestamp yyyyMMdd-HH:mm:ss.SSS (z)
     *
     * @param buf      - buffer to encode the string into
     * @param unixTime
     * @return the buffer passed into the function
     * @NOTE this routine is currently threadsafe in all letters, if implementing new letter check the ThreadSafeTimeUtils and change if needed
     */
    ReusableString unixTimeToLocalTimestamp( ReusableString buf, long unixTime );

    /**
     * allows this TimeUtils to generate a local timestamp for other than the default timezone of this instance of TimeUtils
     * <p>
     * creates temporary objects and relatively slow
     *
     * @param local
     * @param unixTime
     */
    String unixTimeToLocalTimestamp( TimeZone local, long unixTime );

    // ==================================================================================================
    // DateTime conversion long to long

    /**
     * allows this TimeUtils to generate a local timestamp for the default timezone of this instance of TimeUtils
     * <p>
     * creates temporary objects and relatively slow
     *
     * @param unixTime
     */
    String unixTimeToLocalTimestamp( long unixTime );

    void unixTimeToShortLocal( ReusableString buf, long msTime );

    ReusableString unixTimeToUTCTimestamp( ReusableString buf, long unixTime );

    /**
     * generate a UTC timestamp
     * <p>
     * creates temporary objects and relatively slow
     * <p>
     * 20210227-10:04:27.370 (UTC)
     *
     * @param unixTime
     */
    String unixTimeToUTCTimestamp( long unixTime );

}