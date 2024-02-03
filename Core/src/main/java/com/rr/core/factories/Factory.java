/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.factories;

/**
 * A Factory for creating new instances
 *
 * @param <KTYPE> type of key
 * @param <VTYPE> type of value
 */
public interface Factory<KTYPE, VTYPE> {

    VTYPE create( KTYPE key );
}
