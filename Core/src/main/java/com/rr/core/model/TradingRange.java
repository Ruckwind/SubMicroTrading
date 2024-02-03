/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.ReusableString;

/**
 * the PT server when it detects a PT movement should order messages to ensure that the band is as wide as possible
 * eg if current is 10 - 12, and new is 11 - 13, then first event should be 13 so 10 - 13, then 11 so 11 - 13
 * its unlikely in aggressive trading this extra band will cause exchange PT failure ...
 * this is consistent anyway with conflation and reuters even wombat feed
 */

public interface TradingRange {

    /**
     * set the maximum value to sell
     *
     * @param tickId
     * @param upper
     */
    void setMaxBuy( long tickId, double upper, int flags );

    /**
     * it is invokers responsibility to verify the prices, this routine doesnt check the values
     *
     * @param lower
     * @param flags bit flags indicating how threshold was calculated
     */
    void setMinSell( long tickId, double lower, int flags );

    /**
     * validate the price must be threadsafe against the PT update thread
     *
     * @param price price of the order
     * @param err   the buffer to APPEND a failed validation message
     * @return
     */
    boolean valid( double price, boolean isBuySide, ReusableString err );
}
