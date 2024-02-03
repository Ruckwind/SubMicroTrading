/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

/**
 * threadsafe BookContext
 *
 * @TODO if need support different trackers / reservers then push DoubleSidedBookReserver down
 */
public interface BookContext extends Context, DoubleSidedBookReserver {
    // tag interface
}
