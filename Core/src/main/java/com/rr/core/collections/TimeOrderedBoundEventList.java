package com.rr.core.collections;

import com.rr.core.model.Event;

import java.util.Iterator;

/**
 * threadsafe ordered event list with fixed bound size which can be large
 * <p>
 * efficient as long as events inserted in mostly timeorder
 * <p>
 * doesnt use an array list due to cost of insertion near start of large array
 * <p>
 * note size can exceed bound when pushback used to add at end
 * <p>
 * WORST CASE is O(N) for insert
 * <p>
 * WARNING !  ONLY FOR USE WITH SINGLE CONSUMER
 */
public class TimeOrderedBoundEventList implements TimeOrderedEventList {

    private final String                   _id;
    private final TimeOrderedEventListImpl _events;
    private final int                      _max;
    private final int                      _maxWaitMS;

    public TimeOrderedBoundEventList( String id, int max, int maxWaitMS ) {
        _id        = id;
        _max       = max;
        _events    = new TimeOrderedEventListImpl( _id );
        _maxWaitMS = maxWaitMS;
    }

    @Override public String getComponentId()                  { return _id; }

    @Override public <T> T getLast() {

        do {
            synchronized( this ) {
                int size = _events.size();

                if ( size > 0 ) {
                    return (T) _events.getLast();
                }

                waitFor();
            }

        } while( true );
    }

    @Override public void moveTo( final DoubleLinkedEventQueue dest ) {
        synchronized( this ) {
            _events.moveTo( dest );
        }
    }

    @Override public <T> T removeLast() {

        do {
            synchronized( this ) {
                int size = _events.size();

                if ( size > 0 ) {
                    return (T) _events.removeLast();
                }

                waitFor();
            }

        } while( true );
    }

    /**
     * @return
     * @WARNING non thread safe iterator
     */
    @Override public Iterator<Event> iterator() { return _events.iterator(); }

    @Override public Event poll() {

        Event m;

        synchronized( this ) {

            int size = _events.size();

            m = _events.poll();

            if ( m != null && size == _max ) {
                this.notifyAll(); // was blocked .. wake up any pending writers
            }
        }

        return m;
    }

    @Override public Event next() {
        Event m;

        do {
            synchronized( this ) {
                m = poll();

                if ( m == null ) {
                    waitFor();
                }
            }

        } while( m == null );

        return m;
    }

    @Override public boolean add( final Event event ) {
        boolean added = false;

        do {
            synchronized( this ) {

                int size = _events.size();

                if ( size == _max ) {
                    waitFor();
                } else {
                    _events.add( event );
                    added = true;
                }
            }

        } while( !added );

        return true;
    }

    @Override public synchronized boolean isEmpty()           { return _events.isEmpty(); }

    @Override public boolean canMessageAttachMultipleQueues() { return true; }

    @Override public synchronized int size()                  { return _events.size(); }

    @Override public synchronized void clear()                { _events.clear(); }

    @Override public int maxCapacity()                        { return _max; }

    @Override public EventQueue newInstance() {
        return new TimeOrderedBoundEventList( _id, _max, _maxWaitMS );
    }

    @Override public String toString() {
        return "TimeOrderedBoundEventList{" +
               "_id='" + _id + '\'' +
               ", _events=" + _events.size() +
               ", _max=" + _max +
               '}';
    }

    public synchronized boolean isFull() { return _events.size() >= _max; }

    private void waitFor() {
        if ( _maxWaitMS >= 0 ) {
            notifyAll();

            try {
                this.wait( _maxWaitMS );

            } catch( InterruptedException e ) {
                // ignore
            }
        }
    }
}
