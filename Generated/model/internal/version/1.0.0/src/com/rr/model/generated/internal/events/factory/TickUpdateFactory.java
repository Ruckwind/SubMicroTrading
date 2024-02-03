package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.TickUpdateImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class TickUpdateFactory implements PoolFactory<TickUpdateImpl> {

    private SuperPool<TickUpdateImpl> _superPool;

    private TickUpdateImpl _root;

    public TickUpdateFactory(  SuperPool<TickUpdateImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public TickUpdateImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        TickUpdateImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
