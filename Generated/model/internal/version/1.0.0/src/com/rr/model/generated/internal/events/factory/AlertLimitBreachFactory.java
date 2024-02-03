package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.AlertLimitBreachImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class AlertLimitBreachFactory implements PoolFactory<AlertLimitBreachImpl> {

    private SuperPool<AlertLimitBreachImpl> _superPool;

    private AlertLimitBreachImpl _root;

    public AlertLimitBreachFactory(  SuperPool<AlertLimitBreachImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public AlertLimitBreachImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        AlertLimitBreachImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
