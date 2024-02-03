/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.lang.ReusableString;
import com.rr.core.utils.Percentiles;

import java.util.ArrayList;
import java.util.List;

/**
 * non thread safe templated hash map which uses primitive ints as keys without autoboxing
 * <p>
 * currently does NOT pool entry objects so only populate at startup with minor updates during day
 */
public class IntToIntHashMap {

    private static final int MAXIMUM_CAPACITY = 1 << 30;

    private static class HashEntry {

        int       _key;
        int       _value;
        HashEntry _next;

        static HashEntry[] newArray( int size ) {
            return new HashEntry[ size ];
        }

        public HashEntry( int key, HashEntry next, int value ) {
            _key   = key;
            _next  = next;
            _value = value;
        }
    }
    private final float       _loadFactor;
    private       int         _count;
    private       HashEntry[] _table;
    private       int         _tableIndexMask;
    private       int         _threshold;

    static int hash( int h ) {
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    public IntToIntHashMap( int initialCapacity, float loadFactor ) {
        if ( !(loadFactor > 0) || initialCapacity < 0 )
            throw new IllegalArgumentException();

        if ( initialCapacity > MAXIMUM_CAPACITY )
            initialCapacity = MAXIMUM_CAPACITY;

        int capacity = 1;
        while( capacity < initialCapacity )
            capacity <<= 1;

        _loadFactor = loadFactor;
        HashEntry[] table = HashEntry.newArray( capacity );
        setTable( table );
    }

    public void clear() {
        final int max = _table.length;
        for ( int i = 0; i < max; ++i ) {
            _table[ i ] = null;
        }
        _count = 0;
    }

    public boolean containsKey( int key ) {
        final int hash = hash( key );
        HashEntry e    = getFirst( hash );
        while( e != null ) {
            if ( key == e._key )
                return true;
            e = e._next;
        }
        return false;
    }

    public boolean containsValue( int value ) {
        if ( value == 0 ) return false;

        final HashEntry[] tab = _table;
        int               len = tab.length;
        for ( int i = 0; i < len; i++ ) {
            for ( HashEntry e = tab[ i ]; e != null; e = e._next ) {
                int v = e._value;
                if ( value == v )
                    return true;
            }
        }

        return false;
    }

    /**
     * return the corresponding int value or ZERO if no entry
     *
     * @param key
     * @return
     */
    public int get( int key ) {
        final int hash = hash( key );
        HashEntry e    = getFirst( hash );
        while( e != null ) {
            if ( key == e._key ) {
                return e._value;
            }
            e = e._next;
        }
        return 0;
    }

    public boolean isEmpty() {
        return _count == 0;
    }

    public void logStats( ReusableString out ) {
        int cnt = 0;
        out.append( "IntTointHashMap logStats() capacity=" + _table.length + ", entries=" + _count ).append( "\n" );

        List<Integer> sizes = new ArrayList<>( cnt / 10 );
        for ( int i = 0; i < _table.length; ++i ) {
            HashEntry e       = _table[ i ];
            int       entries = 0;
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

    public int put( int key, int value ) {
        final int hash = hash( key );
        int       c    = _count;
        if ( c++ > _threshold ) // ensure capacity
            rehash();
        final HashEntry[] tab   = _table;
        int               index = hash & _tableIndexMask;
        HashEntry         first = tab[ index ];
        HashEntry         e     = first;
        while( e != null && key != e._key )
            e = e._next;

        int oldValue;
        if ( e != null ) {
            oldValue = e._value;
            e._value = value;
        } else {
            oldValue     = 0;
            tab[ index ] = new HashEntry( key, first, value );
            _count       = c;
        }
        return oldValue;
    }

    public int remove( int key ) {
        final int         hash  = hash( key );
        final HashEntry[] tab   = _table;
        int               index = hash & _tableIndexMask;
        HashEntry         first = tab[ index ];
        HashEntry         prev  = first;
        HashEntry         e     = first;
        while( e != null && key != e._key ) {
            prev = e;
            e    = e._next;
        }

        int oldValue = 0;
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

    public boolean replace( int key, int oldValue, int newValue ) {
        final int hash = hash( key );
        HashEntry e    = getFirst( hash );
        while( e != null && key != e._key )
            e = e._next;

        boolean replaced = false;
        if ( e != null && oldValue == e._value ) {
            replaced = true;
            e._value = newValue;
        }
        return replaced;
    }

    public int replace( int key, int newValue ) {
        final int hash = hash( key );
        HashEntry e    = getFirst( hash );
        while( e != null && e._key != key )
            e = e._next;

        int oldValue = 0;
        if ( e != null ) {
            oldValue = e._value;
            e._value = newValue;
        }
        return oldValue;
    }

    public int size() {
        return _count;
    }

    private HashEntry getFirst( int hash ) {
        return _table[ hash & _tableIndexMask ];
    }

    private void rehash() {
        HashEntry[] oldTable    = _table;
        int         oldCapacity = oldTable.length;
        if ( oldCapacity >= MAXIMUM_CAPACITY )
            return;

        HashEntry[] newTable = HashEntry.newArray( oldCapacity << 1 );
        int         sizeMask = newTable.length - 1;
        for ( int i = 0; i < oldCapacity; i++ ) {
            HashEntry e = oldTable[ i ];

            if ( e != null ) {

                do {
                    HashEntry       next            = e._next;
                    int             idx             = hash( e._key ) & sizeMask;
                    final HashEntry entryInNewTable = newTable[ idx ];            // keep current root entry in new table

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

    private void setTable( HashEntry[] newTable ) {
        _table          = newTable;
        _threshold      = (int) (_table.length * _loadFactor);
        _tableIndexMask = _table.length - 1;    // as table must be power of 2
    }
}
