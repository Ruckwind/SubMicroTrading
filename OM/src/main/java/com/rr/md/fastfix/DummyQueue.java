/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.fastfix;

import com.rr.core.collections.EventQueue;
import com.rr.core.model.Event;

import java.util.Iterator;

/**
 * @WARNING USE WITH CAUTION ITEMS ADDED NOT RECYCLED SO WILL CONTRIB GC
 */
public class DummyQueue implements EventQueue {

    private final String _id;

    public DummyQueue() {
        this( null );
    }

    public DummyQueue( String id ) {
        _id = id;
    }

    @Override public String getComponentId() {
        return _id;
    }

    @Override public Iterator<Event> iterator() {
        return new Iterator<Event>() {

            @Override public boolean hasNext() {
                return false;
            }

            @Override public Event next() {
                return null;
            }
        };
    }

    @Override public Event poll() {
        return null;
    }

    @Override public Event next() {
        return null;
    }

    @Override public boolean add( Event e ) {
        return false;
    }

    @Override public boolean isEmpty() {
        return true;
    }

    @Override public boolean canMessageAttachMultipleQueues() { return true; }

    @Override public int size() {
        return 0;
    }

    @Override public void clear()             { /* nothing */ }

    @Override public EventQueue newInstance() { return new DummyQueue( _id ); }
}
