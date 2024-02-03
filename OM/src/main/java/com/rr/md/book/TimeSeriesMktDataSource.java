/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.book;

import com.rr.core.collections.TimeSeries;
import com.rr.core.model.InstRefData;
import com.rr.core.model.MktDataSrc;

/**
 * source for time series inst related lva
 * <p>
 * T implements TimeSeries which in general will be a rolling set of MaxTimeSeriesEntries
 * <p>
 * pipeLineId is a logical pipe representing market data sessions + book source ... its used for load balancing, either round robin or by specific pipe
 * <p>
 * init or prepare method should load the start-end time interval of lva
 */
public interface TimeSeriesMktDataSource<T extends InstRefData & TimeSeries> extends MktDataSrc<T> {

    long endTimeInternal();

    int getMaxTimeSeriesEntries();

    long getStartTimeInternal();
}
