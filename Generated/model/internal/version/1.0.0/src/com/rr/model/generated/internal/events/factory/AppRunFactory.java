package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.AppRunImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class AppRunFactory implements PoolFactory<AppRunImpl> {

    private SuperPool<AppRunImpl> _superPool;

    private AppRunImpl _root;

    public AppRunFactory(  SuperPool<AppRunImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public AppRunImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        AppRunImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
