/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public final class LongHashEntryFactory<T> implements PoolFactory<LongHashEntry<T>> {

    private SuperPool<LongHashEntry<T>> _superPool;

    private LongHashEntry<T> _root;

    public LongHashEntryFactory( SuperPool<LongHashEntry<T>> superPool ) {
        _superPool = superPool;
        _root      = _superPool.getChain();
    }

    @Override
    public LongHashEntry<T> get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        LongHashEntry<T> obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
