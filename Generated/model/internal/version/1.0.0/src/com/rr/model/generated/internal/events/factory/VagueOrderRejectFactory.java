package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.VagueOrderRejectImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class VagueOrderRejectFactory implements PoolFactory<VagueOrderRejectImpl> {

    private SuperPool<VagueOrderRejectImpl> _superPool;

    private VagueOrderRejectImpl _root;

    public VagueOrderRejectFactory(  SuperPool<VagueOrderRejectImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public VagueOrderRejectImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        VagueOrderRejectImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
