package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.StrategyStateImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class StrategyStateFactory implements PoolFactory<StrategyStateImpl> {

    private SuperPool<StrategyStateImpl> _superPool;

    private StrategyStateImpl _root;

    public StrategyStateFactory(  SuperPool<StrategyStateImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public StrategyStateImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        StrategyStateImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
