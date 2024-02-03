/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.order.collections;

import com.rr.core.collections.DoubleLinkedEventQueueImpl;
import com.rr.core.collections.EventQueue;

public class DoubleLinkedQueueTest extends BaseQueueTst {

    @Override protected void doTestMixedThreaded( int count, int threads ) {
        /* disable as q not threadsafe */
    }

    @Override protected void doTestThreaded( int count, int threads ) {
        /* disable as q not threadsafe */
    }

    @Override protected EventQueue getNewQueue( int presize ) {
        return new DoubleLinkedEventQueueImpl();
    }
}
