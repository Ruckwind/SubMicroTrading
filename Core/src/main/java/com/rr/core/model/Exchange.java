/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;
import com.rr.core.tasks.ScheduledEvent;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Represents a Market / Exchange identifier is the MIC
 * <p>
 * Use the ExchangeSession to get access to holidays etc
 */
public interface Exchange {

    /**
     * generate an appropriate clOrdId for the marketside order
     *
     * @param dest          - the reusable string to hold the mkt clOrdId
     * @param clientClOrdId - client clOrdId will be used as market clOrdId if allowed by exchange
     */
    void generateMarketClOrdId( ReusableString dest, ZString clientClOrdId );

    Enricher getEnricher();

    ExchangeCode getExchangeCode();

    ExchangeValidator getExchangeEventValidator();

    ScheduledEvent getExchangeResetEvent();

    /**
     * some exchanges have different trading segments for different markets
     *
     * @param marketSegment
     * @return
     */
    ExchangeSession getExchangeSession( ZString marketSegment );

    /**
     * @return the exchange state now
     */
    ExchangeState getExchangeState();

    long getExpireTimeToSendEndOfDayEvents();

    /**
     * @return the unique sequential identifier for this exchange
     */
    int getId();

    Calendar getResetTime();

    ExchangeSession getSession();

    TimeZone getTimeZone();

    boolean isExchangeAnMTF();

    /**
     * @return true if the exchange symbol is a long identifier
     */
    boolean isExchangeSymbolLongId();

    boolean isGeneratedExecIDRequired();

    boolean isPrimaryRICRequired();

    boolean isSendCancelToExchangeAtEOD();

    /**
     * @return true if the exchange requires trade busts / corrects to be supported
     * this requires execId/qty/price for all fills to be kept
     */
    boolean isTradeCorrectionSupported();

    /**
     * some exchanges like ENX require the instrument and possbly side to make unique
     *
     * @param execIdForUpdate
     * @param execId
     * @param inst
     */
    void makeExecIdUnique( ReusableString execIdForUpdate, ZString execId, Instrument inst );

    /**
     * @param resetTime     - next reset time
     * @param exchangeReset
     */
    void setResetTime( Calendar resetTime, final ScheduledEvent exchangeReset );

    /**
     * write the exchange info into the supplied buf in a readable logger format
     *
     * @param buf
     * @return
     */
    ReusableString toString( ReusableString buf );
}
