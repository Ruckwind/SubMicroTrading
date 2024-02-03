package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.SoupDebugPacketImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class SoupDebugPacketFactory implements PoolFactory<SoupDebugPacketImpl> {

    private SuperPool<SoupDebugPacketImpl> _superPool;

    private SoupDebugPacketImpl _root;

    public SoupDebugPacketFactory(  SuperPool<SoupDebugPacketImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public SoupDebugPacketImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        SoupDebugPacketImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
