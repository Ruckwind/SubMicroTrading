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

public class BlockingSyncQueue implements BlockingEventQueue {

    static class MessageHead extends BaseEvent {

        @Override public void dump( final ReusableString out ) { /* nothing */ }

        @Override public ReusableType getReusableType() {
            return CollectionTypes.BlockSyncQueueHead;
        }
    }

    /**
     * Pointer to header node, initialized to a dummy node.  The first actual node is at head.getNext().
     * The header node itself never changes
     */
    private final Event  _head        = new MessageHead();
    private final String _id;
    private       Event  _tail        = _head;
    private       long   _interuptCnt = 0;

    public BlockingSyncQueue() {
        this( null );
    }

    public BlockingSyncQueue( String id ) {
        _id = id;
    }

    @Override public String getComponentId() {
        return _id;
    }

    @Override public Object getMutex()                        { return _head; }

    /**
     * @return
     * @WARNING non thread safe iterator
     */
    @Override public Iterator<Event> iterator() { return new EventQueueIterator( _head.getNextQueueEntry() ); }

    @Override
    public Event poll() {
        Event t;

        synchronized( _head ) {
            if ( _tail == _head ) return null;

            t = _head.getNextQueueEntry();

            if ( _tail != t ) {
                _head.attachQueue( t.getNextQueueEntry() );
            } else {
                _head.attachQueue( null );
                _tail = _head;
            }
        }

        t.attachQueue( null );

        return t;
    }

    @Override public Event next() {
        Event t;

        do {

            synchronized( _head ) {

                if ( _tail == _head ) {
                    try {
                        _head.wait();
                    } catch( InterruptedException e ) {
                        ++_interuptCnt;
                    }
                }

                t = _head.getNextQueueEntry();

                if ( t != null ) {
                    if ( _tail != t ) {
                        _head.attachQueue( t.getNextQueueEntry() );
                    } else {
                        _head.attachQueue( null );
                        _tail = _head;
                    }
                }
            }

        } while( t == null );

        t.attachQueue( null );

        return t;
    }

    @Override public boolean add( Event e ) {

        if ( e.getNextQueueEntry() != null ) throw new SMTRuntimeException( getComponentId() + " BlockingSyncQueue elements can only belong to one queue at a time" );

        synchronized( _head ) {
            boolean signal = (_head == _tail);

            _tail.attachQueue( e );
            _tail = e;

            if ( signal ) _head.notifyAll();
        }

        return true;
    }

    @Override public boolean isEmpty() {
        synchronized( _head ) {
            return (_head.getNextQueueEntry() == null);
        }
    }

    @Override public boolean canMessageAttachMultipleQueues() { return false; }

    @Override public int size() {
        int cnt = 0;

        Event m;

        synchronized( _head ) {
            m = _head.getNextQueueEntry();
        }

        while( m != null ) {
            ++cnt;
            m = m.getNextQueueEntry();
        }

        return cnt;
    }

    @Override public void clear() {
        while( poll() != null ) ;
    }

    @Override public EventQueue newInstance() {
        return new BlockingSyncQueue( _id );
    }

    /**
     * @return chain of Messages in order of insertion
     * @NOTE for some reason this fails in PerfTestQueueTest
     */
    public Event getChain() {
        Event t;

        synchronized( _head ) {
            t = _head.getNextQueueEntry();
            _head.attachQueue( null );
            _tail = _head;
        }

        return t;
    }
}
