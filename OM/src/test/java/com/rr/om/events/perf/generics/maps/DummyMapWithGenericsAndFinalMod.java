/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.events.perf.generics.maps;

/**
 * not real map implementation, just looking at performance
 * of map type structure using generics and final where possible
 *
 * @param <K>
 * @param <V>
 * @author Richard Rose
 */

public final class DummyMapWithGenericsAndFinalMod<K, V> {

    public final static class MapEntry<K, V> {

        K key;
        V value;

        MapEntry() {
            // nada
        }

        final K getKey()                    { return key; }

        final V getValue()                  { return value; }

        final void setValue( final V aVal ) { value = aVal; }
    }

    private MapEntry<K, V>[] _array;

    @SuppressWarnings( "unchecked" )
    public DummyMapWithGenericsAndFinalMod( final int size ) {
        _array = new MapEntry[ size ];

        for ( int i = 0; i < size; i++ ) {
            _array[ i ] = new MapEntry<>();
        }
    }

    final public K getKey( final int idx ) {
        return _array[ idx ].key;
    }

    final public V getValue( final int idx ) {
        return _array[ idx ].value;
    }

    final public void set( final K key, final V value, final int idx ) {
        final MapEntry<K, V> entry = _array[ idx ];
        entry.key   = key;
        entry.value = value;
    }
}
