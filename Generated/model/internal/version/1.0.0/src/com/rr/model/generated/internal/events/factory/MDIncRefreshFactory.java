package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MDIncRefreshImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MDIncRefreshFactory implements PoolFactory<MDIncRefreshImpl> {

    private SuperPool<MDIncRefreshImpl> _superPool;

    private MDIncRefreshImpl _root;

    public MDIncRefreshFactory(  SuperPool<MDIncRefreshImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MDIncRefreshImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MDIncRefreshImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
