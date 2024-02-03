package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.SecDefLegImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class SecDefLegFactory implements PoolFactory<SecDefLegImpl> {

    private SuperPool<SecDefLegImpl> _superPool;

    private SecDefLegImpl _root;

    public SecDefLegFactory(  SuperPool<SecDefLegImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public SecDefLegImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        SecDefLegImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
