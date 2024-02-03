package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClientExpiredImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ClientExpiredFactory implements PoolFactory<ClientExpiredImpl> {

    private SuperPool<ClientExpiredImpl> _superPool;

    private ClientExpiredImpl _root;

    public ClientExpiredFactory(  SuperPool<ClientExpiredImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ClientExpiredImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ClientExpiredImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
