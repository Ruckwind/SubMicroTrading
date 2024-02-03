/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TimeUtilsFactory;
import com.rr.core.lang.ViewString;

import java.util.Calendar;

public class Auction {

    private static final ViewString AUCTION    = new ViewString( "Auction " );
    private static final ViewString START_TIME = new ViewString( ", start=" );
    private static final ViewString END_TIME   = new ViewString( ", end=" );

    public enum Type {Open, Intraday, Close, Null}

    private final Calendar _startCal;
    private final Calendar _endCal;
    private final Type     _type;

    private long _startTimeUTC;
    private long _endTimeUTC;

    public Auction( Calendar startCal, Calendar endCal, Type type ) {

        _startCal = startCal;
        _endCal   = endCal;
        _type     = type;

        setToday();
    }

    public long getEndTime() {
        return _endTimeUTC;
    }

    public final Calendar getEndTimeCalendar() {
        return _endCal;
    }

    public long getStartTime() {
        return _startTimeUTC;
    }

    public final Calendar getStartTimeCalendar() {
        return _startCal;
    }

    public Type getType() {
        return _type;
    }

    public boolean isIn( long timeUTC ) {
        return timeUTC >= _startTimeUTC && timeUTC <= _endTimeUTC;
    }

    public void setHalfDay( long closeTime ) {

        long auctionLengthMS = _endTimeUTC - _startTimeUTC;

        if ( _startCal != null ) {
            _startTimeUTC = closeTime - auctionLengthMS;
        }

        if ( _endCal != null ) {
            _endTimeUTC = closeTime;
        }
    }

    public void setToday() {

        if ( _startCal != null ) {
            Calendar c = TimeUtilsFactory.safeTimeUtils().getCalendar( _startCal.getTimeZone() );
            _startCal.set( c.get( Calendar.YEAR ),
                           c.get( Calendar.MONTH ),
                           c.get( Calendar.DAY_OF_MONTH ) );
            _startTimeUTC = _startCal.getTimeInMillis();
        } else {
            _startTimeUTC = 0;
        }

        if ( _endCal != null ) {
            Calendar c = TimeUtilsFactory.safeTimeUtils().getCalendar( _endCal.getTimeZone() );
            _endCal.set( c.get( Calendar.YEAR ),
                         c.get( Calendar.MONTH ),
                         c.get( Calendar.DAY_OF_MONTH ) );
            _endTimeUTC = _endCal.getTimeInMillis();
        } else {
            _endTimeUTC = 0;
        }
    }

    public ReusableString toString( ReusableString s ) {
        s.append( AUCTION ).append( _type );
        s.append( START_TIME );
        TimeUtilsFactory.safeTimeUtils().unixTimeToShortLocal( s, _startTimeUTC );
        s.append( END_TIME );
        TimeUtilsFactory.safeTimeUtils().unixTimeToShortLocal( s, _endTimeUTC );
        return s;
    }
}
