/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.mds.common.events;

import com.rr.core.lang.Constants;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.RuntimePoolingException;
import com.rr.core.pool.SuperPool;

public class TradingRangeUpdateRecycler implements Recycler<TradingRangeUpdate> {

    private final SuperPool<TradingRangeUpdate> _superPool;
    private final int _recycleSize;
    private TradingRangeUpdate _root;
    private       int _count = 0;

    public TradingRangeUpdateRecycler( int recycleSize, SuperPool<TradingRangeUpdate> superPool ) {
        _superPool   = superPool;
        _root        = _superPool.getChain();
        _recycleSize = recycleSize;
        try {
            _root = TradingRangeUpdate.class.newInstance();
        } catch( Exception e ) {
            throw new RuntimePoolingException( "Unable to create recycle root for TradingRangeUpdate : " + e.getMessage(), e );
        }
    }

    @Override
    public final void recycle( TradingRangeUpdate obj ) {
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
