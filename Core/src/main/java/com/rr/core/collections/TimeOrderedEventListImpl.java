package com.rr.core.collections;

import com.rr.core.lang.Constants;
import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;
import com.rr.core.session.Session;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * non threadsafe ordered event list
 * efficient as long as events inserted in mostly timeorder
 * <p>
 * WORST CASE is O(N) for insert
 */
public class TimeOrderedEventListImpl implements TimeOrderedEventList {

    private final String                     _id;
    private final DoubleLinkedEventQueueImpl _events = new DoubleLinkedEventQueueImpl();

    public TimeOrderedEventListImpl( String id ) {
        _id = id;
    }

    @Override public String getComponentId() {
        return _id;
    }

    @Override public Event getLast()            { return _events.last(); }

    @Override public void moveTo( final DoubleLinkedEventQueue dest ) {
        dest.clear();

        _events.moveToEndOfDestQueue( dest );
    }

    @Override public Event removeLast() {
        return _events.removeLast();
    }

    @Override public Iterator<Event> iterator() { return _events.iterator(); }

    @Override public EventQueue newInstance() {
        return new TimeOrderedEventListImpl( _id );
    }

    @Override public boolean add( final Event e ) {
        enqueueByTime( e );
        return true;
    }

    @Override public boolean canMessageAttachMultipleQueues() { return true; }

    @Override public void clear() {
        _events.clear();
    }

    @Override public boolean isEmpty() {
        return _events.isEmpty();
    }

    @Override public Event next() {
        Event m;

        do {
            m = poll();
        } while( m == null );

        return m;
    }

    @Override public Event poll() {
        return _events.poll();
    }

    @Override public int size() {
        return _events.size();
    }

    public Iterator<Event> descendingIterator() {
        return _events.descendingIterator();
    }

    /**
     * oldest events at end of queue, this is by far optimal as new events coming into queue will be in time order and generally have newer timestamps so will be quickly inserted at the head
     *
     * @param msg
     */
    private void enqueueByTime( final Event msg ) {
        long newEventTime = msg.getEventTimestamp();

        if ( newEventTime == Constants.UNSET_LONG ) {
            _events.addFirst( msg ); // no time so add to start of the list ... it will assume time order
        } else {
            // generally expect new events to have newer timestamps and go at start of the queue
            ListIterator<Event> it = _events.listIterator();
            while( it.hasNext() ) {
                Event cur          = it.next();
                long  curEventTime = cur.getEventTimestamp();

                if ( newEventTime == curEventTime || curEventTime == Constants.UNSET_LONG ) {

                    EventHandler curHandler = cur.getEventHandler();
                    EventHandler newHandler = msg.getEventHandler();

                    if ( curHandler instanceof Session && newHandler instanceof Session ) {
                        if ( ((Session) newHandler).getIntId() >= ((Session) curHandler).getIntId() ) {
                            it.previous();
                            it.add( msg );
                            return;
                        }
                    } else {
                        it.previous();
                        it.add( msg );
                        return;
                    }

                } else if ( newEventTime > curEventTime ) {
                    it.previous();
                    it.add( msg );
                    return;
                }
            }

            _events.add( msg );
        }
    }
}
