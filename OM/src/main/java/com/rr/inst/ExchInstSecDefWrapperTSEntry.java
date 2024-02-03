/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.inst;

import com.rr.core.collections.TimeSeriesEntry;

/**
 * Exchange Instrument Security Definition Wrapper Time Series Entry
 */
public interface ExchInstSecDefWrapperTSEntry extends HistExchInst, TimeSeriesEntry<ExchInstSecDefWrapperTSEntry> {

    /**
     * a instSecDefWrapper in a time series allowing navigation in series
     */

    @Override HistExchInstSecDefWrapperTS getSeries();
}
