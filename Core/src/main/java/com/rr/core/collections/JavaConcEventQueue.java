package com.rr.core.collections;

import com.rr.core.model.Event;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * Creates GC in its temp nodes
 * <p>
 * spins on next if queue empty
 */
public class JavaConcEventQueue extends ConcurrentLinkedQueue<Event> implements EventQueue {

    private static final long serialVersionUID = 1L;

    private final String _id;

    public JavaConcEventQueue( Collection<? extends Event> c ) {
        super( c );
        _id = null;
    }

    public JavaConcEventQueue() {
        super();
        _id = null;
    }

    public JavaConcEventQueue( String id ) {
        super();
        _id = id;
    }

    @Override
    public String getComponentId() {
        return _id;
    }

    @Override public EventQueue newInstance() {
        return new JavaConcEventQueue( _id );
    }

    @Override public boolean canMessageAttachMultipleQueues() {
        return true;
    }

    @Override
    public Event next() {
        Event m;

        do {
            m = poll();
        } while( m == null );

        return m;
    }

    @Override public Event poll( final long timeout, final TimeUnit unit ) {
        Event m;

        m = poll();

        if ( m == null ) {
            try {
                long waitMS = unit.toMillis( timeout );

                m.wait( waitMS );

            } catch( InterruptedException e ) {
                // ignore
            }

            m = poll();
        }

        return m;
    }
}
