package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MDSnapshotFullRefreshImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MDSnapshotFullRefreshFactory implements PoolFactory<MDSnapshotFullRefreshImpl> {

    private SuperPool<MDSnapshotFullRefreshImpl> _superPool;

    private MDSnapshotFullRefreshImpl _root;

    public MDSnapshotFullRefreshFactory(  SuperPool<MDSnapshotFullRefreshImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MDSnapshotFullRefreshImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MDSnapshotFullRefreshImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
