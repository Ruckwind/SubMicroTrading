package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MilleniumMissedMsgReportImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class MilleniumMissedMsgReportFactory implements PoolFactory<MilleniumMissedMsgReportImpl> {

    private SuperPool<MilleniumMissedMsgReportImpl> _superPool;

    private MilleniumMissedMsgReportImpl _root;

    public MilleniumMissedMsgReportFactory(  SuperPool<MilleniumMissedMsgReportImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public MilleniumMissedMsgReportImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        MilleniumMissedMsgReportImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
