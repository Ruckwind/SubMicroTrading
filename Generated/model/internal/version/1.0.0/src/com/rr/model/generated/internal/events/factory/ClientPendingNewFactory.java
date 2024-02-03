package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClientPendingNewImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ClientPendingNewFactory implements PoolFactory<ClientPendingNewImpl> {

    private SuperPool<ClientPendingNewImpl> _superPool;

    private ClientPendingNewImpl _root;

    public ClientPendingNewFactory(  SuperPool<ClientPendingNewImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ClientPendingNewImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ClientPendingNewImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
