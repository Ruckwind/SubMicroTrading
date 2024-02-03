package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.SettlementPriceEventImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class SettlementPriceEventFactory implements PoolFactory<SettlementPriceEventImpl> {

    private SuperPool<SettlementPriceEventImpl> _superPool;

    private SettlementPriceEventImpl _root;

    public SettlementPriceEventFactory(  SuperPool<SettlementPriceEventImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public SettlementPriceEventImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        SettlementPriceEventImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
