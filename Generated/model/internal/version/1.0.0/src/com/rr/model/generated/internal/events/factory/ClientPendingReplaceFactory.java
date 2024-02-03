package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClientPendingReplaceImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ClientPendingReplaceFactory implements PoolFactory<ClientPendingReplaceImpl> {

    private SuperPool<ClientPendingReplaceImpl> _superPool;

    private ClientPendingReplaceImpl _root;

    public ClientPendingReplaceFactory(  SuperPool<ClientPendingReplaceImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ClientPendingReplaceImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ClientPendingReplaceImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
