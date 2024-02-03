package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.AlertTradeMissingOrdersImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class AlertTradeMissingOrdersFactory implements PoolFactory<AlertTradeMissingOrdersImpl> {

    private SuperPool<AlertTradeMissingOrdersImpl> _superPool;

    private AlertTradeMissingOrdersImpl _root;

    public AlertTradeMissingOrdersFactory(  SuperPool<AlertTradeMissingOrdersImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public AlertTradeMissingOrdersImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        AlertTradeMissingOrdersImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
