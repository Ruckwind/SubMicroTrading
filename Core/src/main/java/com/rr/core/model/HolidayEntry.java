package com.rr.core.model;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.core.lang.*;
import com.rr.core.utils.Utils;

@SuppressWarnings( { "unused", "override" } )

public final class HolidayEntry {

    // Attrs

    private final ReusableString _holidayName = new ReusableString( 15 );
    private final ReusableString _market      = new ReusableString( 12 );
    private       int            _date        = Constants.UNSET_INT;
    private       long           _openTime    = Constants.UNSET_LONG;
    private       long           _closeTime   = Constants.UNSET_LONG;
    private       int            _msgSeqNum   = Constants.UNSET_INT;
    private       int            _closeHHMMSS = Constants.UNSET_INT;

    private ExchangeCode _mic;

    private int     _flags     = 0;
    private boolean _isHalfDay = false;

    @Override
    public String toString() {
        ReusableString buf = TLC.instance().pop();
        dump( buf );
        String rs = buf.toString();
        TLC.instance().pushback( buf );
        return rs;
    }

    public final void dump( final ReusableString out ) {
        out.append( "HolidayEntryImpl" ).append( ' ' );
        if ( Constants.UNSET_INT != getDate() && 0 != getDate() ) out.append( ", date=" ).append( getDate() );
        if ( getHolidayName().length() > 0 ) out.append( ", holidayName=" ).append( getHolidayName() );
        if ( getMarket().length() > 0 ) out.append( ", market=" ).append( getMarket() );
        if ( Constants.UNSET_LONG != getOpenTime() && 0 != getOpenTime() ) {
            out.append( ", openTime=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getOpenTime() );
        }
        if ( Constants.UNSET_LONG != getCloseTime() && 0 != getCloseTime() ) {
            out.append( ", closeTime=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getCloseTime() );
        }
        if ( getMic() != null ) out.append( ", mic=" );
        if ( getMic() != null ) out.append( getMic().id() );
        if ( Constants.UNSET_INT != getMsgSeqNum() && 0 != getMsgSeqNum() ) out.append( ", msgSeqNum=" ).append( getMsgSeqNum() );
    }

    public int getCloseHHMMSS()                         { return _closeHHMMSS; }

    public void setCloseHHMMSS( final int closeHHMMSS ) { _closeHHMMSS = closeHHMMSS; }

    public final long getCloseTime()                                    { return _closeTime; }

    public final void setCloseTime( long val ) {
        if ( !Utils.isNull( val ) ) {
            _isHalfDay = true;
            _closeTime = val;
        }
    }

    // Getters and Setters
    public final int getDate() { return _date; }

    public final void setDate( int val )                                { _date = val; }

    public final ViewString getHolidayName()                            { return _holidayName; }

    public final ReusableString getHolidayNameForUpdate()               { return _holidayName; }

    public final ViewString getMarket()                                 { return _market; }

    public final ReusableString getMarketForUpdate()                    { return _market; }

    public final ExchangeCode getMic()                  { return _mic; }

    public final void setMic( ExchangeCode val )        { _mic = val; }

    public final int getMsgSeqNum()                     { return _msgSeqNum; }

    public final void setMsgSeqNum( int val )           { _msgSeqNum = val; }

    public final long getOpenTime()                                     { return _openTime; }

    public final void setOpenTime( long val )                           { _openTime = val; }

    public boolean isHalfDay() {
        return _isHalfDay;
    }

    public final void setHolidayName( byte[] buf, int offset, int len ) { _holidayName.setValue( buf, offset, len ); }

    public final void setMarket( byte[] buf, int offset, int len )      { _market.setValue( buf, offset, len ); }
}
