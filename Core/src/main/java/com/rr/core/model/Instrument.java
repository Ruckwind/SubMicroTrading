/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;

/**
 * interface to represent a tradeable instrument or a synthetic instrument such as Generic Future
 * <p>
 * key is association with exchange and ccy and standard identifiers
 */
public interface Instrument extends PointInTime, Identifiable {

    long    DUMMY_INSTRUMENT_LONG_ID = 0;
    ZString DUMMY_INSTRUMENT_ID      = new ViewString( Long.toString( DUMMY_INSTRUMENT_LONG_ID ) );

    void dump( ReusableString out );

    Currency getCurrency();

    /**
     * Ticket symbol, identified by 22=8, 48=tickerSymbol OR if not present then tag55
     *
     * @return
     * @TODO move method to ExchangeSymbol ... but that requires alot of refactoring, also need to revisit the need for getSymbol and getExchangeSymbol as not consistent atm
     */
    ZString getExchangeSymbol();

    /**
     * @return true if the instrument is restricted
     */
    default LoanAvailability getLoanAvailability() { return null; }

    default ExchangeCode getPrimaryExchangeCode() { return getSecurityExchange(); }

    ZString getSecurityDesc();

    /**
     * @return by default strategy instrument is not tied to single exchange
     */
    default ExchangeCode getSecurityExchange() { return null; }

    ;

    default ZString getSecurityGroup() { return null; }

    ;

    SecurityType getSecurityType();

    /**
     * symbol, identified by tag55 ... not necessarily unique, depends on inst source
     *
     * @return
     */
    ZString getSymbol();

    ;

    default TradeRestriction getTradeRestriction() { return null; }

    ;

    /**
     * @return the uniqueInstId or UNSET_LONG if instrument doesnt have one (eg not FX/FUT/GFUT/EQUITYY)
     */
    long getUniqueInstId();

    boolean isDead();
}
