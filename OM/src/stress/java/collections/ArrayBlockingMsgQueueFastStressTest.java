/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package collections;

import com.rr.core.collections.ArrayBlockingEventQueue;
import com.rr.core.collections.EventQueue;

public class ArrayBlockingMsgQueueFastStressTest extends StressBaseQueueTst {

    @Override protected EventQueue getNewQueue( int presize ) {
        return new ArrayBlockingEventQueue( "testq", presize, true );
    }
}
