/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.factories;

import com.rr.core.logger.LogEventLarge;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

// ResuableString is used alot this factory is faster than generics

public class LogEventLargeFactory implements PoolFactory<LogEventLarge> {

    private SuperPool<LogEventLarge> _superPool;

    private LogEventLarge _root;

    public LogEventLargeFactory( SuperPool<LogEventLarge> superPool ) {
        _superPool = superPool;
        _root      = _superPool.getChain();
    }

    @Override
    public LogEventLarge get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        LogEventLarge obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
