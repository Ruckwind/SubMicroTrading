package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.BookClearImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class BookClearFactory implements PoolFactory<BookClearImpl> {

    private SuperPool<BookClearImpl> _superPool;

    private BookClearImpl _root;

    public BookClearFactory(  SuperPool<BookClearImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public BookClearImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        BookClearImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
