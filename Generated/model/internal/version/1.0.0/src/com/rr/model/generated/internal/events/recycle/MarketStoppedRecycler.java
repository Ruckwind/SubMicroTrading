package com.rr.model.generated.internal.events.recycle;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MarketStoppedImpl;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.SuperPool;
import com.rr.core.lang.Constants;
import com.rr.core.pool.RuntimePoolingException;

public class MarketStoppedRecycler implements Recycler<MarketStoppedImpl> {

    private SuperPool<MarketStoppedImpl> _superPool;

    private MarketStoppedImpl _root;

    private int          _recycleSize;
    private int          _count = 0;
    public MarketStoppedRecycler( int recycleSize, SuperPool<MarketStoppedImpl> superPool ) {
        _superPool = superPool;
        _recycleSize = recycleSize;
        try {
            _root    = MarketStoppedImpl.class.newInstance();
        } catch( Exception e ) {
            throw new RuntimePoolingException( "Unable to create recycle root for MarketStoppedImpl : " + e.getMessage(), e );
        }
    }


    @Override public void recycle( MarketStoppedImpl obj ) {
        if ( Constants.DISABLE_RECYCLING ) return;
        if ( obj == null ) return;
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
