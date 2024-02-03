/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.time;

import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.lang.*;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.tasks.BasicSchedulerCallback;
import com.rr.core.tasks.CoreScheduledEvent;
import com.rr.core.tasks.SchedulerFactory;
import com.rr.core.tasks.ZLocalDateTime;
import com.rr.core.utils.NumberFormatUtils;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.Utils;

import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Standard TimeUtils implementation where internal format is same as unix time
 */
@SuppressWarnings( "unused" ) // used via reflection
public final class StandardTimeUtils implements TimeUtils {

    private static final Logger _log = ConsoleFactory.console( StandardTimeUtils.class, Level.info );

    private static final ZString   NONE           = new ViewString( "N/A" );
    private static final ErrorCode ERR_PARSE_DATE = new ErrorCode( "STU100", "Error parsing date " );
    private static final TimeZone  UTC_TZ         = TimeZone.getTimeZone( "UTC" );
    private static final TimeZone  LDN_TZ         = TimeZone.getTimeZone( "Europe/London" );
    private static       boolean   _appendUTC     = true;

    private final ReusableString               _errMsg              = new ReusableString( 1 );
    private final DateCache.TimeZoneCacheEntry _todayDateEntryUTC   = new DateCache.TimeZoneCacheEntry();
    private final DateCache.TimeZoneCacheEntry _todayDateEntryLocal = new DateCache.TimeZoneCacheEntry();
    //                                                                                      20200907-06:54:03.000 (CDT)
    private final SimpleDateFormat _localParser = new SimpleDateFormat( "yyyyMMdd-HH:mm:ss.SSS (z)" );
    private       int                          _localDateYYYYMMDD;
    private       int                          _utcDateYYYYMMDD;
    private       TimeZone                     _localTimezone;
    private       byte[]                       _localTimeZoneLogStrToday;
    private       byte[]                       _localTimeZoneLogStrDaylightSavings;
    private       byte[]                       _localTimeZoneLogStrNormal;
    private       long                         _nextThreshold; // switch between end UTC day and end local
    private       DateCache                    _dateCacheLocalTZ;

    private static TimeZone getTimeZone( String id ) {
        if ( id == null ) {
            return UTC_TZ;
        }

        TimeZone tz = TimeZone.getTimeZone( id );

        return tz;
    }

    public static boolean isAppendUTC()                        { return _appendUTC; }

    public static void setAppendUTC( final boolean appendUTC ) { _appendUTC = appendUTC; }

    /**
     * FOR DEBUG ONLY
     *
     * @param time
     * @return
     */
    public static String slowUnixTimeToUTCStr( long time ) {

        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS (z)" );
        df.setTimeZone( TimeZone.getTimeZone( "UTC" ) );

        return df.format( new Date( time ) );
    }

    @SuppressWarnings( "unused" ) // constructor used via reflection
    public StandardTimeUtils() {
        this( AppProps.instance().getProperty( CoreProps.APP_TIMEZONE, false, null ) );
    }

    public StandardTimeUtils( String t ) {
        this( getTimeZone( t ) );
    }

    public StandardTimeUtils( TimeZone t ) {

        setLocalTimezone( t );

        SchedulerFactory.get().registerForGroupEvent( CoreScheduledEvent.UTCDateRoll, new BasicSchedulerCallback( "TimeZoneCalDateRoll", ( e ) -> setTodayAsNow() ) );
    }

    @Override public long currentTimeMillis() {
        final long now = ClockFactory.get().currentTimeMillis();

        if ( now >= _nextThreshold ) {
            thresholdExceeded( now );
        }

        return now;
    }

    @Override
    public void setTodayFromLocalStr( String date ) {

        if ( date == null ) return;

        if ( date.length() < 9 ) date = date + "-";

        final String     ptn                = (date.length() == 9) ? Constants.FIX_TIMESTAMP_DATE_FORMAT : Constants.FIX_TIMESTAMP_MILLIS_FORMAT;
        final DateFormat dateFormatToStrUTC = _dateCacheLocalTZ.createFormatter( _localTimezone, ptn );

        Date d;
        try {
            d = dateFormatToStrUTC.parse( date );
        } catch( ParseException e ) {
            throw new RuntimeException( "Attempt to set Date to illegal string [" + date + "]" );
        }

        setUTCDateAsNow( d.getTime() );
        setLocalDateAsNow( d.getTime() );
    }

    @Override
    public void setTodayFromLocalStr( String date, String ptn ) {

        if ( date == null ) return;

        if ( date.length() < 11 ) {
            date = date + "-12:00:00.000";
            ptn  = ptn + "-HH:mm:ss.SSS";
        }

        final DateFormat dateFormatToStrLocal = _dateCacheLocalTZ.createFormatter( _localTimezone, ptn );

        Date d;
        try {
            d = dateFormatToStrLocal.parse( date );
        } catch( ParseException e ) {
            throw new RuntimeException( "Attempt to set Date to illegal string [" + date + "]" );
        }

        setUTCDateAsNow( d.getTime() );
        setLocalDateAsNow( d.getTime() );
    }

    @Override
    public void setTodayFromUTCStr( String date ) {

        if ( date == null ) return;

        if ( date.length() < 9 ) date = date + "-";

        final String     ptn                = (date.length() == 9) ? Constants.FIX_TIMESTAMP_DATE_FORMAT : Constants.FIX_TIMESTAMP_MILLIS_FORMAT;
        final DateFormat dateFormatToStrUTC = _dateCacheLocalTZ.createFormatter( UTC_TZ, ptn );

        Date d;
        try {
            d = dateFormatToStrUTC.parse( date );
        } catch( ParseException e ) {
            throw new RuntimeException( "Attempt to set Date to illegal string [" + date + "]" );
        }

        setUTCDateAsNow( d.getTime() );
        setLocalDateAsNow( d.getTime() );
    }

