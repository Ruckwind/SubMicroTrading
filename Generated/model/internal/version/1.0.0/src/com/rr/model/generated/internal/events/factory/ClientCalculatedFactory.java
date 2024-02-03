package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClientCalculatedImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ClientCalculatedFactory implements PoolFactory<ClientCalculatedImpl> {

    private SuperPool<ClientCalculatedImpl> _superPool;

    private ClientCalculatedImpl _root;

    public ClientCalculatedFactory(  SuperPool<ClientCalculatedImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ClientCalculatedImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ClientCalculatedImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
