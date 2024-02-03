/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.factories;

import com.rr.core.lang.ReusableString;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

// ResuableString is used alot this factory is faster than generics

public class ReusableStringFactory implements PoolFactory<ReusableString> {

    private SuperPool<ReusableString> _superPool;

    private ReusableString _root;

    public ReusableStringFactory( SuperPool<ReusableString> superPool ) {
        _superPool = superPool;
        _root      = _superPool.getChain();
    }

    @Override
    public ReusableString get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ReusableString obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
