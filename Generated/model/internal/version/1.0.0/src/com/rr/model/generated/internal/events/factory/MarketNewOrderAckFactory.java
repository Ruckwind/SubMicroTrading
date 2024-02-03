package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MarketNewOrderAckImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MarketNewOrderAckFactory implements PoolFactory<MarketNewOrderAckImpl> {

    private SuperPool<MarketNewOrderAckImpl> _superPool;

    private MarketNewOrderAckImpl _root;

    public MarketNewOrderAckFactory(  SuperPool<MarketNewOrderAckImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MarketNewOrderAckImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MarketNewOrderAckImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
