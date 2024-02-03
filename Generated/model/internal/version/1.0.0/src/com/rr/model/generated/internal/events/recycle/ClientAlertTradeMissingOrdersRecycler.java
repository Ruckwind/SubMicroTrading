package com.rr.model.generated.internal.events.recycle;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.ClientAlertTradeMissingOrdersImpl;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.SuperPool;
import com.rr.core.lang.Constants;
import com.rr.core.pool.RuntimePoolingException;

public class ClientAlertTradeMissingOrdersRecycler implements Recycler<ClientAlertTradeMissingOrdersImpl> {

    private SuperPool<ClientAlertTradeMissingOrdersImpl> _superPool;

    private ClientAlertTradeMissingOrdersImpl _root;

    private int          _recycleSize;
    private int          _count = 0;
    public ClientAlertTradeMissingOrdersRecycler( int recycleSize, SuperPool<ClientAlertTradeMissingOrdersImpl> superPool ) {
        _superPool = superPool;
        _recycleSize = recycleSize;
        try {
            _root    = ClientAlertTradeMissingOrdersImpl.class.newInstance();
        } catch( Exception e ) {
            throw new RuntimePoolingException( "Unable to create recycle root for ClientAlertTradeMissingOrdersImpl : " + e.getMessage(), e );
        }
    }


    @Override public void recycle( ClientAlertTradeMissingOrdersImpl obj ) {
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
