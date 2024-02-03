package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.SDFeedTypeImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class SDFeedTypeFactory implements PoolFactory<SDFeedTypeImpl> {

    private SuperPool<SDFeedTypeImpl> _superPool;

    private SDFeedTypeImpl _root;

    public SDFeedTypeFactory(  SuperPool<SDFeedTypeImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public SDFeedTypeImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        SDFeedTypeImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
