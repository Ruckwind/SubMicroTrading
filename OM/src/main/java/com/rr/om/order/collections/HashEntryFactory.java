/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.order.collections;

import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public final class HashEntryFactory implements PoolFactory<HashEntry> {

    private SuperPool<HashEntry> _superPool;

    private HashEntry _root;

    public HashEntryFactory( SuperPool<HashEntry> superPool ) {
        _superPool = superPool;
        _root      = _superPool.getChain();
    }

    @Override
    public HashEntry get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        HashEntry obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
