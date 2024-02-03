package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.OpenInterestEventImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class OpenInterestEventFactory implements PoolFactory<OpenInterestEventImpl> {

    private SuperPool<OpenInterestEventImpl> _superPool;

    private OpenInterestEventImpl _root;

    public OpenInterestEventFactory(  SuperPool<OpenInterestEventImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public OpenInterestEventImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        OpenInterestEventImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
