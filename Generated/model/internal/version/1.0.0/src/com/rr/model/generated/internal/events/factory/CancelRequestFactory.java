package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.CancelRequestImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class CancelRequestFactory implements PoolFactory<CancelRequestImpl> {

    private SuperPool<CancelRequestImpl> _superPool;

    private CancelRequestImpl _root;

    public CancelRequestFactory(  SuperPool<CancelRequestImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public CancelRequestImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        CancelRequestImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
