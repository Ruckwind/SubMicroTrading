package com.rr.core.collections;

import com.rr.core.lang.ReusableString;
import com.rr.core.utils.SMTRuntimeException;

/**
 * return the largest value in sliding window of specified size
 */
public class MaxLongValueInSlidingWindow {

    private static final long UNSET = -1;

    private static final class Node {

        long _value;
        long _streamEntry;

        public Node() {
            _value       = 0;
            _streamEntry = UNSET;
        }

        public void dump( ReusableString sb ) {
            sb.append( "_value=" ).append( _value ).append( ", _streamIdx=" ).append( _streamEntry );
        }
    }

    private final Node[] _values;

    // dont use ArrayDeque as want to avoid any temp object creation, should push logic for non growable deque into seperate class

    private long _streamIdx = 0;
    private long _headIdx   = 0;
    private long _tailIdx   = 0;

    private int _maxWindow;

    private long _idxMask;

    public MaxLongValueInSlidingWindow( int windowSize ) {

        int size = 1;

        while( size < windowSize ) {
            size <<= 1;
        }

        _idxMask = size - 1;

        if ( (int) _idxMask < 0 ) throw new SMTRuntimeException( "WindowSize " + windowSize + " too big" );

        _maxWindow = windowSize;

        _values = new Node[ size ]; // some memory wasted by pow2, but cost is worth it, sliding window is retricted to requested windowSize
        for ( int i = 0; i < size; i++ ) {
            _values[ i ] = new Node();
        }
    }

    @Override
    public String toString() {
        ReusableString sb = new ReusableString();
        dump( sb );
        return sb.toString();
    }

    public int activeEntries() {
        return (int) (_tailIdx - _headIdx);
    }

    /**
     * return the maximum value in the sliding window including the new value being added
     *
     * @param nextVal
     * @return max in window
     */
    public long add( long nextVal ) {

        long maxInWindow = nextVal;

        ++_streamIdx;

        long oldestAllowedStreamIdx = _streamIdx - _maxWindow;

        for ( long i = _headIdx; i < _tailIdx; i++ ) {
            int  idx = (int) (i & _idxMask);
            Node n   = _values[ idx ];
            if ( n._streamEntry <= oldestAllowedStreamIdx ) {
                ++_headIdx;
                // if we wanted to we could clean the value/streamIdx but we trust the _headIdx/_tailIdx so dont
            } else {
                if ( n._value > maxInWindow ) maxInWindow = n._value;

                break; // hit a node which is younger than the oldest allowed
            }
        }

        // can delete any older entries with values lower than the nextVal
        for ( long i = _tailIdx - 1; i >= _headIdx; --i ) {
            int  idx    = (int) (i & _idxMask);
            long curVal = _values[ idx ]._value;
            if ( curVal <= nextVal ) {
                --_tailIdx;
                // if we wanted to we could clean the value/streamIdx but we trust the _headIdx/_tailIdx so dont
            } else {
                break; // list is in desc order hit a number bigger than this so stop
            }
        }

        int  idx = (int) (_tailIdx & _idxMask);
        Node n   = _values[ idx ];

        n._streamEntry = _streamIdx;
        n._value       = nextVal;

        ++_tailIdx;

        return maxInWindow;
    }

    public void dump( ReusableString sb ) {
        sb.append( "_headIdx=" ).append( _headIdx ).append( ", _tailIdx=" ).append( _tailIdx ).append( " : " );

        for ( long i = _headIdx; i < _tailIdx; i++ ) {
            int idx = (int) (i & _idxMask);
            if ( i > _headIdx ) {
                sb.append( ", " );
            }
            _values[ idx ].dump( sb );
        }
    }

    public long getEntry( int logicalEntryIdx ) {

        int idx = (int) ((_headIdx + logicalEntryIdx) & _idxMask);

        if ( idx > _tailIdx ) throw new RuntimeException( "Invalid index " + logicalEntryIdx );

        return _values[ idx ]._value;
    }
}

