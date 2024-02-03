package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MarketRejectedImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MarketRejectedFactory implements PoolFactory<MarketRejectedImpl> {

    private SuperPool<MarketRejectedImpl> _superPool;

    private MarketRejectedImpl _root;

    public MarketRejectedFactory(  SuperPool<MarketRejectedImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MarketRejectedImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MarketRejectedImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
