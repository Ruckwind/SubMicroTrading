package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MDRequestImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MDRequestFactory implements PoolFactory<MDRequestImpl> {

    private SuperPool<MDRequestImpl> _superPool;

    private MDRequestImpl _root;

    public MDRequestFactory(  SuperPool<MDRequestImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MDRequestImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MDRequestImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
