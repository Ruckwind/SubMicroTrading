package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.SecurityAltIDImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class SecurityAltIDFactory implements PoolFactory<SecurityAltIDImpl> {

    private SuperPool<SecurityAltIDImpl> _superPool;

    private SecurityAltIDImpl _root;

    public SecurityAltIDFactory(  SuperPool<SecurityAltIDImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public SecurityAltIDImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        SecurityAltIDImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
