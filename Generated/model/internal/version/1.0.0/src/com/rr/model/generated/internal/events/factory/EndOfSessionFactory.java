package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.EndOfSessionImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class EndOfSessionFactory implements PoolFactory<EndOfSessionImpl> {

    private SuperPool<EndOfSessionImpl> _superPool;

    private EndOfSessionImpl _root;

    public EndOfSessionFactory(  SuperPool<EndOfSessionImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public EndOfSessionImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        EndOfSessionImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
