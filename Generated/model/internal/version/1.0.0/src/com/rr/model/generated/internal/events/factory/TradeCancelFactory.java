package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.TradeCancelImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class TradeCancelFactory implements PoolFactory<TradeCancelImpl> {

    private SuperPool<TradeCancelImpl> _superPool;

    private TradeCancelImpl _root;

    public TradeCancelFactory(  SuperPool<TradeCancelImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public TradeCancelImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        TradeCancelImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
