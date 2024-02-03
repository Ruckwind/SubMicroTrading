package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.LogoutImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class LogoutFactory implements PoolFactory<LogoutImpl> {

    private SuperPool<LogoutImpl> _superPool;

    private LogoutImpl _root;

    public LogoutFactory(  SuperPool<LogoutImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public LogoutImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        LogoutImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
