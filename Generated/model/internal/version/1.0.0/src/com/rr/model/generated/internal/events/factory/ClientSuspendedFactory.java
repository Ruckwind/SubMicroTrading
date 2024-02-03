package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClientSuspendedImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ClientSuspendedFactory implements PoolFactory<ClientSuspendedImpl> {

    private SuperPool<ClientSuspendedImpl> _superPool;

    private ClientSuspendedImpl _root;

    public ClientSuspendedFactory(  SuperPool<ClientSuspendedImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ClientSuspendedImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ClientSuspendedImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
