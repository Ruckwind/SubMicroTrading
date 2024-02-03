package com.rr.model.generated.internal.events.recycle;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.SecMassStatGrpImpl;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.SuperPool;
import com.rr.core.lang.Constants;
import com.rr.core.pool.RuntimePoolingException;

public class SecMassStatGrpRecycler implements Recycler<SecMassStatGrpImpl> {

    private SuperPool<SecMassStatGrpImpl> _superPool;

    private SecMassStatGrpImpl _root;

    private int          _recycleSize;
    private int          _count = 0;
    public SecMassStatGrpRecycler( int recycleSize, SuperPool<SecMassStatGrpImpl> superPool ) {
        _superPool = superPool;
        _recycleSize = recycleSize;
        try {
            _root    = SecMassStatGrpImpl.class.newInstance();
        } catch( Exception e ) {
            throw new RuntimePoolingException( "Unable to create recycle root for SecMassStatGrpImpl : " + e.getMessage(), e );
        }
    }


    @Override public void recycle( SecMassStatGrpImpl obj ) {
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
