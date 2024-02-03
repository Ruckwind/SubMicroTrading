package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ETISessionLogonRequestImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ETISessionLogonRequestFactory implements PoolFactory<ETISessionLogonRequestImpl> {

    private SuperPool<ETISessionLogonRequestImpl> _superPool;

    private ETISessionLogonRequestImpl _root;

    public ETISessionLogonRequestFactory(  SuperPool<ETISessionLogonRequestImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ETISessionLogonRequestImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ETISessionLogonRequestImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