    @Override public void setLocalTimezone( TimeZone t ) {
        if ( t != null ) {
            _localTimezone = t;

            _dateCacheLocalTZ = DateCache.instance( _localTimezone );

            DateFormat df = new SimpleDateFormat( " (z)" );
            df.setTimeZone( _localTimezone );

            DateFormat df2 = new SimpleDateFormat( "yyyyMMdd" );
            df2.setTimeZone( _localTimezone );

            setLocalTZLogStrings( df, df2 );

            setTodayAsNow();
        } else {
            _dateCacheLocalTZ = DateCache.instance( TimeZone.getTimeZone( "UTC" ) );

            DateFormat df = new SimpleDateFormat( " (z)" );
            _localTimeZoneLogStrToday = df.format( new Date() ).getBytes();
        }
    }

    @Override public void getToday( byte[] today ) {
        System.arraycopy( _todayDateEntryUTC.getDateStr().getBytes(), 0, today, 0, Constants.DATE_STR_LEN );
    }

    @Override public byte[] getDateLocal() {
        return _todayDateEntryLocal.getDateStr().getBytes();
    }

    @Override public void setTodayAsNow() {
        long now = ClockFactory.get().currentTimeMillis();

        setLocalDateAsNow( now );
        setUTCDateAsNow( now );
    }

    // return an optional TZ offset
    @Override public int getOffset() {
        return 0;
    }

    @Override public boolean isToday( long dateUTC ) { return _todayDateEntryUTC.inRangeLocal( dateUTC ); }

    /**
     * @return the local timezone
     * @NOTE must be threadsafe ... in reality the local timezone wont change after its set
     */
    @Override public TimeZone getLocalTimeZone() {
        return _localTimezone;
    }

    /**
     * @return YYMMDD-hh:mm:ss in local time
     */
    @Override public String getLocalTimestamp() {
        final long now = ClockFactory.get().currentTimeMillis();

        final DateCache.TimeZoneCacheEntry entry = _dateCacheLocalTZ.getDateEntryLocal( now );

        ReusableString s = TLC.instance().pop().copy( entry.getDateStr() );
        unixTimeToShortLocal( s, now );
        String out = s.toString();
        TLC.instance().pushback( s );

        return out;
    }

    @Override public Calendar getCalendar() {
        Calendar cal = Calendar.getInstance( _localTimezone );

        cal.setTimeInMillis( ClockFactory.get().currentTimeMillis() );

        return cal;
    }

    @Override public Calendar getCalendar( TimeZone tz ) {
        Calendar cal = Calendar.getInstance( tz );

        cal.setTimeInMillis( ClockFactory.get().currentTimeMillis() );

        return cal;
    }

    /**
     * @param time sample in 24 hour clock "07:00:00"
     * @param tz
     * @return calendar for specified timezone and time based on today
     */
    @Override public Calendar getTimeAsToday( String time, TimeZone tz ) {

        if ( time.length() != 8 || time.charAt( 2 ) != ':' || time.charAt( 5 ) != ':' ) {
            throw new SMTRuntimeException( "StandardTimeUtils.getTimeAsToday() Invalid time string expected hh:mm:ss not [" + time + "]" );
        }

        try {
            Calendar cal = getCalendar( tz );

            long now = cal.getTimeInMillis();

            int hour = (time.charAt( 0 ) - '0') * 10 + (time.charAt( 1 ) - '0');
            int min  = (time.charAt( 3 ) - '0') * 10 + (time.charAt( 4 ) - '0');
            int sec  = (time.charAt( 6 ) - '0') * 10 + (time.charAt( 7 ) - '0');
            int ms   = 0;

            if ( time.length() == 12 ) {
                int c1 = time.charAt( 9 ) - '0';
                int c2 = time.charAt( 10 ) - '0';
                int c3 = time.charAt( 11 ) - '0';

                ms = c1 * 100 + c2 * 10 + c3;
            }

            cal.set( Calendar.HOUR_OF_DAY, hour );
            cal.set( Calendar.MINUTE, min );
            cal.set( Calendar.SECOND, sec );
            cal.set( Calendar.MILLISECOND, ms );

            if ( cal.getTimeInMillis() < now ) {
                cal.add( Calendar.DAY_OF_MONTH, 1 );
            }

            return cal;

        } catch( Exception e ) {
            throw new SMTRuntimeException( "StandardTimeUtils.getTimeAsToday() Invalid time string expected hh:mm:ss not [" + time + "]", e );
        }
    }

    /**
     * apply offset to cal
     *
     * @param cal
     * @param offset eg +00:30:00
     * @return
     */
    @Override public Calendar adjust( Calendar cal, String offset ) {
        if ( offset.length() != 9 || offset.charAt( 3 ) != ':' || offset.charAt( 6 ) != ':' ||
             (offset.charAt( 0 ) != '-' && offset.charAt( 0 ) != '+') ) {
            throw new SMTRuntimeException( "StandardTimeUtils.adjust() Invalid time adjust string expected [sign +|-]hh:mm:ss not [" +
                                           offset + "]" );
        }

        try {
            int signAdjust = (offset.charAt( 0 ) == '-') ? -1 : 1;

            int hour = (offset.charAt( 1 ) - '0') * 10 + (offset.charAt( 2 ) - '0');
            int min  = (offset.charAt( 4 ) - '0') * 10 + (offset.charAt( 5 ) - '0');
            int sec  = (offset.charAt( 7 ) - '0') * 10 + (offset.charAt( 8 ) - '0');

            cal.add( Calendar.HOUR_OF_DAY, hour * signAdjust );
            cal.add( Calendar.MINUTE, min * signAdjust );
            cal.add( Calendar.SECOND, sec * signAdjust );

            return cal;

        } catch( Exception e ) {
            throw new SMTRuntimeException( "StandardTimeUtils.adjust() Invalid time adjust string expected [sign +|-]hh:mm:ss not [" +
                                           offset + "]", e );
        }
    }

