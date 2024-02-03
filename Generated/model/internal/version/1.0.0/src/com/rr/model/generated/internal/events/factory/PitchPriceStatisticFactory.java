package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.PitchPriceStatisticImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class PitchPriceStatisticFactory implements PoolFactory<PitchPriceStatisticImpl> {

    private SuperPool<PitchPriceStatisticImpl> _superPool;

    private PitchPriceStatisticImpl _root;

    public PitchPriceStatisticFactory(  SuperPool<PitchPriceStatisticImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public PitchPriceStatisticImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        PitchPriceStatisticImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
