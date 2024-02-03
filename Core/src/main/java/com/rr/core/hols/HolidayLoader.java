package com.rr.core.hols;

import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.CommonTimeUtils;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ViewString;
import com.rr.core.model.ExchDerivInstrument;
import com.rr.core.model.ExchangeCode;
import com.rr.core.model.HolidayEntry;
import com.rr.core.properties.AppProps;
import com.rr.core.utils.FileUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * HolidayLoader ... based on BATS market_holidays.csv file
 *
 * @NOTE HolidayEntry will have null timestamps for full day off
 */
public class HolidayLoader {

    private static final String DEFAULT_HOL_FILE = "./refdata/market_holidays.csv";

    private static final TimeZone      _timeZone = TimeZone.getTimeZone( "Europe/London" );
    private static final HolidayLoader _instance = new HolidayLoader();

    private final Map<ExchangeCode, LinkedHashMap<Integer, HolidayEntry>> _hols = new HashMap<>();
    private final Set<ExchangeCode>                                       _mics = new HashSet<>();

    private int _fromYYYYMMDD = AppProps.instance().getIntProperty( "SYNTH_HOLS_FROM_YYYYMMDD", false, 19800101 );
    private int _uptoYYYYMMDD = AppProps.instance().getIntProperty( "SYNTH_HOLS_UPTO_YYYYMMDD", false, 20301231 );

    public static HolidayLoader instance() { return _instance; }

    /**
     * Parses a date in the format "YYYY-MM-DD' into an integer of the format YYYYMMDD digits.
     * <p>
     * No validation is done on the correctness of the string, beyond testing it for the correct length, nor on the date returned to ensure it is a real date.
     *
     * @param dateStr the date as a string
     * @return the date as an integer
     */
    public static int getDate( String dateStr ) {

        if ( dateStr.length() != 10 ) {
            throw new RuntimeDecodingException( "HolidayLoader getDate str too small [" + dateStr + "]" );
        }

        byte[] buf    = dateStr.getBytes();
        int    offset = 0;

        int y1 = buf[ offset++ ] - '0';
        int y2 = buf[ offset++ ] - '0';
        int y3 = buf[ offset++ ] - '0';
        int y4 = buf[ offset ] - '0';

        offset += 2;

        int m1 = buf[ offset++ ] - '0';
        int m2 = buf[ offset ] - '0';

        offset += 2;

        int d1 = buf[ offset++ ] - '0';
        int d2 = buf[ offset ] - '0';

        int yyyy = y1 * 1000 + y2 * 100 + y3 * 10 + y4;
        int mm   = m1 * 10 + m2;
        int dd   = d1 * 10 + d2;

        return yyyy * 10000 + mm * 100 + dd;
    }

    public static int getTimeHHMMSS( String timeStr ) {

        if ( timeStr == null || timeStr.length() == 0 ) return Constants.UNSET_INT;

        if ( timeStr.length() != 5 ) throw new RuntimeDecodingException( "HolidayFuncs time should have 5 bytes but has " + timeStr.length() + " [" + timeStr + "]" );

        byte[] buf    = timeStr.getBytes();
        int    offset = 0;

        int h1 = buf[ offset++ ] - '0';
        int h2 = buf[ offset ] - '0';

        offset += 2;

        int m1 = buf[ offset++ ] - '0';
        int m2 = buf[ offset ] - '0';

        int hour = h1 * 10 + h2;
        int min  = m1 * 10 + m2;

        return hour * 10000 + min * 100;
    }

