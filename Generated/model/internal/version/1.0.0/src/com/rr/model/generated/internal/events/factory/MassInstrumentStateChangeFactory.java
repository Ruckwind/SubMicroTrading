package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MassInstrumentStateChangeImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MassInstrumentStateChangeFactory implements PoolFactory<MassInstrumentStateChangeImpl> {

    private SuperPool<MassInstrumentStateChangeImpl> _superPool;

    private MassInstrumentStateChangeImpl _root;

    public MassInstrumentStateChangeFactory(  SuperPool<MassInstrumentStateChangeImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MassInstrumentStateChangeImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MassInstrumentStateChangeImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
