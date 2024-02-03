package com.rr.model.generated.internal.events.recycle;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.AuctionUpdateImpl;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.SuperPool;
import com.rr.core.lang.Constants;
import com.rr.core.pool.RuntimePoolingException;

public class AuctionUpdateRecycler implements Recycler<AuctionUpdateImpl> {

    private SuperPool<AuctionUpdateImpl> _superPool;

    private AuctionUpdateImpl _root;

    private int          _recycleSize;
    private int          _count = 0;
    public AuctionUpdateRecycler( int recycleSize, SuperPool<AuctionUpdateImpl> superPool ) {
        _superPool = superPool;
        _recycleSize = recycleSize;
        try {
            _root    = AuctionUpdateImpl.class.newInstance();
        } catch( Exception e ) {
            throw new RuntimePoolingException( "Unable to create recycle root for AuctionUpdateImpl : " + e.getMessage(), e );
        }
    }


    @Override public void recycle( AuctionUpdateImpl obj ) {
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
