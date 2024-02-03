/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.pool;

import com.rr.core.lang.Reusable;

public interface Recycler<T extends Reusable<T>> {

    /**
     * recycle object
     *
     * @param obj
     * @NOTE next pointer must be cleared before invoking recycle or object will NOT be recycled
     */
    void recycle( T obj );
}
