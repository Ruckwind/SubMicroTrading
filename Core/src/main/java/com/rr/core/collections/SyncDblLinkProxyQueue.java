/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.model.Event;

import java.util.Iterator;

/**
 * threadsafe wrapper to a DoubleLinkedMessageQueue
 * <p>
 * acts as proxy safety is by locking
 */
public class SyncDblLinkProxyQueue implements DoubleLinkedEventQueue {

    private DoubleLinkedEventQueue _letter;

    public SyncDblLinkProxyQueue( final DoubleLinkedEventQueue letter ) {
        _letter = letter;
    }

    @Override public void addFirst( final Event msg ) {
        synchronized( _letter ) {
            _letter.addFirst( msg );
        }
    }

    @Override public Iterator<Event> descendingIterator() {
        synchronized( _letter ) {
            return _letter.descendingIterator();
        }
    }

    @Override public DoubleLinkedNode getHead() {
        synchronized( _letter ) {
            return _letter.getHead();
        }
    }

    @Override public DoubleLinkedNode getTail() {
        synchronized( _letter ) {
            return _letter.getTail();
        }
    }

    @Override public void setTail( final DoubleLinkedNode tail ) {
        synchronized( _letter ) {
            _letter.setTail( tail );
        }
    }

    @Override public Event last() {
        synchronized( _letter ) {
            return _letter.last();
        }
    }

    @Override public void moveToEndOfDestQueue( final DoubleLinkedEventQueue dest ) {
        synchronized( _letter ) {
            _letter.moveToEndOfDestQueue( dest );
        }
    }

    @Override public Event removeLast() {
        synchronized( _letter ) {
            return _letter.removeLast();
        }
    }

    @Override public void setSize( final int newSize ) {
        synchronized( _letter ) {
            _letter.setSize( newSize );
        }
    }

    @Override public String getComponentId() {
        return _letter.getComponentId();
    }

    @Override public Iterator<Event> iterator() {
        synchronized( _letter ) {
            return _letter.iterator();
        }
    }

    @Override public Event poll() {
        synchronized( _letter ) {
            return _letter.poll();
        }
    }

    @Override public Event next() {
        synchronized( _letter ) {
            return _letter.next();
        }
    }

    @Override public boolean add( final Event e ) {
        synchronized( _letter ) {
            return _letter.add( e );
        }
    }

    @Override public boolean isEmpty() {
        synchronized( _letter ) {
            return _letter.isEmpty();
        }
    }

    @Override public boolean canMessageAttachMultipleQueues() {
        synchronized( _letter ) {
            return _letter.canMessageAttachMultipleQueues();
        }
    }

    @Override public int size() {
        synchronized( _letter ) {
            return _letter.size();
        }
    }

    @Override public void clear() {
        synchronized( _letter ) {
            _letter.clear();
        }
    }

    @Override public EventQueue newInstance() {
        return _letter.newInstance();
    }
}
