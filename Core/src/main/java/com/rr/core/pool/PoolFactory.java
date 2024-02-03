/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.pool;

import com.rr.core.lang.Reusable;

/**
 * pool factory designed, each instance to be used on single thread
 */
public interface PoolFactory<T extends Reusable<T>> {

    T get();
}
