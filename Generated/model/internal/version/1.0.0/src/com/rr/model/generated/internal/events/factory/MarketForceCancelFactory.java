package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MarketForceCancelImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MarketForceCancelFactory implements PoolFactory<MarketForceCancelImpl> {

    private SuperPool<MarketForceCancelImpl> _superPool;

    private MarketForceCancelImpl _root;

    public MarketForceCancelFactory(  SuperPool<MarketForceCancelImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MarketForceCancelImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MarketForceCancelImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