    public static long getTimestamp( int date, String timeStr ) {

        if ( timeStr == null || timeStr.length() == 0 ) return Constants.UNSET_LONG;

        if ( timeStr.length() != 5 ) throw new RuntimeDecodingException( "HolidayFuncs time should have 5 bytes but has " + timeStr.length() + " [" + timeStr + "]" );

        byte[] buf    = timeStr.getBytes();
        int    offset = 0;

        int h1 = buf[ offset++ ] - '0';
        int h2 = buf[ offset ] - '0';

        offset += 2;

        int m1 = buf[ offset++ ] - '0';
        int m2 = buf[ offset ] - '0';

        int hour = h1 * 10 + h2;
        int min  = m1 * 10 + m2;

        int year  = date / 10000;
        int month = (date / 100) % 100;
        int day   = date % 100;

        long time = CommonTimeUtils.ddmmyyyyToUnixTime( _timeZone, year, month, day, hour, min, 0, 0 );

        return CommonTimeUtils.unixTimeToInternalTime( time );
    }

    public HolidayLoader() {
        this( DEFAULT_HOL_FILE );
    }

    public HolidayLoader( String fileName ) {
        load( fileName );
    }

    /**
     * @param code
     * @param yyyymmdd
     * @return the holiday entry for the specified date OR null if none
     */
    public HolidayEntry getHoliday( ExchangeCode code, int yyyymmdd ) {
        final LinkedHashMap<Integer, HolidayEntry> hols = _hols.get( code );

        if ( hols == null ) return null;

        return hols.get( yyyymmdd );
    }

