package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MarketVagueOrderRejectImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MarketVagueOrderRejectFactory implements PoolFactory<MarketVagueOrderRejectImpl> {

    private SuperPool<MarketVagueOrderRejectImpl> _superPool;

    private MarketVagueOrderRejectImpl _root;

    public MarketVagueOrderRejectFactory(  SuperPool<MarketVagueOrderRejectImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MarketVagueOrderRejectImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MarketVagueOrderRejectImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
