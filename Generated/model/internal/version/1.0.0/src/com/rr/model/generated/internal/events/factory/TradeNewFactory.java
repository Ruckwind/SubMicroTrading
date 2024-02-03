package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.TradeNewImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class TradeNewFactory implements PoolFactory<TradeNewImpl> {

    private SuperPool<TradeNewImpl> _superPool;

    private TradeNewImpl _root;

    public TradeNewFactory(  SuperPool<TradeNewImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public TradeNewImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        TradeNewImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
