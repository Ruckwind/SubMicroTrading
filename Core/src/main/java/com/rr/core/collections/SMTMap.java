/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.lang.ReusableString;

import java.util.Map;
import java.util.Set;

public interface SMTMap<K, T> extends Map<K, T> {

    @Override int size();

    @Override boolean isEmpty();

    @Override boolean containsKey( Object key );

    @Override boolean containsValue( Object value );

    @Override T get( Object key );

    @Override T put( K key, T value );

    @Override T remove( Object key );

    @Override void clear();

    /**
     * @return snapshot set of keys used in the map .. may be collection and Integer keys created per call
     */
    @Override Set<K> keySet();

    void logStats( ReusableString out );
}
