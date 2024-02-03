package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.BookAddOrderImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class BookAddOrderFactory implements PoolFactory<BookAddOrderImpl> {

    private SuperPool<BookAddOrderImpl> _superPool;

    private BookAddOrderImpl _root;

    public BookAddOrderFactory(  SuperPool<BookAddOrderImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public BookAddOrderImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        BookAddOrderImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
