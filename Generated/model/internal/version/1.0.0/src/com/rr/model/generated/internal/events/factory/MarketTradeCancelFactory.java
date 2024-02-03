package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MarketTradeCancelImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MarketTradeCancelFactory implements PoolFactory<MarketTradeCancelImpl> {

    private SuperPool<MarketTradeCancelImpl> _superPool;

    private MarketTradeCancelImpl _root;

    public MarketTradeCancelFactory(  SuperPool<MarketTradeCancelImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MarketTradeCancelImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MarketTradeCancelImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
