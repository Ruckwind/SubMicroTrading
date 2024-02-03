/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.lang.ReusableString;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public interface LongMap<T> {

    interface Cleaner<T> {

        void clean( T valToClean );
    }

    void clear();

    T computeIfAbsent( long key, Function<Long, ? extends T> mappingFunction );

    boolean containsKey( long key );

    boolean containsValue( T value );

    void forEach( BiConsumer<Long, ? super T> action );

    void forEach( Consumer<? super T> action );

    T get( long key );

    boolean isEmpty();

    Collection<Long> keys();

    void logStats( ReusableString out );

    T put( long key, T value );

    boolean putIfKeyAbsent( long key, T value );

    void registerCleaner( Cleaner<T> cleaner );

    T remove( long key );

    int size();
}
