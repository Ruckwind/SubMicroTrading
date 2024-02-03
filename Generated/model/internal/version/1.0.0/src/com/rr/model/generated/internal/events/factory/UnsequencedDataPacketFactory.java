package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.UnsequencedDataPacketImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class UnsequencedDataPacketFactory implements PoolFactory<UnsequencedDataPacketImpl> {

    private SuperPool<UnsequencedDataPacketImpl> _superPool;

    private UnsequencedDataPacketImpl _root;

    public UnsequencedDataPacketFactory(  SuperPool<UnsequencedDataPacketImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public UnsequencedDataPacketImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        UnsequencedDataPacketImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
