package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ETIUnsubscribeImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ETIUnsubscribeFactory implements PoolFactory<ETIUnsubscribeImpl> {

    private SuperPool<ETIUnsubscribeImpl> _superPool;

    private ETIUnsubscribeImpl _root;

    public ETIUnsubscribeFactory(  SuperPool<ETIUnsubscribeImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ETIUnsubscribeImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ETIUnsubscribeImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
