package com.rr.model.generated.internal.events.recycle;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MDUpdateImpl;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.SuperPool;
import com.rr.core.lang.Constants;
import com.rr.core.pool.RuntimePoolingException;
import com.rr.model.generated.internal.events.impl.TickUpdateImpl;
import com.rr.core.pool.SuperpoolManager;

public class MDUpdateRecycler implements Recycler<MDUpdateImpl> {

    private SuperPool<MDUpdateImpl> _superPool;


    private TickUpdateRecycler _tickUpdatesRecycler = SuperpoolManager.instance().getRecycler( TickUpdateRecycler.class, TickUpdateImpl.class );

    private MDUpdateImpl _root;

    private int          _recycleSize;
    private int          _count = 0;
    public MDUpdateRecycler( int recycleSize, SuperPool<MDUpdateImpl> superPool ) {
        _superPool = superPool;
        _recycleSize = recycleSize;
        try {
            _root    = MDUpdateImpl.class.newInstance();
        } catch( Exception e ) {
            throw new RuntimePoolingException( "Unable to create recycle root for MDUpdateImpl : " + e.getMessage(), e );
        }
    }


    @Override public void recycle( MDUpdateImpl obj ) {
        if ( Constants.DISABLE_RECYCLING ) return;
        if ( obj == null ) return;
        if ( obj.getNext() == null ) {
            TickUpdateImpl tickUpdates = (TickUpdateImpl) obj.getTickUpdates();
            while ( tickUpdates != null ) {
                TickUpdateImpl t = tickUpdates;
                tickUpdates = tickUpdates.getNext();
                t.setNext( null );
                _tickUpdatesRecycler.recycle( t );
            }

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
