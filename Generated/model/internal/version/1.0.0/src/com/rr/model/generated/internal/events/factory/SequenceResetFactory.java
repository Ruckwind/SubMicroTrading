package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.SequenceResetImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class SequenceResetFactory implements PoolFactory<SequenceResetImpl> {

    private SuperPool<SequenceResetImpl> _superPool;

    private SequenceResetImpl _root;

    public SequenceResetFactory(  SuperPool<SequenceResetImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public SequenceResetImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        SequenceResetImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
