package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ResendRequestImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ResendRequestFactory implements PoolFactory<ResendRequestImpl> {

    private SuperPool<ResendRequestImpl> _superPool;

    private ResendRequestImpl _root;

    public ResendRequestFactory(  SuperPool<ResendRequestImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ResendRequestImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ResendRequestImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
