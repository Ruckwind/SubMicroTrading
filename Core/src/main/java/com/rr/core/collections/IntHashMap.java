/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.lang.ReusableString;
import com.rr.core.utils.Percentiles;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * non thread safe templated hash map which uses primitive ints as keys without autoboxing
 * <p>
 * currently does NOT pool entry objects so only populate at startup with minor updates during day
 *
 * @param <T>
 */
public class IntHashMap<T> implements IntMap<T> {

    private static final int MAXIMUM_CAPACITY = 1 << 30;

    private static class HashEntry<T> implements Map.Entry<Integer, T> {

        int          _key;
        T            _value;
        HashEntry<T> _next;

        @SuppressWarnings( "unchecked" )
        static <T> HashEntry<T>[] newArray( int size ) {
            return new HashEntry[ size ];
        }

        public HashEntry( int key, HashEntry<T> next, T value ) {
            _key   = key;
            _next  = next;
            _value = value;
        }

        @Override public Integer getKey()            { return _key; }

        @Override public T getValue()                { return _value; }

        @Override public T setValue( final T value ) { return _value = value; }
    }
    private final float          _loadFactor;
    transient Set<Map.Entry<Integer, T>> _entrySet;
    transient Collection<T>              _values;
    transient KeySet                     _keySet;
    private       int            _count;
    private       HashEntry<T>[] _table;
    private       int            _tableIndexMask;
    private       int            _threshold;

    static int hash( int h ) {
        return h;
    }

    public IntHashMap() {
        this( 16, 0.75f );
    }

    public IntHashMap( int initialCapacity, float loadFactor ) {
        if ( !(loadFactor > 0) || initialCapacity < 0 )
            throw new IllegalArgumentException();

        if ( initialCapacity > MAXIMUM_CAPACITY )
            initialCapacity = MAXIMUM_CAPACITY;

        int capacity = 1;
        while( capacity < initialCapacity )
            capacity <<= 1;

        _loadFactor = loadFactor;
        HashEntry<T>[] table = HashEntry.newArray( capacity );
        setTable( table );
    }

    @Override
    public void clear() {
        final int max = _table.length;
        for ( int i = 0; i < max; ++i ) {
            _table[ i ] = null;
        }
        _count = 0;
    }

    @Override
    public boolean containsKey( int key ) {
        final int    hash = hash( key );
        HashEntry<T> e    = getFirst( hash );
        while( e != null ) {
            if ( key == e._key )
                return true;
            e = e._next;
        }
        return false;
    }

    @Override
    public boolean containsValue( T value ) {
        if ( value == null ) return false;

        final HashEntry<T>[] tab = _table;
        int                  len = tab.length;
        for ( int i = 0; i < len; i++ ) {
            for ( HashEntry<T> e = tab[ i ]; e != null; e = e._next ) {
                T v = e._value;
                if ( value.equals( v ) )
                    return true;
            }
        }

        return false;
    }

    /**
     * Returns a {@link SMTSet} view of the mappings contained in this map. The set is backed by the map, so changes to the map are reflected in the set, and
     * vice-versa. The set supports element removal, which removes the corresponding mapping from the map, via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>
     * , <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt> operations. It does not support the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * <p>
     * The view's <tt>iterator</tt> is a "weakly consistent" iterator that will never throw {@link ConcurrentModificationException}, and guarantees to traverse
     * elements as they existed upon construction of the iterator, and may (but is not guaranteed to) reflect any modifications subsequent to construction.
     */
    @Override public Set<Map.Entry<Integer, T>> entrySet() {
        Set<Map.Entry<Integer, T>> es = _entrySet;
        return (es != null) ? es : (_entrySet = new IntHashMap.EntrySet());
    }

    @Override public void forEach( BiConsumer<Integer, ? super T> action ) {
        if ( action == null ) return;

        final HashEntry<T>[] tab = _table;
        int                  len = tab.length;
        for ( int i = 0; i < len; i++ ) {
            for ( HashEntry<T> e = tab[ i ]; e != null; e = e._next ) {
                T v = e._value;
                action.accept( e._key, v );
            }
        }
    }

