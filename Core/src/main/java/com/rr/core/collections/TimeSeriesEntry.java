package com.rr.core.collections;

import com.rr.core.model.PointInTime;
import com.rr.core.utils.Utils;

/**
 * represents a TimeSeriesEntry
 * <p>
 * entries in a time series that implement the OPTIONAL TimeSeriesEntry can get access to the series to which the entry belongs
 * <p>
 * furthermore the entries are doubly linked for easy navigation from latest to oldest
 *
 * @param <T>
 * @WARNING the time series entry can belong to only one timeseries .... if you need multie then create TimeSeriesEntry which hold the data that needs to be shared so hasA instead of isA
 */
public interface TimeSeriesEntry<T extends PointInTime> extends PointInTime {

    long getEndTimestamp();

    void setEndTimestamp( long endTimestamp );

    /**
     * @return the next in chain (next oldest) or NULL if none
     */
    T getNext();

    /**
     * @return the previous in chain (next youngest)
     */
    T getPrev();

    TimeSeries<T> getSeries();

    long getStartTimestamp();

    void setStartTimestamp( long startTimestamp );

    default boolean isActiveAt( long timestamp ) {
        return (Utils.isNull( timestamp ) ||
                (Utils.isNull( getStartTimestamp() ) || (getStartTimestamp() <= timestamp)) &&
                (Utils.isNull( getEndTimestamp() ) || (timestamp < getEndTimestamp())));
    }

    /**
     * set the next entry in the chain
     *
     * @param next
     * @return the old "next" value
     */
    T setNext( T next );

    T setPrev( T prev );
}
