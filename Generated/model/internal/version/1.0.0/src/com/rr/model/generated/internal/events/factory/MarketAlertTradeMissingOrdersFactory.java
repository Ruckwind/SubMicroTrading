package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MarketAlertTradeMissingOrdersImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MarketAlertTradeMissingOrdersFactory implements PoolFactory<MarketAlertTradeMissingOrdersImpl> {

    private SuperPool<MarketAlertTradeMissingOrdersImpl> _superPool;

    private MarketAlertTradeMissingOrdersImpl _root;

    public MarketAlertTradeMissingOrdersFactory(  SuperPool<MarketAlertTradeMissingOrdersImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MarketAlertTradeMissingOrdersImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MarketAlertTradeMissingOrdersImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
