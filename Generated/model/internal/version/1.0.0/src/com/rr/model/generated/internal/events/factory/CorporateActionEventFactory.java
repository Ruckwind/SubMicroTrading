package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.CorporateActionEventImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class CorporateActionEventFactory implements PoolFactory<CorporateActionEventImpl> {

    private SuperPool<CorporateActionEventImpl> _superPool;

    private CorporateActionEventImpl _root;

    public CorporateActionEventFactory(  SuperPool<CorporateActionEventImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public CorporateActionEventImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        CorporateActionEventImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
