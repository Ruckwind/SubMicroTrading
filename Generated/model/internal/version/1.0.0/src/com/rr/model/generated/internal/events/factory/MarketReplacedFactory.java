package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MarketReplacedImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MarketReplacedFactory implements PoolFactory<MarketReplacedImpl> {

    private SuperPool<MarketReplacedImpl> _superPool;

    private MarketReplacedImpl _root;

    public MarketReplacedFactory(  SuperPool<MarketReplacedImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MarketReplacedImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MarketReplacedImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
