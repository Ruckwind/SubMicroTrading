/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.pool;

import com.rr.core.lang.Constants;
import com.rr.core.lang.Reusable;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TLC;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.HasInstanceId;
import com.rr.core.utils.Utils;

public final class RecyclerImpl<T extends Reusable<T>> implements Recycler<T> {

    public static final Logger _log = LoggerFactory.create( RecyclerImpl.class );

    private final SuperPool<T> _superPool;
    private final T            _root;
    private final Class<T>     _poolClass;
    private final int          _recycleSize;
    private       int          _count = 0;

    public RecyclerImpl( Class<T> poolClass, int recycleSize, SuperPool<T> superPool ) {
        _poolClass   = poolClass;
        _superPool   = superPool;
        _recycleSize = recycleSize;

        try {
            _root = _poolClass.newInstance();

        } catch( Exception e ) {

            throw new RuntimePoolingException( "Unable to create recycle root for " + poolClass.getSimpleName() + " : " + e.getMessage(), e );
        }
    }

    @Override
    public void recycle( final T obj ) {

        if ( obj == null ) return;

        if ( Constants.DISABLE_RECYCLING ) return;

        if ( Constants.RECYCLE_DEBUG && obj instanceof HasInstanceId ) {
            HasInstanceId  idf        = (HasInstanceId) obj;
            String         simpleName = idf.getClass().getSimpleName();
            ReusableString t          = TLC.strPop();
            t.copy( "SuperPool RECYCLING " ).append( simpleName ).append( "  ID " ).append( idf.getInstanceId() ).append( " " );
            t.append( Thread.currentThread().getName() ).append( "\n" );

            Utils.getStackTrace( t );

            _log.info( t );

            TLC.strPush( t );
        }

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
