package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.StratInstrumentStateImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class StratInstrumentStateFactory implements PoolFactory<StratInstrumentStateImpl> {

    private SuperPool<StratInstrumentStateImpl> _superPool;

    private StratInstrumentStateImpl _root;

    public StratInstrumentStateFactory(  SuperPool<StratInstrumentStateImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public StratInstrumentStateImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        StratInstrumentStateImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
