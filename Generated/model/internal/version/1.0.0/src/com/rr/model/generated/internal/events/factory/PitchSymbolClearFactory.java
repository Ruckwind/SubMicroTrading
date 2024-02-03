package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.PitchSymbolClearImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class PitchSymbolClearFactory implements PoolFactory<PitchSymbolClearImpl> {

    private SuperPool<PitchSymbolClearImpl> _superPool;

    private PitchSymbolClearImpl _root;

    public PitchSymbolClearFactory(  SuperPool<PitchSymbolClearImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public PitchSymbolClearImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        PitchSymbolClearImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
