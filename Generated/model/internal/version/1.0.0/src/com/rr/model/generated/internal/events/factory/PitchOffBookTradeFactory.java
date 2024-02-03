package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.PitchOffBookTradeImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class PitchOffBookTradeFactory implements PoolFactory<PitchOffBookTradeImpl> {

    private SuperPool<PitchOffBookTradeImpl> _superPool;

    private PitchOffBookTradeImpl _root;

    public PitchOffBookTradeFactory(  SuperPool<PitchOffBookTradeImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public PitchOffBookTradeImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        PitchOffBookTradeImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
