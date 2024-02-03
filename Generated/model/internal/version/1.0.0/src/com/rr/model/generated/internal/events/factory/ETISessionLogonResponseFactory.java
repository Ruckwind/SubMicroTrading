package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ETISessionLogonResponseImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ETISessionLogonResponseFactory implements PoolFactory<ETISessionLogonResponseImpl> {

    private SuperPool<ETISessionLogonResponseImpl> _superPool;

    private ETISessionLogonResponseImpl _root;

    public ETISessionLogonResponseFactory(  SuperPool<ETISessionLogonResponseImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ETISessionLogonResponseImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ETISessionLogonResponseImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
