package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.SecurityDefinitionImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class SecurityDefinitionFactory implements PoolFactory<SecurityDefinitionImpl> {

    private SuperPool<SecurityDefinitionImpl> _superPool;

    private SecurityDefinitionImpl _root;

    public SecurityDefinitionFactory(  SuperPool<SecurityDefinitionImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public SecurityDefinitionImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        SecurityDefinitionImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
