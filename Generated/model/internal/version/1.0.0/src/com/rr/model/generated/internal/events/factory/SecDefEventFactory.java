package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.SecDefEventImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class SecDefEventFactory implements PoolFactory<SecDefEventImpl> {

    private SuperPool<SecDefEventImpl> _superPool;

    private SecDefEventImpl _root;

    public SecDefEventFactory(  SuperPool<SecDefEventImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public SecDefEventImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        SecDefEventImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
