package com.rr.core.collections;

import com.rr.core.model.PointInTime;

/**
 * provide factory methods for the most used use cases
 */
public class TimeSeriesFactory {

    /**
     * create an unbounded time series that rarely changes and access is mostly at latest versions
     * <p>
     * NOT ThreadSafe
     *
     * @param initSize
     * @param <T>
     * @return
     */
    public static <T extends PointInTime> TimeSeries<T> createUnboundedSmallSeries( int initSize ) {
        return new TimeSeriesDescArrayList<>( initSize );
    }
}
