/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.factories;

import com.rr.core.logger.LogEventHuge;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

// ResuableString is used alot this factory is faster than generics

public class LogEventHugeFactory implements PoolFactory<LogEventHuge> {

    private SuperPool<LogEventHuge> _superPool;

    private LogEventHuge _root;

    public LogEventHugeFactory( SuperPool<LogEventHuge> superPool ) {
        _superPool = superPool;
        _root      = _superPool.getChain();
    }

    @Override
    public LogEventHuge get() {
        if ( _root == null ) {
            _root = _superPool.getChain();
        }
        LogEventHuge obj = _root;
        _root = _root.getNext();
        obj.setNext( null );
        return obj;
    }
}
