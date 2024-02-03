/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.model.Event;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.SuperpoolManager;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * NON THREADSAFE DOUBLE LINKED MESSAGE QUEUE
 */
public class DoubleLinkedEventQueueImpl implements DoubleLinkedEventQueue {

    public static class ForwardIterator implements Iterator<Event> {

        private DoubleLinkedNode _next;

        public ForwardIterator( DoubleLinkedNode root ) {
            _next = root;
        }

        @Override public boolean hasNext() {
            return _next != null;
        }

        @Override public Event next() {
            DoubleLinkedNode curr = _next;
            if ( curr == null ) return null;
            _next = _next.getNext();
            return curr.getValue();
        }
    }

    /**
     * Adapter to provide descending iterators via ListItr.previous
     */
    private static class DescendingIterator implements Iterator<Event> {

        private DoubleLinkedNode _tail;
        private DoubleLinkedNode _head;

        public DescendingIterator( DoubleLinkedNode head, DoubleLinkedNode tail ) {
            _tail = tail;
            _head = head;
        }

        @Override public boolean hasNext() {
            return _tail != _head;
        }

        @Override public Event next() {
            DoubleLinkedNode curr = _tail;
            if ( curr == _head ) return null;
            _tail = _tail.getPrev();
            return curr.getValue();
        }
    }
    /**
     * Pointer to header node, initialized to a dummy node.  The first actual node is at head.getNext().
     * The header node itself never changes
     */
    private final DoubleLinkedNode _head = new DoubleLinkedNode();
    private final String _id;
    private final DoubleLinkedNode.DoubleLinkedNodeFactory _entryFactory;
    private final Recycler<DoubleLinkedNode>               _entryRecycler;
    private       DoubleLinkedNode _tail = _head;
    private       int    _size = 0;
    public DoubleLinkedEventQueueImpl() {
        this( null );
    }

    public DoubleLinkedEventQueueImpl( String id ) {
        _id = id;
        Recycler<DoubleLinkedNode> entryRecycler;
        _entryFactory  = SuperpoolManager.instance().getFactory( DoubleLinkedNode.DoubleLinkedNodeFactory.class, DoubleLinkedNode.class );
        entryRecycler  = SuperpoolManager.instance().getRecycler( DoubleLinkedNode.class );
        _entryRecycler = entryRecycler;
    }

    @Override public String getComponentId() { return _id; }

    /**
     * @return
     * @WARNING non thread safe iterator
     */
    @Override public Iterator<Event> iterator() { return new ForwardIterator( _head.getNext() ); }

    /**
     * moves all elements in this queue to the end of the destination queue leaving this queue empty
     *
     * @param dest
     */
    @Override public void moveToEndOfDestQueue( final DoubleLinkedEventQueue dest ) {

        if ( _size > 0 ) {

            DoubleLinkedNode srcFirstElem = getHead().getNext();
            DoubleLinkedNode srcLastElem  = getTail();

            final DoubleLinkedNode destHead      = dest.getHead();
            DoubleLinkedNode       destFirstElem = destHead.getNext();
            DoubleLinkedNode       destLastElem  = dest.getTail();

            int totSize = dest.size() + _size;

            if ( dest.size() == 0 ) {
                destHead.setNext( srcFirstElem );
                srcFirstElem.setPrev( destHead );
            } else {
                destLastElem.setNext( srcFirstElem );
                srcFirstElem.setPrev( destLastElem );
            }

            dest.setTail( srcLastElem );
            dest.setSize( totSize );

            _head.setNext( null );
            _tail = _head;

            _size = 0;
        }
    }

    @Override public DoubleLinkedNode getHead() { return _head; }

    @Override public DoubleLinkedNode getTail() { return _tail; }

    @Override public void setTail( final DoubleLinkedNode tail ) {
        _tail = tail;
    }

    @Override public void setSize( final int newSize ) {
        _size = newSize;
    }

    @Override public void addFirst( final Event e ) {
        DoubleLinkedNode newNode = _entryFactory.get();

        final DoubleLinkedNode origHead = _head.getNext();

        newNode.set( _head, origHead, e );

        _head.setNext( newNode );

        if ( _tail == _head ) {
            _tail = newNode;
        } else {
            origHead.setPrev( newNode );
        }

        ++_size;
    }

    /**
     * @return NULL
     */
    @Override public Event removeLast() {
        if ( _tail == _head ) return null;

        DoubleLinkedNode t = _tail;

        if ( _head.getNext() == t ) {
            _head.setNext( null );
            _tail = _head;
        } else {
            _tail = t.getPrev();
            _tail.setNext( null );
        }

        Event m = t.getValue();

        t.setNext( null );
        _entryRecycler.recycle( t );

        --_size;

        return m;
    }

    @Override public Event last() {
        if ( _tail == _head ) return null;

        return _tail.getValue();
    }

