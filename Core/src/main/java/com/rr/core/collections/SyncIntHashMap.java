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

/**
 * thread safe templated hash map which uses primitive ints as keys without autoboxing
 * <p>
 * currently does NOT pool entry objects so only populate at startup with minor updates during day
 *
 * @param <T>
 */
public class SyncIntHashMap<T> implements IntMap<T> {

    private final IntMap<T> _letter;

    public SyncIntHashMap( IntMap<T> letter ) {
        _letter = letter;
    }

    @Override public synchronized void clear() {
        _letter.clear();
    }

    @Override public synchronized boolean containsKey( final int key ) {
        return _letter.containsKey( key );
    }

    @Override public synchronized boolean containsValue( final T value ) {
        return _letter.containsValue( value );
    }

    @Override public Set<Map.Entry<Integer, T>> entrySet() { return _letter.entrySet(); }

    @Override public synchronized void forEach( final BiConsumer<Integer, ? super T> action ) { _letter.forEach( action ); }

    @Override public synchronized void forEach( final Consumer<? super T> action )            { _letter.forEach( action ); }

    @Override public synchronized T get( final int key ) {
        return _letter.get( key );
    }

    @Override public synchronized boolean isEmpty() {
        return _letter.isEmpty();
    }

    @Override public Set<Integer> keySet()                 { return _letter.keySet(); }

    @Override public synchronized Collection<Integer> keys() {
        return _letter.keys();
    }

    @Override public synchronized void logStats( final ReusableString out )                   { _letter.logStats( out ); }

    @Override public synchronized T put( final int key, final T value ) { return _letter.put( key, value ); }

    @Override public synchronized boolean putIfKeyAbsent( final int key, final T value ) { return _letter.putIfKeyAbsent( key, value ); }

    @Override public synchronized T remove( final int key ) {
        return _letter.remove( key );
    }

    @Override public synchronized int size() {
        return _letter.size();
    }

    @Override public Collection<T> values()                { return _letter.values(); }
}
