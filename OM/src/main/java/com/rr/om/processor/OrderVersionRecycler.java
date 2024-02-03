/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.processor;

import com.rr.core.lang.Constants;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.RuntimePoolingException;
import com.rr.core.pool.SuperPool;
import com.rr.om.order.OrderVersion;

public class OrderVersionRecycler implements Recycler<OrderVersion> {

    private SuperPool<OrderVersion> _superPool;

    private OrderVersion _root;

    private int _recycleSize;
    private int _count = 0;

    public OrderVersionRecycler( int recycleSize, SuperPool<OrderVersion> superPool ) {

        _superPool   = superPool;
        _root        = _superPool.getChain();
        _recycleSize = recycleSize;

        try {
            _root = OrderVersion.class.newInstance();
        } catch( Exception e ) {
            throw new RuntimePoolingException( "Unable to create recycle root for OrderVersion : " + e.getMessage(), e );
        }
    }

    @Override
    public void recycle( OrderVersion obj ) {

        if ( obj == null ) return;

        if ( Constants.DISABLE_RECYCLING ) return;

        if ( obj.getNext() == null ) {
            obj.reset();
            obj.setNext( _root.getNext() );
            _root.setNext( obj );

            if ( ++_count == _recycleSize ) {
                _superPool.returnChain( _root.getNext() );
                _root.setNext( null );
                _count = 0;
            }
        }
    }
}
