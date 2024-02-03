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
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class SMTLinkedHashMap<K, V> implements SMTMap<K, V> {

    private static final int  MAXIMUM_CAPACITY = 1 << 30;
    private static final long serialVersionUID = 1L;

    public static final class HashEntry<K, V> implements Reusable<HashEntry<K, V>>, Map.Entry<K, V> {

        V               _value;
        K               _key;
        HashEntry<K, V> _next;

        // linkage for iteration order
        HashEntry<K, V> _before;
        HashEntry<K, V> _after;

        static <K, V> HashEntry<K, V>[] newArray( int i ) { return new HashEntry[ i ]; }

        @Override public K getKey()                          { return _key; }

        @Override public V getValue()                        { return _value; }

        @Override public V setValue( final V newValue ) {
            V oldValue = _value;
            _value = newValue;
            return oldValue;
        }

        @Override public HashEntry<K, V> getNext()           { return _next; }

        @Override public void setNext( HashEntry<K, V> nxt ) { _next = nxt; }

        @Override public ReusableType getReusableType()      { return CollectionTypes.LinkedMapEntry; }

        @Override public void reset() {
            _key    = null;
            _value  = null;
            _next   = null;
            _before = null;
            _after  = null;
        }

        void set( K key, HashEntry<K, V> next, V value ) {
            _key   = key;
            _next  = next;
            _value = value;
        }
    }

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
    /**
     * The iteration ordering method for this linked hash map: true for access-order, false for insertion-order.
     */
    final boolean _accessOrder;
    private transient final HashEntryFactory<K, V>    _entryFactory;
    private transient final Recycler<HashEntry<K, V>> _entryRecycler;
    private final float             _loadFactor;
    transient Set<K>               _keySet;
    transient Set<Map.Entry<K, V>> _entrySet;
    transient Collection<V>        _values;
    transient HashEntry<K, V>      _head;
    transient HashEntry<K, V>      _tail;
    private       int               _count;
    private       HashEntry<K, V>[] _table;
    private       int               _tableIndexMask;
    private       int               _threshold;
    public SMTLinkedHashMap()                                        { this( 16, 0.75f, false ); }

    public SMTLinkedHashMap( int initialCapacity )                   { this( initialCapacity, 0.75f, false ); }
    public SMTLinkedHashMap( int initialCapacity, float loadFactor ) { this( initialCapacity, loadFactor, false ); }
    /**
     * Constructs an empty <tt>/tt> instance with the
     * specified initial capacity, load factor and ordering mode.
     *
     * @param initialCapacity the initial capacity
     * @param loadFactor      the load factor
     * @param accessOrder     the ordering mode - true for access-order, false for insertion-order
     */
    public SMTLinkedHashMap( int initialCapacity, float loadFactor, boolean accessOrder ) {
        Recycler<?> entryRecycler;

        _entryFactory  = SuperpoolManager.instance().getFactory( HashEntryFactory.class, HashEntry.class );
        entryRecycler  = SuperpoolManager.instance().getRecycler( HashEntry.class );
        _entryRecycler = (Recycler<HashEntry<K, V>>) entryRecycler;

        _accessOrder = accessOrder;

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

    @Override public void putAll( final Map<? extends K, ? extends V> m ) {
        for ( Map.Entry<? extends K, ? extends V> e : m.entrySet() ) {
            put( e.getKey(), e.getValue() );
        }
    }

    @Override public Collection<V> values() {
        Collection<V> vs = _values;
        return (vs != null) ? vs : (_values = new SMTLinkedHashMap.Values());
    }

    @Override public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> es = _entrySet;
        return (es != null) ? es : (_entrySet = new EntrySet());
    }

    @Override public V getOrDefault( Object key, V defaultValue ) {
        HashEntry<K, V> e;
        if ( (e = getNode( key )) == null )
            return defaultValue;
        if ( _accessOrder )
            afterNodeAccess( e );
        return e._value;
    }

    @Override public void forEach( BiConsumer<? super K, ? super V> action ) {
        if ( action == null )
            throw new NullPointerException();
        for ( HashEntry<K, V> e = _head; e != null; e = e._after )
              action.accept( e._key, e._value );
    }

    @Override public void replaceAll( BiFunction<? super K, ? super V, ? extends V> function ) {
        if ( function == null )
            throw new NullPointerException();
        for ( HashEntry<K, V> e = _head; e != null; e = e._after )
              e._value = function.apply( e._key, e._value );
    }

    @Override public V putIfAbsent( K key, V value ) {
        return putVal( key, value, true, true );
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

    @Override public int size()        { return _count; }

    @Override public boolean isEmpty() { return _count == 0; }

    @Override public boolean containsKey( Object key ) {
        final int       hash = hash( key );
        HashEntry<K, V> e    = getFirst( hash );
        while( e != null ) {
            if ( key.equals( e._key ) )
                return true;
            e = e._next;
        }
        return false;
    }

    @Override public boolean containsValue( Object value ) {
        if ( value == null ) return false;

        final HashEntry<K, V>[] tab = _table;
        int                     len = tab.length;
        for ( int i = 0; i < len; i++ ) {
            for ( HashEntry<K, V> e = tab[ i ]; e != null; e = e._next ) {
                V v = e._value;
                if ( v == value || (value != null && value.equals( v )) )
                    return true;
            }
        }

        return false;
    }

    @Override public V get( Object key ) {
        HashEntry<K, V> e;
        if ( (e = getNode( key )) == null )
            return null;
        if ( _accessOrder )
            afterNodeAccess( e );
        return e._value;
    }

    @Override public V put( K key, V value ) {
        return putVal( key, value, false, true );
    }

    @Override public V remove( Object key ) {
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

            afterNodeRemoval( e );

            _entryRecycler.recycle( e );

            --_count;
        }
        return oldValue;
    }

    @Override public void clear() {
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
        _head  = _tail = null;
    }

    @Override public Set<K> keySet() {
        Set<K> ks = _keySet;
        return (ks != null) ? ks : (_keySet = new SMTLinkedHashMap.KeySet());
    }

    @Override public void logStats( ReusableString out ) {
        int cnt = 0;
        out.append( "SMTLinkedHashMap logStats() capacity=" + _table.length + ", entries=" + _count ).append( "\n" );

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

    @Override public String toString() {
        return "SMTLinkedHashMap{" + "_count=" + _count + ", _table=" + Arrays.toString( _table ) + '}';
    }

    protected boolean removeEldestEntry( Map.Entry<K, V> eldest ) { return false; }

    void afterNodeAccess( HashEntry<K, V> e ) { // move HashEntry to last
        HashEntry<K, V> last;
        if ( _accessOrder && (last = _tail) != e ) {
            HashEntry<K, V> p = e, b = p._before, a = p._after;
            p._after = null;
            if ( b == null )
                _head = a;
            else
                b._after = a;
            if ( a != null )
                a._before = b;
            else
                last = b;
            if ( last == null )
                _head = p;
            else {
                p._before   = last;
                last._after = p;
            }
            _tail = p;
        }
    }

    void afterNodeInsertion( boolean evict ) { // possibly remove eldest
        HashEntry<K, V> first;
        if ( evict && (first = _head) != null && removeEldestEntry( first ) ) {
            K key = first._key;
            remove( key );
        }
    }

    void afterNodeRemoval( HashEntry<K, V> e ) { // unlink
        HashEntry<K, V> p = e, b = p._before, a = p._after;
        p._before = p._after = null;
        if ( b == null )
            _head = a;
        else
            b._after = a;
        if ( a == null )
            _tail = b;
        else
            a._before = b;
    }

    private HashEntry<K, V> getFirst( int hash ) {
        return _table[ hash & _tableIndexMask ];
    }

    private HashEntry<K, V> getNode( Object key ) {
        if ( key != null ) {
            final int       hash = hash( key );
            HashEntry<K, V> e    = getFirst( hash );
            while( e != null ) {
                if ( key.equals( e._key ) )
                    return e;
                e = e._next;
            }
        }
        return null;
    }

    private int hash( Object h ) {
        if ( h == null ) return 0;

        return h.hashCode();
    }

    private void linkNodeLast( HashEntry<K, V> p ) {
        HashEntry<K, V> last = _tail;
        _tail = p;
        if ( last == null )
            _head = p;
        else {
            p._before   = last;
            last._after = p;
        }
    }

    private HashEntry<K, V> newEntry( K key, HashEntry<K, V> next, V value ) {
        HashEntry<K, V> entry;
        entry = _entryFactory.get();
        entry.set( key, next, value );
        linkNodeLast( entry );
        return entry;
    }

    private V putVal( K key, V value, boolean onlyIfAbsent, boolean evict ) {
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

            if ( !onlyIfAbsent || oldValue == null ) {
                e._value = value;
            }

            afterNodeAccess( e );

        } else {
            oldValue     = null;
            tab[ index ] = newEntry( key, first, value );
            afterNodeInsertion( evict );
            _count = c;
        }

        return oldValue;
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

    abstract class HashIterator {

        HashEntry<K, V> _next;
        HashEntry<K, V> _current;

        HashIterator() {
            _next    = _head;
            _current = null;
        }

        public final boolean hasNext() {
            return _next != null;
        }

        public final void remove() {
            HashEntry<K, V> p = _current;
            if ( p == null )
                throw new IllegalStateException();
            _current = null;
            K key = p._key;
            SMTLinkedHashMap.this.remove( key );
        }

        final HashEntry<K, V> nextHashEntry() {
            HashEntry<K, V> e = _next;
            if ( e == null )
                throw new NoSuchElementException();
            _current = e;
            _next    = e._after;
            return e;
        }
    }

    final class KeyIterator extends HashIterator implements Iterator<K> {

        @Override public final K next() { return nextHashEntry().getKey(); }
    }

    final class ValueIterator extends HashIterator implements Iterator<V> {

        @Override public final V next() { return nextHashEntry()._value; }
    }

    final class EntryIterator extends HashIterator implements Iterator<Map.Entry<K, V>> {

        @Override public final Map.Entry<K, V> next() { return nextHashEntry(); }
    }

    final class KeySet extends AbstractSet<K> {

        @Override public final void forEach( Consumer<? super K> action ) {
            if ( action == null )
                throw new NullPointerException();
            for ( HashEntry<K, V> e = _head; e != null; e = e._after )
                  action.accept( e._key );
        }

        @Override public final Iterator<K> iterator()       { return new KeyIterator(); }

        @Override public final int size()                   { return SMTLinkedHashMap.this.size(); }

        @Override public final boolean contains( Object o ) { return containsKey( o ); }

        @Override public final boolean remove( Object key ) { return SMTLinkedHashMap.this.remove( key ) != null; }

        @Override public final void clear()                 { this.clear(); }
    }

    final class Values extends AbstractCollection<V> {

        @Override public final void forEach( Consumer<? super V> action ) {
            if ( action == null ) throw new NullPointerException();
            for ( HashEntry<K, V> e = _head; e != null; e = e._after )
                  action.accept( e._value );
        }

        @Override public Iterator<V> iterator()       { return new SMTLinkedHashMap.ValueIterator(); }

        @Override public int size()                   { return SMTLinkedHashMap.this.size(); }

        @Override public boolean contains( Object o ) { return SMTLinkedHashMap.this.containsValue( o ); }

        @Override public void clear()                 { SMTLinkedHashMap.this.clear(); }
    }

    final class EntrySet extends AbstractSet<Map.Entry<K, V>> {

        @Override public final void forEach( Consumer<? super Map.Entry<K, V>> action ) {
            if ( action == null ) throw new NullPointerException();
            for ( HashEntry<K, V> e = _head; e != null; e = e._after )
                  action.accept( e );
        }

        @Override public final Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override public final int size()   { return SMTLinkedHashMap.this.size(); }

        @Override public final boolean contains( Object o ) {
            if ( !(o instanceof Map.Entry) )
                return false;
            Map.Entry<?, ?> e         = (Map.Entry<?, ?>) o;
            Object          key       = e.getKey();
            HashEntry<K, V> candidate = getNode( key );
            return candidate != null && candidate.equals( e );
        }

        @Override public final boolean remove( Object o ) {
            if ( o instanceof Map.Entry ) {
                Map.Entry<?, ?> e     = (Map.Entry<?, ?>) o;
                Object          key   = e.getKey();
                Object          value = e.getValue();
                return SMTLinkedHashMap.this.remove( key ) != null;
            }
            return false;
        }

        @Override public final void clear() { SMTLinkedHashMap.this.clear(); }
    }
}
