/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.lang.Reusable;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableType;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.SuperPool;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.utils.Percentiles;

import java.util.*;

/**
 * non thread safe HashMap which uses SuperPools to avoid GC
 *
 * @param <K,V>
 * @WARNING doesnt implement the mod count used in HashMap
 */
@SuppressWarnings( "unchecked" )
public class SMTHashMap<K, V> implements SMTMap<K, V> {

    private static final int MAXIMUM_CAPACITY = 1 << 30;

    public static final class HashEntryFactory<K, V> implements PoolFactory<HashEntry<K, V>> {

        private SuperPool<HashEntry<K, V>> _superPool;
        private HashEntry<K, V>            _root;

        public HashEntryFactory( SuperPool<HashEntry<K, V>> superPool ) {
            _superPool = superPool;
            _root      = _superPool.getChain();
        }

        @Override public HashEntry<K, V> get() {
            if ( _root == null ) {
                _root = _superPool.getChain();
            }
            HashEntry<K, V> obj = _root;
            _root = _root.getNext();
            obj.setNext( null );
            return obj;
        }

    }

    public static final class HashEntry<K, V> implements Reusable<HashEntry<K, V>>, Map.Entry<K, V> {

        V               _value;
        K               _key;
        HashEntry<K, V> _next;

        static <K, V> HashEntry<K, V>[] newArray( int i ) {
            return new HashEntry[ i ];
        }

        @Override public K getKey()   { return _key; }

        @Override public V getValue() { return _value; }

        @Override public V setValue( final V newValue ) {
            V oldValue = _value;
            _value = newValue;
            return oldValue;
        }

        @Override public HashEntry<K, V> getNext() {
            return _next;
        }

        @Override public void setNext( HashEntry<K, V> nxt ) {
            _next = nxt;
        }

        @Override public ReusableType getReusableType() {
            return CollectionTypes.MapEntry;
        }

        @Override public void reset() {
            _key   = null;
            _value = null;
            _next  = null;
        }

        void set( K key, HashEntry<K, V> next, V value ) {
            _key   = key;
            _next  = next;
            _value = value;
        }
    }

    private transient final HashEntryFactory<K, V>    _entryFactory;
    private transient final Recycler<HashEntry<K, V>> _entryRecycler;
    private final float             _loadFactor;
    transient Set<K>           _keySet;
    transient Set<Entry<K, V>> _entrySet;
    transient Collection<V>    _values;
    private       int               _count;
    private       HashEntry<K, V>[] _table;
    private       int               _tableIndexMask;
    private       int               _threshold;

    public SMTHashMap() {
        this( 16, 0.75f );
    }

    public SMTHashMap( int initialCapacity ) {
        this( initialCapacity, 0.75f );
    }

    public SMTHashMap( int initialCapacity, float loadFactor ) {
        Recycler<?> entryRecycler;

        _entryFactory  = SuperpoolManager.instance().getFactory( HashEntryFactory.class, HashEntry.class );
        entryRecycler  = SuperpoolManager.instance().getRecycler( HashEntry.class );
        _entryRecycler = (Recycler<HashEntry<K, V>>) entryRecycler;

        if ( !(loadFactor > 0) || initialCapacity < 0 )
            throw new IllegalArgumentException();

        if ( initialCapacity > MAXIMUM_CAPACITY )
            initialCapacity = MAXIMUM_CAPACITY;

        int capacity = 1;
        while( capacity < initialCapacity )
            capacity <<= 1;

        _loadFactor = loadFactor;
        HashEntry<K, V>[] table = HashEntry.newArray( capacity );
        setTable( table );
    }

    @Override
    public V get( Object key ) {
        final int       hash = hash( key );
        HashEntry<K, V> e    = getFirst( hash );
        while( e != null ) {
            if ( key.equals( e._key ) )
                return e._value;
            e = e._next;
        }
        return null;
    }

    @Override
    public V put( K key, V value ) {
        final int hash = hash( key );
        int       c    = _count;
        if ( c++ > _threshold ) // ensure capacity
            rehash();
        final HashEntry<K, V>[] tab   = _table;
        int                     index = hash & _tableIndexMask;
        HashEntry<K, V>         first = tab[ index ];
        HashEntry<K, V>         e     = first;
        while( e != null && !e._key.equals( key ) )
            e = e._next;

        V oldValue;
        if ( e != null ) {
            oldValue = e._value;
            e._value = value;
        } else {
            oldValue     = null;
            tab[ index ] = newEntry( key, first, value );

            _count = c;
        }
        return oldValue;
    }

