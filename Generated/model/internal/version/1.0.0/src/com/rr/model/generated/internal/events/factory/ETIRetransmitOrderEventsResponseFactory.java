package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ETIRetransmitOrderEventsResponseImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ETIRetransmitOrderEventsResponseFactory implements PoolFactory<ETIRetransmitOrderEventsResponseImpl> {

    private SuperPool<ETIRetransmitOrderEventsResponseImpl> _superPool;

    private ETIRetransmitOrderEventsResponseImpl _root;

    public ETIRetransmitOrderEventsResponseFactory(  SuperPool<ETIRetransmitOrderEventsResponseImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ETIRetransmitOrderEventsResponseImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ETIRetransmitOrderEventsResponseImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
