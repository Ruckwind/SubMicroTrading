package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.PitchBookCancelOrderImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class PitchBookCancelOrderFactory implements PoolFactory<PitchBookCancelOrderImpl> {

    private SuperPool<PitchBookCancelOrderImpl> _superPool;

    private PitchBookCancelOrderImpl _root;

    public PitchBookCancelOrderFactory(  SuperPool<PitchBookCancelOrderImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public PitchBookCancelOrderImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        PitchBookCancelOrderImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
