package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MarketTradeCorrectImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MarketTradeCorrectFactory implements PoolFactory<MarketTradeCorrectImpl> {

    private SuperPool<MarketTradeCorrectImpl> _superPool;

    private MarketTradeCorrectImpl _root;

    public MarketTradeCorrectFactory(  SuperPool<MarketTradeCorrectImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MarketTradeCorrectImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MarketTradeCorrectImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
