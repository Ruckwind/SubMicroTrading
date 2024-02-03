/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.lang.ReusableString;

import java.util.Collection;
import java.util.Set;

public interface SMTSet<T> extends Set<T> {

    @Override int size();

    @Override boolean isEmpty();

    @Override boolean contains( Object key );

    /**
     * add entry to set, return true if added, false if key already existed
     */
    @Override boolean add( T key );

    /**
     * @return true if key removed, false if not in set
     */
    @Override boolean remove( Object key );

    @Override void clear();

    /**
     * @return set of keys used in the map .. collection and keys created per call
     */
    Collection<T> keys();

    void logStats( ReusableString out );
}
