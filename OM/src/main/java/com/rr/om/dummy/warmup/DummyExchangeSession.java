/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.dummy.warmup;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.model.Auction;
import com.rr.core.model.ExchangeSession;
import com.rr.core.model.ExchangeState;
import com.rr.core.model.HolidayEntry;

import java.util.Calendar;
import java.util.LinkedHashMap;

public class DummyExchangeSession implements ExchangeSession {

    private final ZString _id   = new ViewString( "DummyExSession" );
    private       long    _open = 0;

    @Override public ReusableString dump( ReusableString buf ) {
        buf.append( "DummySession" );
        return buf;
    }

    @Override public Auction getCloseAuction() {
        return null;
    }

    @Override public long getCloseTime() {
        return 0;
    }

    @Override public long getContinuousEndTime()   { return 0; }

    @Override public long getContinuousStartTime() { return 0; }

    @Override public ExchangeSession getExchangeSession( ZString marketSegment ) {
        return this;
    }

    @Override public ExchangeState getExchangeStateAt( final Calendar dateTime ) { return ExchangeState.Continuous; }

    @Override public ExchangeState getExchangeStateNow() {
        return ExchangeState.Continuous;
    }

    @Override public ExchangeState getExchangeStateToday( long time )            { return ExchangeState.Continuous; }

    @Override public long getHalfDayCloseTime() {
        return 0;
    }

    @Override public ZString getId() {
        return _id;
    }

    @Override public Auction getIntradayAuction() {
        return null;
    }

    @Override public Auction getOpenAuction() {
        return null;
    }

    @Override public long getOpenTime() {
        return 0;
    }

    @Override public boolean isHalfDay()                                                     { return false; }

    @Override public void setHalfDay( boolean isHalfDay )                                    { /* nothing */ }

    @Override public boolean isHalfDayAt( final Calendar date )                  { return true; }

    @Override public boolean isOpen()                                            { return true; }

    @Override public void setOpen( long openTimeUTC )                                        { _open = openTimeUTC; }

    @Override public boolean isOpenAt( final Calendar dateTime )                 { return true; }

    @Override public boolean isOpenToday( long time ) {
        return time > _open;
    }

    @Override public void setHolidays( final LinkedHashMap<Integer, HolidayEntry> holidays ) { /* nothing */ }

    @Override public void setToday()                                                         { /* nothing */ }
}
