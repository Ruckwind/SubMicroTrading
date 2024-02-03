package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MilleniumLogonImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MilleniumLogonFactory implements PoolFactory<MilleniumLogonImpl> {

    private SuperPool<MilleniumLogonImpl> _superPool;

    private MilleniumLogonImpl _root;

    public MilleniumLogonFactory(  SuperPool<MilleniumLogonImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MilleniumLogonImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MilleniumLogonImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
