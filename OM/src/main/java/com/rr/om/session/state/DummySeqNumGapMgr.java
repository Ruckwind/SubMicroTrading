/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.state;

public class DummySeqNumGapMgr implements SequenceNumGapMgr {

    @Override
    public SequenceGapRange next() {
        return null;
    }

    @Override
    public boolean anyPendingSequenceGaps() {
        return false;
    }

    @Override
    public boolean add( int from, int too ) {
        return false;
    }

    @Override
    public void clear() {
        // nothing
    }

    @Override
    public void gapFillRequested( SequenceGapRange gap ) {
        // nothing
    }

    @Override
    public boolean received( int seq ) {
        return false;
    }

    @Override
    public int pending() {
        return 0;
    }

    @Override
    public int queuedGaps() {
        return 0;
    }

    @Override
    public boolean inGapRequest() {
        return false;
    }

    @Override
    public SequenceGapRange checkSubGap( int seqNum ) {
        return null;
    }

    @Override
    public int nextExpectedSeqNum() {
        return -1;
    }
}
