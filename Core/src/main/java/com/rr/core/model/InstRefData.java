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

public interface InstRefData<T extends Instrument> extends Identifiable, Event, HasInstrument<T> {

    @Override default String id() { return getInstrument() == null ? null : getInstrument().id(); }

    /**
     * @return the sequence number which is specifically for the instrument ref data in question .. eg the book per tick id at exchange or UNSET_LONG if not available
     */
    long getDataSeqNum();

    ;

    default DataSrc getDataSrc()  { return DataSrc.UNS; }

    /**
     * @param instrument
     * @TODO move this to InstRefDataWrite ... requires generator change
     */
    void setInstrument( T instrument );
}
