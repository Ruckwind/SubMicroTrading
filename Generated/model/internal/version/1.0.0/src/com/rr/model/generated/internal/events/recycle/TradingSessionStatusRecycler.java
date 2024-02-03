package com.rr.model.generated.internal.events.recycle;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.TradingSessionStatusImpl;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.SuperPool;
import com.rr.core.lang.Constants;
import com.rr.core.pool.RuntimePoolingException;

public class TradingSessionStatusRecycler implements Recycler<TradingSessionStatusImpl> {

    private SuperPool<TradingSessionStatusImpl> _superPool;

    private TradingSessionStatusImpl _root;

    private int          _recycleSize;
    private int          _count = 0;
    public TradingSessionStatusRecycler( int recycleSize, SuperPool<TradingSessionStatusImpl> superPool ) {
        _superPool = superPool;
        _recycleSize = recycleSize;
        try {
            _root    = TradingSessionStatusImpl.class.newInstance();
        } catch( Exception e ) {
            throw new RuntimePoolingException( "Unable to create recycle root for TradingSessionStatusImpl : " + e.getMessage(), e );
        }
    }


    @Override public void recycle( TradingSessionStatusImpl obj ) {
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
