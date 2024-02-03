package com.rr.core.recovery.json.custom;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Collections.singleton;

public class SingletonMap<K, V> extends AbstractMap<K, V> {

    private K k;
    private V v;
    private transient Set<K>               keySet;
    private transient Set<Map.Entry<K, V>> entrySet;
    private transient Collection<V>        values;

    static boolean eq( Object o1, Object o2 ) {
        return o1 == null ? o2 == null : o1.equals( o2 );
    }

    SingletonMap() {
        /* for reflection */
    }

    SingletonMap( K key, V value ) {
        k = key;
        v = value;
    }

    // Override default methods in Map
    @Override
    public V getOrDefault( Object key, V defaultValue ) {
        return eq( key, k ) ? v : defaultValue;
    }

    @Override
    public void forEach( BiConsumer<? super K, ? super V> action ) {
        action.accept( k, v );
    }

    @Override
    public void replaceAll( BiFunction<? super K, ? super V, ? extends V> function ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V putIfAbsent( K key, V value ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove( Object key, Object value ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean replace( K key, V oldValue, V newValue ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V replace( K key, V value ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V computeIfAbsent( K key,
                              Function<? super K, ? extends V> mappingFunction ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V computeIfPresent( K key,
                               BiFunction<? super K, ? super V, ? extends V> remappingFunction ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V compute( K key,
                      BiFunction<? super K, ? super V, ? extends V> remappingFunction ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V merge( K key, V value,
                    BiFunction<? super V, ? super V, ? extends V> remappingFunction ) {
        throw new UnsupportedOperationException();
    }

    @Override public int size()                            { return 1; }

    @Override public boolean isEmpty()                     { return false; }

    @Override public boolean containsValue( Object value ) { return eq( value, v ); }

    @Override public boolean containsKey( Object key )     { return eq( key, k ); }

    @Override public V get( Object key )                   { return (eq( key, k ) ? v : null); }

    @Override
    public V put( K key, V value ) {
        if ( k == null ) {
            k = key;
            v = value;
        } else {
            throw new UnsupportedOperationException();
        }

        return null;
    }

    @Override public Set<K> keySet() {
        if ( keySet == null )
            keySet = singleton( k );
        return keySet;
    }

    @Override public Collection<V> values() {
        if ( values == null )
            values = singleton( v );
        return values;
    }

    @Override public Set<Map.Entry<K, V>> entrySet() {
        if ( entrySet == null )
            entrySet = singleton(
                    new SimpleImmutableEntry<>( k, v ) );
        return entrySet;
    }
}
