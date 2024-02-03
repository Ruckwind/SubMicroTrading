/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.order.collections;

import com.rr.core.collections.BlockingSyncQueue;
import com.rr.core.collections.EventQueue;

public class BlockSyncLinkedMsgQueueFastTest extends BaseQueueTst {

    @Override
    protected EventQueue getNewQueue( int presize ) {
        return new BlockingSyncQueue();
    }
}
