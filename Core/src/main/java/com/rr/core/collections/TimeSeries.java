package com.rr.core.collections;

import com.rr.core.model.PointInTime;

import java.util.Iterator;

/**
 * represents a TimeSeries
 * <p>
 * used to hold versioned instances of type T through time
 * <p>
 * only one instance of T may be active ... ie have UNSET_LONG endTimestamp
 * <p>
 * iterator() iterates from the newest to the oldest
 * <p>
 * when iterating through time series you shoudl synchronise on the TimeSeries instance
 * <p>
 * even if your code is single threaded you need to protect other threads that may maintain the data ... eg instrument updates.
 *
 * @param <T>
 */
public interface TimeSeries<T extends PointInTime> extends Iterable<T> {

    /**
     * add new entry to the series at PointInTime identified within entry
     * <p>
     * if the new entry is active (ie endTimestamp is null) then set the previous active entry to have endTime which is same as the startTime of the new entry
     *
     * @param entry
     * @return true if item added ... ie was not already in series
     */
    boolean add( T entry );

    T getAt( long timeMS );

    /**
     * @return the newest / latest version
     */
    T latest();

    /**
     * @return a reverse iterator from oldest entry to latest entry
     */
    Iterator<T> oldestToNewestIterator();

    int size();

}
