/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

/**
 * Reference Data Interface - represent data related to single instrument
 *
 * @NOTE book is used in maps, so the equals and hashCode methods are java default
 * do NOT implement to compare prices in two books .... create new compare methods for that
 * <p>
 * Changed lock from simple CAS to StampedLock for optimistic locking on concurrent snapshotting
 */

public interface InstRefDataWrite<T extends Instrument> extends InstRefData<T> {

    void setMsgSeqNum( int seqNum );

    void setEventTimestamp( long timeStamp );

    void reset();

    void setDataSeqNum( long dataSeqNum );
}
