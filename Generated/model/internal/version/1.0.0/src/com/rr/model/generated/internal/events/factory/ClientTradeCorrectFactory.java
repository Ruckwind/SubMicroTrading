package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClientTradeCorrectImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ClientTradeCorrectFactory implements PoolFactory<ClientTradeCorrectImpl> {

    private SuperPool<ClientTradeCorrectImpl> _superPool;

    private ClientTradeCorrectImpl _root;

    public ClientTradeCorrectFactory(  SuperPool<ClientTradeCorrectImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ClientTradeCorrectImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ClientTradeCorrectImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
