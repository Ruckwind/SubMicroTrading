package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ETIConnectionGatewayResponseImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ETIConnectionGatewayResponseFactory implements PoolFactory<ETIConnectionGatewayResponseImpl> {

    private SuperPool<ETIConnectionGatewayResponseImpl> _superPool;

    private ETIConnectionGatewayResponseImpl _root;

    public ETIConnectionGatewayResponseFactory(  SuperPool<ETIConnectionGatewayResponseImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ETIConnectionGatewayResponseImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ETIConnectionGatewayResponseImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
