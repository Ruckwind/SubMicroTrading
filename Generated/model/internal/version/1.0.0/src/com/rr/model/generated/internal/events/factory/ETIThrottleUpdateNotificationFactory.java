package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ETIThrottleUpdateNotificationImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ETIThrottleUpdateNotificationFactory implements PoolFactory<ETIThrottleUpdateNotificationImpl> {

    private SuperPool<ETIThrottleUpdateNotificationImpl> _superPool;

    private ETIThrottleUpdateNotificationImpl _root;

    public ETIThrottleUpdateNotificationFactory(  SuperPool<ETIThrottleUpdateNotificationImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ETIThrottleUpdateNotificationImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ETIThrottleUpdateNotificationImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
