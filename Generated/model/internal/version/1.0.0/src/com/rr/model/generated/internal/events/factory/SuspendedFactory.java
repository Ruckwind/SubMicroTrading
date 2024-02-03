package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.SuspendedImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class SuspendedFactory implements PoolFactory<SuspendedImpl> {

    private SuperPool<SuspendedImpl> _superPool;

    private SuspendedImpl _root;

    public SuspendedFactory(  SuperPool<SuspendedImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public SuspendedImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        SuspendedImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