    @Override public long getNowAsInternalTime() {
        return currentTimeMillis();
    }

    @Override public void parseUTCStringToInternalTime( byte[] src, int idx, DateParseResults out ) {

        long ms;

        if ( chkToday( src, idx ) ) {
            ms = _todayDateEntryUTC.getStartOfDayUnix();
        } else {
            ms = _dateCacheLocalTZ.getStartOfDayUnixTimeUTC( src, idx );
        }

        idx += Constants.DATE_STR_LEN;

        int hTen = src[ idx++ ] - '0';
        int hDig = src[ idx++ ] - '0';
        idx++;
        int mTen = src[ idx++ ] - '0';
        int mDig = src[ idx++ ] - '0';
        idx++;
        int sTen = src[ idx++ ] - '0';
        int sDig = src[ idx++ ] - '0';

        int hour = ((hTen) * 10) + (hDig);
        int min  = ((mTen) * 10) + (mDig);
        int sec  = ((sTen) * 10) + (sDig);

        if ( hour < 0 || hour > 23 ) {
            throw new RuntimeDecodingException( "Invalid hour '" + hour + "' in time format " + ", idx=" + idx );
        }

        if ( min < 0 || min > 59 ) {
            throw new RuntimeDecodingException( "Invalid min '" + min + "' in time format " + ", idx=" + idx );
        }

        if ( sec < 0 || sec > 59 ) {
            throw new RuntimeDecodingException( "Invalid sec '" + sec + "' in time format " + ", idx=" + idx );
        }

        // TODO bench the multiply vs table look up on target hw & os

        ms += (TimeTables._hourToMS[ hour ] + TimeTables._minToMS[ min ] + TimeTables._secToMS[ sec ] + getOffset());
        // int ms = ((hour * 3600) + (min*60) + sec) * 1000 + _tzCalculator.getOffset();

        if ( src[ idx ] == '.' ) {
            idx++;
            int msHun = src[ idx++ ] - '0';
            int msTen = src[ idx++ ] - '0';
            int msDig = src[ idx++ ] - '0';

            ms += TimeTables._msHundreds[ msHun ] + TimeTables._msTen[ msTen ] + msDig;

            // ms += ((msHun*100) + (msTen*10) + msDig);

            if ( idx < src.length && Character.isDigit( src[ idx ] ) ) { // we have micros
                int microHun = src[ idx++ ] - '0';
                int microTen = src[ idx++ ] - '0';
                int microDig = src[ idx++ ] - '0';

                // PENDING ADDITION OF MICROS
                if ( idx < src.length && Character.isDigit( src[ idx ] ) ) { // we have micros
                    int nanoHun = src[ idx++ ] - '0';
                    int nanoTen = src[ idx++ ] - '0';
                    int nanoDig = src[ idx++ ] - '0';

                    // PENDING ADDITION OF NANOS
                }
            }
        }

        out._internalTime = ms;
        out._nextIdx      = idx;
    }

    @Override public long parseUTCStringToInternalTime( byte[] src, int idx, int len ) {

        if ( len < Constants.FIX_TIMESTAMP_SEC_FORMAT_LEN ) {
            String dateStr = new String( src, idx, len );

            throw new RuntimeDecodingException( "Min date size is " + Constants.FIX_TIMESTAMP_SEC_FORMAT_LEN + ", [" + dateStr + "] is only " + len );
        }

        int maxIdx = idx + len;

        long ms;

        if ( chkToday( src, idx ) ) {
            ms = _todayDateEntryUTC.getStartOfDayUnix();
        } else {
            ms = _dateCacheLocalTZ.getStartOfDayUnixTimeUTC( src, idx );
        }

        idx += Constants.DATE_STR_LEN;

        int hTen = src[ idx++ ] - '0';
        int hDig = src[ idx++ ] - '0';
        idx++;
        int mTen = src[ idx++ ] - '0';
        int mDig = src[ idx++ ] - '0';
        idx++;
        int sTen = src[ idx++ ] - '0';
        int sDig = src[ idx++ ] - '0';

        int hour = ((hTen) * 10) + (hDig);
        int min  = ((mTen) * 10) + (mDig);
        int sec  = ((sTen) * 10) + (sDig);

        if ( hour < 0 || hour > 23 ) {
            throw new RuntimeDecodingException( "Invalid hour '" + hour + "' in time format " + ", idx=" + idx );
        }

        if ( min < 0 || min > 59 ) {
            throw new RuntimeDecodingException( "Invalid min '" + min + "' in time format " + ", idx=" + idx );
        }

        if ( sec < 0 || sec > 59 ) {
            throw new RuntimeDecodingException( "Invalid sec '" + sec + "' in time format " + ", idx=" + idx );
        }

        // TODO bench the multiply vs table look up on target hw & os

        ms += (TimeTables._hourToMS[ hour ] + TimeTables._minToMS[ min ] + TimeTables._secToMS[ sec ] + getOffset());
        // int ms = ((hour * 3600) + (min*60) + sec) * 1000 + _tzCalculator.getOffset();

        if ( (idx + 4) < maxIdx && src[ idx ] == '.' ) {
            idx++;
            int msHun = src[ idx++ ] - '0';
            int msTen = src[ idx++ ] - '0';
            int msDig = src[ idx++ ] - '0';

            ms += TimeTables._msHundreds[ msHun ] + TimeTables._msTen[ msTen ] + msDig;

            // ms += ((msHun*100) + (msTen*10) + msDig);

            if ( (idx + 3) < maxIdx && Character.isDigit( src[ idx ] ) ) { // we have micros
                int microHun = src[ idx++ ] - '0';
                int microTen = src[ idx++ ] - '0';
                int microDig = src[ idx++ ] - '0';

                // PENDING ADDITION OF MICROS
                if ( idx < src.length && Character.isDigit( src[ idx ] ) ) { // we have micros
                    int nanoHun = src[ idx++ ] - '0';
                    int nanoTen = src[ idx++ ] - '0';
                    int nanoDig = src[ idx++ ] - '0';

                    // PENDING ADDITION OF NANOS
                }
            }
        }

        return ms;
    }

