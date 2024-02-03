package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MarketCalculatedImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MarketCalculatedFactory implements PoolFactory<MarketCalculatedImpl> {

    private SuperPool<MarketCalculatedImpl> _superPool;

    private MarketCalculatedImpl _root;

    public MarketCalculatedFactory(  SuperPool<MarketCalculatedImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MarketCalculatedImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MarketCalculatedImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
