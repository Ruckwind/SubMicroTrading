package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ForceCancelImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ForceCancelFactory implements PoolFactory<ForceCancelImpl> {

    private SuperPool<ForceCancelImpl> _superPool;

    private ForceCancelImpl _root;

    public ForceCancelFactory(  SuperPool<ForceCancelImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ForceCancelImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ForceCancelImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
