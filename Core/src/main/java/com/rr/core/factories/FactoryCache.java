/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.factories;

/**
 * A cache wrapper for a factory
 * <p>
 * implementations can cache by key
 *
 * @param <KTYPE> type of key
 * @param <VTYPE> type of value
 */
public interface FactoryCache<KTYPE, VTYPE> {

    /**
     * get cached instance, create one if none exists by invoking the registered factory
     *
     * @param key
     * @return
     */
    VTYPE getItem( KTYPE key );
}
