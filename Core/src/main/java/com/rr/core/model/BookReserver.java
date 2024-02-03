/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

/**
 * allows tracking and reservation of outstanding orders aligned against book bid/ask qty
 * <p>
 * provides envelope to allow dynamic runtime implementation change based on number of thread using reserver
 *
 * @author Richard Rose
 */
public interface BookReserver {

    void attachReserveWorkerThread( Thread t );

    void completed( double orderQty );

    int getAttachedWorkerThreads();

    int getReserved();

    /**
     * The timestamp is used to reset grab incase of any weird edge cases which fail to reset the current reserver
     * By default the reserve is considered stale after minResetDelayNanos;
     *
     * @param requestedQty - qty want to reserve
     * @param timeNanos    - timestamp of tick update
     * @return quantity successfully reserved or zero if not
     */
    int grabQty( int requestedQty, int currentQtyFromBook, long timeNanos );

    void reset();

    void setMinResetDelayNANOS( long nanos );
}
