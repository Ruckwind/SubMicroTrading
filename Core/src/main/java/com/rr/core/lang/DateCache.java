package com.rr.core.lang;

import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Threadsafe DateCache for fast lookups of date string by using a start of day time
 * <p>
 * One DateCache instance per requested TimeZone
 * <p>
 * Two factory methods, one adds a UTC cache entry per startOfDayUTC,
 * the other adds a localDate cache entry per (startOfDayUTC + RAW timezoneOffet)
 *
 * @WARNING only handles DST changes for the next week.
 */
public class DateCache {

    private static final Logger _log = ConsoleFactory.console( DateCache.class, Level.info );

    private static final TimeZone _utcTZ = TimeZone.getTimeZone( "UTC" );

    private static final ConcurrentHashMap<TimeZone, DateCache> _dateDateCache = new ConcurrentHashMap<>( 1 );

    /**
     * TimeZoneCacheEntry caches the date string and related info for either a UTC date or the local timezone
     */
    public static final class TimeZoneCacheEntry {

        private int     _dstOffset;
        private long    _startOfDayUnix;                 // unixTime from Epoch to midnight in "local" timezone
        private long    _endOfDayUnix;
        private boolean _startDayInDST;                     // is this date in daylight savings
        private boolean _endDayInDST;                       // is this date in daylight savings
        private long    _dstSwitchThreshold;                // the time when DST starts or ends depending on the start/end flags

        private ReusableString _dateStr = new ReusableString( Constants.DATE_STR_LEN ); // the YYMMDD- string for this date (UTC or local)

        public TimeZoneCacheEntry() {
        }

        /**
         * @param dateStr - ReusableString owned by this entry
         */
        public TimeZoneCacheEntry( final long startOfDayUnix, final long endOfDayUnix,
                                   final boolean startDayInDST,
                                   final boolean endDayInDST, final long dstSwitchThreshold,
                                   final int dstOffset,
                                   final ReusableString dateStr ) {
            _startOfDayUnix     = startOfDayUnix;
            _endOfDayUnix       = endOfDayUnix;
            _startDayInDST      = startDayInDST;
            _endDayInDST        = endDayInDST;
            _dstSwitchThreshold = dstSwitchThreshold;
            _dateStr            = dateStr;
            _dstOffset          = dstOffset;
        }

        @Override public String toString() {
            return "TimeZoneCacheEntry{" +
                   " _startOfDayLocalMS=" + _startOfDayUnix +
                   ", _endOfDayLocalMS=" + _endOfDayUnix +
                   ", _startDayInDST=" + _startDayInDST +
                   ", _endDayInDST=" + _endDayInDST +
                   ", _dstSwitchThreshold=" + _dstSwitchThreshold +
                   ", _dateStr=" + _dateStr +
                   '}';
        }

        public void copy( final TimeZoneCacheEntry from ) {
            _startOfDayUnix     = from._startOfDayUnix;
            _endOfDayUnix       = from._endOfDayUnix;
            _startDayInDST      = from._startDayInDST;
            _endDayInDST        = from._endDayInDST;
            _dstSwitchThreshold = from._dstSwitchThreshold;
            _dstOffset          = from._dstOffset;

            _dateStr.copy( from._dateStr );
        }

        public ReusableString getDateStr()  { return _dateStr; }

        public long getDstSwitchThreshold() { return _dstSwitchThreshold; }

        public long getEndOfDayUnix()       { return _endOfDayUnix; }

        public long getMillisFromStartOfDay( final long unixTime ) {
            if ( _startDayInDST != _endDayInDST ) {
                if ( _endDayInDST ) { // switching to DST
                    if ( unixTime > _dstSwitchThreshold ) {
                        return unixTime - (_startOfDayUnix - _dstOffset); // switched clock forward
                    }
                } else if ( unixTime > _dstSwitchThreshold ) {          // else end day out of DST
                    return unixTime - (_startOfDayUnix + _dstOffset); // switched clock backward
                }
            }
            return unixTime - _startOfDayUnix;
        }

        public long getStartOfDayUnix()     { return _startOfDayUnix; }

        public boolean inRangeLocal( final long localTimeMS ) {
            return (localTimeMS >= _startOfDayUnix && localTimeMS < _endOfDayUnix);
        }

        public boolean isEndDayInDST()      { return _endDayInDST; }

        public boolean isStartDayInDST()    { return _startDayInDST; }
    }

    private final TimeZone _localTZ;
    private final long     _localTZRawOffset;
    private final long     _localTZDSTSavings;
    private final long     _localTZOfsetWithDST;

