package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.PendingCancelImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class PendingCancelFactory implements PoolFactory<PendingCancelImpl> {

    private SuperPool<PendingCancelImpl> _superPool;

    private PendingCancelImpl _root;

    public PendingCancelFactory(  SuperPool<PendingCancelImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public PendingCancelImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        PendingCancelImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
