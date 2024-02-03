package com.rr.model.generated.internal.events.recycle;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.SecurityTradingStatusEventImpl;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.SuperPool;
import com.rr.core.lang.Constants;
import com.rr.core.pool.RuntimePoolingException;

public class SecurityTradingStatusEventRecycler implements Recycler<SecurityTradingStatusEventImpl> {

    private SuperPool<SecurityTradingStatusEventImpl> _superPool;

    private SecurityTradingStatusEventImpl _root;

    private int          _recycleSize;
    private int          _count = 0;
    public SecurityTradingStatusEventRecycler( int recycleSize, SuperPool<SecurityTradingStatusEventImpl> superPool ) {
        _superPool = superPool;
        _recycleSize = recycleSize;
        try {
            _root    = SecurityTradingStatusEventImpl.class.newInstance();
        } catch( Exception e ) {
            throw new RuntimePoolingException( "Unable to create recycle root for SecurityTradingStatusEventImpl : " + e.getMessage(), e );
        }
    }


    @Override public void recycle( SecurityTradingStatusEventImpl obj ) {
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
