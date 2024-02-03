package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ETIUnsubscribeResponseImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ETIUnsubscribeResponseFactory implements PoolFactory<ETIUnsubscribeResponseImpl> {

    private SuperPool<ETIUnsubscribeResponseImpl> _superPool;

    private ETIUnsubscribeResponseImpl _root;

    public ETIUnsubscribeResponseFactory(  SuperPool<ETIUnsubscribeResponseImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ETIUnsubscribeResponseImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ETIUnsubscribeResponseImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
