package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.RestatedImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class RestatedFactory implements PoolFactory<RestatedImpl> {

    private SuperPool<RestatedImpl> _superPool;

    private RestatedImpl _root;

    public RestatedFactory(  SuperPool<RestatedImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public RestatedImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        RestatedImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
