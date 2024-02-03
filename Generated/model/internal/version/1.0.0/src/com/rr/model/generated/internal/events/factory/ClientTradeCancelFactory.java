package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClientTradeCancelImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ClientTradeCancelFactory implements PoolFactory<ClientTradeCancelImpl> {

    private SuperPool<ClientTradeCancelImpl> _superPool;

    private ClientTradeCancelImpl _root;

    public ClientTradeCancelFactory(  SuperPool<ClientTradeCancelImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ClientTradeCancelImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ClientTradeCancelImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
