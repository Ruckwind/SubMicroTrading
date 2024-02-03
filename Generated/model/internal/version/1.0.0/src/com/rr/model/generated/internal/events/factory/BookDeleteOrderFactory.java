package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.BookDeleteOrderImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class BookDeleteOrderFactory implements PoolFactory<BookDeleteOrderImpl> {

    private SuperPool<BookDeleteOrderImpl> _superPool;

    private BookDeleteOrderImpl _root;

    public BookDeleteOrderFactory(  SuperPool<BookDeleteOrderImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public BookDeleteOrderImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        BookDeleteOrderImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
