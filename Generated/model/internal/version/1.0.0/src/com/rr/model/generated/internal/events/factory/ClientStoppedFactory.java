package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClientStoppedImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ClientStoppedFactory implements PoolFactory<ClientStoppedImpl> {

    private SuperPool<ClientStoppedImpl> _superPool;

    private ClientStoppedImpl _root;

    public ClientStoppedFactory(  SuperPool<ClientStoppedImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ClientStoppedImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ClientStoppedImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
