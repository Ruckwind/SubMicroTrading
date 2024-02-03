package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClientNewOrderAckImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ClientNewOrderAckFactory implements PoolFactory<ClientNewOrderAckImpl> {

    private SuperPool<ClientNewOrderAckImpl> _superPool;

    private ClientNewOrderAckImpl _root;

    public ClientNewOrderAckFactory(  SuperPool<ClientNewOrderAckImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ClientNewOrderAckImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ClientNewOrderAckImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
