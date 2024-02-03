package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.PendingNewImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class PendingNewFactory implements PoolFactory<PendingNewImpl> {

    private SuperPool<PendingNewImpl> _superPool;

    private PendingNewImpl _root;

    public PendingNewFactory(  SuperPool<PendingNewImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public PendingNewImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        PendingNewImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
