package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClientDoneForDayImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ClientDoneForDayFactory implements PoolFactory<ClientDoneForDayImpl> {

    private SuperPool<ClientDoneForDayImpl> _superPool;

    private ClientDoneForDayImpl _root;

    public ClientDoneForDayFactory(  SuperPool<ClientDoneForDayImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ClientDoneForDayImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ClientDoneForDayImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
