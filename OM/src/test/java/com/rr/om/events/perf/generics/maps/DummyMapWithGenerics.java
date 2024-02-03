/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.events.perf.generics.maps;

/**
 * not real map implementation, just looking at performance
 * of map type structure using generics
 *
 * @param <K>
 * @param <V>
 * @author Richard Rose
 */

public class DummyMapWithGenerics<K, V> {

    public static class MapEntry<K, V> {

        K key;
        V value;

        MapEntry() {
            // nada
        }

        K getKey()              { return key; }

        V getValue()            { return value; }

        void setValue( V aVal ) { value = aVal; }
    }

    protected MapEntry<K, V>[] _array;

    @SuppressWarnings( "unchecked" )
    public DummyMapWithGenerics( int size ) {
        _array = new MapEntry[ size ];

        for ( int i = 0; i < size; i++ ) {
            _array[ i ] = new MapEntry<>();
        }
    }

    public K getKey( int idx ) {
        return _array[ idx ].key;
    }

    public V getValue( int idx ) {
        return _array[ idx ].value;
    }

    public void set( K key, V value, int idx ) {
        MapEntry<K, V> entry = _array[ idx ];
        entry.key   = key;
        entry.value = value;
    }
}
