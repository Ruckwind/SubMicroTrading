/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.order.collections;

import com.rr.core.collections.EventQueue;
import com.rr.core.collections.NonBlockingSyncQueue;

public class NonBlockingLinkedEventQueueFastTest extends BaseQueueTst {

    @Override
    protected EventQueue getNewQueue( int presize ) {
        return new NonBlockingSyncQueue();
    }
}
