/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.book;

import com.rr.core.component.SMTInitialisableComponent;
import com.rr.core.model.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * manages subscription of books by subscribers
 * <p>
 * supports dynamic addition and removal of MarketDataSource's
 * <p>
 * the fromTimestamp is ONLY utilised on the FIRST subscription request for an instrument ! zero means NOW
 */
public interface MktDataSubsMgr<T extends SnapableMktData> extends MktDataSubscriptions, MktDataListener<T>, SMTInitialisableComponent, MktDataPublisher<T> {

    /**
     * notify subscribers that book has changed
     */
    @Override void marketDataChanged( T book );

    void addSource( MktDataSrc<T> src );

    void addSubscriptions( InstrumentLocator instrumentStore, Set<? extends Instrument> insts, long fromTimestamp ) throws IOException;

    /**
     * bulk subscription method
     *
     * @param callback      callback top be registered for each instrument in list
     * @param insts         list of instruments to subscribe too
     * @param out           list of the market data items returned for each instrument
     * @param fromTimestamp time to replay market data from, 0 means currentTime
     */
    void bulkSubscribe( MktDataListener<T> callback, List<? extends Instrument> insts, List<T> out, long fromTimestamp );

    MktDataSrc<T> findSource( Instrument inst );

    void removeSource( MktDataSrc<T> src );

    /**
     * register a callback thats invoked when the book changes
     *
     * @param callback
     * @return
     */
    T subscribe( MktDataListener<T> callback, Instrument inst, long fromTimestamp );

    /**
     * remove all subscriptions for callback
     *
     * @param callback
     */
    void unsubscribe( MktDataListener<T> callback, Instrument inst );

    /**
     * remove all subscriptions for callback
     *
     * @param callback
     */
    void unsubscribeAll( MktDataListener<T> callback );
}
