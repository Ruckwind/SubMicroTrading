package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MarketDoneForDayImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MarketDoneForDayFactory implements PoolFactory<MarketDoneForDayImpl> {

    private SuperPool<MarketDoneForDayImpl> _superPool;

    private MarketDoneForDayImpl _root;

    public MarketDoneForDayFactory(  SuperPool<MarketDoneForDayImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MarketDoneForDayImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MarketDoneForDayImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
