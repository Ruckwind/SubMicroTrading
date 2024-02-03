package com.rr.model.generated.internal.events.recycle;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MDSnapshotFullRefreshImpl;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.SuperPool;
import com.rr.core.lang.Constants;
import com.rr.core.pool.RuntimePoolingException;
import com.rr.model.generated.internal.events.impl.MDSnapEntryImpl;
import com.rr.core.pool.SuperpoolManager;

public class MDSnapshotFullRefreshRecycler implements Recycler<MDSnapshotFullRefreshImpl> {

    private SuperPool<MDSnapshotFullRefreshImpl> _superPool;


    private MDSnapEntryRecycler _MDEntriesRecycler = SuperpoolManager.instance().getRecycler( MDSnapEntryRecycler.class, MDSnapEntryImpl.class );

    private MDSnapshotFullRefreshImpl _root;

    private int          _recycleSize;
    private int          _count = 0;
    public MDSnapshotFullRefreshRecycler( int recycleSize, SuperPool<MDSnapshotFullRefreshImpl> superPool ) {
        _superPool = superPool;
        _recycleSize = recycleSize;
        try {
            _root    = MDSnapshotFullRefreshImpl.class.newInstance();
        } catch( Exception e ) {
            throw new RuntimePoolingException( "Unable to create recycle root for MDSnapshotFullRefreshImpl : " + e.getMessage(), e );
        }
    }


    @Override public void recycle( MDSnapshotFullRefreshImpl obj ) {
        if ( Constants.DISABLE_RECYCLING ) return;
        if ( obj == null ) return;
        if ( obj.getNext() == null ) {
            MDSnapEntryImpl MDEntries = (MDSnapEntryImpl) obj.getMDEntries();
            while ( MDEntries != null ) {
                MDSnapEntryImpl t = MDEntries;
                MDEntries = MDEntries.getNext();
                t.setNext( null );
                _MDEntriesRecycler.recycle( t );
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
