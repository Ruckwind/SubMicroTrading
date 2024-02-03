/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.order.collections;

import com.rr.core.collections.ConcLinkedEventQueueSingle;
import com.rr.core.collections.EventQueue;

public class ConcLinkedMsgQueueSingleFastTest extends BaseQueueTst {

    @Override
    protected EventQueue getNewQueue( int presize ) {
        return new ConcLinkedEventQueueSingle();
    }
}
