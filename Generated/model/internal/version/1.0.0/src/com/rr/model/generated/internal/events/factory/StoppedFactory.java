package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.StoppedImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class StoppedFactory implements PoolFactory<StoppedImpl> {

    private SuperPool<StoppedImpl> _superPool;

    private StoppedImpl _root;

    public StoppedFactory(  SuperPool<StoppedImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public StoppedImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        StoppedImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
