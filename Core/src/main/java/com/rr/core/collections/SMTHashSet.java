/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.lang.ReusableString;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Objects;

/**
 * non thread safe templated hash _map which uses primitive ints as keys without autoboxing
 * <p>
 * currently does NOT pool entry objects so only populate at startup with minor updates during day
 * <p>
 * based on java.util.HashSet specialised to minimise GC
 */
public class SMTHashSet<E> implements SMTSet<E> {

    // Dummy value to associate with an Object in the backing Map
    private static final Object PRESENT = new Object();
    private final transient SMTHashMap<E, Object> _map;

    public SMTHashSet() {
        this( 16, 0.75f );
    }

    public SMTHashSet( int initialCapacity ) {
        this( initialCapacity, 0.75f );
    }

    public SMTHashSet( int initialCapacity, float loadFactor ) {
        _map = new SMTHashMap<>( initialCapacity, loadFactor );
    }

    /**
     * Adds the specified element to this set if it is not already present.
     * More formally, adds the specified element <tt>e</tt> to this set if
     * this set contains no element <tt>e2</tt> such that
     * <tt>(e==null&nbsp;?&nbsp;e2==null&nbsp;:&nbsp;e.equals(e2))</tt>.
     * If this set already contains the element, the call leaves the set
     * unchanged and returns <tt>false</tt>.
     * <p>
     * NOTE caller MUST make copy of the element if its going to be reused
     *
     * @param e element to be added to this set
     * @return <tt>true</tt> if this set did not already contain the specified
     * element
     */
    @Override public boolean add( E e ) {
        return _map.put( e, PRESENT ) == null;
    }

    /**
     * Returns <tt>true</tt> if this set contains the specified element.
     * More formally, returns <tt>true</tt> if and only if this set
     * contains an element <tt>e</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
     *
     * @param o element whose presence in this set is to be tested
     * @return <tt>true</tt> if this set contains the specified element
     */
    @Override public boolean contains( Object o ) {
        return _map.containsKey( o );
    }

    /**
     * Returns the number of elements in this set (its cardinality).
     *
     * @return the number of elements in this set (its cardinality)
     */
    @Override public int size() {
        return _map.size();
    }

    /**
     * Returns <tt>true</tt> if this set contains no elements.
     *
     * @return <tt>true</tt> if this set contains no elements
     */
    @Override public boolean isEmpty() {
        return _map.isEmpty();
    }

    /**
     * Removes the specified element from this set if it is present.
     * More formally, removes an element <tt>e</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>,
     * if this set contains such an element.  Returns <tt>true</tt> if
     * this set contained the element (or equivalently, if this set
     * changed as a result of the call).  (This set will not contain the
     * element once the call returns.)
     *
     * @param o object to be removed from this set, if present
     * @return <tt>true</tt> if the set contained the specified element
     */
    @Override public boolean remove( Object o ) {
        return _map.remove( o ) == PRESENT;
    }

    /**
     * Removes all of the elements from this set.
     * The set will be empty after this call returns.
     */
    @Override public void clear() {
        _map.clear();
    }

    @Override public void logStats( final ReusableString out ) {
        _map.logStats( out );
    }

    @Override public Collection<E> keys() {
        return _map.keySet();
    }

    /**
     * Returns an iterator over the elements in this set.  The elements
     * are returned in no particular order.
     *
     * @return an Iterator over the elements in this set
     * @see ConcurrentModificationException
     */
    @Override public Iterator<E> iterator() {
        return keys().iterator();
    }

    @Override public Object[] toArray() {
        return keys().toArray();
    }

    @Override public <T> T[] toArray( final T[] a ) {
        return keys().toArray( a );
    }

    @Override public boolean containsAll( final Collection<?> c ) {
        return keys().containsAll( c );
    }

    @Override public boolean addAll( final Collection<? extends E> c ) {
        boolean modified = false;
        for ( E e : c )
            if ( add( e ) )
                modified = true;
        return modified;
    }

    @Override public boolean retainAll( final Collection<?> c ) {
        Objects.requireNonNull( c );
        boolean     modified = false;
        Iterator<E> it       = iterator();
        while( it.hasNext() ) {
            if ( !c.contains( it.next() ) ) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override public boolean removeAll( final Collection<?> c ) {
        Objects.requireNonNull( c );
        boolean     modified = false;
        Iterator<?> it       = iterator();
        while( it.hasNext() ) {
            if ( c.contains( it.next() ) ) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

}


