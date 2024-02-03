package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.AuctionSummaryImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class AuctionSummaryFactory implements PoolFactory<AuctionSummaryImpl> {

    private SuperPool<AuctionSummaryImpl> _superPool;

    private AuctionSummaryImpl _root;

    public AuctionSummaryFactory(  SuperPool<AuctionSummaryImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public AuctionSummaryImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        AuctionSummaryImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
