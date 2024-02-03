package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClientOrderStatusImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ClientOrderStatusFactory implements PoolFactory<ClientOrderStatusImpl> {

    private SuperPool<ClientOrderStatusImpl> _superPool;

    private ClientOrderStatusImpl _root;

    public ClientOrderStatusFactory(  SuperPool<ClientOrderStatusImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ClientOrderStatusImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ClientOrderStatusImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
