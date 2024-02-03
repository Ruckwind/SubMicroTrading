package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ETISessionLogoutNotificationImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ETISessionLogoutNotificationFactory implements PoolFactory<ETISessionLogoutNotificationImpl> {

    private SuperPool<ETISessionLogoutNotificationImpl> _superPool;

    private ETISessionLogoutNotificationImpl _root;

    public ETISessionLogoutNotificationFactory(  SuperPool<ETISessionLogoutNotificationImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ETISessionLogoutNotificationImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ETISessionLogoutNotificationImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
