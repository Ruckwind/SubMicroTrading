/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;

import java.util.Calendar;
import java.util.LinkedHashMap;

/**
 * session will either
 * <p>
 * a multi segment session, ie trading day split into multiple sessions
 * a single session
 * a seperate session per market segment
 */
public interface ExchangeSession {

    ReusableString dump( ReusableString buf );

    Auction getCloseAuction();

    long getCloseTime();

    long getContinuousEndTime();

    long getContinuousStartTime();

    ExchangeSession getExchangeSession( ZString marketSegment );

    /**
     * get the exchange state for a particular time
     *
     * @param dateTime the date/time to check in local time of exchange
     * @WARNING only historic dates added to holiday calendar will be used
     */
    ExchangeState getExchangeStateAt( Calendar dateTime );

    /**
     * @return the exchange state now
     * @NOTE quick, suitable for per order call in ULL strats
     */
    ExchangeState getExchangeStateNow();

    /**
     * @param todayUnixTimeMS Saves the overhead of calling Clock.get().currentTimeMS()
     * @return the exchange state at todayUnixTimeMS for today
     * @WARNING dont use this method with a time which is NOT today ... not future or historic !
     */
    ExchangeState getExchangeStateToday( long todayUnixTimeMS );

    long getHalfDayCloseTime();

    ZString getId();

    Auction getIntradayAuction();

    Auction getOpenAuction();

    long getOpenTime();

    boolean isHalfDay();

    void setHalfDay( boolean isHalfDay );

    /**
     * @param date the date to check
     * @return true if the exchange has a half day on the supplied date
     * @WARNING only historic dates added to holiday calendar will be used
     */
    boolean isHalfDayAt( Calendar date );

    /**
     * @return true if exchange session open now
     */
    boolean isOpen();

    void setOpen( long openTimeUTC );

    /**
     * @param dateTime the date/time to check in local time of exchange
     * @return true if the exchange is open at the supplied date/time
     * @WARNING only historic dates added to holiday calendar will be used
     */
    boolean isOpenAt( Calendar dateTime );

    /**
     * @param todayUnixTimeMS
     * @return if the exchange is open today at the specified time
     * @WARNING dont use this method with a time which is NOT today ... not future or historic !
     * <p>
     * Saves the overhead of calling Clock.get().currentTimeMS()
     */
    boolean isOpenToday( long todayUnixTimeMS );

    void setHolidays( LinkedHashMap<Integer, HolidayEntry> holidays );

    void setToday();
}
