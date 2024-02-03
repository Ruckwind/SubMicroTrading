/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package collections;

import com.rr.core.collections.EventQueue;
import com.rr.core.collections.RingBufferEventQueue1C1P;

public class RingBufferSingleProducerFastStressTest extends StressBaseQueueTst {

    @Override protected EventQueue getNewQueue( int presize ) {
        return new RingBufferEventQueue1C1P( presize );
    }

    @Override protected void doTestMixedThreaded( int count, int threads ) {
        if ( threads == 1 ) { // ONLY ALLOWED ONE PRODUCER
            super.doTestMixedThreaded( count, threads );
        }
    }

    @Override protected void doTestThreaded( int count, int threads ) {
        if ( threads == 1 ) { // ONLY ALLOWED ONE PRODUCER
            super.doTestThreaded( count, threads );
        }
    }
}
