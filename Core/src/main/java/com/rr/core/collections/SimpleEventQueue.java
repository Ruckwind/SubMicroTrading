/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableType;
import com.rr.core.model.BaseEvent;
import com.rr.core.model.Event;
import com.rr.core.utils.SMTRuntimeException;

import java.util.Iterator;

public class SimpleEventQueue implements EventQueue {

    static class MessageHead extends BaseEvent {

        @Override public void dump( ReusableString out ) { /* nothing */ }

        @Override public ReusableType getReusableType() {
            return CollectionTypes.SimpleMessageQueue;
        }
    }

    /**
     * Pointer to header node, initialized to a dummy node.  The first actual node is at head.getNext().
     * The header node itself never changes
     */
    private final Event _head = new MessageHead();
    private final String _id;
    private       Event _tail = _head;
    private       int    _size = 0;

    public SimpleEventQueue() {
        this( null );
    }

    public SimpleEventQueue( String id ) {
        _id = id;
    }

    @Override
    public String getComponentId() {
        return _id;
    }

    /**
     * @return
     * @WARNING non thread safe iterator
     */
    @Override public Iterator<Event> iterator() { return new EventQueueIterator( _head.getNextQueueEntry() ); }

    @Override public EventQueue newInstance() {
        return new SimpleEventQueue( _id );
    }

    @Override
    public boolean add( Event e ) {
        if ( e.getNextQueueEntry() != null ) throw new SMTRuntimeException( getComponentId() + " SimpleMessageQueue elements can only belong to one queue at a time" );

        _tail.attachQueue( e );
        _tail = e;

        ++_size;

        return true;
    }

    @Override public boolean canMessageAttachMultipleQueues() { return false; }

    @Override
    public void clear() {
        while( poll() != null ) ;
    }

    @Override
    public boolean isEmpty() {
        return (_head.getNextQueueEntry() == null);
    }

    @Override
    public Event next() {
        Event m;

        do {
            m = poll();
        } while( m == null );

        return m;
    }

    @Override
    public Event poll() {
        Event t;

        if ( _tail == _head ) return null;

        t = _head.getNextQueueEntry();

        if ( _tail != t ) {
            _head.attachQueue( t.getNextQueueEntry() );
        } else {
            _head.attachQueue( null );
            _tail = _head;
        }

        t.attachQueue( null );

        --_size;

        return t;
    }

    @Override
    public int size() {
        return _size;
    }

    /**
     * @return chain of Messages in order of insertion
     * @NOTE for some reason this fails in PerfTestQueueTest
     */
    public Event getChain() {
        Event t;

        t = _head.getNextQueueEntry();
        _head.attachQueue( null );
        _tail = _head;

        _size = 0;

        return t;
    }
}
