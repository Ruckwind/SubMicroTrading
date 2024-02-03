package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClientResyncSentMsgsImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ClientResyncSentMsgsFactory implements PoolFactory<ClientResyncSentMsgsImpl> {

    private SuperPool<ClientResyncSentMsgsImpl> _superPool;

    private ClientResyncSentMsgsImpl _root;

    public ClientResyncSentMsgsFactory(  SuperPool<ClientResyncSentMsgsImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ClientResyncSentMsgsImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ClientResyncSentMsgsImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
