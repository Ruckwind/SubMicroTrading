package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.PitchBookOrderExecutedImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class PitchBookOrderExecutedFactory implements PoolFactory<PitchBookOrderExecutedImpl> {

    private SuperPool<PitchBookOrderExecutedImpl> _superPool;

    private PitchBookOrderExecutedImpl _root;

    public PitchBookOrderExecutedFactory(  SuperPool<PitchBookOrderExecutedImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public PitchBookOrderExecutedImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        PitchBookOrderExecutedImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
