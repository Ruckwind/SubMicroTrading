package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClientTradeNewImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ClientTradeNewFactory implements PoolFactory<ClientTradeNewImpl> {

    private SuperPool<ClientTradeNewImpl> _superPool;

    private ClientTradeNewImpl _root;

    public ClientTradeNewFactory(  SuperPool<ClientTradeNewImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ClientTradeNewImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ClientTradeNewImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