    @Override public void forEach( Consumer<? super T> action ) {
        if ( action == null ) return;

        final HashEntry<T>[] tab = _table;
        int                  len = tab.length;
        for ( int i = 0; i < len; i++ ) {
            for ( HashEntry<T> e = tab[ i ]; e != null; e = e._next ) {
                T v = e._value;
                action.accept( v );
            }
        }
    }

    @Override
    public T get( int key ) {
        final int    hash = hash( key );
        HashEntry<T> e    = getFirst( hash );
        while( e != null ) {
            if ( key == e._key ) {
                return e._value;
            }
            e = e._next;
        }
        return null;
    }

    @Override
    public boolean isEmpty() {
        return _count == 0;
    }

    /**
     * Returns a {@link SMTSet} view of the keys contained in this map. The set is backed by the map, so changes to the map are reflected in the set, and
     * vice-versa. The set supports element removal, which removes the corresponding mapping from this map, via the <tt>Iterator.remove</tt>,
     * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt> operations. It does not support the <tt>add</tt> or <tt>addAll</tt>
     * operations.
     *
     * <p>
     * The view's <tt>iterator</tt> is a "weakly consistent" iterator that will never throw {@link ConcurrentModificationException}, and guarantees to traverse
     * elements as they existed upon construction of the iterator, and may (but is not guaranteed to) reflect any modifications subsequent to construction.
     */
    @Override public Set<Integer> keySet() {
        Set<Integer> ks = _keySet;
        return (ks != null) ? ks : (_keySet = new IntHashMap.KeySet());
    }

    @Override
    public Collection<Integer> keys() {
        Collection<Integer> allKeys = new HashSet<>( size() );

        for ( int i = 0; i < _table.length; ++i ) {
            HashEntry<T> e = _table[ i ];
            while( e != null ) {
                allKeys.add( e._key );
                e = e._next;
            }
        }

        return allKeys;
    }

    @Override
    public void logStats( ReusableString out ) {
        int cnt = 0;
        out.append( "SimpleTMap logStats() capacity=" + _table.length + ", entries=" + _count ).append( "\n" );

        List<Integer> sizes = new ArrayList<>( cnt / 10 );
        for ( int i = 0; i < _table.length; ++i ) {
            HashEntry<T> e       = _table[ i ];
            int          entries = 0;
            while( e != null ) {
                ++entries;
                e = e._next;
            }
            if ( cnt > 0 ) {
                sizes.add( entries );
            }
        }

        long[] cnts = new long[ sizes.size() ];
        for ( int i = 0; i < sizes.size(); i++ ) {
            cnts[ i ] = sizes.get( i );
        }

        Percentiles p = new Percentiles( cnts );

        out.append( "Map list chain sizes " + ", chains=" + cnts +
                    ", med=" + p.median() + ", ave=" + p.getAverage() +
                    ", min=" + p.getMinimum() + ", max=" + p.getMaximum() +
                    "\n" +
                    ", p99=" + p.calc( 99 ) + ", p95=" + p.calc( 95 ) +
                    ", p90=" + p.calc( 90 ) + ", p80=" + p.calc( 80 ) +
                    ", p70=" + p.calc( 70 ) + ", p50=" + p.calc( 50 ) + "\n" );
    }

    @Override
    public T put( int key, T value ) {
        final int hash = hash( key );
        int       c    = _count;
        if ( c++ > _threshold ) // ensure capacity
            rehash();
        final HashEntry<T>[] tab   = _table;
        int                  index = hash & _tableIndexMask;
        HashEntry<T>         first = tab[ index ];
        HashEntry<T>         e     = first;
        while( e != null && key != e._key )
            e = e._next;

        T oldValue;
        if ( e != null ) {
            oldValue = e._value;
            e._value = value;
        } else {
            oldValue     = null;
            tab[ index ] = new HashEntry<>( key, first, value );
            _count       = c;
        }
        return oldValue;
    }