    private final ConcurrentHashMap<ReusableString, Long>     _dateToStartDayMSCache = new ConcurrentHashMap<>( 64 );
    private final ConcurrentHashMap<Long, TimeZoneCacheEntry> _dateBufCacheUTC       = new ConcurrentHashMap<>( 64 );
    private final ConcurrentHashMap<Long, TimeZoneCacheEntry> _dateBufCacheLocal     = new ConcurrentHashMap<>( 64 );

    private final ThreadLocal<DateFormat> _dateFormatToStrUTC;   // encoding will use this
    private final ThreadLocal<DateFormat> _dateFormatToStrLocal; // encoding will use this
    private final ThreadLocal<DateFormat> _dateFormatFromStr;    // decoding will use this, keep seperate to avoid contention

    /**
     * Return a DateCache which treats supplied TimeZone as "Local"
     *
     * @param tz
     * @return
     */
    public static DateCache instance( TimeZone tz ) {
        DateCache instance = _dateDateCache.get( tz );

        if ( instance == null ) {
            instance = new DateCache( tz );

            DateCache oldIntance = _dateDateCache.putIfAbsent( tz, instance );

            if ( oldIntance != null ) instance = oldIntance;
        }

        return instance;
    }

    public static void reset() {
        _dateDateCache.clear();
    }

    public DateCache( final TimeZone tz ) {
        _localTZ = tz;

        _localTZRawOffset    = tz.getRawOffset();
        _localTZDSTSavings   = tz.getDSTSavings();
        _localTZOfsetWithDST = _localTZRawOffset + _localTZDSTSavings;

        _dateFormatToStrUTC = ThreadLocal.withInitial( () -> createFormatter( TimeZone.getTimeZone( "UTC" ), Constants.FIX_TIMESTAMP_DATE_FORMAT ) );

        _dateFormatToStrLocal = ThreadLocal.withInitial( () -> createFormatter( tz, Constants.FIX_TIMESTAMP_DATE_FORMAT ) );

        _dateFormatFromStr = ThreadLocal.withInitial( () -> createFormatter( TimeZone.getTimeZone( "UTC" ), Constants.FIX_TIMESTAMP_DATE_FORMAT ) );
    }

    public SimpleDateFormat createFormatter( final TimeZone tz, String ptn ) {
        final SimpleDateFormat f = new SimpleDateFormat( ptn );
        f.setTimeZone( tz );
        return f;
    }

    public TimeZoneCacheEntry getDateEntryLocal( final long unixTime ) {
        TimeZoneCacheEntry buf = doGetTimeZoneCacheEntry( unixTime );

        if ( buf.inRangeLocal( unixTime ) ) {
            return buf;
        }

        if ( unixTime < buf._startOfDayUnix ) {
            return doGetTimeZoneCacheEntry( unixTime - Constants.MS_IN_DAY );
        }

        return doGetTimeZoneCacheEntry( unixTime + Constants.MS_IN_DAY );
    }

    public TimeZoneCacheEntry getDateEntryUTC( final long unixTime ) {

        long dateStart = unixTime - (unixTime % Constants.MS_IN_DAY);

        TimeZoneCacheEntry buf = _dateBufCacheUTC.get( dateStart );

        if ( buf == null ) {
            Date d = new Date( unixTime );

            String         utcDate = _dateFormatToStrUTC.get().format( d );
            ReusableString dateStr = TLC.instance().getString().copy( utcDate ); // owned by cache entry

            buf = new TimeZoneCacheEntry( dateStart, dateStart + Constants.MS_IN_DAY, false, false, 0, 0, dateStr );

            final TimeZoneCacheEntry oldBuf = _dateBufCacheUTC.putIfAbsent( dateStart, buf );

            if ( oldBuf != null ) buf = oldBuf;
        }

        return buf;
    }

    /**
     * @return the epoch time for the start of the day ie 12AM UTC
     */
    public long getStartOfDayUnixTimeUTC( final byte[] src, int offset ) {

        ReusableString dateStr = TLC.instance().pop();
        dateStr.setValue( src, offset, Constants.DATE_STR_LEN );
        Long dateStartInMS = _dateToStartDayMSCache.get( dateStr );

        if ( dateStartInMS == null ) {
            try {
                Date d = _dateFormatFromStr.get().parse( dateStr.toString() );

                dateStartInMS = d.getTime();
            } catch( Exception e ) {
                throw new RuntimeDecodingException( "Unable to parse date " + dateStr + " err=" + e.getMessage(), e );
            }

            _dateToStartDayMSCache.putIfAbsent( dateStr, dateStartInMS );
        } else {
            TLC.instance().pushback( dateStr );
        }

        return dateStartInMS;
    }

