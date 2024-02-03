package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.LogonImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class LogonFactory implements PoolFactory<LogonImpl> {

    private SuperPool<LogonImpl> _superPool;

    private LogonImpl _root;

    public LogonFactory(  SuperPool<LogonImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public LogonImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        LogonImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
