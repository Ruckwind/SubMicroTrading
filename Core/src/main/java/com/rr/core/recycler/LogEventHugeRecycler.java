/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.recycler;

import com.rr.core.lang.Constants;
import com.rr.core.logger.LogEventHuge;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.RuntimePoolingException;
import com.rr.core.pool.SuperPool;

public class LogEventHugeRecycler implements Recycler<LogEventHuge> {

    private SuperPool<LogEventHuge> _superPool;

    private LogEventHuge _root;

    private int _recycleSize;
    private int _count = 0;

    public LogEventHugeRecycler( int recycleSize, SuperPool<LogEventHuge> superPool ) {
        _superPool   = superPool;
        _root        = _superPool.getChain();
        _recycleSize = recycleSize;
        try {
            _root = LogEventHuge.class.newInstance();
        } catch( Exception e ) {
            throw new RuntimePoolingException( "Unable to create recycle root for LogEventHuge : " + e.getMessage(), e );
        }
    }

    @Override
    public void recycle( LogEventHuge obj ) {
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
