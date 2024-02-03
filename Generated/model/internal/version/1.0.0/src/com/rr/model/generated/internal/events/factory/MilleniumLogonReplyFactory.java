package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MilleniumLogonReplyImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MilleniumLogonReplyFactory implements PoolFactory<MilleniumLogonReplyImpl> {

    private SuperPool<MilleniumLogonReplyImpl> _superPool;

    private MilleniumLogonReplyImpl _root;

    public MilleniumLogonReplyFactory(  SuperPool<MilleniumLogonReplyImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MilleniumLogonReplyImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MilleniumLogonReplyImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
