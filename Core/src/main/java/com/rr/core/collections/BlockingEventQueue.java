/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

/**
 * a queue that blocks when needed, users should sync on the mutex and understand calls will block
 *
 * @author Richard Rose
 */
public interface BlockingEventQueue extends EventQueue {

    Object getMutex();
}