    @Override
    public boolean containsKey( Object key ) {
        final int       hash = hash( key );
        HashEntry<K, V> e    = getFirst( hash );
        while( e != null ) {
            if ( key.equals( e._key ) )
                return true;
            e = e._next;
        }
        return false;
    }

    @Override
    public int size() {
        return _count;
    }

    @Override
    public boolean isEmpty() {
        return _count == 0;
    }

    @Override
    public boolean containsValue( Object value ) {
        if ( value == null ) return false;

        final HashEntry<K, V>[] tab = _table;
        int                     len = tab.length;
        for ( int i = 0; i < len; i++ ) {
            for ( HashEntry<K, V> e = tab[ i ]; e != null; e = e._next ) {
                V v = e._value;
                if ( value.equals( v ) )
                    return true;
            }
        }

        return false;
    }

    @Override
    public V remove( Object key ) {
        final int               hash  = hash( key );
        final HashEntry<K, V>[] tab   = _table;
        int                     index = hash & _tableIndexMask;
        HashEntry<K, V>         first = tab[ index ];
        HashEntry<K, V>         prev  = first;
        HashEntry<K, V>         e     = first;
        while( e != null && !e._key.equals( key ) ) {
            prev = e;
            e    = e._next;
        }

        V oldValue = null;
        if ( e != null ) {
            oldValue = e._value;
            if ( e == first ) {
                tab[ index ] = e._next;
            } else {
                prev._next = e._next;
            }

            e.setNext( null ); // critical detach

            _entryRecycler.recycle( e );

            --_count;
        }
        return oldValue;
    }

    @Override
    public void clear() {
        final int max = _table.length;
        for ( int i = 0; i < max; ++i ) {
            HashEntry<K, V> e = _table[ i ];
            while( e != null ) {
                final HashEntry<K, V> next = e._next;
                e._next = null;
                _entryRecycler.recycle( e );
                e = next;
            }
            _table[ i ] = null;
        }
        _count = 0;
    }

