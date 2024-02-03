package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MarketPendingCancelImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MarketPendingCancelFactory implements PoolFactory<MarketPendingCancelImpl> {

    private SuperPool<MarketPendingCancelImpl> _superPool;

    private MarketPendingCancelImpl _root;

    public MarketPendingCancelFactory(  SuperPool<MarketPendingCancelImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MarketPendingCancelImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MarketPendingCancelImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
