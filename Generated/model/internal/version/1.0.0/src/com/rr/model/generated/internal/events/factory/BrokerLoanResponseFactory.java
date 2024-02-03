package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.BrokerLoanResponseImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class BrokerLoanResponseFactory implements PoolFactory<BrokerLoanResponseImpl> {

    private SuperPool<BrokerLoanResponseImpl> _superPool;

    private BrokerLoanResponseImpl _root;

    public BrokerLoanResponseFactory(  SuperPool<BrokerLoanResponseImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public BrokerLoanResponseImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        BrokerLoanResponseImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
