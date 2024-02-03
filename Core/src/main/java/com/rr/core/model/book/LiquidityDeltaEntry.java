/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

/**
 * maintain a book entry for a price with
 * <p>
 * current on book qty
 * added liquidity since last delta
 * removed liquidity since last delta (excludes traded volume)
 * traded volume since last delta
 * number of trades since last delta
 */
public interface LiquidityDeltaEntry {

    void clear();

    double getAddedLiquidity();

    /**
     * mutators
     */

    void setAddedLiquidity( double qty );

    int getNumTrades();

    void setNumTrades( int trades );

    double getPrevQty();

    void setPrevQty( double prevQty );

    double getPrice();

    void setPrice( double price );

    double getQty();

    void setQty( double latestQty );

    double getRemovedLiquidity();

    void setRemovedLiquidity( double qty );

    double getTradedQty();

    void setTradedQty( double qty );

    /**
     * @return true if entry has a price and at least one of qty/added/removed/traded non zero
     */
    boolean isActive();

    void resetLiquidityCounters();

    void resetTradeCounters();

    void set( double latestQty, double price );

    /**
     * set the current liquidity delta entry to be the same as "that"
     *
     * @param that
     */
    void setLiquidityEntry( LiquidityDeltaEntry that );
}