    public boolean isDaylightSavings( final TimeZoneCacheEntry entry, final long unixTime ) {
        if ( !entry._startDayInDST && !entry._endDayInDST ) {
            return false;
        }
        if ( entry._startDayInDST && entry._endDayInDST ) {
            return true;
        }

        /**
         * @NOTE following is incorrect for Southern Hemisphere eg Australia !
         */
        if ( entry._startDayInDST ) {
            return (unixTime <= entry._dstSwitchThreshold);
        }
        return (unixTime >= entry._dstSwitchThreshold);
    }

    private TimeZoneCacheEntry doGetTimeZoneCacheEntry( final long unixTime ) {
        long startDayUnixTime = unixTime - unixTime % Constants.MS_IN_DAY;

        TimeZoneCacheEntry buf = _dateBufCacheLocal.get( startDayUnixTime );

        if ( buf == null ) {
            Date d = new Date( startDayUnixTime );

            String localDate = _dateFormatToStrLocal.get().format( d );

            Calendar c = Calendar.getInstance( _localTZ );

            c.setTime( d );

            int origHour = c.get( Calendar.HOUR_OF_DAY );

            c.set( Calendar.HOUR_OF_DAY, 0 );
            c.set( Calendar.MINUTE, 0 );
            c.set( Calendar.SECOND, 0 );
            c.set( Calendar.MILLISECOND, 0 );

            long dayStartLocal = c.getTimeInMillis();

            boolean startDayInDST = _localTZ.inDaylightTime( c.getTime() );

            long dayEndLocal = dayStartLocal + Constants.MS_IN_DAY;

            c.set( Calendar.HOUR_OF_DAY, 23 );

            boolean endDayInDST = _localTZ.inDaylightTime( c.getTime() );

            long dstSwitchThreshold = 0;

            if ( startDayInDST != endDayInDST ) {

                Level lvl = Env.isProdOrDevOrUAT() ? Level.info : Level.trace;

                if ( startDayInDST ) { // find the hour for end of DST
                    for ( int h = 0; h <= 23; h++ ) {
                        c.set( Calendar.HOUR_OF_DAY, h );
                        if ( !_localTZ.inDaylightTime( c.getTime() ) ) {
                            dstSwitchThreshold = c.getTimeInMillis();
                            _log.log( lvl, "For " + _localTZ.getID() + " local date " + localDate + ", startDayDST=" + startDayInDST + ", endDayDST=" + endDayInDST +
                                           ", switch DST off on hour " + h );
                            break;
                        }
                    }
                } else { // find the hour for DST to start
                    for ( int h = 0; h <= 23; h++ ) {
                        c.set( Calendar.HOUR_OF_DAY, h );
                        if ( _localTZ.inDaylightTime( c.getTime() ) ) {
                            dstSwitchThreshold = c.getTimeInMillis();
                            _log.log( lvl, "For " + _localTZ.getID() + " local date " + localDate + ", startDayDST=" + startDayInDST + ", endDayDST=" + endDayInDST +
                                           ", switch DST on hour " + h );
                            break;
                        }
                    }
                }

                if ( dstSwitchThreshold == 0 ) {
                    _log.warn( "For " + _localTZ.getID() + " local date " + localDate + ", startDayDST=" + startDayInDST + ", endDayDST=" + endDayInDST +
                               ", unable to find switch time" );
                }

                c.setTimeInMillis( dayStartLocal );
                c.set( Calendar.HOUR_OF_DAY, 23 );
                c.set( Calendar.MINUTE, 59 );
                c.set( Calendar.SECOND, 59 );
                c.set( Calendar.MILLISECOND, 999 );

                dayEndLocal = c.getTimeInMillis() + 1;
            }

            ReusableString dateStr = TLC.instance().getString().copy( localDate );

            buf = new TimeZoneCacheEntry( dayStartLocal, dayEndLocal, startDayInDST, endDayInDST, dstSwitchThreshold, _localTZ.getDSTSavings(), dateStr );

            final TimeZoneCacheEntry oldBuf = _dateBufCacheLocal.putIfAbsent( startDayUnixTime, buf );

            if ( oldBuf != null ) buf = oldBuf;
        }

        return buf;
    }
}
