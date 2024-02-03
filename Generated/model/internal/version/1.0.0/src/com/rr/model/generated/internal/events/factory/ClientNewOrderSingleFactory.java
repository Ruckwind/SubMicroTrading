package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ClientNewOrderSingleFactory implements PoolFactory<ClientNewOrderSingleImpl> {

    private SuperPool<ClientNewOrderSingleImpl> _superPool;

    private ClientNewOrderSingleImpl _root;

    public ClientNewOrderSingleFactory(  SuperPool<ClientNewOrderSingleImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ClientNewOrderSingleImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ClientNewOrderSingleImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
