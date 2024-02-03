package com.rr.core.model;

import com.rr.core.collections.TimeSeries;
import com.rr.core.lang.ReusableString;

/**
 * HistoricalExchangeInstrument
 * <p>
 * container for the history of an ExchangeInstrument
 * <p>
 * all ExchangeInstrument methods delegate to the latest instrument version
 * <p>
 * Note only major secdef changes warrant a new version ... trading status is NOT versioned
 */
public interface HistoricalExchangeInstrument<T extends ExchangeInstrument> extends ExchangeInstrument, TimeSeries<T> {

    void getKey( SecurityIDSource securityIDSource, long atInternalTimeMS, ReusableString dest );
}
