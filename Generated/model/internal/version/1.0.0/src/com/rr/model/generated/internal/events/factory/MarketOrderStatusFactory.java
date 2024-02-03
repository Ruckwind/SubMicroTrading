package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MarketOrderStatusImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MarketOrderStatusFactory implements PoolFactory<MarketOrderStatusImpl> {

    private SuperPool<MarketOrderStatusImpl> _superPool;

    private MarketOrderStatusImpl _root;

    public MarketOrderStatusFactory(  SuperPool<MarketOrderStatusImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MarketOrderStatusImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MarketOrderStatusImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
