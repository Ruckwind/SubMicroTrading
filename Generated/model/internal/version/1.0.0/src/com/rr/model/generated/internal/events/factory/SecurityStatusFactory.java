package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.SecurityStatusImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class SecurityStatusFactory implements PoolFactory<SecurityStatusImpl> {

    private SuperPool<SecurityStatusImpl> _superPool;

    private SecurityStatusImpl _root;

    public SecurityStatusFactory(  SuperPool<SecurityStatusImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public SecurityStatusImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        SecurityStatusImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