    @Override public long localTimeStampToUnixTime( ZLocalDateTime localTime ) {

        String fmt = localTime.getTimeStampFormat();
        String val = localTime.getLocalTimeStamp();

        if ( !localTime.getTimeStampFormat().contains( "Y" ) && !localTime.getTimeStampFormat().contains( "y" ) &&
             !localTime.getTimeStampFormat().contains( "M" ) && !localTime.getTimeStampFormat().contains( "D" ) &&
             !localTime.getTimeStampFormat().contains( "w" ) && !localTime.getTimeStampFormat().contains( "d" ) ) {

            fmt = "yyyyMMdd-" + fmt;

            Calendar c = getCalendar( localTime.getTz() );

            DateFormat tmpFormatter = _dateCacheLocalTZ.createFormatter( localTime.getTz(), "yyyyMMdd-" );

            String yyyymmdd = tmpFormatter.format( c.getTime() );

            if ( _log.isEnabledFor( Level.trace ) ) {
                _log.log( Level.trace, "localTime missing date so adding today of " + yyyymmdd );
            }

            val = yyyymmdd + val;
        }

        final DateFormat dateFormatter = _dateCacheLocalTZ.createFormatter( localTime.getTz(), fmt );

        Date d;
        try {
            d = dateFormatter.parse( val );
        } catch( ParseException e ) {
            throw new SMTRuntimeException( "Attempt to set Date from illegal string [" + val + "] ptn=" + fmt );
        }

        if ( !localTime.getTimeStampFormat().contains( "YYYY" ) && !localTime.getTimeStampFormat().contains( "yyyy" ) ) {

            if ( _log.isEnabledFor( Level.trace ) ) {
                _log.log( Level.trace, "StandardTimeUtils local timeStamp request formnat missing year " + localTime.getTimeStampFormat() + ".... move to today" );
            }

            Calendar c = getCalendar( localTime.getTz() );

            int todayYear      = c.get( Calendar.YEAR );
            int todayDayOfyear = c.get( Calendar.DAY_OF_YEAR );

            c.setTime( d );

            c.set( Calendar.YEAR, todayYear );
            c.set( Calendar.DAY_OF_YEAR, todayDayOfyear );

            int now = c.get( Calendar.YEAR );
        }

        return d.getTime();
    }

    /**
     * parse a timestamp in format YYMMDD-hh:mm:ss (z)
     */
    @Override public long localTimeStampToUnixTime( ZString localTime ) {

        Date d;

        try {
            String dStr = localTime.toString();

            d = _localParser.parse( dStr );

        } catch( ParseException e ) {
            throw new SMTRuntimeException( "Attempt to parse local timestamp from illegal string [" + localTime + "]" );
        }

        return d.getTime();
    }

    @Override public ZString internalTimeToFixStrMillis( ReusableString str, long internalTime ) {

        if ( Utils.isNull( internalTime ) ) {
            str.reset();
            return str;
        }

        str.ensureCapacity( Constants.FIX_TIMESTAMP_MILLIS_FORMAT_LEN );

        int len = internalTimeToFixStrMillis( str.getBytes(), 0, internalTime );

        str.setLength( len );

        return str;
    }

    @Override public ZString internalTimeToFixStrSecs( ReusableString str, long internalTime ) {

        if ( Utils.isNull( internalTime ) ) {
            str.reset();
            return str;
        }

        str.ensureCapacity( Constants.FIX_TIMESTAMP_MILLIS_FORMAT_LEN );

        int len = internalTimeToFixStrSecs( str.getBytes(), 0, internalTime );

        str.setLength( len );

        return str;
    }

    @Override public int internalTimeToFixStrMillis( byte[] buf, int idx, long internalTime ) {
        return doInternalTimeToFixStrMillis( buf, idx, internalTime, true );
    }

    @Override public int internalTimeToFixStrSecs( byte[] buf, int idx, long internalTime ) {
        return doInternalTimeToFixStrMillis( buf, idx, internalTime, false );
    }

    @Override public int internalTimeToLocalHHMMSS( byte[] dest, int idx, long internalTime ) {
        // HHMMSS

        long localMillisFromStartDay = getLocalMillisFromStartOfDayFromUnixTimeMS( internalTime );

        int hour = (int) (((localMillisFromStartDay >> 7) * 9773437) >> 38);
        localMillisFromStartDay -= 3600000 * hour;
        int minute = (int) (((localMillisFromStartDay >> 5) * 2290650) >> 32);
        localMillisFromStartDay -= 60000 * minute;
        int second = (int) (((localMillisFromStartDay >> 3) * 67109) >> 23);

        int temp = (hour * 13) >> 7;
        dest[ idx++ ] = (byte) (temp + '0');
        dest[ idx++ ] = (byte) (hour - 10 * temp + '0');

        temp          = (minute * 13) >> 7;
        dest[ idx++ ] = (byte) (temp + '0');
        dest[ idx++ ] = (byte) (minute - 10 * temp + '0');

        temp          = (second * 13) >> 7;
        dest[ idx++ ] = (byte) (temp + '0');
        dest[ idx++ ] = (byte) (second - 10 * temp + '0');

        return idx;
    }

    @Override public int internalTimeToHHMMSS( byte[] dest, int idx, long internalTime ) {
        // HHMMSS

        long ms = internalTimeToUnixTime( internalTime );

        ms = unixTimsMSToMSFromMidnight( ms );

        int hour = (int) (((ms >> 7) * 9773437) >> 38);
        ms -= 3600000 * hour;
        int minute = (int) (((ms >> 5) * 2290650) >> 32);
        ms -= 60000 * minute;
        int second = (int) (((ms >> 3) * 67109) >> 23);

        int temp = (hour * 13) >> 7;
        dest[ idx++ ] = (byte) (temp + '0');
        dest[ idx++ ] = (byte) (hour - 10 * temp + '0');

        temp          = (minute * 13) >> 7;
        dest[ idx++ ] = (byte) (temp + '0');
        dest[ idx++ ] = (byte) (minute - 10 * temp + '0');

        temp          = (second * 13) >> 7;
        dest[ idx++ ] = (byte) (temp + '0');
        dest[ idx++ ] = (byte) (second - 10 * temp + '0');

        return idx;
    }

