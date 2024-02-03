package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.SecMassStatGrpImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class SecMassStatGrpFactory implements PoolFactory<SecMassStatGrpImpl> {

    private SuperPool<SecMassStatGrpImpl> _superPool;

    private SecMassStatGrpImpl _root;

    public SecMassStatGrpFactory(  SuperPool<SecMassStatGrpImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public SecMassStatGrpImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        SecMassStatGrpImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
