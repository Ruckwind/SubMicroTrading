package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MarketPendingReplaceImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MarketPendingReplaceFactory implements PoolFactory<MarketPendingReplaceImpl> {

    private SuperPool<MarketPendingReplaceImpl> _superPool;

    private MarketPendingReplaceImpl _root;

    public MarketPendingReplaceFactory(  SuperPool<MarketPendingReplaceImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MarketPendingReplaceImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MarketPendingReplaceImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
