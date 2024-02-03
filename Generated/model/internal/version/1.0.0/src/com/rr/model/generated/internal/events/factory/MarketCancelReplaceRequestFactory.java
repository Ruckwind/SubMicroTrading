package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MarketCancelReplaceRequestImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MarketCancelReplaceRequestFactory implements PoolFactory<MarketCancelReplaceRequestImpl> {

    private SuperPool<MarketCancelReplaceRequestImpl> _superPool;

    private MarketCancelReplaceRequestImpl _root;

    public MarketCancelReplaceRequestFactory(  SuperPool<MarketCancelReplaceRequestImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MarketCancelReplaceRequestImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MarketCancelReplaceRequestImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
