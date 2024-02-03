package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ETISubscribeImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ETISubscribeFactory implements PoolFactory<ETISubscribeImpl> {

    private SuperPool<ETISubscribeImpl> _superPool;

    private ETISubscribeImpl _root;

    public ETISubscribeFactory(  SuperPool<ETISubscribeImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ETISubscribeImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ETISubscribeImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
