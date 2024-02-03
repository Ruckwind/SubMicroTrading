package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.NewOrderAckImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class NewOrderAckFactory implements PoolFactory<NewOrderAckImpl> {

    private SuperPool<NewOrderAckImpl> _superPool;

    private NewOrderAckImpl _root;

    public NewOrderAckFactory(  SuperPool<NewOrderAckImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public NewOrderAckImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        NewOrderAckImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
