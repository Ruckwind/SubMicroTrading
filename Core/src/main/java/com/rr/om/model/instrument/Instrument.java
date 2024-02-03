/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.model.instrument;

import com.rr.core.lang.ZString;
import com.rr.core.model.*;

@Deprecated
public interface Instrument extends ExchangeInstrument {

    // trading currency and settlement currency
    @Override Currency getCurrency();

    // different id's
    @Override ZString getSymbol();

    @Override Exchange getExchange();

    @Override ZString getISIN();

    @Override TickType getTickType();

    ZString getCountry();

    ZString getCusip();

    // order must be multiple of lot size (for some exchanges)
    long getLotSize();

    // market / segment and sector
    ZString getMarket();

    ZString getMarketSector();

    ZString getMarketSegment();

    // price tolerance
    PriceToleranceLimits getPriceToleranceLimits();

    // id lookup for fix
    ZString getSecurityID( SecurityIDSource idsource );

    Currency getSettlementCurrency();

    // boolean getters
    boolean isPrimaryInstrument();
}
