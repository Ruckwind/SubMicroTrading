package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ETIUserLogonResponseImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ETIUserLogonResponseFactory implements PoolFactory<ETIUserLogonResponseImpl> {

    private SuperPool<ETIUserLogonResponseImpl> _superPool;

    private ETIUserLogonResponseImpl _root;

    public ETIUserLogonResponseFactory(  SuperPool<ETIUserLogonResponseImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ETIUserLogonResponseImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ETIUserLogonResponseImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
