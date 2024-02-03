package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.SoupLogInRequestImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class SoupLogInRequestFactory implements PoolFactory<SoupLogInRequestImpl> {

    private SuperPool<SoupLogInRequestImpl> _superPool;

    private SoupLogInRequestImpl _root;

    public SoupLogInRequestFactory(  SuperPool<SoupLogInRequestImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public SoupLogInRequestImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        SoupLogInRequestImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