    @Override
    public void logStats( ReusableString out ) {
        int cnt = 0;
        out.append( "SMTHashMap logStats() capacity=" + _table.length + ", entries=" + _count ).append( "\n" );

        List<Integer> sizes = new ArrayList<>( cnt / 10 );
        for ( int i = 0; i < _table.length; ++i ) {
            HashEntry<K, V> e       = _table[ i ];
            int             entries = 0;
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
    @Override
    public Set<K> keySet() {
        Set<K> ks = _keySet;
        return (ks != null) ? ks : (_keySet = new KeySet());
    }

    @Override public void putAll( final Map<? extends K, ? extends V> m ) {
        for ( Map.Entry<? extends K, ? extends V> e : m.entrySet() ) {
            put( e.getKey(), e.getValue() );
        }
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
    @Override
    public Collection<V> values() {
        Collection<V> vs = _values;
        return (vs != null) ? vs : (_values = new Values());
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
    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> es = _entrySet;
        return (es != null) ? es : (_entrySet = new EntrySet());
    }

    @Override public boolean replace( K key, V oldValue, V newValue ) {
        final int       hash = hash( key );
        HashEntry<K, V> e    = getFirst( hash );
        while( e != null && !e._key.equals( key ) )
            e = e._next;

        boolean replaced = false;
        if ( e != null && oldValue.equals( e._value ) ) {
            replaced = true;
            e._value = newValue;
        }
        return replaced;
    }

    @Override public V replace( K key, V newValue ) {
        final int       hash = hash( key );
        HashEntry<K, V> e    = getFirst( hash );
        while( e != null && !e._key.equals( key ) )
            e = e._next;

        V oldValue = null;
        if ( e != null ) {
            oldValue = e._value;
            e._value = newValue;
        }
        return oldValue;
    }

    @Override public String toString() {
        return "SMTHashMap{" + "_count=" + _count + ", _table=" + Arrays.toString( _table ) + '}';
    }

    int hash( Object h ) {
        return h.hashCode();
    }

    private HashEntry<K, V> getFirst( int hash ) {
        return _table[ hash & _tableIndexMask ];
    }

    private HashEntry<K, V> newEntry( K key, HashEntry<K, V> next, V value ) {
        HashEntry<K, V> entry;
        entry = _entryFactory.get();
        entry.set( key, next, value );
        return entry;
    }

    private void rehash() {
        HashEntry<K, V>[] oldTable    = _table;
        int               oldCapacity = oldTable.length;
        if ( oldCapacity >= MAXIMUM_CAPACITY )
            return;

        HashEntry<K, V>[] newTable = HashEntry.newArray( oldCapacity << 1 );
        int               sizeMask = newTable.length - 1;
        for ( int i = 0; i < oldCapacity; i++ ) {
            HashEntry<K, V> e = oldTable[ i ];

            if ( e != null ) {

                do {
                    HashEntry<K, V>       next            = e._next;
                    int                   idx             = e._key.hashCode() & sizeMask;
                    final HashEntry<K, V> entryInNewTable = newTable[ idx ];            // keep current root entry in new table

                    newTable[ idx ] = e; // hook in e to the root of the new index

                    // try and avoid mutate calls and index ops by moving consecutive entries with same destHashIdx in one go
                    while( next != null ) {
                        final int nextIdx = next._key.hashCode() & sizeMask;
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

    private void setTable( HashEntry<K, V>[] newTable ) {
        _table          = newTable;
        _threshold      = (int) (_table.length * _loadFactor);
        _tableIndexMask = _table.length - 1;    // as table must be power of 2
    }

    /* ---------------- Iterator Support -------------- */

    abstract class HashIterator {

        HashEntry<K, V> next;        // next entry to return
        HashEntry<K, V> current;     // current entry
        int             expectedModCount;  // for fast-fail
        int             index;             // current slot

        HashIterator() {
            HashEntry<K, V>[] t = _table;
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
            HashEntry<K, V> p = current;
            if ( p == null )
                throw new IllegalStateException();
            current = null;
            SMTHashMap.this.remove( current._key );
        }

        final HashEntry<K, V> nextNode() {
            HashEntry<K, V>[] t;
            HashEntry<K, V>   e = next;
            if ( e == null )
                throw new NoSuchElementException();
            if ( (next = (current = e)._next) == null && (t = _table) != null ) {
                do { } while( index < t.length && (next = t[ index++ ]) == null );
            }
            return e;
        }
    }

    final class KeyIterator extends HashIterator
            implements Iterator<K> {

        @Override public final K next() { return nextNode()._key; }
    }

    final class ValueIterator extends HashIterator
            implements Iterator<V> {

        @Override public final V next() { return nextNode()._value; }
    }

    final class EntryIterator extends HashIterator
            implements Iterator<Map.Entry<K, V>> {

        @Override public final Map.Entry<K, V> next() { return nextNode(); }
    }

    final class KeySet extends AbstractSet<K> {

        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        @Override
        public int size() {
            return SMTHashMap.this.size();
        }

        @Override
        public boolean contains( Object o ) {
            return SMTHashMap.this.containsKey( o );
        }

        @Override
        public boolean remove( Object o ) {
            return SMTHashMap.this.remove( o ) != null;
        }

        @Override
        public void clear() {
            SMTHashMap.this.clear();
        }
    }

    final class Values extends AbstractCollection<V> {

        @Override public Iterator<V> iterator()       { return new ValueIterator(); }

        @Override public int size()                   { return SMTHashMap.this.size(); }

        @Override public boolean contains( Object o ) { return SMTHashMap.this.containsValue( o ); }

        @Override public void clear()                 { SMTHashMap.this.clear(); }
    }

    final class EntrySet extends AbstractSet<Map.Entry<K, V>> {

        @Override public Iterator<Map.Entry<K, V>> iterator() { return new EntryIterator(); }

        @Override public int size()                           { return SMTHashMap.this.size(); }

        @Override public boolean contains( Object o ) {
            if ( !(o instanceof Map.Entry) )
                return false;
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            V               v = SMTHashMap.this.get( e.getKey() );
            return v != null && v.equals( e.getValue() );
        }

        @Override public boolean remove( Object o ) {
            if ( !(o instanceof Map.Entry) )
                return false;
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            return SMTHashMap.this.remove( e.getKey(), e.getValue() );
        }

        @Override public void clear()                         { SMTHashMap.this.clear(); }
    }
}
