/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package collections;

import com.rr.om.order.collections.ConcBlockingSyncLinkedQueueSlowTest;
import org.junit.Test;

/**
 * slow producer due to creating decoder for every message
 * <p>
 * leave in as a good different test to the fast one
 *
 * @author Richard Rose
 */
public class ConcBlockingSyncLinkedQueueSlowStressTest extends ConcBlockingSyncLinkedQueueSlowTest {

    @Test public void testThreadedT128M10000() {
        doTestThreaded( 10000, 128 );
    }

    @Test public void testThreadedT16M100000() {
        doTestThreaded( 100000, 16 );
    }

    @Test public void testThreadedT1M1000() {
        doTestThreaded( 1000, 1 );
    }

    @Test public void testThreadedT2M100000() {
        doTestThreaded( 100000, 2 );
    }

    @Test public void testThreadedT8M100000() {
        doTestThreaded( 100000, 8 );
    }
}
