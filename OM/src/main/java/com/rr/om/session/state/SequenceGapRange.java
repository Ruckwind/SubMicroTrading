/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.state;

import com.rr.core.collections.IntHashSet;
import com.rr.core.collections.IntSet;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * SequenceGapRange - represents a gap fill request for missing messages
 * <p>
 * Detects subGaps within replay for requesting additional subGap fills
 * <p>
 * May have may children representing subGaps, the subGaps may themselves have subGaps so recursion is key
 * <p>
 * Each subGroup will be a subset of the parent
 * <p>
 * Will generate garbage ... assumption is the trading session will be colo and reliable-ish !
 */
public class SequenceGapRange {

    private static final Logger _log = LoggerFactory.create( SequenceGapRange.class );

    public final int _from;
    public final int _to;
    private final IntSet                 _ids;
    public int _nextExpSeqNum;
    private       List<SequenceGapRange> _children = new ArrayList<>();

    public SequenceGapRange( int from, int to ) {
        _from          = from;
        _to            = to;
        _nextExpSeqNum = _from;

        int size = (to - from) + 1;

        _ids = new IntHashSet( size, 1 );

        for ( int i = from; i <= to; ++i ) {
            _ids.add( i );
        }
    }

    public SequenceGapRange checkSubGap( int seqNum ) {
        if ( seqNum <= _nextExpSeqNum ) return null;

        for ( int i = 0; i < _children.size(); ++i ) {
            SequenceGapRange child = _children.get( i );

            SequenceGapRange gap = child.checkSubGap( seqNum ); // RECURSE

            if ( gap != null ) return gap;
        }

        _log.info( "SubGap detected, expected=" + _nextExpSeqNum + ", got=" + seqNum );

        SequenceGapRange gap = new SequenceGapRange( _nextExpSeqNum, seqNum - 1 );

        _children.add( gap );

        return gap;
    }

    public void clear() {
        _ids.clear();
        if ( _children.size() > 0 ) {
            for ( int i = 0; i < _children.size(); ++i ) {
                SequenceGapRange child = _children.get( i );
                child.clear();
            }

            _children.clear();
        }
    }

    public boolean hasSubGap( SequenceGapRange subGap ) {
        return _children.contains( subGap );
    }

    public boolean inRange( int val ) {
        return ((val >= _from) && (val <= _to || _to == 0));
    }

    /**
     * @return next expected seqNum ... or -1 if last seqNum processed (could still be open sub gaps!)
     */
    public int nextExpectedSeqNum() {
        return (_nextExpSeqNum == _to + 1) ? -1 : _nextExpSeqNum;
    }

    /**
     * key function, denotes receipt of a message with specified seqNum
     * <p>
     * increments nextExpected seqNum
     *
     * @param seqNum
     * @return true if seqNum was present in this (or childs) node list and has been removed
     */
    public boolean received( int seqNum ) {
        boolean processed = false;

        if ( inRange( seqNum ) ) {
            if ( _ids.remove( seqNum ) ) {               // remove the seqNum from this node
                if ( seqNum >= _nextExpSeqNum ) {        // dont reduce the nextExpSeqNum .. as this may be a seqNum from subGap of this node
                    _nextExpSeqNum = seqNum + 1;
                }

                processed = true;
            }
        }

        for ( int i = 0; i < _children.size(); ++i ) {
            SequenceGapRange child = _children.get( i );

            child.received( seqNum ); // recursively remove from child nodes
        }

        return processed;
    }

    public int size() {
        return _ids.size();
    }
}
