package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClientIgnoredExecImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ClientIgnoredExecFactory implements PoolFactory<ClientIgnoredExecImpl> {

    private SuperPool<ClientIgnoredExecImpl> _superPool;

    private ClientIgnoredExecImpl _root;

    public ClientIgnoredExecFactory(  SuperPool<ClientIgnoredExecImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ClientIgnoredExecImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ClientIgnoredExecImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