    /**
     * @return YYMMDD-hh:mm:ss (z) in local time
     */
    @Override public void unixTimeToLocalTimestamp( ByteBuffer buf, long unixTime ) {

        final DateCache.TimeZoneCacheEntry entry = _dateCacheLocalTZ.getDateEntryLocal( unixTime );

        final ReusableString d = entry.getDateStr();

        buf.put( d.getBytes(), d.getOffset(), d.length() );

        unixTimeToLocalStr( buf, unixTime );
    }

    @Override public void unixTimeToLocalTimestamp( final ByteBuffer buf, TimeZone local, final long unixTime ) {
        if ( Utils.isNull( unixTime ) ) {
            return;
        }

        doUnixTimeToLocalTZ( buf, local, unixTime );

        if ( _appendUTC && local != LDN_TZ && local != UTC_TZ ) {
            buf.put( " / ".getBytes() );

            doUnixTimeToLocalTZ( buf, UTC_TZ, unixTime );
        }
    }

    /**
     * @return YYMMDD-hh:mm:ss (z) in local time
     */
    @Override public ReusableString unixTimeToLocalTimestamp( ReusableString buf, TimeZone local, long unixTime ) {

        if ( Utils.isNull( unixTime ) ) {
            return buf;
        }

        doUnixTimeToLocalTimestamp( buf, local, unixTime );

        return buf;
    }

    /**
     * @return YYMMDD-hh:mm:ss (z) in local time
     */
    @Override public ReusableString unixTimeToLocalTimestamp( ReusableString buf, TimeZone local, long unixTime, boolean appendUTC ) {

        if ( Utils.isNull( unixTime ) ) {
            return buf;
        }

        doUnixTimeToLocalTimestamp( buf, local, unixTime );

        if ( appendUTC && local != LDN_TZ && local != UTC_TZ ) {
            buf.append( " / ".getBytes() );

            doUnixTimeToLocalTimestamp( buf, UTC_TZ, unixTime );
        }

        return buf;
    }

    /**
     * @return YYMMDD-hh:mm:ss (z) in local time
     */
    @Override public ReusableString unixTimeToLocalTimestamp( ReusableString buf, long unixTime ) {

        if ( Utils.isNull( unixTime ) ) {
            return buf;
        }

        doUnixTimeToLocalTimestamp( buf, _localTimezone, unixTime );

        if ( _appendUTC && _localTimezone != LDN_TZ && _localTimezone != UTC_TZ ) {
            buf.append( " / ".getBytes() );

            doUnixTimeToLocalTimestamp( buf, UTC_TZ, unixTime );
        }

        return buf;
    }

    @Override public ReusableString unixTimeToUTCTimestamp( ReusableString buf, long unixTime ) {
        return unixTimeToLocalTimestamp( buf, UTC_TZ, unixTime );
    }

    @Override public String unixTimeToLocalTimestamp( TimeZone local, long unixTime ) {
        Calendar c = Calendar.getInstance( local );
        c.setTimeInMillis( unixTime );
        DateFormat df = new SimpleDateFormat( "yyyyMMdd-HH:mm:ss.SSS (z)" );
        df.setTimeZone( local );
        String s = df.format( c.getTime() );
        return s;
    }

    @Override public String unixTimeToLocalTimestamp( long unixTime ) {
        Calendar c = Calendar.getInstance( _localTimezone );
        c.setTimeInMillis( unixTime );
        DateFormat df = new SimpleDateFormat( "yyyyMMdd-HH:mm:ss.SSS (z)" );
        df.setTimeZone( _localTimezone );
        String s = df.format( c.getTime() );
        return s;
    }

    @Override public String unixTimeToUTCTimestamp( long unixTime ) {
        unixTime = TimeUtils.toMS( unixTime );

        if ( Utils.isNullOrZero( unixTime ) ) return "";

        final TimeZone utc = TimeZone.getTimeZone( "UTC" );
        Calendar       c   = Calendar.getInstance( utc );
        c.setTimeInMillis( unixTime );
        DateFormat df = new SimpleDateFormat( "yyyyMMdd-HH:mm:ss.SSS (z)" );
        df.setTimeZone( utc );
        String s = df.format( c.getTime() );
        return s;
    }

    @Override public String internalTimeToLocalTimestamp( long internalTime ) {
        long unixTime = internalTimeToUnixTime( internalTime );
        return unixTimeToLocalTimestamp( unixTime );
    }

    @Override public String nowToLocalTimestamp() {
        Calendar c = Calendar.getInstance( _localTimezone );
        c.setTimeInMillis( currentTimeMillis() );
        DateFormat df = new SimpleDateFormat( "yyyyMMdd-HH:mm:ss.SSS (z)" );
        df.setTimeZone( _localTimezone );
        String s = df.format( c.getTime() );
        return s;
    }

    @Override public void unixTimeToLocalStr( ByteBuffer buf, long unixTime ) {
        DateCache.TimeZoneCacheEntry entry = getLocalDateEntry( unixTime );

        long ms = doGetLocalMillisFromStartOfDayFromUnixTimeMS( unixTime, entry );

        doUnixTimeToLocalStr( buf, ms, _dateCacheLocalTZ.isDaylightSavings( entry, unixTime ) );
    }

