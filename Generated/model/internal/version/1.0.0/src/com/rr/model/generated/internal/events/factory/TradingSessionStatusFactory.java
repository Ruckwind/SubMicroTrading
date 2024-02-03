package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.TradingSessionStatusImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class TradingSessionStatusFactory implements PoolFactory<TradingSessionStatusImpl> {

    private SuperPool<TradingSessionStatusImpl> _superPool;

    private TradingSessionStatusImpl _root;

    public TradingSessionStatusFactory(  SuperPool<TradingSessionStatusImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public TradingSessionStatusImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        TradingSessionStatusImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
