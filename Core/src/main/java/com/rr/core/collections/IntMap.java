/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.lang.ReusableString;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface IntMap<T> {

    void clear();

    boolean containsKey( int key );

    boolean containsValue( T value );

    Set<Map.Entry<Integer, T>> entrySet();

    void forEach( BiConsumer<Integer, ? super T> action );

    void forEach( Consumer<? super T> action );

    T get( int key );

    boolean isEmpty();

    Set<Integer> keySet();

    /**
     * @return set of keys used in the map .. collection and Integer keys created per call
     */
    Collection<Integer> keys();

    void logStats( ReusableString out );

    /**
     * @param key
     * @param value
     * @return old value or NULL if no previous entry
     */
    T put( int key, T value );

    boolean putIfKeyAbsent( int key, T value );

    T remove( int key );

    int size();

    Collection<T> values();
}
