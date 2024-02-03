package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.PendingReplaceImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class PendingReplaceFactory implements PoolFactory<PendingReplaceImpl> {

    private SuperPool<PendingReplaceImpl> _superPool;

    private PendingReplaceImpl _root;

    public PendingReplaceFactory(  SuperPool<PendingReplaceImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public PendingReplaceImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        PendingReplaceImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
