package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.SessionRejectImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class SessionRejectFactory implements PoolFactory<SessionRejectImpl> {

    private SuperPool<SessionRejectImpl> _superPool;

    private SessionRejectImpl _root;

    public SessionRejectFactory(  SuperPool<SessionRejectImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public SessionRejectImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        SessionRejectImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
