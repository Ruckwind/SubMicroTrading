package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.CancelReplaceRequestImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class CancelReplaceRequestFactory implements PoolFactory<CancelReplaceRequestImpl> {

    private SuperPool<CancelReplaceRequestImpl> _superPool;

    private CancelReplaceRequestImpl _root;

    public CancelReplaceRequestFactory(  SuperPool<CancelReplaceRequestImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public CancelReplaceRequestImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        CancelReplaceRequestImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
