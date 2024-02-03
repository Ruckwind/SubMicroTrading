package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClientForceCancelImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ClientForceCancelFactory implements PoolFactory<ClientForceCancelImpl> {

    private SuperPool<ClientForceCancelImpl> _superPool;

    private ClientForceCancelImpl _root;

    public ClientForceCancelFactory(  SuperPool<ClientForceCancelImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ClientForceCancelImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ClientForceCancelImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
