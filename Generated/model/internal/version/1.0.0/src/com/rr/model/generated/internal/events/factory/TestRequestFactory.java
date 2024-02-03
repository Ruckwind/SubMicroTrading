package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.TestRequestImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class TestRequestFactory implements PoolFactory<TestRequestImpl> {

    private SuperPool<TestRequestImpl> _superPool;

    private TestRequestImpl _root;

    public TestRequestFactory(  SuperPool<TestRequestImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public TestRequestImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        TestRequestImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
