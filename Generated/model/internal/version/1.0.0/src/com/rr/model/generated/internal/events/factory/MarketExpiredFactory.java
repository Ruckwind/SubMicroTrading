package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MarketExpiredImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MarketExpiredFactory implements PoolFactory<MarketExpiredImpl> {

    private SuperPool<MarketExpiredImpl> _superPool;

    private MarketExpiredImpl _root;

    public MarketExpiredFactory(  SuperPool<MarketExpiredImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MarketExpiredImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MarketExpiredImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
