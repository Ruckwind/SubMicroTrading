/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;

/**
 * interface to represent a tradeable instrument on an exchange
 * <p>
 * key is association with exchange and ccy and standard identifiers
 */
public interface ExchangeInstrument extends Instrument {

    @Override ZString getSecurityDesc();

    /**
     * @return number of book levels for instrument, or 0 if unknown
     */
    int getBookLevels();

    /**
     * @return CommonInstrument or NULL if none
     */
    CommonInstrument getCommonInstrument();

    /**
     * @return the version end timestamp
     */
    long getEndTimestamp();

    Exchange getExchange();

    /**
     * @return the exchange specific long identifier for the Instrument IF it has one, otherwise Constants.UNSET_LONG
     */
    long getExchangeLongId();

    /**
     * different instrument segments can be traded at different times
     *
     * @return exchange session appropriate for the instrument
     */
    ExchangeSession getExchangeSession();

    /**
     * @return ISIN of instrument of null if unknown
     */
    ZString getISIN();

    /**
     * get the requested instrument event, note implementation uses linked list
     *
     * @param idx event index into list
     * @return
     */
    InstrumentEvent getInstrumentEvent( int idx );

    /**
     * @return the integer segment the instrument is assigned to (or 0 if none)
     * @TODO refactor intSegment out of instrument ... use decorator pattern
     * <p>
     * aka channel
     */
    int getIntSegment();

    void getKey( SecurityIDSource securityIDSource, ReusableString dest );

    String getKey( SecurityIDSource securityIDSource );

    int getMinQty();

    /**
     * @return number of instrument events
     */
    int getNumEvents();

    int getSecurityGroupId();

    ViewString getSecurityID();

    SecurityIDSource getSecurityIDSource();

    /**
     * @return the version start timestamp
     */
    long getStartTimestamp();

    /**
     * @return the tickscale can be fixed or banded
     */
    TickType getTickType();

    /**
     * @return the unit of measure or null if unknown
     */
    UnitOfMeasure getUnitOfMeasure();

    double getUnitOfMeasureQuantity();

    /**
     * @return valid trading range for this stock assuming price tolerance is running
     */
    TradingRange getValidTradingRange();

    /**
     * @param source
     * @return true if the source object MAY change status of the instrument
     */
    boolean hasChanged( Object source );

    /**
     * @return true if the instrument has been marked as deleted from Store
     */
    boolean isDeleted();

    /**
     * @return true if the instrument is enabled for trading
     */
    boolean isEnabled();

    void setEnabled( boolean isEnabled );

    boolean isFlagSet( MsgFlag flag );

    /**
     * @return true if this instrument is a test instrument at the exchange
     */
    boolean isTestInstrument();
}
