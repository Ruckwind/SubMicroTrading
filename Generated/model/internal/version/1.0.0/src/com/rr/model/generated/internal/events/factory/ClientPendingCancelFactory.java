package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClientPendingCancelImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ClientPendingCancelFactory implements PoolFactory<ClientPendingCancelImpl> {

    private SuperPool<ClientPendingCancelImpl> _superPool;

    private ClientPendingCancelImpl _root;

    public ClientPendingCancelFactory(  SuperPool<ClientPendingCancelImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ClientPendingCancelImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ClientPendingCancelImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
