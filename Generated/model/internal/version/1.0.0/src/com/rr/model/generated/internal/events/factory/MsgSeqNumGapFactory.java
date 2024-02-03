package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MsgSeqNumGapImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MsgSeqNumGapFactory implements PoolFactory<MsgSeqNumGapImpl> {

    private SuperPool<MsgSeqNumGapImpl> _superPool;

    private MsgSeqNumGapImpl _root;

    public MsgSeqNumGapFactory(  SuperPool<MsgSeqNumGapImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MsgSeqNumGapImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MsgSeqNumGapImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
