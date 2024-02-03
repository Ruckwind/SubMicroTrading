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
public interface InstrumentWrite extends Instrument {

    void setLoanAvailability( LoanAvailability loanAvailability );

    void setTradeRestriction( TradeRestriction tradeRestriction );
}
