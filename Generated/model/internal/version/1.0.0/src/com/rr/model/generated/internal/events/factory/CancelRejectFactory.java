package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.CancelRejectImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class CancelRejectFactory implements PoolFactory<CancelRejectImpl> {

    private SuperPool<CancelRejectImpl> _superPool;

    private CancelRejectImpl _root;

    public CancelRejectFactory(  SuperPool<CancelRejectImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public CancelRejectImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        CancelRejectImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
