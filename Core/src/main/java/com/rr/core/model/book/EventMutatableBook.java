/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

import com.rr.core.model.Event;

/**
 * EventMutableBook is a book designed to only be updated via events
 */
public interface EventMutatableBook extends MutableBook {

    /**
     * apply the event to the order book in an atomic manner
     *
     * @param event
     * @return true if the event changed the book
     */
    boolean apply( Event event );
}
