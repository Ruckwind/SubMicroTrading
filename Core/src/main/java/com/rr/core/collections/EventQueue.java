/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.component.SMTComponent;
import com.rr.core.lang.CopyConstructor;
import com.rr.core.model.Event;
import com.rr.core.utils.SMTRuntimeException;

import java.util.concurrent.TimeUnit;

/**
 * queue implementation as specifically required to pass Messages from multiple producers
 * to single consumer.
 * <p>
 * NEWEST EVENT AT TAIL OF QUEUE
 *
 * @author Richard Rose
 * @NOTE MUST BE THREADSAFE (except iterator which is not!)
 */
public interface EventQueue extends SMTComponent, CopyConstructor<EventQueue>, Iterable<Event> {

    /**
     * @return a new EMPTY instance of the same type with same queue bound, does NOT duplicate any data in the queue
     */
    @Override EventQueue newInstance();

    /**
     * @return true if successful as requiried by Queue interface ... adds to tail of queue
     */
    boolean add( Event e );

    ;

    /**
     * @return false if message can only be attached to single queue
     */
    boolean canMessageAttachMultipleQueues();

    /**
     * clear any messages in the q
     */
    void clear();

    /**
     * system is trying to stop, clear queue if possible
     * <p>
     * default is do nothing as must be thread safe
     */
    default void forceStop() { }

    /**
     * @return true if the queue is empty
     */
    boolean isEmpty();

    /**
     * @return the maximum capacity for the queue or Integer.MAX_VALUE if  not fixed size
     */
    default int maxCapacity() {
        return Integer.MAX_VALUE;
    }

    /**
     * @return next entry in queue, block if queue empty .. MAY SPIN BLOCK SO BE CAREFUL
     */
    Event next();

    /**
     * @return either single Message OR the chain of all entries in queue in order of insertion, or null if queue empty
     */
    Event poll();

    /* poll and wait upto timeunit */
    public default Event poll( long timeout, TimeUnit unit ) {
        throw new SMTRuntimeException( "poll with timeout NYI for " + this.getClass().getSimpleName() );
    }

    /**
     * @return size of the Q ... iterates over q counting elems
     */
    int size();

    ;
}
