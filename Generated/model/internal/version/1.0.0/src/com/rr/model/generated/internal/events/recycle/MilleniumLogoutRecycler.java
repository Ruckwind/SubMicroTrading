package com.rr.model.generated.internal.events.recycle;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MilleniumLogoutImpl;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.SuperPool;
import com.rr.core.lang.Constants;
import com.rr.core.pool.RuntimePoolingException;

public class MilleniumLogoutRecycler implements Recycler<MilleniumLogoutImpl> {

    private SuperPool<MilleniumLogoutImpl> _superPool;

    private MilleniumLogoutImpl _root;

    private int          _recycleSize;
    private int          _count = 0;
    public MilleniumLogoutRecycler( int recycleSize, SuperPool<MilleniumLogoutImpl> superPool ) {
        _superPool = superPool;
        _recycleSize = recycleSize;
        try {
            _root    = MilleniumLogoutImpl.class.newInstance();
        } catch( Exception e ) {
            throw new RuntimePoolingException( "Unable to create recycle root for MilleniumLogoutImpl : " + e.getMessage(), e );
        }
    }


    @Override public void recycle( MilleniumLogoutImpl obj ) {
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
