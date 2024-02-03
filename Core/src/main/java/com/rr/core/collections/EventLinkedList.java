/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.model.Event;

/**
 * generally processor will send only one message up or downstream so optimise for that
 *
 * @author Richard Rose
 */
public class EventLinkedList {

    private final EventHead _head = new EventHead();
    private Event _first;
    private Event _tail = _head;

    public boolean add( Event msg ) {
        if ( _first == null ) {
            _first = msg;
        } else {
            _tail.attachQueue( msg );
            _tail = msg;
        }

        return true;
    }

    public void clear() {
        // dont recycle, somethings wrong to be clearing the queue, dont assume ownershipof instances in Q
        _head.detachQueue();
        _tail = _head;
    }

    public Event get() {
        if ( _first != null ) {
            Event tmp = _first;
            _first = null;
            return tmp;
        } else if ( _head == _tail ) {
            return null;
        } else {
            Event tmp  = _head.getNextQueueEntry();
            Event next = tmp.getNextQueueEntry();
            _head.attachQueue( next );

            if ( next == null ) {
                _tail = _head;
            }

            return tmp;
        }
    }

    public Event next() {
        return get();
    }

    public Event poll() {
        return get();
    }

    public int size() {

        int count = 0;

        if ( _first != null ) {
            ++count;
        }

        if ( _head == _tail ) return count;

        Event m = _head.getNextQueueEntry();

        while( m != null ) {
            ++count;

            m = m.getNextQueueEntry();
        }

        return count;
    }
}
