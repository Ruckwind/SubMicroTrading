package com.rr.model.generated.internal.events.recycle;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MarketDoneForDayImpl;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.SuperPool;
import com.rr.core.lang.Constants;
import com.rr.core.pool.RuntimePoolingException;

public class MarketDoneForDayRecycler implements Recycler<MarketDoneForDayImpl> {

    private SuperPool<MarketDoneForDayImpl> _superPool;

    private MarketDoneForDayImpl _root;

    private int          _recycleSize;
    private int          _count = 0;
    public MarketDoneForDayRecycler( int recycleSize, SuperPool<MarketDoneForDayImpl> superPool ) {
        _superPool = superPool;
        _recycleSize = recycleSize;
        try {
            _root    = MarketDoneForDayImpl.class.newInstance();
        } catch( Exception e ) {
            throw new RuntimePoolingException( "Unable to create recycle root for MarketDoneForDayImpl : " + e.getMessage(), e );
        }
    }


    @Override public void recycle( MarketDoneForDayImpl obj ) {
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
