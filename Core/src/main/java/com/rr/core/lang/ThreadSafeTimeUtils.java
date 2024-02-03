/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

import com.rr.core.tasks.ZLocalDateTime;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * SMT initially held all timestamps internally as int's being number of milliseconds from last midnight to the timestamp time
 * <p>
 * Now all timestamps are held in internal time format known as InternalTime
 * Functions exist to convert between
 * <p>
 * unixTime          -   unix time, number of millis since 1 Jan 1970 (same as System.currentTimeMillis)
 * unixTimeLocal     -   unix time, adjusted to represent the local timezone that the TimeZoneCalculator is currently using
 * internalTime      -   internal format for a UTC time
 * internalTimeLocal -   internal format for a local time
 * <p>
 * The application will either run using StandardTimeZoneCalculator where internal time is same as unix time or as
 * TimeZoneCalculatorHFTTodayOpt where a long holds the number of millis since the last midnight
 */
public class ThreadSafeTimeUtils implements TimeUtils {

    private final ThreadLocal<TimeUtils> _letter = ThreadLocal.withInitial( () -> TimeUtilsFactory.instance().create() );

    public ThreadSafeTimeUtils() {
    }

    public ThreadSafeTimeUtils( final Class<TimeUtils> timeClass ) { }

    /**
     * in letter this method must be threadsafe
     */
    @Override public long currentTimeMillis() {
        return _letter.get().currentTimeMillis();
    }

    @Override public void setTodayFromLocalStr( final String date ) {
        _letter.get().setTodayFromLocalStr( date );
    }

    @Override public void setTodayFromLocalStr( String date, String ptn ) {
        _letter.get().setTodayFromLocalStr( date, ptn );
    }

    @Override public void setTodayFromUTCStr( final String date ) {
        _letter.get().setTodayFromUTCStr( date );
    }

    @Override public void setLocalTimezone( final TimeZone t ) {
        _letter.get().setLocalTimezone( t );
    }

    @Override public void getToday( final byte[] today ) {
        _letter.get().getToday( today );
    }

    @Override public byte[] getDateLocal() {
        return _letter.get().getDateLocal();
    }

    @Override public void setTodayAsNow() {
        _letter.get().setTodayAsNow();
    }

    @Override public int getOffset() {
        return _letter.get().getOffset();
    }

    @Override public boolean isToday( final long dateUTC ) {
        return _letter.get().isToday( dateUTC );
    }

    /**
     * NOT synchronized .. in reality the local timezone is not changing so dont need to protect that
     *
     * @return
     */
    @Override public TimeZone getLocalTimeZone() {
        return _letter.get().getLocalTimeZone();
    }

    @Override public String getLocalTimestamp() {
        return _letter.get().getLocalTimestamp();
    }

    @Override public Calendar getCalendar() {
        return _letter.get().getCalendar();
    }

    @Override public Calendar getCalendar( final TimeZone tz ) {
        return _letter.get().getCalendar( tz );
    }

    @Override public Calendar getTimeAsToday( final String time, final TimeZone tz ) {
        return _letter.get().getTimeAsToday( time, tz );
    }

    @Override public Calendar adjust( final Calendar cal, final String offset ) {
        return _letter.get().adjust( cal, offset );
    }

    @Override public long getNowAsInternalTime() {
        return _letter.get().getNowAsInternalTime();
    }

    @Override public void parseUTCStringToInternalTime( final byte[] src, final int startIdx, final DateParseResults out ) {
        _letter.get().parseUTCStringToInternalTime( src, startIdx, out );
    }

    @Override public long parseUTCStringToInternalTime( final byte[] src, final int startIdx, int len ) { return _letter.get().parseUTCStringToInternalTime( src, startIdx, len ); }

    @Override public long localTimeStampToUnixTime( final ZLocalDateTime localTime ) {
        return _letter.get().localTimeStampToUnixTime( localTime );
    }

    /**
     * parse a timestamp in format YYMMDD-hh:mm:ss (z)
     */
    @Override public long localTimeStampToUnixTime( final ZString localTimeStamp ) {
        return _letter.get().localTimeStampToUnixTime( localTimeStamp );
    }

    @Override public ZString internalTimeToFixStrMillis( final ReusableString str, final long internalTime ) {
        return _letter.get().internalTimeToFixStrMillis( str, internalTime );
    }

