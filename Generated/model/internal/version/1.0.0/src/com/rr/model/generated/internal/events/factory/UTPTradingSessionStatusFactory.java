package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.UTPTradingSessionStatusImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class UTPTradingSessionStatusFactory implements PoolFactory<UTPTradingSessionStatusImpl> {

    private SuperPool<UTPTradingSessionStatusImpl> _superPool;

    private UTPTradingSessionStatusImpl _root;

    public UTPTradingSessionStatusFactory(  SuperPool<UTPTradingSessionStatusImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public UTPTradingSessionStatusImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        UTPTradingSessionStatusImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
