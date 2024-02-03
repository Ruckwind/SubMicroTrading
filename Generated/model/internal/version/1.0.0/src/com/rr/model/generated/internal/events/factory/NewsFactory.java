package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.NewsImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class NewsFactory implements PoolFactory<NewsImpl> {

    private SuperPool<NewsImpl> _superPool;

    private NewsImpl _root;

    public NewsFactory(  SuperPool<NewsImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public NewsImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        NewsImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
