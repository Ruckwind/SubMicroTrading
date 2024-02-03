/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.thread;

/**
 * NonBlockingWorkerMultiplexor final class allowing BaseNonBlockingWorkerMultiplexor methods to be finalised by compiler
 */

public final class NonBlockingWorkerMultiplexor extends BaseNonBlockingWorkerMultiplexor {

    public NonBlockingWorkerMultiplexor( String id, ControlThread ctl ) {
        super( id, ctl );
    }
}
