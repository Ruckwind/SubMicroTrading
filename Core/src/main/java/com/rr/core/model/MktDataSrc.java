/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.component.SMTControllableComponent;
import com.rr.core.factories.FactoryCache;
import com.rr.core.thread.PipeLineable;

/**
 * source for subscription of books
 * <p>
 * pipeLineId is a logical pipe representing market data sessions + book source ... its used for load balancing, either round robin or by specific pipe
 */
public interface MktDataSrc<T extends InstRefData> extends EventHandler, FactoryCache<Instrument, T>, PipeLineable, SMTControllableComponent {

    enum Priority {High, Medium, Low} // order must be left to right as default comparator used on enum

    /**
     * subscribe to requested book
     * <p>
     * use snapTo a local copy for threadsafe atomic change protection
     *
     * @return the actual book instance used by the BookSrc or null ... maybe changing asynchronously
     */
    @Override T getItem( Instrument inst );

    /**
     * This is used by book consumers to force instrument subscription to particular controller via an allocated pipeLineId
     *
     * @param pipeLineId
     * @return true if the BookSrc has the supplied pipeLineId
     */
    @Override boolean hasPipeLineId( String pipeLineId );

    default Priority getPriority() { return Priority.Medium; }

    default void setSubscriptionFile( final String subscriptionFile ) { /* default no file */ }

    /**
     * @param inst
     * @return if the source supports the denoted instrument
     */
    boolean supports( Instrument inst );
}
