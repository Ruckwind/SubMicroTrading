package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ETIUserLogoutRequestImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ETIUserLogoutRequestFactory implements PoolFactory<ETIUserLogoutRequestImpl> {

    private SuperPool<ETIUserLogoutRequestImpl> _superPool;

    private ETIUserLogoutRequestImpl _root;

    public ETIUserLogoutRequestFactory(  SuperPool<ETIUserLogoutRequestImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ETIUserLogoutRequestImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ETIUserLogoutRequestImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
