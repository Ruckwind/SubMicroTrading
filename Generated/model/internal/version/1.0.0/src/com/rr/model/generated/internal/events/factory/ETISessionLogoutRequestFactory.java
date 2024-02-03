package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ETISessionLogoutRequestImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ETISessionLogoutRequestFactory implements PoolFactory<ETISessionLogoutRequestImpl> {

    private SuperPool<ETISessionLogoutRequestImpl> _superPool;

    private ETISessionLogoutRequestImpl _root;

    public ETISessionLogoutRequestFactory(  SuperPool<ETISessionLogoutRequestImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ETISessionLogoutRequestImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ETISessionLogoutRequestImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