    @Override public void unixTimeToShortLocal( ReusableString buf, long unixTime ) {

        if ( unixTime == 0 ) {
            buf.append( NONE );
            return;
        }

        // HH:MM:SS

        long ms = getLocalMillisFromStartOfDayFromUnixTimeMS( unixTime );

        encodeTime( buf, ms, false );
    }

    @Override public String unixTimeToLocalStr( long unixTime ) {
        ReusableString s    = new ReusableString( Constants.FIX_TIMESTAMP_MILLIS_FORMAT_LEN );
        ByteBuffer     wrap = ByteBuffer.wrap( s.getBytes() );
        unixTimeToLocalStr( wrap, unixTime );
        s.setLength( wrap.position() );
        return s.toString();
    }

    @Override
    public final long internalTimeToUnixTime( long internalTime ) {
        return internalTime;
    }

    @Override
    public final long internalTimeToLocalMSFromStartDay( long internalTime ) {
        return doUnixTimeToLocalMSFromStartDay( internalTime );
    }

    @Override
    public long localMSFromMidnightToInternalTimeToday( final long localTimeMSFromMidnight ) {
        long utcOffset = _localTimezone.getRawOffset();
        long unixTime  = _todayDateEntryLocal.getStartOfDayUnix() + localTimeMSFromMidnight;
        long daySav    = _localTimezone.getDSTSavings();

        if ( !_todayDateEntryLocal.isStartDayInDST() && !_todayDateEntryLocal.isEndDayInDST() ) {
            // nothing to do
        } else if ( _todayDateEntryLocal.isStartDayInDST() && _todayDateEntryLocal.isEndDayInDST() ) {
            // nothing to do .. already adjusted
        } else {
            if ( _todayDateEntryLocal.isStartDayInDST() ) {
                /**
                 * started in DST and switched OUT during day out of it
                 * critically for day in which start in DST cannot tell with  localTimeMSFromMidnight
                 * if 1:01 ... is the first time its called or the second after clock winds back at 2am
                 * so assume its the second time its called
                 */
                if ( unixTime > _todayDateEntryLocal.getDstSwitchThreshold() ) {
                    unixTime += daySav; // start of day has DST applied so after FST switch must reverse DST compensation
                }
            } else { // ending day in DST
                if ( unixTime > _todayDateEntryLocal.getDstSwitchThreshold() ) {
                    unixTime -= daySav;
                }
            }
        }

        return unixTime;
    }

    @Override public int unixTimeToIntLocalHHMMSSzzz( long unixTime ) {

        long localMillisFromStartDay = getLocalMillisFromStartOfDayFromUnixTimeMS( unixTime );

        int hour = (int) (((localMillisFromStartDay >> 7) * 9773437) >> 38);   // ms / 3600000
        localMillisFromStartDay -= 3600000 * hour;
        int minute = (int) (((localMillisFromStartDay >> 5) * 2290650) >> 32); // ms / 60000
        localMillisFromStartDay -= 60000 * minute;
        int second = (int) (((localMillisFromStartDay >> 3) * 67109) >> 23);   // ms / 1000;
        localMillisFromStartDay -= 1000 * second;

        long val = (((((hour * 100) + minute) * 100) + second) * 1000) + localMillisFromStartDay;

        return (int) val;
    }

    @Override public String toString() {
        return "StandardTimeUtils todayUTC=" + _todayDateEntryUTC.getDateStr();
    }

    public long doUnixTimeToLocalMSFromStartDay( final long unixTime ) {
        final DateCache.TimeZoneCacheEntry entry = getLocalDateEntry( unixTime );
        return entry.getMillisFromStartOfDay( unixTime );
    }

    public int getDateYYYYMMDD( byte[] dateStrLocal ) {
        int y1 = dateStrLocal[ 0 ] - '0';
        int y2 = dateStrLocal[ 1 ] - '0';
        int y3 = dateStrLocal[ 2 ] - '0';
        int y4 = dateStrLocal[ 3 ] - '0';
        int m1 = dateStrLocal[ 4 ] - '0';
        int m2 = dateStrLocal[ 5 ] - '0';
        int d1 = dateStrLocal[ 6 ] - '0';
        int d2 = dateStrLocal[ 7 ] - '0';

        int yyyy = y1 * 1000 + y2 * 100 + y3 * 10 + y4;
        int mm   = m1 * 10 + m2;
        int dd   = d1 * 10 + d2;

        return yyyy * 10000 + mm * 100 + dd;
    }

    public long getLocalToday() {
        return _localDateYYYYMMDD;
    }

    private boolean chkToday( byte[] src, int idx ) {
        boolean ok = true;

        final ReusableString dateStr    = _todayDateEntryUTC.getDateStr();
        final int            dateStrLen = dateStr.length();
        final byte[]         dateStrUTC = dateStr.getBytes();

        for ( int i = 0; i < dateStrLen; ++i ) {
            if ( src[ idx + i ] != dateStrUTC[ i ] ) {
                ok = false;
                break;
            }
        }

        return ok;
    }

    private long doGetLocalMillisFromStartOfDayFromUnixTimeMS( final long unixTime, DateCache.TimeZoneCacheEntry entry ) {

        return entry.getMillisFromStartOfDay( unixTime );
    }

