/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.lang.ReusableString;

import java.util.Collection;

public interface IntSet {

    /**
     * add entry to set, return true if added, false if key already existed
     */
    boolean add( int key );

    void clear();

    boolean contains( int key );

    boolean isEmpty();

    /**
     * @return set of keys used in the map .. collection and Integer keys created per call
     */
    Collection<Integer> keys();

    void logStats( ReusableString out );

    /**
     * @return true if key removed, false if not in set
     */
    boolean remove( int key );

    int size();
}
