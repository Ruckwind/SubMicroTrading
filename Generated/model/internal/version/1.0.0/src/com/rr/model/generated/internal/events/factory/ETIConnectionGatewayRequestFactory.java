package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ETIConnectionGatewayRequestImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ETIConnectionGatewayRequestFactory implements PoolFactory<ETIConnectionGatewayRequestImpl> {

    private SuperPool<ETIConnectionGatewayRequestImpl> _superPool;

    private ETIConnectionGatewayRequestImpl _root;

    public ETIConnectionGatewayRequestFactory(  SuperPool<ETIConnectionGatewayRequestImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ETIConnectionGatewayRequestImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ETIConnectionGatewayRequestImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
