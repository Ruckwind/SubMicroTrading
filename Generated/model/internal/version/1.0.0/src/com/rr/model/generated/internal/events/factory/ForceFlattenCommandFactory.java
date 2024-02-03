package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ForceFlattenCommandImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ForceFlattenCommandFactory implements PoolFactory<ForceFlattenCommandImpl> {

    private SuperPool<ForceFlattenCommandImpl> _superPool;

    private ForceFlattenCommandImpl _root;

    public ForceFlattenCommandFactory(  SuperPool<ForceFlattenCommandImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ForceFlattenCommandImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ForceFlattenCommandImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
