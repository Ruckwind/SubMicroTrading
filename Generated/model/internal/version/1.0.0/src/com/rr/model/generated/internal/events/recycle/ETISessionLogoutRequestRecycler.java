package com.rr.model.generated.internal.events.recycle;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ETISessionLogoutRequestImpl;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.SuperPool;
import com.rr.core.lang.Constants;
import com.rr.core.pool.RuntimePoolingException;

public class ETISessionLogoutRequestRecycler implements Recycler<ETISessionLogoutRequestImpl> {

    private SuperPool<ETISessionLogoutRequestImpl> _superPool;

    private ETISessionLogoutRequestImpl _root;

    private int          _recycleSize;
    private int          _count = 0;
    public ETISessionLogoutRequestRecycler( int recycleSize, SuperPool<ETISessionLogoutRequestImpl> superPool ) {
        _superPool = superPool;
        _recycleSize = recycleSize;
        try {
            _root    = ETISessionLogoutRequestImpl.class.newInstance();
        } catch( Exception e ) {
            throw new RuntimePoolingException( "Unable to create recycle root for ETISessionLogoutRequestImpl : " + e.getMessage(), e );
        }
    }


    @Override public void recycle( ETISessionLogoutRequestImpl obj ) {
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
