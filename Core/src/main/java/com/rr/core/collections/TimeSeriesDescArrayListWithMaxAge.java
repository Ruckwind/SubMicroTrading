package com.rr.core.collections;

import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.Constants;
import com.rr.core.model.PointInTime;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.Utils;

import java.util.Iterator;

/**
 * represents a TimeSeries in an arraylist .. non threadsafe
 * <p>
 * used to hold versioned instances of type T through time from newest to oldest
 *
 * @param <T> holds entries in list from newest to oldest
 *            <p>
 *            has a max age threshold, when adding new entries will remove any entries older than max days allowed.
 *            <p>
 *            suitable for use where
 *            a) dont have much change over time such as instruments, not suitable for market data as unbounded and mutation is relatively expensive due to array shifting
 *            b) are accessed mainly for latest version, random access getAt is O(logger N) as bin chops list
 *            c) addition is assumed to be in order and will be inserted at front of list .. if not will search from start to find proper insertion point
 *            currently all uses enforce time ordering so wont be issue
 */
public class TimeSeriesDescArrayListWithMaxAge<T extends PointInTime> implements TimeSeries<T> {

    /**
     * on backtest bars are decoded on seperate thread and clock is not in sync with pipeline so must not verify not forward
     */

    private static final int DEFAULT_SIZE = 2;
    private final SMTArrayList<T> _entries;
    private long _maxAgeMS = Constants.UNSET_LONG;

    public TimeSeriesDescArrayListWithMaxAge() { _entries = null; /* for reflection only */ }

    public TimeSeriesDescArrayListWithMaxAge( int initSize, int maxAgeDays ) {
        _entries = new SMTArrayList<>( initSize );

        _maxAgeMS = maxAgeDays * Constants.MS_IN_DAY;
    }

    @Override public boolean add( final T newEntry ) {

        boolean added = doAdd( newEntry );

        if ( Utils.hasVal( _maxAgeMS ) ) {

            long minTS = ClockFactory.get().currentTimeMillis() - _maxAgeMS;

            final int size = _entries.size();

            int endRange = size;
            int i        = size - 1;
            int oldCnt   = 0;

            while( i >= 0 ) {
                final T curEntry = _entries.get( i );

                if ( curEntry.getEventTimestamp() >= minTS ) {
                    break;
                }

                ++oldCnt;

                --i;
            }

            if ( oldCnt > 0 ) {
                _entries.removeRange( endRange - oldCnt, endRange );
            }
        }

        return added;
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

    @Override public T latest() {
        final int size = _entries.size();

        if ( size == 0 ) return null;

        return (Utils.useClockForLatest() ? getAt( ClockFactory.get().currentTimeMillis() ) : _entries.get( 0 ));
    }

    @Override public Iterator<T> oldestToNewestIterator() { return new ReverseIterator(); }

    @Override public int size() {
        return _entries.size();
    }

    @Override public Iterator<T> iterator()               { return new ForwardIterator(); }

    private boolean doAdd( final T newEntry ) {

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
