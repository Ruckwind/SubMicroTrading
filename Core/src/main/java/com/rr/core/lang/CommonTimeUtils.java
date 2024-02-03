/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.hols.HolidayLoader;
import com.rr.core.model.ExchangeCode;
import com.rr.core.model.HolidayEntry;
import com.rr.core.utils.NumberFormatUtils;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.Utils;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.TimeZone;

/**
 * Common Time Utils
 * <p>
 * Placeholder for common useful time utils that dont require local timezone
 * <p>
 * Put functions here instead of TimeUtils IF it meets conditions :-
 * <p>
 * All functions must be stateless
 * All functions cannot use System.currentTimeMillis
 */
public class CommonTimeUtils {

    public static final TimeZone UTC = TimeZone.getTimeZone( "UTC" );

    private static ThreadLocal<Calendar> _cal = ThreadLocal.withInitial( () -> Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) ) );

    public static long unixTimeToInternalTime( final long unixTime ) {
        return unixTime;
    }

    public static long internalTimeToUnixTime( final long internalTime ) {
        return internalTime;
    }

    public static Calendar getCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis( ClockFactory.get().currentTimeMillis() );
        return cal;
    }

    public static Calendar getCalendarUTC() {
        Calendar cal = Calendar.getInstance( UTC );
        cal.setTimeInMillis( ClockFactory.get().currentTimeMillis() );
        return cal;
    }

    public static Calendar getCalendar( TimeZone tz ) {
        Calendar cal = (tz != null) ? Calendar.getInstance( tz ) : Calendar.getInstance();
        cal.setTimeInMillis( ClockFactory.get().currentTimeMillis() );
        return cal;
    }

    public static long ddmmyyyyToUnixTime( TimeZone timezone, int yyyy, int mm, int dd, int hh, int mins, int ss, int ms ) {
        try {
            Calendar c = Calendar.getInstance( timezone );

            return ddmmyyyyToUnixTime( c, yyyy, mm, dd, hh, mins, ss, ms );

        } catch( Exception e ) {
            throw new SMTRuntimeException( "TimeZoneCalculator.ddmmyyyyToUTC() exception : " + e.getMessage(), e );
        }
    }

    public static long ddmmyyyyToUnixTime( Calendar c, int yyyy, int mm, int dd, int hh, int mins, int ss, int ms ) {
        try {
            c.set( Calendar.YEAR, yyyy );
            c.set( Calendar.MONTH, mm - 1 );
            c.set( Calendar.DAY_OF_MONTH, dd );
            c.set( Calendar.HOUR_OF_DAY, hh );
            c.set( Calendar.MINUTE, mins );
            c.set( Calendar.SECOND, ss );
            c.set( Calendar.MILLISECOND, ms );

            return c.getTimeInMillis();

        } catch( Exception e ) {
            throw new SMTRuntimeException( "TimeZoneCalculator.ddmmyyyyToUTC() exception : " + e.getMessage(), e );
        }
    }

    /**
     * @param timezone
     * @param ddmmyyyy "24/12/2010" for 24th December 2010
     * @return number of MS in UTC to the month and day for this year
     * @NOTE only for use in setup code
     */
    public static long ddmmyyyyToUnixTime( TimeZone timezone, String ddmmyyyy ) {

        if ( ddmmyyyy.length() != 10 || ddmmyyyy.charAt( 2 ) != '/' || ddmmyyyy.charAt( 5 ) != '/' ) {
            throw new SMTRuntimeException( "TimeZoneCalculator.ddmmyyyyToUTC() Invalid date string expected dd/mm/yyyy not [" + ddmmyyyy + "]" );
        }

        int day   = (ddmmyyyy.charAt( 0 ) - '0') * 10 + (ddmmyyyy.charAt( 1 ) - '0');
        int month = (ddmmyyyy.charAt( 3 ) - '0') * 10 + (ddmmyyyy.charAt( 4 ) - '0');
        int year = (ddmmyyyy.charAt( 6 ) - '0') * 1000 + (ddmmyyyy.charAt( 7 ) - '0') * 100 +
                   (ddmmyyyy.charAt( 8 ) - '0') * 10 + (ddmmyyyy.charAt( 9 ) - '0');

        try {
            Calendar c = TimeUtilsFactory.safeTimeUtils().getCalendar( timezone );

            c.set( Calendar.YEAR, year );
            c.set( Calendar.MONTH, month - 1 );
            c.set( Calendar.DAY_OF_MONTH, day );

            return c.getTimeInMillis();

        } catch( Exception e ) {
            throw new SMTRuntimeException( "TimeZoneCalculator.ddmmyyyyToUTC() Invalid date string expected dd/mm/yyyy not [" + ddmmyyyy + "]", e );
        }
    }

    /**
     * converts minutes to millis
     *
     * @param minutes
     * @return the millis in specified amount of minutes
     */
    public static int minutesToMillis( final int minutes ) {
        return minutes * Constants.MS_IN_MINUTE;
    }

    /**
     * converts days to millis
     *
     * @param days
     * @return the millis in specified amount of days
     */
    public static long daysToMillis( final long days ) {
        return days * Constants.MS_IN_DAY;
    }

    public static int epochMillisToYYYYMMDD( final long timeMS ) {
        Calendar c = _cal.get();

        c.setTimeInMillis( timeMS );

        int yyyy = c.get( Calendar.YEAR );
        int mm   = c.get( Calendar.MONTH );
        int day  = c.get( Calendar.DAY_OF_MONTH );

        int date = yyyy * 10000 + (mm + 1) * 100 + day;

        return date;
    }

    public static int calendarToYYYYMMDD( final Calendar c ) {
        int yyyy = c.get( Calendar.YEAR );
        int mm   = c.get( Calendar.MONTH );
        int day  = c.get( Calendar.DAY_OF_MONTH );

        int date = yyyy * 10000 + (mm + 1) * 100 + day;

        return date;
    }

    public static int calendarToHHMMSS( final Calendar c ) {
        int hh = c.get( Calendar.HOUR_OF_DAY );
        int mm = c.get( Calendar.MINUTE );
        int se = c.get( Calendar.SECOND );

        int date = hh * 10000 + mm * 100 + se;

        return date;
    }

    public static void yyyymmToCalEndMonths( Calendar gapCal, int yyyymm ) {

        int yyyy = (yyyymm / 100);
        int mm   = (yyyymm % 100);
        gapCal.set( Calendar.YEAR, yyyy );
        gapCal.set( Calendar.MONTH, mm - 1 );
        gapCal.set( Calendar.DAY_OF_MONTH, gapCal.getActualMaximum( Calendar.DAY_OF_MONTH ) );
        gapCal.set( Calendar.HOUR_OF_DAY, 23 );
        gapCal.set( Calendar.MINUTE, 59 );
        gapCal.set( Calendar.SECOND, 59 );
        gapCal.set( Calendar.MILLISECOND, 0 );
    }

    public static long yyyymmddToUnixTime( long yyyymmdd ) {
        try {
            Calendar c = TimeUtilsFactory.safeTimeUtils().getCalendar();

            int yyyy = (int) (yyyymmdd / 10000);
            int mm   = (int) ((yyyymmdd / 100) % 100);
            int dd   = (int) (yyyymmdd % 100);

            c.set( yyyy, mm - 1, dd );
            c.set( Calendar.HOUR_OF_DAY, 00 );
            c.set( Calendar.MINUTE, 00 );
            c.set( Calendar.SECOND, 00 );
            c.set( Calendar.MILLISECOND, 1 );

            return c.getTimeInMillis();

        } catch( Exception e ) {
            throw new SMTRuntimeException( "TimeZoneCalculator.ddmmyyyyToUTC() Invalid date string expected yyyymmdd not [" + yyyymmdd + "]", e );
        }
    }

    public static void yyyymmddToCalendar( Calendar gapCal, long yyyymmdd ) {
        int yyyy = (int) (yyyymmdd / 10000);
        int mm   = (int) ((yyyymmdd / 100) % 100);
        int dd   = (int) (yyyymmdd % 100);
        gapCal.set( yyyy, mm - 1, dd );
        gapCal.set( Calendar.HOUR_OF_DAY, 00 );
        gapCal.set( Calendar.MINUTE, 00 );
        gapCal.set( Calendar.SECOND, 00 );
        gapCal.set( Calendar.MILLISECOND, 1 );
    }

    public static int getYYYYMMDD( final String v ) { // 2012-04-30

        if ( v == null || v.length() == 0 ) return Constants.UNSET_INT;

        int y1 = 0, y2 = 0, y3 = 0, y4 = 0;
        int m1 = 0, m2 = 0;
        int d1 = 0, d2 = 0;

        y1 = v.charAt( 0 ) - '0';
        y2 = v.charAt( 1 ) - '0';
        y3 = v.charAt( 2 ) - '0';
        y4 = v.charAt( 3 ) - '0';

        m1 = v.charAt( 5 ) - '0';
        m2 = v.charAt( 6 ) - '0';

        d1 = v.charAt( 8 ) - '0';
        d2 = v.charAt( 9 ) - '0';

        int yyyy = (((((y1 * 10) + y2) * 10) + y3) * 10) + y4;
        int mm   = (m1 * 10) + m2;
        int dd   = (d1 * 10) + d2;

        return yyyy * 10000 + mm * 100 + dd;
    }

    public static int getHHMMSS( final String v ) { // hh:mm:ss

        if ( v == null || v.length() == 0 ) return Constants.UNSET_INT;

        if ( v.length() < 8 ) {
            throw new RuntimeDecodingException( "Invalid date of " + v + " expected min 8 bytes not " + v );
        }

        int h1 = 0, h2 = 0;
        int m1 = 0, m2 = 0;
        int s1 = 0, s2 = 0;

        h1 = v.charAt( 0 ) - '0';
        h2 = v.charAt( 1 ) - '0';
        m1 = v.charAt( 3 ) - '0';
        m2 = v.charAt( 4 ) - '0';
        s1 = v.charAt( 6 ) - '0';
        s2 = v.charAt( 7 ) - '0';

        int hh = (h1 * 10) + h2;
        int mm = (m1 * 10) + m2;
        int ss = (s1 * 10) + s2;

        return ((hh * 100) + mm) * 100 + ss;
    }

    public static Calendar addWorkingDays( final ExchangeCode exchangeCode, final Calendar c, int daysToAdd ) {
        if ( Utils.isNullOrZero( daysToAdd ) ) return c;
        if ( daysToAdd < 0 ) throw new SMTRuntimeException( "addWorkingDays called with -ve number " + daysToAdd );

        LinkedHashMap<Integer, HolidayEntry> hols = HolidayLoader.instance().getHolidays( exchangeCode );

        while( daysToAdd > 0 ) {
            int     dayOfWeek = c.get( Calendar.DAY_OF_WEEK );
            boolean weekEnd   = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);

            if ( !weekEnd ) {
                int yyyymmdd = CommonTimeUtils.calendarToYYYYMMDD( c );

                if ( hols == null || !hols.containsKey( yyyymmdd ) ) {
                    --daysToAdd;
                }

                c.add( Calendar.DAY_OF_MONTH, +1 );
            } else {
                c.add( Calendar.DAY_OF_MONTH, +1 );
            }
        }

        // check havent ended on holiday or weekend

        while( true ) {
            int     dayOfWeek = c.get( Calendar.DAY_OF_WEEK );
            boolean weekEnd   = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);

            if ( !weekEnd ) {
                int yyyymmdd = CommonTimeUtils.calendarToYYYYMMDD( c );

                if ( hols == null || !hols.containsKey( yyyymmdd ) ) {
                    break;
                }

                c.add( Calendar.DAY_OF_MONTH, +1 );
            } else {
                c.add( Calendar.DAY_OF_MONTH, +1 );
            }
        }

        return c;
    }

    public static void yyyymmddToCalendarNextWorkDay( final ExchangeCode exchangeCode, Calendar c, long yyyymmdd ) {
        int yyyy = (int) (yyyymmdd / 10000);
        int mm   = (int) ((yyyymmdd / 100) % 100);
        int dd   = (int) (yyyymmdd % 100);
        c.set( yyyy, mm - 1, dd );
        c.set( Calendar.HOUR_OF_DAY, 00 );
        c.set( Calendar.MINUTE, 00 );
        c.set( Calendar.SECOND, 00 );
        c.set( Calendar.MILLISECOND, 1 );

        addWorkingDays( exchangeCode, c, 1 );
    }

    public static Calendar lastWorkingDayOnOrBefore( final ExchangeCode exchangeCode, final Calendar c ) {

        LinkedHashMap<Integer, HolidayEntry> hols = HolidayLoader.instance().getHolidays( exchangeCode );

        // check havent ended on holiday or weekend

        while( true ) {
            int     dayOfWeek = c.get( Calendar.DAY_OF_WEEK );
            boolean weekEnd   = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);

            if ( !weekEnd ) {
                int yyyymmdd = CommonTimeUtils.calendarToYYYYMMDD( c );

                if ( hols == null || !hols.containsKey( yyyymmdd ) ) {
                    break;
                }

                c.add( Calendar.DAY_OF_MONTH, -1 );
            } else {
                c.add( Calendar.DAY_OF_MONTH, -1 );
            }
        }

        return c;
    }

    public static Calendar lastWorkingDay( final ExchangeCode exchangeCode, final Calendar c, int daysToSubtract ) {

        if ( daysToSubtract < 0 ) throw new SMTRuntimeException( "subtractWorkingDays called with -ve number " + daysToSubtract );

        LinkedHashMap<Integer, HolidayEntry> hols = (exchangeCode == null) ? null : HolidayLoader.instance().getHolidays( exchangeCode );

        while( daysToSubtract > 0 ) {
            int     dayOfWeek = c.get( Calendar.DAY_OF_WEEK );
            boolean weekEnd   = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);

            if ( !weekEnd ) {
                int yyyymmdd = CommonTimeUtils.calendarToYYYYMMDD( c );

                if ( hols == null || !hols.containsKey( yyyymmdd ) ) {
                    --daysToSubtract;
                }

                c.add( Calendar.DAY_OF_MONTH, -1 );
            } else {
                c.add( Calendar.DAY_OF_MONTH, -1 );
            }
        }

        // check havent ended on holiday or weekend

        while( true ) {
            int     dayOfWeek = c.get( Calendar.DAY_OF_WEEK );
            boolean weekEnd   = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);

            if ( !weekEnd ) {
                int yyyymmdd = CommonTimeUtils.calendarToYYYYMMDD( c );

                if ( hols == null || !hols.containsKey( yyyymmdd ) ) {
                    break;
                }

                c.add( Calendar.DAY_OF_MONTH, -1 );
            } else {
                c.add( Calendar.DAY_OF_MONTH, -1 );
            }
        }

        return c;
    }

    public static long lastWorkingDay( ExchangeCode exchangeCode, long ts, int days ) {
        Calendar c = Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) );
        c.setTimeInMillis( ts );

        lastWorkingDay( exchangeCode, c, days );

        return c.getTimeInMillis();
    }

    public static long addWorkingDays( ExchangeCode exchangeCode, long ts, int days ) {
        Calendar c = Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) );
        c.setTimeInMillis( ts );

        addWorkingDays( exchangeCode, c, days );

        return c.getTimeInMillis();
    }

    public static void yyyymmddToCalendar( Calendar gapCal, long yyyymmdd, int hhmmss ) {
        int yyyy = (int) (yyyymmdd / 10000);
        int mm   = (int) ((yyyymmdd / 100) % 100);
        int dd   = (int) (yyyymmdd % 100);

        int hh  = hhmmss / 10000;
        int min = (hhmmss / 100) % 100;
        int sec = hhmmss % 100;

        gapCal.set( yyyy, mm - 1, dd );
        gapCal.set( Calendar.HOUR_OF_DAY, hh );
        gapCal.set( Calendar.MINUTE, min );
        gapCal.set( Calendar.SECOND, sec );
    }

    /**
     * check if unix time is in local date range .. uses the localTZ set in the timeUtils
     *
     * @param localTU
     * @param unixTime
     * @param startHHMMSSzzz start of range ... inclusive
     * @param endHMMSSzzz    end of range ... NON inclusive
     * @return true if unixTime falls in the time range range based on the date in the unixTime ... ie the local date will be the 24 hours which encompasses the specified unix date / time
     * @WARNING you must be very careful when shortening dates for comparison to local times and understand which local date will be used !!!
     * eg GMT will match to LA time for either the date or date-1 (as -8 UTC offset)
     * eg GMT will match to Tokyo time for either the date or date+1 (as +9 UTC offset)
     */
    public static boolean inLocalRange( TimeUtils localTU, long unixTime, int startHHMMSSzzz, int endHMMSSzzz ) {
        int localHHMMSSzzz = localTU.unixTimeToIntLocalHHMMSSzzz( unixTime );

        return (localHHMMSSzzz >= startHHMMSSzzz && localHHMMSSzzz < endHMMSSzzz);
    }

    /**
     * check if unix time is at or after local time
     *
     * @param localTU
     * @param unixTime
     * @param startHHMMSSzzz start of range ... inclusive
     * @return true if unixTime is equal of after the time range range based on the date in the unixTime ... ie the local date will be the 24 hours which encompasses the specified unix date / time
     * @WARNING you must be very careful when shortening dates for comparison to local times and understand which local date will be used !!!
     * eg GMT will match to LA time for either the date or date-1 (as -8 UTC offset)
     * eg GMT will match to Tokyo time for either the date or date+1 (as +9 UTC offset)
     */
    public static boolean isAtOrAfter( TimeUtils localTU, long unixTime, int startHHMMSSzzz ) {
        int localHHMMSSzzz = localTU.unixTimeToIntLocalHHMMSSzzz( unixTime );

        return (localHHMMSSzzz >= startHHMMSSzzz);
    }

    /**
     * extract YYYYMMDD from fix str  20110823-05:57:01
     *
     * @param fixStr
     * @return
     */
    public static int fixStrToYYYYMMDD( final ReusableString fixStr ) {
        final int fixLen = fixStr.length();

        if ( fixLen == 0 ) return Constants.UNSET_INT;

        if ( fixLen < 8 ) {
            throw new SMTRuntimeException( "CommonTimeUtils.fixStrToYYYYMMDD() Invalid date string expected 20110823-05:57:01 not [" + fixStr + "]" );
        }

        int year = (fixStr.getByte( 0 ) - '0') * 1000 + (fixStr.getByte( 1 ) - '0') * 100 +
                   (fixStr.getByte( 2 ) - '0') * 10 + (fixStr.getByte( 3 ) - '0');
        int month = (fixStr.getByte( 4 ) - '0') * 10 + (fixStr.getByte( 5 ) - '0');
        int day   = (fixStr.getByte( 6 ) - '0') * 10 + (fixStr.getByte( 7 ) - '0');

        return (((year * 100) + month) * 100) + day;
    }

    /**
     * extract HHMMSS from fix str  20110823-05:57:01
     *
     * @param fixStr
     * @return
     */
    public static int fixStrToHHMMSS( final ReusableString fixStr ) {
        final int fixLen = fixStr.length();

        if ( fixLen == 0 ) return Constants.UNSET_INT;

        if ( fixLen < 17 ) {
            throw new SMTRuntimeException( "CommonTimeUtils.fixStrToYYYYMMDD() Invalid date string expected 20110823-05:57:01 not [" + fixStr + "]" );
        }

        int hh = (fixStr.getByte( 9 ) - '0') * 10 + (fixStr.getByte( 10 ) - '0');
        int mm = (fixStr.getByte( 12 ) - '0') * 10 + (fixStr.getByte( 13 ) - '0');
        int ss = (fixStr.getByte( 15 ) - '0') * 10 + (fixStr.getByte( 16 ) - '0');

        return (((hh * 100) + mm) * 100) + ss;
    }

    public static void formatDate( int date, final ReusableString dest ) {
        if ( Utils.isNull( date ) || date <= 0 ) return;

        int valLen = NumberFormatUtils.getPosIntLen( date );

        if ( valLen != 8 ) throw new SMTRuntimeException( "invalid date of " + date + " expected 8 digits" );

        int day = date % 100; // should really avoid mod func but this routine only used for CSV export so dont worry

        date = date / 100;

        int month = date % 100;

        date = date / 100;

        int year = date;

        dest.append( year ).append( '-' );

        if ( month < 10 ) dest.append( '0' );

        dest.append( month ).append( '-' );

        if ( day < 10 ) dest.append( '0' );

        dest.append( day );
    }

    public static void formatTime( int time, final ReusableString dest ) {

        if ( Utils.isNull( time ) ) return;

        int valLen = NumberFormatUtils.getPosIntLen( time );

        switch( valLen ) {
        case 1:
            dest.append( "00:00:0" ).append( time );
            break;
        case 2:
            dest.append( "00:00:" ).append( time );
            break;
        case 3: {
            int min  = time / 100;
            int secs = time - (min * 100);
            dest.append( "00:0" ).append( min ).append( ':' );

            if ( secs < 10 ) dest.append( '0' );

            dest.append( secs );

            break;
        }
        case 4: {
            int mins = time / 100;
            int secs = time - (mins * 100);
            dest.append( "00:" ).append( mins ).append( ':' );

            if ( secs < 10 ) dest.append( '0' );

            dest.append( secs );

            break;
        }
        case 5: { // should really avoid mod func but this routine only used for CSV export so dont worry
            int secs = time % 100;
            time /= 100;
            int mins = time % 100;
            time /= 100;
            int hour = time;

            dest.append( "0" ).append( hour ).append( ':' );

            if ( mins < 10 ) dest.append( '0' );
            dest.append( mins ).append( ':' );

            if ( secs < 10 ) dest.append( '0' );
            dest.append( secs );
            break;
        }
        case 6: { // should really avoid mod func but this routine only used for CSV export so dont worry
            int secs = time % 100;
            time /= 100;
            int mins = time % 100;
            time /= 100;
            int hours = time;

            dest.append( hours ).append( ':' );

            if ( mins < 10 ) dest.append( '0' );
            dest.append( mins ).append( ':' );

            if ( secs < 10 ) dest.append( '0' );
            dest.append( secs );
            break;
        }
        default:
            throw new SMTRuntimeException( "invalid time of " + time );
        }
    }
}
