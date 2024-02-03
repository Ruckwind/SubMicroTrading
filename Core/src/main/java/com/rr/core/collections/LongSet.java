/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.lang.ReusableString;

import java.util.Collection;

/**
 * LongSet - no autoboxing cost, no GC, no generic casting overhead
 */
public interface LongSet {

    /**
     * add entry to set, return true if added, false if key already existed
     */
    boolean add( long key );

    void clear();

    boolean contains( long key );

    boolean isEmpty();

    /**
     * @return set of keys used in the map .. collection and Integer keys created per call
     */
    Collection<Long> keys();

    void logStats( ReusableString out );

    /**
     * @return true if key removed, false if not in set
     */
    boolean remove( long key );

    int size();
}
