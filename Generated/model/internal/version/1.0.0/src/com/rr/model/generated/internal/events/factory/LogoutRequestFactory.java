package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.LogoutRequestImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class LogoutRequestFactory implements PoolFactory<LogoutRequestImpl> {

    private SuperPool<LogoutRequestImpl> _superPool;

    private LogoutRequestImpl _root;

    public LogoutRequestFactory(  SuperPool<LogoutRequestImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public LogoutRequestImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        LogoutRequestImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
