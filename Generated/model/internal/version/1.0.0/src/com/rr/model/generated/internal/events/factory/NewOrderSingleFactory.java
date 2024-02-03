package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.NewOrderSingleImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class NewOrderSingleFactory implements PoolFactory<NewOrderSingleImpl> {

    private SuperPool<NewOrderSingleImpl> _superPool;

    private NewOrderSingleImpl _root;

    public NewOrderSingleFactory(  SuperPool<NewOrderSingleImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public NewOrderSingleImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        NewOrderSingleImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
