package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.SecurityDefinitionUpdateImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class SecurityDefinitionUpdateFactory implements PoolFactory<SecurityDefinitionUpdateImpl> {

    private SuperPool<SecurityDefinitionUpdateImpl> _superPool;

    private SecurityDefinitionUpdateImpl _root;

    public SecurityDefinitionUpdateFactory(  SuperPool<SecurityDefinitionUpdateImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public SecurityDefinitionUpdateImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        SecurityDefinitionUpdateImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
