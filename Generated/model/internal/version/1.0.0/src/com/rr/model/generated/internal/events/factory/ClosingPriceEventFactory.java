package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClosingPriceEventImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ClosingPriceEventFactory implements PoolFactory<ClosingPriceEventImpl> {

    private SuperPool<ClosingPriceEventImpl> _superPool;

    private ClosingPriceEventImpl _root;

    public ClosingPriceEventFactory(  SuperPool<ClosingPriceEventImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ClosingPriceEventImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ClosingPriceEventImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
