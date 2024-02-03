package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClientVagueOrderRejectImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ClientVagueOrderRejectFactory implements PoolFactory<ClientVagueOrderRejectImpl> {

    private SuperPool<ClientVagueOrderRejectImpl> _superPool;

    private ClientVagueOrderRejectImpl _root;

    public ClientVagueOrderRejectFactory(  SuperPool<ClientVagueOrderRejectImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ClientVagueOrderRejectImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ClientVagueOrderRejectImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
