package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.UTPLogonImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class UTPLogonFactory implements PoolFactory<UTPLogonImpl> {

    private SuperPool<UTPLogonImpl> _superPool;

    private UTPLogonImpl _root;

    public UTPLogonFactory(  SuperPool<UTPLogonImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public UTPLogonImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        UTPLogonImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
