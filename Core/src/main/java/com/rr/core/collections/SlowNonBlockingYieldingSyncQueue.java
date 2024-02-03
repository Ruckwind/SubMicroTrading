/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.model.Event;
import com.rr.core.utils.ThreadUtilsFactory;

/**
 * Queue which can be used on PC to stop multi sessions spinning and grinding box to halt
 */
public class SlowNonBlockingYieldingSyncQueue extends NonBlockingSyncQueue {

    private static final int MOD_CNT = 1000;

    private int  _delayMS       = 10;
    private int  _throttleBatch = MOD_CNT;
    private long _cnt           = 0;

    public SlowNonBlockingYieldingSyncQueue() {
        super();
    }

    public SlowNonBlockingYieldingSyncQueue( String id ) {
        super( id );
    }

    @Override
    public Event poll() {
        Event t = super.poll();

        if ( t == null ) {
            if ( (_cnt++ % _throttleBatch) == 0 ) {
                ThreadUtilsFactory.get().sleep( _delayMS );
            }
        }

        return t;
    }
}
