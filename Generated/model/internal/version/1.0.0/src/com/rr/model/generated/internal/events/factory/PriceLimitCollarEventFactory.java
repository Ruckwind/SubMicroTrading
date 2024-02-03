package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.PriceLimitCollarEventImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class PriceLimitCollarEventFactory implements PoolFactory<PriceLimitCollarEventImpl> {

    private SuperPool<PriceLimitCollarEventImpl> _superPool;

    private PriceLimitCollarEventImpl _root;

    public PriceLimitCollarEventFactory(  SuperPool<PriceLimitCollarEventImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public PriceLimitCollarEventImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        PriceLimitCollarEventImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
