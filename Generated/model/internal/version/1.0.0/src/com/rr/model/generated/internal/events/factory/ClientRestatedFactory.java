package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClientRestatedImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ClientRestatedFactory implements PoolFactory<ClientRestatedImpl> {

    private SuperPool<ClientRestatedImpl> _superPool;

    private ClientRestatedImpl _root;

    public ClientRestatedFactory(  SuperPool<ClientRestatedImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ClientRestatedImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ClientRestatedImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
