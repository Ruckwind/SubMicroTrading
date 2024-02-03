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

/**
 * pool factory designed, each instance to be used on single thread
 */
public class PoolFactoryImpl<T extends Reusable<T>> implements PoolFactory<T> {

    public static final Logger _log = LoggerFactory.create( PoolFactoryImpl.class );

    private SuperPool<T> _superPool;

    private T _root;

    PoolFactoryImpl( SuperPool<T> superPool ) {

        _superPool = superPool;

        allocate();
    }

    @Override
    public T get() {

        if ( _root == null ) {
            allocate();
        }

        T obj = _root;
        _root = _root.getNext();
        obj.setNext( null );

        if ( Constants.RECYCLE_DEBUG && obj instanceof HasInstanceId ) {
            HasInstanceId  idf        = (HasInstanceId) obj;
            String         simpleName = idf.getClass().getSimpleName();
            ReusableString t          = TLC.strPop();
            t.copy( "SuperPool ALLOCATING " ).append( simpleName ).append( "  ID " ).append( idf.getInstanceId() ).append( " " );
            t.append( Thread.currentThread().getName() ).append( "\n" );

            Utils.getStackTrace( t );

            _log.info( t );

            TLC.strPush( t );
        }

        return obj;
    }

    private void allocate() {

        _root = _superPool.getChain();
    }

}
