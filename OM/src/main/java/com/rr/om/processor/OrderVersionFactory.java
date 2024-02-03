/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.processor;

import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;
import com.rr.om.order.OrderVersion;

public class OrderVersionFactory implements PoolFactory<OrderVersion> {

    private SuperPool<OrderVersion> _superPool;

    private OrderVersion _root;

    public OrderVersionFactory( SuperPool<OrderVersion> superPool ) {
        _superPool = superPool;
        _root      = _superPool.getChain();
    }

    @Override
    public OrderVersion get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        OrderVersion obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
