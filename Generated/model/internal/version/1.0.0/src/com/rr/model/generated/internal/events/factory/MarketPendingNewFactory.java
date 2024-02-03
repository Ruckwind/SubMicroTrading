package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MarketPendingNewImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MarketPendingNewFactory implements PoolFactory<MarketPendingNewImpl> {

    private SuperPool<MarketPendingNewImpl> _superPool;

    private MarketPendingNewImpl _root;

    public MarketPendingNewFactory(  SuperPool<MarketPendingNewImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MarketPendingNewImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MarketPendingNewImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
