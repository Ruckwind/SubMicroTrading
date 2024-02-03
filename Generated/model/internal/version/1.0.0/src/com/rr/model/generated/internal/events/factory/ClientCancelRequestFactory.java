package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClientCancelRequestImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ClientCancelRequestFactory implements PoolFactory<ClientCancelRequestImpl> {

    private SuperPool<ClientCancelRequestImpl> _superPool;

    private ClientCancelRequestImpl _root;

    public ClientCancelRequestFactory(  SuperPool<ClientCancelRequestImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ClientCancelRequestImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ClientCancelRequestImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
