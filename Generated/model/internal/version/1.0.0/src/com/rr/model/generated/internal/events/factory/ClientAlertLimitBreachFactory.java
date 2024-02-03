package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClientAlertLimitBreachImpl;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class ClientAlertLimitBreachFactory implements PoolFactory<ClientAlertLimitBreachImpl> {

    private SuperPool<ClientAlertLimitBreachImpl> _superPool;

    private ClientAlertLimitBreachImpl _root;

    public ClientAlertLimitBreachFactory(  SuperPool<ClientAlertLimitBreachImpl> superPool ) {
        _superPool = superPool;
        _root = _superPool.getChain();
    }


    @Override public ClientAlertLimitBreachImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        ClientAlertLimitBreachImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
