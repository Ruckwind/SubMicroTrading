package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.SymbolRepeatingGrpImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class SymbolRepeatingGrpFactory implements PoolFactory<SymbolRepeatingGrpImpl> {

    private SuperPool<SymbolRepeatingGrpImpl> _superPool;

    private SymbolRepeatingGrpImpl _root;

    public SymbolRepeatingGrpFactory(  SuperPool<SymbolRepeatingGrpImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public SymbolRepeatingGrpImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        SymbolRepeatingGrpImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
