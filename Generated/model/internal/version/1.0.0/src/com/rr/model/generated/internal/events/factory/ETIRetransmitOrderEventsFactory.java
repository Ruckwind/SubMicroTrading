package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ETIRetransmitOrderEventsImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ETIRetransmitOrderEventsFactory implements PoolFactory<ETIRetransmitOrderEventsImpl> {

    private SuperPool<ETIRetransmitOrderEventsImpl> _superPool;

    private ETIRetransmitOrderEventsImpl _root;

    public ETIRetransmitOrderEventsFactory(  SuperPool<ETIRetransmitOrderEventsImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ETIRetransmitOrderEventsImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ETIRetransmitOrderEventsImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