    @Override public Iterator<Event> descendingIterator() { return new DescendingIterator( _head, _tail ); }

    @Override public Event poll() {
        DoubleLinkedNode t;

        if ( _tail == _head ) return null;

        t = _head.getNext();

        if ( _tail != t ) {
            DoubleLinkedNode next = t.getNext();
            _head.setNext( next );
            if ( next != null ) next.setPrev( _head );
        } else {
            _head.setNext( null );
            _tail = _head;
        }

        Event m = t.getValue();

        t.setNext( null );
        _entryRecycler.recycle( t );

        --_size;

        return m;
    }

    /**
     * BLOCKING call to queue, SPINS if
     *
     * @return
     */
    @Override public Event next() {
        Event m;

        do {
            m = poll();
        } while( m == null );

        return m;
    }

    @Override public boolean add( Event e ) {

        DoubleLinkedNode newNode = _entryFactory.get();

        newNode.set( _tail, null, e );
        _tail.setNext( newNode );
        _tail = newNode;

        ++_size;

        return true;
    }

    @Override public boolean isEmpty() {
        return (_head.getNext() == null);
    }

    @Override public boolean canMessageAttachMultipleQueues() { return true; }

    @Override public int size() {
        return _size;
    }

    @Override public void clear() {
        while( poll() != null ) {
            /* nothing */
        }
    }

    @Override public EventQueue newInstance() {
        return new DoubleLinkedEventQueueImpl( _id );
    }

    public ListIterator<Event> listIterator() {
        return listIterator( 0 );
    }

    void linkBefore( Event e, DoubleLinkedNode succ ) {

        DoubleLinkedNode newNode = _entryFactory.get();

        DoubleLinkedNode pred = succ.getPrev();

        newNode.set( pred, succ, e );

        succ.setPrev( newNode );
        if ( pred == _head )
            _head.setNext( newNode );
        else
            pred.setNext( newNode );

        _size++;
    }

    DoubleLinkedNode node( int index ) {
        if ( index < (_size >> 1) ) {
            DoubleLinkedNode x = _head.getNext();
            for ( int i = 0; i < index; i++ )
                  x = x.getNext();
            return x;
        } else {
            DoubleLinkedNode x = _tail;
            for ( int i = _size - 1; i > index; i-- )
                  x = x.getPrev();
            return x;
        }
    }

    Event unlink( DoubleLinkedNode x ) {

        if ( x == null || x == _head ) return null;

        final Event            element = x.getValue();
        final DoubleLinkedNode next    = x.getNext();
        final DoubleLinkedNode prev    = x.getPrev();

        prev.setNext( next );

        if ( x == _tail ) {
            _tail = prev;
            _tail.setNext( null );
        } else {
            next.setPrev( prev );
        }

        x.setPrev( null );
        x.setVal( null );
        x.setNext( null );

        _entryRecycler.recycle( x );

        --_size;

        return element;
    }

    private ListIterator<Event> listIterator( final int idx ) {
        return new ListItr( idx );
    }

    private class ListItr implements ListIterator<Event> {

        private DoubleLinkedNode _lastReturned;
        private DoubleLinkedNode _next;
        private int              _nextIndex;

        ListItr( int index ) {
            // assert isPositionIndex(index);
            _next      = (index == _size) ? null : node( index );
            _nextIndex = index;
        }

        @Override public boolean hasNext() {
            return _nextIndex < _size;
        }

        @Override public Event next() {
            if ( !hasNext() )
                throw new NoSuchElementException();

            _lastReturned = _next;
            _next         = _next.getNext();
            _nextIndex++;
            return _lastReturned.getValue();
        }

        @Override public boolean hasPrevious() {
            return _nextIndex > 0;
        }

        @Override public Event previous() {
            if ( !hasPrevious() )
                throw new NoSuchElementException();

            _lastReturned = _next = (_next == null) ? _tail : _next.getPrev();
            _nextIndex--;
            return _lastReturned.getValue();
        }

        @Override public int nextIndex() {
            return _nextIndex;
        }

        @Override public int previousIndex() {
            return _nextIndex - 1;
        }

        @Override public void remove() {
            if ( _lastReturned == null )
                throw new IllegalStateException();

            DoubleLinkedNode lastNext = _lastReturned.getNext();
            unlink( _lastReturned );
            if ( _next == _lastReturned )
                _next = lastNext;
            else
                _nextIndex--;
            _lastReturned = null;
        }

        @Override public void set( Event e ) {
            if ( _lastReturned == null )
                throw new IllegalStateException();
            _lastReturned.setVal( e );
        }

        @Override public void add( Event e ) {
            _lastReturned = null;
            if ( _next == null )
                add( e );
            else
                linkBefore( e, _next );
            _nextIndex++;
        }
    }
}
