/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.lang.ReusableString;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * thread safe templated hash map which uses primitive longs as keys without autoboxing
 * <p>
 * uses superpool so some overhead from pool management
 *
 * @param <T>
 */
public class SyncLongHashMap<T> implements LongMap<T> {

    private final LongMap<T> _letter;

    public SyncLongHashMap( LongMap<T> letter ) {
        _letter = letter;
    }

    @Override public synchronized void clear() {
        _letter.clear();
    }

    @Override public synchronized T computeIfAbsent( final long key, final Function<Long, ? extends T> mappingFunction ) { return _letter.computeIfAbsent( key, mappingFunction ); }

    @Override public synchronized boolean containsKey( final long key ) {
        return _letter.containsKey( key );
    }

    @Override public synchronized boolean containsValue( final T value ) {
        return _letter.containsValue( value );
    }

    @Override public synchronized void forEach( final BiConsumer<Long, ? super T> action ) { _letter.forEach( action ); }

    @Override public synchronized void forEach( final Consumer<? super T> action )         { _letter.forEach( action ); }

    @Override public synchronized T get( final long key ) {
        return _letter.get( key );
    }

    @Override public synchronized boolean isEmpty() {
        return _letter.isEmpty();
    }

    @Override public synchronized Collection<Long> keys() { return _letter.keys(); }

    @Override public synchronized void logStats( final ReusableString out ) {
        _letter.logStats( out );
    }

    @Override public synchronized T put( final long key, final T value ) {
        return _letter.put( key, value );
    }

    @Override public synchronized boolean putIfKeyAbsent( final long key, final T value ) {
        return _letter.putIfKeyAbsent( key, value );
    }

    @Override public synchronized void registerCleaner( final Cleaner<T> cleaner ) {
        _letter.registerCleaner( cleaner );
    }

    @Override public synchronized T remove( final long key ) {
        return _letter.remove( key );
    }

    @Override public synchronized int size() {
        return _letter.size();
    }
}
