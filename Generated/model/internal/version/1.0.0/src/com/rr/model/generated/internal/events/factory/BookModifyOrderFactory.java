package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.BookModifyOrderImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class BookModifyOrderFactory implements PoolFactory<BookModifyOrderImpl> {

    private SuperPool<BookModifyOrderImpl> _superPool;

    private BookModifyOrderImpl _root;

    public BookModifyOrderFactory(  SuperPool<BookModifyOrderImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public BookModifyOrderImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        BookModifyOrderImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
