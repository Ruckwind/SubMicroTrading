package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MarketCancelledImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MarketCancelledFactory implements PoolFactory<MarketCancelledImpl> {

    private SuperPool<MarketCancelledImpl> _superPool;

    private MarketCancelledImpl _root;

    public MarketCancelledFactory(  SuperPool<MarketCancelledImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MarketCancelledImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MarketCancelledImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
