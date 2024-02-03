package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClientRejectedImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ClientRejectedFactory implements PoolFactory<ClientRejectedImpl> {

    private SuperPool<ClientRejectedImpl> _superPool;

    private ClientRejectedImpl _root;

    public ClientRejectedFactory(  SuperPool<ClientRejectedImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ClientRejectedImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ClientRejectedImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
