package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.TradeCorrectImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class TradeCorrectFactory implements PoolFactory<TradeCorrectImpl> {

    private SuperPool<TradeCorrectImpl> _superPool;

    private TradeCorrectImpl _root;

    public TradeCorrectFactory(  SuperPool<TradeCorrectImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public TradeCorrectImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        TradeCorrectImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
