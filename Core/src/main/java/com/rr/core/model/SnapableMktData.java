/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.Snapable;

/**
 * Market Data Interface - represent data related to single instrument that can be snapped
 * <p>
 * Used where the source item can change async, snap gives an atomic safe capture
 *
 * @NOTE book is used in maps, so the equals and hashCode methods are java default
 * do NOT implement to compare prices in two books .... create new compare methods for that
 * <p>
 * Changed lock from simple CAS to StampedLock for optimistic locking on concurrent snapshotting
 */

public interface SnapableMktData<T extends Snapable> extends InstRefData<Instrument>, Snapable<T>, Event {

    boolean isValid();

    /**
     * mark item as potentially out of date
     */
    void setDirty( boolean isDirty );
}
