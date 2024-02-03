/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.factories;

import com.rr.core.logger.LogEventSmall;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

// ResuableString is used alot this factory is faster than generics

public class LogEventSmallFactory implements PoolFactory<LogEventSmall> {

    private SuperPool<LogEventSmall> _superPool;

    private LogEventSmall _root;

    public LogEventSmallFactory( SuperPool<LogEventSmall> superPool ) {
        _superPool = superPool;
        _root      = _superPool.getChain();
    }

    @Override
    public LogEventSmall get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        LogEventSmall obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
