/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import java.io.IOException;
import java.util.Set;

public interface MktDataSubscriptions {

    /**
     * register a listener invoked on first subscription to an instrument and when no subscribers remain for that instrument
     *
     * @param listener
     */
    void addSubscriptionListener( InstrumentSubscriptionListener listener );

    /**
     * bulk subscribe from instruments specified in file
     *
     * @param instrumentStore
     * @param subscriptionFile
     * @throws IOException
     */
    void addSubscriptions( InstrumentLocator instrumentStore, String subscriptionFile, long fromTimestamp ) throws IOException;

    /**
     * get the set of subscribed instruments
     *
     * @param dest
     * @WARNING will generate temporary objects
     */
    void getSubscribedInstruments( Set<Instrument> dest );
}
