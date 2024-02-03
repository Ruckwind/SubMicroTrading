/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package collections;

import com.rr.core.collections.EventQueue;
import com.rr.core.collections.NonBlockingSyncQueue;

public class NonBlockingLinkedEventQueueFastStressTest extends StressBaseQueueTst {

    @Override protected EventQueue getNewQueue( int presize ) {
        return new NonBlockingSyncQueue();
    }
}
