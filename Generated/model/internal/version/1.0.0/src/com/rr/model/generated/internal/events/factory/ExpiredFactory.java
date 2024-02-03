package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ExpiredImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ExpiredFactory implements PoolFactory<ExpiredImpl> {

    private SuperPool<ExpiredImpl> _superPool;

    private ExpiredImpl _root;

    public ExpiredFactory(  SuperPool<ExpiredImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ExpiredImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ExpiredImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
