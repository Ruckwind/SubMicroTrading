/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.inst;

import com.rr.core.collections.TimeSeriesEntry;

/**
 * Exchange Instrument Security Definition Wrapper Time Series Entry
 */
public interface HistExchInst extends InstrumentSecurityDefWrapper {

    /**
     * a instSecDefWrapper in a time series allowing navigation in series
     */

    HistExchInstSecDefWrapperTS getSeries();
}
