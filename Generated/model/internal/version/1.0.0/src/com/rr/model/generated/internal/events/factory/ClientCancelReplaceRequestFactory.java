package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClientCancelReplaceRequestImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ClientCancelReplaceRequestFactory implements PoolFactory<ClientCancelReplaceRequestImpl> {

    private SuperPool<ClientCancelReplaceRequestImpl> _superPool;

    private ClientCancelReplaceRequestImpl _root;

    public ClientCancelReplaceRequestFactory(  SuperPool<ClientCancelReplaceRequestImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ClientCancelReplaceRequestImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ClientCancelReplaceRequestImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
