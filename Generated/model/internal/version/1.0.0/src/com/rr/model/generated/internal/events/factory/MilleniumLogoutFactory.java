package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MilleniumLogoutImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MilleniumLogoutFactory implements PoolFactory<MilleniumLogoutImpl> {

    private SuperPool<MilleniumLogoutImpl> _superPool;

    private MilleniumLogoutImpl _root;

    public MilleniumLogoutFactory(  SuperPool<MilleniumLogoutImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MilleniumLogoutImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MilleniumLogoutImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
