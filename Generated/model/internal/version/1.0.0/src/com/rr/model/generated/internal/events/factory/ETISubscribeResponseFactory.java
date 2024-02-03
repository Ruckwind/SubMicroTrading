package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ETISubscribeResponseImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ETISubscribeResponseFactory implements PoolFactory<ETISubscribeResponseImpl> {

    private SuperPool<ETISubscribeResponseImpl> _superPool;

    private ETISubscribeResponseImpl _root;

    public ETISubscribeResponseFactory(  SuperPool<ETISubscribeResponseImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ETISubscribeResponseImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ETISubscribeResponseImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
