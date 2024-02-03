package com.rr.core.collections;

import com.rr.core.model.Event;

import java.util.Iterator;

/**
 * Simple iterator for Message queue .... only usable for MessageQueues which use the Message next queue pointer
 *
 * @WARNING not threadsafe, do not modify the returned objects next pointer or you will corrupt the queue !
 */
public class EventQueueIterator implements Iterator<Event> {

    private Event _next;

    public EventQueueIterator( Event root ) {
        _next = root;
    }

    @Override public boolean hasNext() {
        return _next != null;
    }

    @Override public Event next() {
        Event curr = _next;
        if ( curr == null ) return null;
        _next = _next.getNextQueueEntry();
        return curr;
    }
}
