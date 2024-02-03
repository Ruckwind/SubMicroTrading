package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.InstrumentSimDataImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class InstrumentSimDataFactory implements PoolFactory<InstrumentSimDataImpl> {

    private SuperPool<InstrumentSimDataImpl> _superPool;

    private InstrumentSimDataImpl _root;

    public InstrumentSimDataFactory(  SuperPool<InstrumentSimDataImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public InstrumentSimDataImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        InstrumentSimDataImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
