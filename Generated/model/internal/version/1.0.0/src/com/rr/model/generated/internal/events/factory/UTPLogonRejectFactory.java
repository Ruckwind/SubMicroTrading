package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.UTPLogonRejectImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class UTPLogonRejectFactory implements PoolFactory<UTPLogonRejectImpl> {

    private SuperPool<UTPLogonRejectImpl> _superPool;

    private UTPLogonRejectImpl _root;

    public UTPLogonRejectFactory(  SuperPool<UTPLogonRejectImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public UTPLogonRejectImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        UTPLogonRejectImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
