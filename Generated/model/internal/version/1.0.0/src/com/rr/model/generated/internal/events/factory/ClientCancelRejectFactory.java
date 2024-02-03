package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClientCancelRejectImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ClientCancelRejectFactory implements PoolFactory<ClientCancelRejectImpl> {

    private SuperPool<ClientCancelRejectImpl> _superPool;

    private ClientCancelRejectImpl _root;

    public ClientCancelRejectFactory(  SuperPool<ClientCancelRejectImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ClientCancelRejectImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ClientCancelRejectImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