    private int doInternalTimeToFixStrMillis( byte[] buf, int idx, long internalTimeMS, boolean encodeMillis ) {

        final long    unixTime = internalTimeToUnixTime( internalTimeMS );
        final boolean isToday  = isToday( unixTime );

        final ReusableString src    = isToday ? _todayDateEntryUTC.getDateStr() : _dateCacheLocalTZ.getDateEntryUTC( unixTime ).getDateStr();
        final byte[]         srcBuf = src.getBytes();

        int i = 0;
        while( i < Constants.DATE_STR_LEN ) {
            buf[ idx++ ] = srcBuf[ i++ ];
        }

        // HH:mm:ss.SSS

        long ms = isToday ? (unixTime - _todayDateEntryUTC.getStartOfDayUnix()) : (unixTime % Constants.MS_IN_DAY);

        int hour = (int) (((ms >> 7) * 9773437) >> 38);
        ms -= 3600000 * hour;
        int minute = (int) (((ms >> 5) * 2290650) >> 32);
        ms -= 60000 * minute;
        int second = (int) (((ms >> 3) * 67109) >> 23);

        int leftOverMillis = (int) (ms - (1000 * second));

        int temp = (hour * 13) >> 7;
        buf[ idx++ ] = (byte) (temp + '0');
        buf[ idx++ ] = (byte) (hour - 10 * temp + '0');
        buf[ idx++ ] = (byte) ':';

        temp         = (minute * 13) >> 7;
        buf[ idx++ ] = (byte) (temp + '0');
        buf[ idx++ ] = (byte) (minute - 10 * temp + '0');
        buf[ idx++ ] = (byte) ':';

        temp         = (second * 13) >> 7;
        buf[ idx++ ] = (byte) (temp + '0');
        buf[ idx++ ] = (byte) (second - 10 * temp + '0');

        if ( encodeMillis ) {
            buf[ idx++ ] = (byte) '.';

            temp         = (leftOverMillis * 41) >> 12;
            buf[ idx++ ] = (byte) (temp + '0');
            leftOverMillis -= 100 * temp;

            final int t = NumberFormatUtils._dig100[ leftOverMillis ];

            buf[ idx++ ] = (byte) (t >> 8);     // tens
            buf[ idx++ ] = (byte) (t & 0xFF);   // units;
        }

        return idx;
    }

    private void doUnixTimeToLocalStr( ByteBuffer buf, long ms, boolean isDaylightSavings ) {

        // HH:mm:ss.SSS (z)

        int hour = (int) (((ms >> 7) * 9773437) >> 38);
        ms -= 3600000 * hour;
        int minute = (int) (((ms >> 5) * 2290650) >> 32);
        ms -= 60000 * minute;
        int second = (int) (((ms >> 3) * 67109) >> 23);

        int leftOverMillis = (int) (ms - (1000 * second));

        int temp = (hour * 13) >> 7;
        buf.put( (byte) (temp + '0') );
        buf.put( (byte) (hour - 10 * temp + '0') );
        buf.put( (byte) ':' );

        temp = (minute * 13) >> 7;
        buf.put( (byte) (temp + '0') );
        buf.put( (byte) (minute - 10 * temp + '0') );
        buf.put( (byte) ':' );

        temp = (second * 13) >> 7;
        buf.put( (byte) (temp + '0') );
        buf.put( (byte) (second - 10 * temp + '0') );
        buf.put( (byte) '.' );

        temp = (leftOverMillis * 41) >> 12;
        buf.put( (byte) (temp + '0') );
        leftOverMillis -= 100 * temp;

        final int t = NumberFormatUtils._dig100[ leftOverMillis ];

        buf.put( (byte) (t >> 8) );     // tens
        buf.put( (byte) (t & 0xFF) );   // units;

        buf.put( (isDaylightSavings) ? _localTimeZoneLogStrDaylightSavings : _localTimeZoneLogStrNormal );
    }

    private void doUnixTimeToLocalTZ( final ByteBuffer buf, TimeZone local, final long unixTime ) {

        if ( local == null ) local = _localTimezone;

        final DateCache tzCache = DateCache.instance( local );

        DateCache.TimeZoneCacheEntry entry = tzCache.getDateEntryLocal( unixTime );

        long msFromStartDay = entry.getMillisFromStartOfDay( unixTime );

        final ReusableString d = entry.getDateStr();
        buf.put( d.getBytes(), d.getOffset(), d.length() );

        encodeTime( buf, msFromStartDay, true );

        boolean dst = _dateCacheLocalTZ.isDaylightSavings( entry, unixTime );

        buf.put( " (".getBytes() );
        buf.put( local.getDisplayName( dst, TimeZone.SHORT ).getBytes() );
        buf.put( ")".getBytes() );

        return;
    }

    private ReusableString doUnixTimeToLocalTimestamp( final ReusableString buf, final TimeZone local, long unixTime ) {

        unixTime = TimeUtils.toMS( unixTime );

        final DateCache tzCache = DateCache.instance( local );

        DateCache.TimeZoneCacheEntry entry = tzCache.getDateEntryLocal( unixTime );

        long msFromStartDay = entry.getMillisFromStartOfDay( unixTime );

        final ReusableString d = entry.getDateStr();
        buf.append( d );

        encodeTime( buf, msFromStartDay, true );

        boolean dst = _dateCacheLocalTZ.isDaylightSavings( entry, unixTime );

        buf.append( " (" );
        buf.append( local.getDisplayName( dst, TimeZone.SHORT ) );
        buf.append( ')' );

        return buf;
    }

    private void encodeTime( final ReusableString buf, long ms, boolean encodeMillisFromStartOfDay ) {

        int hour = (int) (((ms >> 7) * 9773437) >> 38);
        ms -= 3600000 * hour;
        int minute = (int) (((ms >> 5) * 2290650) >> 32);
        ms -= 60000 * minute;
        int second = (int) (((ms >> 3) * 67109) >> 23);

        int temp = (hour * 13) >> 7;
        buf.append( (byte) (temp + '0') );
        buf.append( (byte) (hour - 10 * temp + '0') );
        buf.append( (byte) ':' );

        temp = (minute * 13) >> 7;
        buf.append( (byte) (temp + '0') );
        buf.append( (byte) (minute - 10 * temp + '0') );
        buf.append( (byte) ':' );

        temp = (second * 13) >> 7;
        buf.append( (byte) (temp + '0') );
        buf.append( (byte) (second - 10 * temp + '0') );

        if ( encodeMillisFromStartOfDay ) {
            int leftOverMillis = (int) (ms - (1000 * second));

            buf.append( (byte) '.' );

            temp = (leftOverMillis * 41) >> 12;
            buf.append( (byte) (temp + '0') );
            leftOverMillis -= 100 * temp;

            final int t = NumberFormatUtils._dig100[ leftOverMillis ];

            buf.append( (byte) (t >> 8) );     // tens
            buf.append( (byte) (t & 0xFF) );   // units;
        }
    }

