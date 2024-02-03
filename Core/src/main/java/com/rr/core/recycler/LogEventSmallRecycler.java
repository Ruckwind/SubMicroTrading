/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.recycler;

import com.rr.core.lang.Constants;
import com.rr.core.logger.LogEventSmall;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.RuntimePoolingException;
import com.rr.core.pool.SuperPool;

public class LogEventSmallRecycler implements Recycler<LogEventSmall> {

    private SuperPool<LogEventSmall> _superPool;

    private LogEventSmall _root;

    private int _recycleSize;
    private int _count = 0;

    public LogEventSmallRecycler( int recycleSize, SuperPool<LogEventSmall> superPool ) {
        _superPool   = superPool;
        _root        = _superPool.getChain();
        _recycleSize = recycleSize;
        try {
            _root = LogEventSmall.class.newInstance();
        } catch( Exception e ) {
            throw new RuntimePoolingException( "Unable to create recycle root for LogEventSmall : " + e.getMessage(), e );
        }
    }

    @Override
    public void recycle( LogEventSmall obj ) {
        if ( obj == null ) return;

        if ( Constants.DISABLE_RECYCLING ) return;

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
