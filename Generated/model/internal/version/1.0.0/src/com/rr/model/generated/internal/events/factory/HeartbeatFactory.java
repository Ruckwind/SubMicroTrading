package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.HeartbeatImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class HeartbeatFactory implements PoolFactory<HeartbeatImpl> {

    private SuperPool<HeartbeatImpl> _superPool;

    private HeartbeatImpl _root;

    public HeartbeatFactory(  SuperPool<HeartbeatImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public HeartbeatImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        HeartbeatImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
