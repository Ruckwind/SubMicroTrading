/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.java.FieldOffsetDict;
import com.rr.core.java.FieldOffsetDictCache;
import com.rr.core.java.JavaSpecific;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableType;
import com.rr.core.lang.ZString;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.model.BaseEvent;
import com.rr.core.model.Event;
import com.rr.core.utils.SMTRuntimeException;

import java.util.Collection;
import java.util.Iterator;

/**
 * Queue optimised for use with Message, only at most ever one consumer from the queue
 * Multiple possible producers on different threads entering Messages to the queue
 *
 * @NOTE Message a message must only ever be put on one queue at a time .. this is NOT checked
 * @NOTE optimisations made based on single thread consuming from queue
 * <p>
 * Only a subset of the queue methods are implemented
 */
public final class ConcLinkedEventQueueSingle implements EventQueue {

    private static final Logger _log = ConsoleFactory.console( ConcLinkedEventQueueSingle.class, Level.WARN );

    private static final ErrorCode UNABLE_UPDATE_PREV_TAIL = new ErrorCode( "CLM100", "ConcLinkedMsgQueueSingle.add() " +
                                                                                      "Unable to link new tail to  prev node" );

    private static final ErrorCode UNABLE_RESTORE_HEAD = new ErrorCode( "CLM200", "ConcLinkedMsgQueueSingle.poll() " +
                                                                                  "Unable to restore head link to prev value" );

    static final class EventHead extends BaseEvent {

        @Override public void dump( ReusableString out ) { /* nothing */ }

        @Override public ReusableType getReusableType() {
            return CollectionTypes.ConcurrentLinkedQueueHead;
        }
    }
    /**
     * Pointer to header node, initialized to a dummy node.  The first actual node is at head.getNext().
     * The header node itself never changes
     */
    private final EventHead _head = new EventHead();
    private final long _tailOffset = getTailOffset();
    private final String _id;
    private volatile boolean _loggedAnError = false;
    /**
     * Pointer to last node on list
     **/
    private transient volatile Event _tail = _head;
    private FieldOffsetDict _nextMessageOffset = FieldOffsetDictCache.getFieldOffsetDict( Event.class, "_nextMessage" );

    public ConcLinkedEventQueueSingle() {
        this( null );
    }

    public ConcLinkedEventQueueSingle( String id ) {
        _id = id;
    }

    @Override public String getComponentId() {
        return _id;
    }

    /**
     * @return
     * @WARNING non thread safe iterator
     */
    @Override public Iterator<Event> iterator() { return new EventQueueIterator( _head.getNextQueueEntry() ); }

    /**
     * only ONE thread is allowed to CONSUME !
     */

    @Override public Event poll() {
        Event h;
        Event t;
        Event tmp;

        for ( ; ; ) {
            h = _head; // head element itself never changes
            t = _tail;

            if ( t == h ) {
                return null; // nothing in queue
            }

            final Event first = h.getNextQueueEntry();

            if ( t == first ) {
                // only one entry in list, move tail to head then return first

                tmp = t.getNextQueueEntry();

                if ( tmp == null ) {
                    if ( casSetNextEntry( _head, first, null ) ) {
                        if ( casTail( t, h ) ) {
                            first.detachQueue();
                            return first;
                        }
                        // problem now we NULL'd the head next but since then, a new node was added .. restore and cont
                        if ( casSetNextEntry( _head, null, first ) == false ) {
                            // should never happen
                            logError( UNABLE_RESTORE_HEAD );
                        }
                    }
                }
            } else if ( first != null ) {
                tmp = first.getNextQueueEntry();
                // tmp is the third node, if tmp is null then tail should equal the second node, if not a node is midway thru being added
                // in which case circle round and wait for add to complete
                if ( tmp != null ) {
                    if ( casSetNextEntry( _head, first, tmp ) ) {
                        // ok dumy head now points to the next AFTER first ... first is now detached from queue
                        first.detachQueue();
                        return first;
                    }
                }
            }
        }
    }

    @Override public Event next() {
        Event m;

        do {
            m = poll();
        } while( m == null );

        return m;
    }

    /**
     * Inserts the specified element at the tail of this queue.
     *
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     * @throws NullPointerException if the specified element is null
     */
    @Override public boolean add( Event entry ) {
        if ( entry == null ) return true;

        if ( entry.getNextQueueEntry() != null ) throw new SMTRuntimeException( getComponentId() + " ConcLinkedMsgQueueSingle elements can only belong to one queue at a time" );

        for ( ; ; ) {
            Event       tail       = _tail;
            final Event nextToTail = tail.getNextQueueEntry();
            if ( tail == _tail ) {
                if ( nextToTail == null ) {
                    // move tail pointer to new entry
                    if ( casTail( tail, entry ) ) {
                        // relink previous node next ptr to new tail
                        if ( casSetNextEntry( tail, null, entry ) == false ) {
                            // shouldnt be possible
                            logError( UNABLE_UPDATE_PREV_TAIL );
                        }
                        return true;
                    }
                } else {
                    // another thread has entered something to tail of queue, spin waiting for tail ptr to be updated
                }
            }
        }
    }

    /**
     * Returns <tt>true</tt> if this queue contains no elements.
     *
     * @return <tt>true</tt> if this queue contains no elements
     */
    @Override public boolean isEmpty() {
        return _head.getNextQueueEntry() == null;
    }

    @Override public boolean canMessageAttachMultipleQueues() { return false; }

    /**
     * Returns the number of elements in this queue.  If this queue
     * contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * <p>Beware that, unlike in most collections, this method is
     * <em>NOT</em> a constant-time operation. Because of the
     * asynchronous nature of these queues, determining the current
     * number of elements requires an O(n) traversal.
     *
     * @return the number of elements in this queue
     */
    @Override public int size() {

        int count = 0;

        for ( Event p = _head.getNextQueueEntry(); p != null; p = p.getNextQueueEntry() ) {
            // Collections.size() spec says to max out
            if ( ++count == Integer.MAX_VALUE )
                break;
        }

        return count;
    }

    @Override public void clear() {
        while( poll() != null ) {
            // nada
        }
    }

    @Override public EventQueue newInstance() {
        return new ConcLinkedEventQueueSingle( _id );
    }

    Event first() {
        return _head.getNextQueueEntry();
    }

    private boolean casSetNextEntry( Event entry, Event cmp, Event val ) {
        long offset = _nextMessageOffset.getOffset( entry, true );
        return JavaSpecific.instance().compareAndSwapObject( entry, offset, cmp, val );
    }

    private boolean casTail( Event cmp, Event val ) {
        return JavaSpecific.instance().compareAndSwapObject( this, _tailOffset, cmp, val );
    }

    private long getTailOffset() {
        long offset = JavaSpecific.instance().getOffset( ConcLinkedEventQueueSingle.class, "_tail", true );

        if ( offset < 0 ) {
            throw new RuntimeException( "ConcLinkedMsgQueueSingle cant find offset of field _tail" );
        }

        return offset;
    }

    private void logError( ErrorCode code ) {
        if ( _loggedAnError == false ) {       // avoid spinning filling disk with errors
            _log.error( code, (ZString) null );
            _loggedAnError = true;
        }
    }
}
