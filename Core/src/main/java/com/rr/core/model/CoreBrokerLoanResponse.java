/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.ViewString;

/**
 * Reference Data Interface - represent data related to single instrument
 *
 * @NOTE book is used in maps, so the equals and hashCode methods are java default
 * do NOT implement to compare prices in two books .... create new compare methods for that
 * <p>
 * Changed lock from simple CAS to StampedLock for optimistic locking on concurrent snapshotting
 */

public interface CoreBrokerLoanResponse extends InstRefData<Instrument> {

    double getAmount();

    double getApproveQty();

    PartyID getBroker();

    boolean getIsDisabled();

    ViewString getReference();
}
