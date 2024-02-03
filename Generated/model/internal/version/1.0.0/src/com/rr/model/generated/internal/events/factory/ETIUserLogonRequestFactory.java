package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ETIUserLogonRequestImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ETIUserLogonRequestFactory implements PoolFactory<ETIUserLogonRequestImpl> {

    private SuperPool<ETIUserLogonRequestImpl> _superPool;

    private ETIUserLogonRequestImpl _root;

    public ETIUserLogonRequestFactory(  SuperPool<ETIUserLogonRequestImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ETIUserLogonRequestImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ETIUserLogonRequestImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
