package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.CancelledImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class CancelledFactory implements PoolFactory<CancelledImpl> {

    private SuperPool<CancelledImpl> _superPool;

    private CancelledImpl _root;

    public CancelledFactory(  SuperPool<CancelledImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public CancelledImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        CancelledImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
