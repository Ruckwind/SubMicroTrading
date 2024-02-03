/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.order.collections;

import com.rr.core.collections.ArrayBlockingEventQueue;
import com.rr.core.collections.EventQueue;

public class ArrayBlockingMsgQueueFastTest extends BaseQueueTst {

    @Override
    protected EventQueue getNewQueue( int presize ) {
        return new ArrayBlockingEventQueue( "testq", presize, true );
    }
}
