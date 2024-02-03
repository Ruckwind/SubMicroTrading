package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.OpenPriceEventImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class OpenPriceEventFactory implements PoolFactory<OpenPriceEventImpl> {

    private SuperPool<OpenPriceEventImpl> _superPool;

    private OpenPriceEventImpl _root;

    public OpenPriceEventFactory(  SuperPool<OpenPriceEventImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public OpenPriceEventImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        OpenPriceEventImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