    @Override
    public boolean putIfKeyAbsent( int key, T value ) {
        final int hash = hash( key );
        int       c    = _count;
        if ( c++ > _threshold ) // ensure capacity
            rehash();
        final HashEntry<T>[] tab   = _table;
        int                  index = hash & _tableIndexMask;
        HashEntry<T>         first = tab[ index ];
        HashEntry<T>         e     = first;
        while( e != null && key != e._key )
            e = e._next;

        if ( e != null ) {
            return false;
        }

        tab[ index ] = new HashEntry<>( key, first, value );
        return true;
    }

    @Override
    public T remove( int key ) {
        final int            hash  = hash( key );
        final HashEntry<T>[] tab   = _table;
        int                  index = hash & _tableIndexMask;
        HashEntry<T>         first = tab[ index ];
        HashEntry<T>         prev  = first;
        HashEntry<T>         e     = first;
        while( e != null && key != e._key ) {
            prev = e;
            e    = e._next;
        }

        T oldValue = null;
        if ( e != null ) {
            oldValue = e._value;
            if ( e == first ) {
                tab[ index ] = e._next;
            } else {
                prev._next = e._next;
            }
            --_count;
        }
        return oldValue;
    }

    @Override
    public int size() {
        return _count;
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map. The collection is backed by the map, so changes to the map are reflected in the
     * collection, and vice-versa. The collection supports element removal, which removes the corresponding mapping from this map, via the
     * <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt> operations. It does not support the
     * <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * <p>
     * The view's <tt>iterator</tt> is a "weakly consistent" iterator that will never throw {@link ConcurrentModificationException}, and guarantees to traverse
     * elements as they existed upon construction of the iterator, and may (but is not guaranteed to) reflect any modifications subsequent to construction.
     */
    @Override public Collection<T> values() {
        Collection<T> vs = _values;
        return (vs != null) ? vs : (_values = new IntHashMap.Values());
    }

    public boolean replace( int key, T oldValue, T newValue ) {
        final int    hash = hash( key );
        HashEntry<T> e    = getFirst( hash );
        while( e != null && key != e._key )
            e = e._next;

        boolean replaced = false;
        if ( e != null && oldValue.equals( e._value ) ) {
            replaced = true;
            e._value = newValue;
        }
        return replaced;
    }

    public T replace( int key, T newValue ) {
        final int    hash = hash( key );
        HashEntry<T> e    = getFirst( hash );
        while( e != null && e._key != key )
            e = e._next;

        T oldValue = null;
        if ( e != null ) {
            oldValue = e._value;
            e._value = newValue;
        }
        return oldValue;
    }

    private HashEntry<T> getFirst( int hash ) {
        return _table[ hash & _tableIndexMask ];
    }

    private void rehash() {
        HashEntry<T>[] oldTable    = _table;
        int            oldCapacity = oldTable.length;
        if ( oldCapacity >= MAXIMUM_CAPACITY )
            return;

        HashEntry<T>[] newTable = HashEntry.newArray( oldCapacity << 1 );
        int            sizeMask = newTable.length - 1;
        for ( int i = 0; i < oldCapacity; i++ ) {
            HashEntry<T> e = oldTable[ i ];

            if ( e != null ) {

                do {
                    HashEntry<T>       next            = e._next;
                    int                idx             = hash( e._key ) & sizeMask;
                    final HashEntry<T> entryInNewTable = newTable[ idx ];            // keep current root entry in new table

                    newTable[ idx ] = e; // hook in e to the root of the new index

                    // try and avoid mutate calls and index ops by moving consecutive entries with same destHashIdx in one go
                    while( next != null ) {
                        final int nextIdx = hash( next._key ) & sizeMask;
                        if ( nextIdx == idx ) {
                            e    = next;
                            next = next._next;
                        } else {
                            break;
                        }
                    }

                    e._next = entryInNewTable;
                    e       = next;
                } while( e != null );
            }
        }
        setTable( newTable );
    }

    private void setTable( HashEntry<T>[] newTable ) {
        _table          = newTable;
        _threshold      = (int) (_table.length * _loadFactor);
        _tableIndexMask = _table.length - 1;    // as table must be power of 2
    }

    /* ---------------- Iterator Support -------------- */

    abstract class HashIterator {

        IntHashMap.HashEntry<T> next;        // next entry to return
        IntHashMap.HashEntry<T> current;     // current entry
        int                     expectedModCount;  // for fast-fail
        int                     index;             // current slot

        HashIterator() {
            IntHashMap.HashEntry<T>[] t = _table;
            current = next = null;
            index   = 0;
            if ( t != null && _count > 0 ) { // advance to first entry
                do { } while( index < t.length && (next = t[ index++ ]) == null );
            }
        }

        public final boolean hasNext() {
            return next != null;
        }

        public final void remove() {
            IntHashMap.HashEntry<T> p = current;
            if ( p == null )
                throw new IllegalStateException();
            current = null;
            IntHashMap.this.remove( current._key );
        }

        final IntHashMap.HashEntry<T> nextNode() {
            IntHashMap.HashEntry<T>[] t;
            IntHashMap.HashEntry<T>   e = next;
            if ( e == null )
                throw new NoSuchElementException();
            if ( (next = (current = e)._next) == null && (t = _table) != null ) {
                do { } while( index < t.length && (next = t[ index++ ]) == null );
            }
            return e;
        }
    }

    final class KeyIterator extends IntHashMap.HashIterator
            implements Iterator<Integer> {

        @Override public final Integer next() { return nextNode()._key; }
    }

    final class ValueIterator extends IntHashMap.HashIterator
            implements Iterator<T> {

        @Override public final T next() { return (T) nextNode()._value; }
    }

    final class EntryIterator extends IntHashMap.HashIterator
            implements Iterator<Map.Entry<Integer, T>> {

        @Override public final Map.Entry<Integer, T> next() { return nextNode(); }
    }

    final class KeySet extends AbstractSet<Integer> {

        @Override
        public Iterator<Integer> iterator() {
            return new IntHashMap.KeyIterator();
        }

        @Override
        public int size() {
            return IntHashMap.this.size();
        }

        @Override
        public boolean contains( Object o ) {
            return IntHashMap.this.containsKey( (Integer) o );
        }

        @Override
        public boolean remove( Object o ) {
            final Integer key = (Integer) o;
            if ( !containsKey( key ) ) return false;
            IntHashMap.this.remove( key );
            return false;
        }

        @Override
        public void clear() {
            IntHashMap.this.clear();
        }
    }

    final class Values extends AbstractCollection<T> {

        @Override public Iterator<T> iterator()       { return new IntHashMap.ValueIterator(); }

        @Override public int size()                   { return IntHashMap.this.size(); }

        @Override public boolean contains( Object o ) { return IntHashMap.this.containsValue( (T) o ); }

        @Override public void clear()                 { IntHashMap.this.clear(); }
    }

    final class EntrySet extends AbstractSet<Map.Entry<Integer, T>> {

        @Override public Iterator<Map.Entry<Integer, T>> iterator() { return new IntHashMap.EntryIterator(); }

        @Override public int size()                                 { return IntHashMap.this.size(); }

        @Override public boolean contains( Object o ) {
            if ( !(o instanceof Map.Entry) )
                return false;
            Map.Entry<Integer, ?> e = (Map.Entry<Integer, ?>) o;
            T                     v = IntHashMap.this.get( e.getKey() );
            return v != null && v.equals( e.getValue() );
        }

        @Override public boolean remove( Object o ) {
            if ( !(o instanceof Map.Entry) )
                return false;
            Map.Entry<Integer, ?> e = (Map.Entry<Integer, ?>) o;
            if ( !contains( e.getKey() ) ) return false;
            IntHashMap.this.remove( e.getKey() );
            return false;
        }

        @Override public void clear()                               { IntHashMap.this.clear(); }
    }
}
