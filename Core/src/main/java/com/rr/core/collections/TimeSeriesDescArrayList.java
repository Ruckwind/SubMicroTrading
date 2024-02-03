package com.rr.core.collections;

import com.rr.core.lang.ClockFactory;
import com.rr.core.model.PointInTime;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * represents a TimeSeries in an arraylist .. non threadsafe
 * <p>
 * used to hold versioned instances of type T through time from newest to oldest
 *
 * @param <T> holds entries in list from newest to oldest
 *            <p>
 *            suitable for use where
 *            a) dont have much change over time such as instruments, not suitable for market data as unbounded and mutation is relatively expensive due to array shifting
 *            b) are accessed mainly for latest version, random access getAt is O(logger N) as bin chops list
 *            c) addition is assumed to be in order and will be inserted at front of list .. if not will search from start to find proper insertion point
 *            currently all uses enforce time ordering so wont be issue
 */
public class TimeSeriesDescArrayList<T extends PointInTime> implements TimeSeries<T> {

    private static final int DEFAULT_SIZE = 2;

    private final ArrayList<T> _entries;

    public TimeSeriesDescArrayList()           { this( DEFAULT_SIZE ); }

    public TimeSeriesDescArrayList( int size ) { _entries = new ArrayList<>( size ); }

    @Override public Iterator<T> iterator()               { return new ForwardIterator(); }

    @Override public T latest() {
        final int size = _entries.size();

        if ( size == 0 ) return null;

        T i = _entries.get( 0 );

        if ( Utils.useClockForLatest() ) {
            T ti = getAt( ClockFactory.get().currentTimeMillis() );

            if ( ti != null ) { // backtest hack to avoid null ptr
                i = ti;
            }
        }

        return i;
    }

    /**
     * use binary chop if entry required is not the first entry in array
     *
     * @param timeMS
     * @return
     */
    @Override public T getAt( final long timeMS ) {

        if ( Utils.isNull( timeMS ) || timeMS == 0 ) return _entries.get( 0 );

        final int size = _entries.size();

        if ( size == 0 ) return null;

        final T firstEntry = _entries.get( 0 );

        if ( timeMS >= firstEntry.getEventTimestamp() ) {
            return firstEntry;
        }

        int low  = 1;
        int high = size - 1;

        int minLowIdx = -1;

        while( low <= high ) {

            final int midIdx = (high + low) / 2;

            final T    entry     = _entries.get( midIdx );
            final long entryTime = entry.getEventTimestamp();
            final int  cmp       = Long.compare( timeMS, entryTime );

            if ( cmp > 0 ) { // time > currentEntry ... so bring Low bound to current idx
                high      = midIdx - 1;
                minLowIdx = midIdx;
            } else if ( cmp < 0 ) {
                low = midIdx + 1;
            } else {
                return entry;
            }
        }

        if ( minLowIdx != -1 ) {
            final T entry = _entries.get( minLowIdx );
            return entry;
        }

        return null;
    }

    @Override public int size() {
        return _entries.size();
    }

    @Override public boolean add( final T newEntry ) {

        final long eventTime = newEntry.getEventTimestamp();

        if ( Utils.isNull( eventTime ) ) {
            throw new SMTRuntimeException( "TimeSeriesArrayList must be used with entries with getEventTimestamp representing time of creation/activation not null" );
        }

        final int size = _entries.size();

        for ( int i = 0; i < size; i++ ) {
            final T curEntry = _entries.get( i );

            final long cmpTS = eventTime - curEntry.getEventTimestamp();
            if ( cmpTS > 0 ) { // eventTime has higher ms count so is the later
                _entries.add( i, newEntry );
                return true;
            }
            if ( cmpTS == 0 && newEntry == curEntry ) return false;
        }

        _entries.add( newEntry );

        return true;
    }

    @Override public Iterator<T> oldestToNewestIterator() { return new ReverseIterator(); }

    private class ReverseIterator implements Iterator<T> {

        private int _nextIdx;

        public ReverseIterator() { _nextIdx = _entries.size() - 1; }

        @Override public boolean hasNext() {
            return _nextIdx >= 0;
        }

        @Override public T next() { return _entries.get( _nextIdx-- ); }
    }

    private class ForwardIterator implements Iterator<T> {

        private int _nextIdx;

        public ForwardIterator() {
            _nextIdx = 0;
        }

        @Override public boolean hasNext() {
            return _nextIdx < _entries.size();
        }

        @Override public T next() { return _entries.get( _nextIdx++ ); }
    }
}


