/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.processor;

import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;
import com.rr.om.order.OrderImpl;

public class OrderFactory implements PoolFactory<OrderImpl> {

    private SuperPool<OrderImpl> _superPool;

    private OrderImpl _root;

    public OrderFactory( SuperPool<OrderImpl> superPool ) {
        _superPool = superPool;
        _root      = _superPool.getChain();
    }

    @Override
    public OrderImpl get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        OrderImpl obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
