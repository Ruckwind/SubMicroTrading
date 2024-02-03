package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.PitchBookAddOrderImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class PitchBookAddOrderFactory implements PoolFactory<PitchBookAddOrderImpl> {

    private SuperPool<PitchBookAddOrderImpl> _superPool;

    private PitchBookAddOrderImpl _root;

    public PitchBookAddOrderFactory(  SuperPool<PitchBookAddOrderImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public PitchBookAddOrderImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        PitchBookAddOrderImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
