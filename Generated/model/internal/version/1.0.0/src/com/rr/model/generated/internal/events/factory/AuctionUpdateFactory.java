package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.AuctionUpdateImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class AuctionUpdateFactory implements PoolFactory<AuctionUpdateImpl> {

    private SuperPool<AuctionUpdateImpl> _superPool;

    private AuctionUpdateImpl _root;

    public AuctionUpdateFactory(  SuperPool<AuctionUpdateImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public AuctionUpdateImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        AuctionUpdateImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
