package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MDEntryImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MDEntryFactory implements PoolFactory<MDEntryImpl> {

    private SuperPool<MDEntryImpl> _superPool;

    private MDEntryImpl _root;

    public MDEntryFactory(  SuperPool<MDEntryImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MDEntryImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MDEntryImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
