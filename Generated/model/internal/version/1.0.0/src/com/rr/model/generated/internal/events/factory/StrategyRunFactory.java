package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.StrategyRunImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class StrategyRunFactory implements PoolFactory<StrategyRunImpl> {

    private SuperPool<StrategyRunImpl> _superPool;

    private StrategyRunImpl _root;

    public StrategyRunFactory(  SuperPool<StrategyRunImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public StrategyRunImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        StrategyRunImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
