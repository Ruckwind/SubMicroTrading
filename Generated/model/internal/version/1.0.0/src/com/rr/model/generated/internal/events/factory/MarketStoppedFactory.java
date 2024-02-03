package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MarketStoppedImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MarketStoppedFactory implements PoolFactory<MarketStoppedImpl> {

    private SuperPool<MarketStoppedImpl> _superPool;

    private MarketStoppedImpl _root;

    public MarketStoppedFactory(  SuperPool<MarketStoppedImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MarketStoppedImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MarketStoppedImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