    public LinkedHashMap<Integer, HolidayEntry> getHolidays( ExchangeCode code ) {
        if ( code == null ) return null;

        final LinkedHashMap<Integer, HolidayEntry> hols = _hols.get( code );

        if ( hols != null ) return hols;

        long fromTS;

        Calendar cal = Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) );

        if ( _fromYYYYMMDD > 0 ) {
            CommonTimeUtils.yyyymmddToCalendar( cal, _fromYYYYMMDD );
            fromTS = cal.getTimeInMillis();
        } else {
            fromTS = ClockFactory.get().currentTimeMillis();
        }

        long uptoMS;

        if ( _uptoYYYYMMDD > 0 ) {
            CommonTimeUtils.yyyymmddToCalendar( cal, _uptoYYYYMMDD );
            uptoMS = cal.getTimeInMillis();
        } else {
            uptoMS = ClockFactory.get().currentTimeMillis() + (Constants.MS_IN_DAY * 365L); // add approx 1 year to now
        }

        int days = (int) TimeUnit.MILLISECONDS.toDays( uptoMS - fromTS );

        return synthesizeHols( code, fromTS, days );
    }

    public boolean isHoliday( ExchangeCode code, int yyyymmdd ) {

        final LinkedHashMap<Integer, HolidayEntry> hols = getHolidays( code );

        return hols.containsKey( yyyymmdd );
    }

    public boolean isHoliday( final int dateYYYYMMDD, final ExchDerivInstrument edi ) {
        return isHoliday( edi.getPrimaryExchangeCode(), dateYYYYMMDD ); // TODO add future series specific calendar
    }

    public void load( final String fileName ) {
        final List<String> lines = new ArrayList<>();

        try {
            FileUtils.read( lines, fileName, true, true );
        } catch( IOException e ) {
            throw new RuntimeDecodingException( "BatsHolidayLoader error loading file " + fileName + " : " + e.getMessage(), e );
        }

        String firstLine = lines.get( 0 );

        if ( firstLine.toLowerCase().contains( "date," ) ) {
            lines.remove( 0 );
        }

        Collections.sort( lines );

        genMicList( lines );

        int lastDay = 0;

        for ( String line : lines ) {
            if ( line.startsWith( "\"" ) || line.startsWith( "#" ) || line.startsWith( "date" ) ) {
                continue;
            }

            int cur = procLine( line );

            if ( cur != 0 && cur > lastDay ) lastDay = cur;
        }

        long now = ClockFactory.get().currentTimeMillis();
        long fromTS;

        Calendar cal = Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) );

        if ( lastDay == 0 ) {
            fromTS = now;
        } else {
            CommonTimeUtils.yyyymmddToCalendar( cal, lastDay );
            fromTS = cal.getTimeInMillis();
        }

        long uptoMS;

        if ( _uptoYYYYMMDD > 0 ) {
            CommonTimeUtils.yyyymmddToCalendar( cal, _uptoYYYYMMDD );
            uptoMS = cal.getTimeInMillis();
        } else {
            uptoMS = now + (Constants.MS_IN_DAY * 365L); // add approx 1 year to now
        }

        int days = (int) TimeUnit.MILLISECONDS.toDays( uptoMS - fromTS );

        // ensure we have synth hols for next year
        for ( ExchangeCode mic : _mics ) {
            synthesizeHols( mic, fromTS, days );
        }
    }

    public void reload() {
        reset();
        load( DEFAULT_HOL_FILE );
    }

    public void reset() {
        _hols.clear();
    }

    public void setFromYYYYMMDD( final int fromYYYYMMDD ) { _fromYYYYMMDD = _fromYYYYMMDD; }

    public void setUptoYYYYMMDD( final int uptoYYYYMMDD ) { _uptoYYYYMMDD = _uptoYYYYMMDD; }

    private void addHol( int date, String holName, String market, long openTimestamp, long endTimestamp, int endHHMMSS, ExchangeCode exchangeCode ) {
        HolidayEntry hol = new HolidayEntry();
        hol.setDate( date );
        hol.setHolidayName( holName.getBytes(), 0, holName.length() );
        hol.setMarket( market.getBytes(), 0, market.length() );
        hol.setOpenTime( openTimestamp );

        hol.setCloseTime( endTimestamp );
        hol.setMic( exchangeCode );
        hol.setCloseHHMMSS( endHHMMSS );

        addHoliday( hol );
    }

    private void addHoliday( final HolidayEntry hol ) {
        LinkedHashMap<Integer, HolidayEntry> hols = _hols.computeIfAbsent( hol.getMic(), ( k ) -> new LinkedHashMap<>() );
        hols.put( hol.getDate(), hol );
    }

    private void genMicList( final List<String> lines ) {
        /**
         * inefficient to parse twice but given file is small and only invoked once its fine
         */
        for ( String line : lines ) {
            line = line.trim();

            if ( line.startsWith( "\"" ) || line.startsWith( "#" ) || line.toLowerCase().startsWith( "date" ) ) {
                continue;
            }

            String[] parts = line.split( "," );

            String mic = parts[ 5 ];

            if ( mic.length() > 0 && !"*".equals( mic ) ) {
                ExchangeCode exchangeCode = null;
                try {
                    exchangeCode = ExchangeCode.getVal( new ViewString( mic ) );

                    _mics.add( exchangeCode );
                } catch( Exception e ) {
                    // ignore MIC's that are not configured
                }
            }
        }
    }

    private int procLine( final String line ) {
        String[] parts = line.split( "," );

        if ( parts.length != 6 ) {
            throw new RuntimeDecodingException( "HolidayLoader bad line format, expected 6 fields not " + parts.length + " [" + line + "]" );
        }

        int    date          = getDate( parts[ 0 ] );
        String holName       = parts[ 1 ];
        String market        = parts[ 2 ];
        long   openTimestamp = getTimestamp( date, parts[ 3 ] );
        long   endTimestamp  = getTimestamp( date, parts[ 4 ] );
        int    endHHMMSS     = getTimeHHMMSS( parts[ 4 ] );
        String mic           = parts[ 5 ];

        if ( "*".equals( mic ) ) {
            for ( ExchangeCode ec : _mics ) {
                addHol( date, holName, market, openTimestamp, endTimestamp, endHHMMSS, ec );
            }

        } else {
            ExchangeCode exchangeCode = null;
            try {
                exchangeCode = ExchangeCode.getVal( new ViewString( mic ) );
            } catch( Exception e ) {
                return date; // ignore MIC's that are not configured
            }

            addHol( date, holName, market, openTimestamp, endTimestamp, endHHMMSS, exchangeCode );
        }

        return date;
    }

    private LinkedHashMap<Integer, HolidayEntry> synthesizeHols( final ExchangeCode code, long fromTS, int days ) {

        // NOT YET IMPLEMENTED

        return _hols.get( code );
    }

}
