/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.state;

import com.rr.core.lang.BaseTestCase;
import org.junit.Test;

import static org.junit.Assert.*;

public class BatchedSeqNumGapMgrTest extends BaseTestCase {

    private BatchedSeqNumGapMgr _mgr = new BatchedSeqNumGapMgr();

    @Test
    public void testGapInGap() {
        assertEquals( 0, _mgr.pending() );

        assertTrue( _mgr.add( 10, 13 ) );

        SequenceGapRange gap = _mgr.next();
        checkGap( gap, 10, 13 );

        _mgr.gapFillRequested( gap );

        assertEquals( 4, _mgr.pending() );

        assertEquals( 10, _mgr.nextExpectedSeqNum() );

        SequenceGapRange subGap = _mgr.checkSubGap( 10 );
        assertNull( subGap );
        assertTrue( _mgr.received( 10 ) );
        assertEquals( 3, _mgr.pending() );

        subGap = _mgr.checkSubGap( 13 );
        assertNotNull( subGap );
        assertTrue( gap.hasSubGap( subGap ) );

        assertEquals( 11, subGap._from );
        assertEquals( 12, subGap._to );

        assertEquals( 3, _mgr.pending() );
        assertTrue( _mgr.received( 13 ) );
        assertEquals( 2, _mgr.pending() );

        assertEquals( -1, _mgr.nextExpectedSeqNum() );
        assertEquals( -1, gap.nextExpectedSeqNum() );
        assertEquals( 11, subGap.nextExpectedSeqNum() );

        assertEquals( 2, _mgr.pending() );
        assertEquals( 2, gap.size() );

        assertTrue( _mgr.received( 11 ) );
        assertEquals( 1, _mgr.pending() );
        assertEquals( 1, gap.size() );
        assertEquals( -1, _mgr.nextExpectedSeqNum() );
        assertEquals( -1, gap.nextExpectedSeqNum() );
        assertEquals( 12, subGap.nextExpectedSeqNum() );

        assertTrue( _mgr.received( 12 ) );
        assertEquals( 0, _mgr.pending() );
        assertEquals( 0, gap.size() );

        assertEquals( -1, _mgr.nextExpectedSeqNum() );
        assertEquals( -1, gap.nextExpectedSeqNum() );
        assertEquals( -1, subGap.nextExpectedSeqNum() );

        assertTrue( _mgr.add( 10, 12 ) ); // now fully received can rerequest
    }

    @Test
    public void testOneGap() {
        assertFalse( _mgr.received( 9 ) );
        assertFalse( _mgr.anyPendingSequenceGaps() );
        assertEquals( 0, _mgr.pending() );

        assertTrue( _mgr.add( 10, 12 ) );
        assertTrue( _mgr.anyPendingSequenceGaps() );
        assertFalse( _mgr.add( 10, 12 ) );

        SequenceGapRange gap = _mgr.next();
        checkGap( gap, 10, 12 );
        assertEquals( 0, _mgr.pending() );

        _mgr.gapFillRequested( gap );
        assertFalse( _mgr.add( 10, 12 ) );

        assertFalse( _mgr.anyPendingSequenceGaps() );
        assertEquals( 3, _mgr.pending() );

        assertTrue( _mgr.received( 10 ) );
        assertEquals( 2, _mgr.pending() );
        assertFalse( _mgr.received( 10 ) );
        assertEquals( 2, _mgr.pending() );

        assertTrue( _mgr.received( 11 ) );
        assertEquals( 1, _mgr.pending() );
        assertFalse( _mgr.received( 11 ) );

        assertTrue( _mgr.received( 12 ) );
        assertEquals( 0, _mgr.pending() );
        assertFalse( _mgr.received( 12 ) );

        assertFalse( _mgr.received( 13 ) );
        assertEquals( 0, _mgr.pending() );

        assertTrue( _mgr.add( 10, 12 ) ); // now fully received can rerequest
    }

    @Test
    public void testThreeConseqGap() {
        assertTrue( _mgr.add( 10, 12 ) );
        assertTrue( _mgr.add( 13, 15 ) );
        assertTrue( _mgr.add( 11, 17 ) );

        checkNextGap( 3, 10, 12 );
        checkNextGap( 2, 13, 15 );
        checkNextGap( 1, 16, 17 );
    }

    @Test
    public void testTwoGapCompleteOverlap() {
        assertTrue( _mgr.add( 10, 15 ) );
        assertFalse( _mgr.add( 13, 15 ) );

        checkNextGap( 1, 10, 15 );
    }

    @Test
    public void testTwoGapWithNoOverlap() {
        assertTrue( _mgr.add( 10, 12 ) );
        assertTrue( _mgr.add( 14, 17 ) );

        checkNextGap( 2, 10, 12 );
        checkNextGap( 1, 14, 17 );
    }

    private void checkGap( SequenceGapRange gap, int from, int to ) {
        assertEquals( from, gap._from );
        assertEquals( to, gap._to );
    }

    private void checkNextGap( int queued, int from, int to ) {
        assertEquals( queued, _mgr.queuedGaps() );
        SequenceGapRange gap = _mgr.next();
        assertNotNull( gap );
        assertEquals( queued - 1, _mgr.queuedGaps() );

        checkGap( gap, from, to );
        _mgr.gapFillRequested( gap );

        int expected = (to - from) + 1;

        for ( int i = from; i <= to; i++ ) {
            assertEquals( expected--, _mgr.pending() );
            assertTrue( _mgr.received( i ) );
            assertFalse( _mgr.received( i ) );
            assertEquals( expected, _mgr.pending() );
        }

        assertEquals( 0, _mgr.pending() );
    }
}
