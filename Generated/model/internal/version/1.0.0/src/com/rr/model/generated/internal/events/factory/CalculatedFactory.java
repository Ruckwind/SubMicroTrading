package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.CalculatedImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class CalculatedFactory implements PoolFactory<CalculatedImpl> {

    private SuperPool<CalculatedImpl> _superPool;

    private CalculatedImpl _root;

    public CalculatedFactory(  SuperPool<CalculatedImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public CalculatedImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        CalculatedImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
