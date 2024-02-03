/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.model.Event;

import java.util.Iterator;

/**
 * NON THREADSAFE DOUBLE LINKED MESSAGE QUEUE
 */
public interface DoubleLinkedEventQueue extends EventQueue {

    void addFirst( Event msg );

    Iterator<Event> descendingIterator();

    DoubleLinkedNode getHead();

    DoubleLinkedNode getTail();

    void setTail( DoubleLinkedNode tail );

    /**
     * @return the last entry in the queue or null if none, doesnt REMOVE from q
     */
    Event last();

    /**
     * moves all elements in this queue to the end of the destination queue leaving this queue empty
     *
     * @param dest
     */
    void moveToEndOfDestQueue( final DoubleLinkedEventQueue dest );

    Event removeLast();

    void setSize( int newSize );
}
