/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.state;

public interface SequenceNumGapMgr {

    /**
     * add gap fill request
     *
     * @param from - from seqNum
     * @param to   - upto and including seqNum, 0 = all
     * @return true if new sequence added
     */
    boolean add( int from, int to );

    boolean anyPendingSequenceGaps();

    /**
     * if seqNum is in current gap request and denotes a gap within a gap (ie a subgap)
     * <p>
     * then create a subgap and attach to current gap
     *
     * @param seqNum
     * @return
     */
    SequenceGapRange checkSubGap( int seqNum );

    void clear();

    /**
     * set current gap being processed
     *
     * @param gap
     */
    void gapFillRequested( SequenceGapRange gap );

    /**
     * @return true if gapFill currently set
     */
    boolean inGapRequest();

    /**
     * @return next range to request or null if none left
     */
    SequenceGapRange next();

    /**
     * @return next expected seqNum in gap, or -1 if not in gap or last seqNum processed (could be open child gaps still)
     */
    int nextExpectedSeqNum();

    int pending();

    /**
     * @return number of enqueued gap requests ... excludes active gap request
     */
    int queuedGaps();

    boolean received( int seq );

}