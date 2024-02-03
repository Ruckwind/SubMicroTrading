package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MarketRestatedImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MarketRestatedFactory implements PoolFactory<MarketRestatedImpl> {

    private SuperPool<MarketRestatedImpl> _superPool;

    private MarketRestatedImpl _root;

    public MarketRestatedFactory(  SuperPool<MarketRestatedImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MarketRestatedImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MarketRestatedImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