    private void encodeTime( final ByteBuffer buf, long ms, boolean encodeMillisFromStartOfDay ) {

        int hour = (int) (((ms >> 7) * 9773437) >> 38);
        ms -= 3600000 * hour;
        int minute = (int) (((ms >> 5) * 2290650) >> 32);
        ms -= 60000 * minute;
        int second = (int) (((ms >> 3) * 67109) >> 23);

        int temp = (hour * 13) >> 7;
        buf.put( (byte) (temp + '0') );
        buf.put( (byte) (hour - 10 * temp + '0') );
        buf.put( (byte) ':' );

        temp = (minute * 13) >> 7;
        buf.put( (byte) (temp + '0') );
        buf.put( (byte) (minute - 10 * temp + '0') );
        buf.put( (byte) ':' );

        temp = (second * 13) >> 7;
        buf.put( (byte) (temp + '0') );
        buf.put( (byte) (second - 10 * temp + '0') );

        if ( encodeMillisFromStartOfDay ) {
            int leftOverMillis = (int) (ms - (1000 * second));

            buf.put( (byte) '.' );

            temp = (leftOverMillis * 41) >> 12;
            buf.put( (byte) (temp + '0') );
            leftOverMillis -= 100 * temp;

            final int t = NumberFormatUtils._dig100[ leftOverMillis ];

            buf.put( (byte) (t >> 8) );     // tens
            buf.put( (byte) (t & 0xFF) );   // units;
        }
    }

    private DateCache.TimeZoneCacheEntry getLocalDateEntry( final long unixTime ) {

        final DateCache.TimeZoneCacheEntry e = (_todayDateEntryLocal.inRangeLocal( unixTime ) ? _todayDateEntryLocal : _dateCacheLocalTZ.getDateEntryLocal( unixTime ));

        if ( !e.inRangeLocal( unixTime ) ) {
            _log.warn( "ERROR OUT OF RANGE" );
        }

        return e;
    }

    private long getLocalMillisFromStartOfDayFromUnixTimeMS( final long unixTime ) {

        final DateCache.TimeZoneCacheEntry entry = getLocalDateEntry( unixTime );

        return entry.getMillisFromStartOfDay( unixTime );
    }

    private boolean isTodayUnixLocal( long unixTime ) {
        return _todayDateEntryLocal.inRangeLocal( unixTime );
    }

    private synchronized void setLocalDateAsNow( long unixNowMS ) {

        _todayDateEntryLocal.copy( _dateCacheLocalTZ.getDateEntryLocal( unixNowMS ) );

        _localDateYYYYMMDD = getDateYYYYMMDD( _todayDateEntryLocal.getDateStr().getBytes() );

        if ( _log.isEnabledFor( Level.debug ) ) {
            _log.log( Level.debug, "StandardTimeUtils : setting today for " + _localTimezone.getID() + " as " + _todayDateEntryLocal + ", curTime=" + unixTimeToLocalTimestamp( _localTimezone, unixNowMS ) );
        }

        setNewThreshold();
    }

    private void setLocalTZLogStrings( final DateFormat dfTZID, final DateFormat dfDate ) {
        _localTimeZoneLogStrToday = dfTZID.format( new Date() ).getBytes();

        if ( _localTimezone.useDaylightTime() ) {

            String dateStr = "20181201";

            try {
                Date dec = dfDate.parse( dateStr );
                dateStr = "20180601";

                Date jun = dfDate.parse( dateStr );

                if ( _localTimezone.inDaylightTime( jun ) ) { // northern hemisphere
                    _localTimeZoneLogStrNormal          = dfTZID.format( dec ).getBytes();
                    _localTimeZoneLogStrDaylightSavings = dfTZID.format( jun ).getBytes();
                } else { // southern hemisphere
                    _localTimeZoneLogStrNormal          = dfTZID.format( jun ).getBytes();
                    _localTimeZoneLogStrDaylightSavings = dfTZID.format( dec ).getBytes();
                }

            } catch( ParseException e ) {
                _log.error( ERR_PARSE_DATE, " with " + dateStr );
            }

        } else {
            _localTimeZoneLogStrNormal          = _localTimeZoneLogStrToday;
            _localTimeZoneLogStrDaylightSavings = _localTimeZoneLogStrToday;
        }
    }

    private void setNewThreshold() {
        if ( _todayDateEntryUTC.getEndOfDayUnix() < _todayDateEntryLocal.getEndOfDayUnix() ) {
            _nextThreshold = _todayDateEntryUTC.getEndOfDayUnix();
        } else {
            _nextThreshold = _todayDateEntryLocal.getEndOfDayUnix();
        }
    }

    private synchronized void setUTCDateAsNow( long now ) {

        _todayDateEntryUTC.copy( _dateCacheLocalTZ.getDateEntryUTC( now ) );

        if ( _log.isEnabledFor( Level.debug ) ) {
            _log.log( Level.debug, "StandardTimeUtils setting today for UTC as " + _todayDateEntryUTC.toString() );
        }

        setNewThreshold();
    }

    private void thresholdExceeded( long nowUTC ) {
        if ( nowUTC > _todayDateEntryUTC.getEndOfDayUnix() ) {
            setUTCDateAsNow( nowUTC );
        }

        if ( nowUTC > _todayDateEntryLocal.getEndOfDayUnix() ) {
            setLocalDateAsNow( nowUTC );
        }
    }

    private long unixTimsMSToMSFromMidnight( long ms ) {
        if ( isToday( ms ) ) {
            ms -= _todayDateEntryUTC.getStartOfDayUnix();
        } else {
            ms = ms % Constants.MS_IN_DAY;
        }
        return ms;
    }
}
