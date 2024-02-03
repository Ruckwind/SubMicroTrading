package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.StratInstrumentImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class StratInstrumentFactory implements PoolFactory<StratInstrumentImpl> {

    private SuperPool<StratInstrumentImpl> _superPool;

    private StratInstrumentImpl _root;

    public StratInstrumentFactory(  SuperPool<StratInstrumentImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public StratInstrumentImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        StratInstrumentImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
