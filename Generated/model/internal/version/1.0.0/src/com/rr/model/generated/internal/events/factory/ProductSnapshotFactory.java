package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ProductSnapshotImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ProductSnapshotFactory implements PoolFactory<ProductSnapshotImpl> {

    private SuperPool<ProductSnapshotImpl> _superPool;

    private ProductSnapshotImpl _root;

    public ProductSnapshotFactory(  SuperPool<ProductSnapshotImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ProductSnapshotImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ProductSnapshotImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
