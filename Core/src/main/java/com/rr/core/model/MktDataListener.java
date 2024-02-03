/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.ZString;

public interface MktDataListener<T extends SnapableMktData> extends Identifiable {

    /**
     * clear and state maintained by the MktDataListener
     */
    void clearMktData();

    /**
     * notification on MD thread that market data has changed ... shoudl return ASAP
     *
     * @param mktData a TEMP instance of the book, must copy data from this in the routine and DO NOT hold reference as it could be recycled
     */
    void marketDataChanged( T mktData );

    /**
     * receive an inst related data event relevant to the listener
     * <p>
     * use subject to understand what is being sent
     * use instanceof and cast on data to decode
     * use timestamp and seqnum for any needed sequencing
     */
    default void receive( ZString subject, Instrument inst, Object data, long timestamp, long seqNum ) { /* nothing */ }

    /**
     * used to listen to mkt data
     *
     * @param marketData
     */
    default void snapableMktDataChanged( SnapableMktData<?> marketData ) { /* nothing */ }
}