    @Override public ZString internalTimeToFixStrSecs( final ReusableString str, final long internalTime ) {
        return _letter.get().internalTimeToFixStrSecs( str, internalTime );
    }

    @Override public int internalTimeToFixStrMillis( final byte[] buf, final int idx, final long internalTime ) {
        return _letter.get().internalTimeToFixStrMillis( buf, idx, internalTime );
    }

    @Override public int internalTimeToFixStrSecs( final byte[] buf, final int idx, final long internalTime ) {
        return _letter.get().internalTimeToFixStrSecs( buf, idx, internalTime );
    }

    @Override public int internalTimeToLocalHHMMSS( final byte[] dest, final int idx, final long internalTime ) { return _letter.get().internalTimeToLocalHHMMSS( dest, idx, internalTime ); }

    @Override public int internalTimeToHHMMSS( final byte[] dest, final int idx, final long internalTime ) {
        return _letter.get().internalTimeToHHMMSS( dest, idx, internalTime );
    }

    @Override public void unixTimeToLocalTimestamp( final ByteBuffer buf, final long unixTime )                                                              { _letter.get().unixTimeToLocalTimestamp( buf, unixTime ); }

    @Override public void unixTimeToLocalTimestamp( final ByteBuffer buf, final TimeZone local, final long unixTime )                                        { _letter.get().unixTimeToLocalTimestamp( buf, local, unixTime ); }

    @Override public ReusableString unixTimeToLocalTimestamp( ReusableString buf, TimeZone local, long unixTime ) { return _letter.get().unixTimeToLocalTimestamp( buf, local, unixTime ); }

    @Override public ReusableString unixTimeToLocalTimestamp( final ReusableString buf, final TimeZone local, final long unixTime, final boolean appendUTC ) {
                                                                                                                                                                 return _letter.get()
                                                                                                                                                                               .unixTimeToLocalTimestamp( buf, local, unixTime, appendUTC );
                                                                                                                                                             }

    @Override public ReusableString unixTimeToLocalTimestamp( ReusableString buf, long unixTime )                 { return _letter.get().unixTimeToLocalTimestamp( buf, unixTime ); }

    @Override public ReusableString unixTimeToUTCTimestamp( final ReusableString buf, final long unixTime )                                                  { return _letter.get().unixTimeToUTCTimestamp( buf, unixTime ); }

    @Override public String unixTimeToLocalTimestamp( final TimeZone local, final long unixTime )                                                            { return _letter.get().unixTimeToLocalTimestamp( local, unixTime ); }

    @Override public String unixTimeToLocalTimestamp( final long unixTime )                                                                                  { return _letter.get().unixTimeToLocalTimestamp( unixTime ); }

    @Override public String unixTimeToUTCTimestamp( final long unixTime )                                                                                    { return _letter.get().unixTimeToUTCTimestamp( unixTime ); }

    @Override public String internalTimeToLocalTimestamp( final long internalTime )                                                                          { return _letter.get().internalTimeToLocalTimestamp( internalTime ); }

    @Override public String nowToLocalTimestamp()                                                                                                            { return _letter.get().nowToLocalTimestamp(); }

    @Override public void unixTimeToLocalStr( final ByteBuffer buf, final long unixTime ) {
        _letter.get().unixTimeToLocalStr( buf, unixTime );
    }

    @Override public void unixTimeToShortLocal( final ReusableString buf, final long msTime ) {
        _letter.get().unixTimeToShortLocal( buf, msTime );
    }

    @Override public String unixTimeToLocalStr( final long unixTime ) {
        return _letter.get().unixTimeToLocalStr( unixTime );
    }

    @Override public long internalTimeToUnixTime( final long internalTime ) {
        return _letter.get().internalTimeToUnixTime( internalTime );
    }

    @Override public long internalTimeToLocalMSFromStartDay( final long internalTime ) {
        return _letter.get().internalTimeToLocalMSFromStartDay( internalTime );
    }

    /*
     * METHODS THAT DONT REQUIRE SYNCHRONISATION AS THEY DONT CHANGE ANY INTERNAL STATE
     */

    @Override public long localMSFromMidnightToInternalTimeToday( final long unixTimeLocal ) {
        return _letter.get().localMSFromMidnightToInternalTimeToday( unixTimeLocal );
    }

    @Override public int unixTimeToIntLocalHHMMSSzzz( long unixTime ) { return _letter.get().unixTimeToIntLocalHHMMSSzzz( unixTime ); }

}
