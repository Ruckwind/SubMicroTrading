package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.SecurityTradingStatusEventImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class SecurityTradingStatusEventFactory implements PoolFactory<SecurityTradingStatusEventImpl> {

    private SuperPool<SecurityTradingStatusEventImpl> _superPool;

    private SecurityTradingStatusEventImpl _root;

    public SecurityTradingStatusEventFactory(  SuperPool<SecurityTradingStatusEventImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public SecurityTradingStatusEventImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        SecurityTradingStatusEventImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
