package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.RejectedImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class RejectedFactory implements PoolFactory<RejectedImpl> {

    private SuperPool<RejectedImpl> _superPool;

    private RejectedImpl _root;

    public RejectedFactory(  SuperPool<RejectedImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public RejectedImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        RejectedImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
