package com.rr.core.collections;

import com.rr.core.model.Event;
import com.rr.core.properties.AppProps;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Fxied Size Blocking MessageQueue
 * <p>
 * adapter to an ArrayBlockingQueue
 */
public class ArrayBlockingEventQueue implements EventQueue {

    private final ArrayBlockingQueue<Event> _queue;
    private final int                       _size;
    private final String                    _id;
    private final boolean                   _fairness;

    public ArrayBlockingEventQueue( String id, int size, final boolean fairness ) {
        _size     = size;
        _fairness = fairness;
        _queue    = new ArrayBlockingQueue<>( size, fairness );
        _id       = id;
    }

    @Override public String getComponentId() {
        return _id;
    }

    @Override public Iterator<Event> iterator() {
        return _queue.iterator();
    }

    @Override public Event poll() { return _queue.poll(); }

    @Override public Event poll( long timeout, TimeUnit unit ) {
        try {
            return _queue.poll( timeout, unit );
        } catch( InterruptedException e ) {
            // ignore
        }
        return null;
    }

    @Override public Event next() {

        Event m = null;

        while( m == null ) {
            try {
                m = _queue.take();
            } catch( InterruptedException e ) {
                // ignore
            }
        }

        return m;
    }

    @Override public boolean add( final Event e ) {

        while( true ) {
            try {
                _queue.put( e );
                break;
            } catch( InterruptedException e1 ) {
                // ignore
            }
        }

        return true;
    }

    @Override public boolean isEmpty() {
        return _queue.isEmpty();
    }

    @Override public boolean canMessageAttachMultipleQueues() { return true; }

    @Override public int size() {
        return _queue.size();
    }

    @Override public void clear() { _queue.clear(); }

    @Override public int maxCapacity() {
        return _queue.size() + _queue.remainingCapacity();
    }

    @Override public EventQueue newInstance() {
        return new ArrayBlockingEventQueue( _id, _size, _fairness );
    }

    @Override public void forceStop() {
        clear();
    }
}
