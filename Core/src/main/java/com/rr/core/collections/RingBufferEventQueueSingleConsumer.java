/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.model.Event;
import sun.misc.Contended;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

/**
 * message queue consumer, supports multi producer single consumer
 *
 * @TODO replace with latest Disruptor
 */
public final class RingBufferEventQueueSingleConsumer implements EventQueue {

    private static final long NOT_SET = -1;

    private final               Event[]    _ring;
    private final               int        _arrayMask;
    private final               int        _size;
    private final               String     _id;
    private @Contended volatile long       _lastConsumed          = NOT_SET;
    private @Contended volatile long       _lastFullyAdded        = NOT_SET;
    private       long       _availableToConsume    = NOT_SET;
    private final AtomicLong _lastClaimedByProducer = new AtomicLong( NOT_SET );

    public RingBufferEventQueueSingleConsumer( int size ) {
        this( null, size );
    }

    public RingBufferEventQueueSingleConsumer( String id, int size ) {

        _id = id;

        // Find a power of 2 >= initialCapacity
        int capacity = 1;
        while( capacity < size )
            capacity <<= 1;

        _size = capacity;
        _ring = new Event[ capacity ];

        for ( int i = 0; i < capacity; i++ ) {
            _ring[ i ] = null;
        }

        _arrayMask = capacity - 1;
    }

    @Override
    public String getComponentId() {
        return _id;
    }

    @Override public Iterator<Event> iterator() {
        return new Iterator<Event>() {

            private long _next = _lastConsumed + 1;
            private final long _last = _lastFullyAdded;

            @Override public boolean hasNext() {
                return (_next <= _last);
            }

            @Override public Event next() {
                if ( _next > _last ) {
                    return null;
                }

                int idx = getIndex( _next++ );
                return _ring[ idx ];
            }
        };
    }

    @Override public EventQueue newInstance() {
        return new RingBufferEventQueueSingleConsumer( _id, _size );
    }

    @Override
    public boolean add( Event msg ) {
        final long putSeqNum        = waitForNextFreeEntry();
        final int  entry            = getIndex( putSeqNum );
        final long expectedSequence = putSeqNum - 1;
        while( expectedSequence != _lastFullyAdded ) {
            // potential multi producer race, each producer thread will have unique putSeqNum, spin with read barrier, until this thread is next in line
            Thread.yield(); // give the other thread chance to go
        }
        _ring[ entry ] = msg;
                         _lastFullyAdded = putSeqNum; // write barrier
        return true;
    }

    @Override public boolean canMessageAttachMultipleQueues() { return true; }

    @Override
    public void clear() {
        _lastConsumed       = NOT_SET;
        _availableToConsume = NOT_SET;
        _lastClaimedByProducer.set( NOT_SET );
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override public int maxCapacity() {
        return _size;
    }

    @Override
    public Event next() {
        Event m;

        do {
            m = poll();
        } while( m == null );

        return m;
    }

    @Override
    public Event poll() {
        final long lastConsumed = _lastConsumed;

        if ( lastConsumed < _availableToConsume ) {  // in batch consume from previous call

            final int   idx = getIndex( ++_lastConsumed );
            final Event m   = _ring[ idx ]; // grab value before declare it free
            _ring[ idx ] = null;
            return m;
        }

        final long lastFullyAdded = _lastFullyAdded;        // read barrier
        if ( lastConsumed == lastFullyAdded ) {
            return null;
        }

        _availableToConsume = lastFullyAdded;
        final int   idx = getIndex( ++_lastConsumed );
        final Event m   = _ring[ idx ]; // grab value before declare it free
        _ring[ idx ] = null;

        return m;
    }

    @Override
    public int size() {
        return (int) (_lastClaimedByProducer.longValue() - _lastConsumed);
    }

    private int getIndex( final long sequence ) {
        return (int) sequence & _arrayMask;
    }

    private long waitForNextFreeEntry() {
        final long sequenceForPut = _lastClaimedByProducer.incrementAndGet();
        final long wrapPoint      = sequenceForPut - _size;

        // note the unsafeProducerLastSeenConsumed means safe a mem read barrier when there was space in the buffer
        // when the wrap point is greater then the spin will occur
        while( wrapPoint > _lastConsumed ) {
            // spin
        }

        return sequenceForPut;
    }
}
