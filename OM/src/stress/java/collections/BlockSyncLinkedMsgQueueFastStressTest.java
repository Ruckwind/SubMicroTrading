/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package collections;

import com.rr.core.collections.BlockingSyncQueue;
import com.rr.core.collections.EventQueue;

public class BlockSyncLinkedMsgQueueFastStressTest extends StressBaseQueueTst {

    @Override protected EventQueue getNewQueue( int presize ) {
        return new BlockingSyncQueue();
    }
}
