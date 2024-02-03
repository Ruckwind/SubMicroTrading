package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ETIRetransmitImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ETIRetransmitFactory implements PoolFactory<ETIRetransmitImpl> {

    private SuperPool<ETIRetransmitImpl> _superPool;

    private ETIRetransmitImpl _root;

    public ETIRetransmitFactory(  SuperPool<ETIRetransmitImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ETIRetransmitImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ETIRetransmitImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
