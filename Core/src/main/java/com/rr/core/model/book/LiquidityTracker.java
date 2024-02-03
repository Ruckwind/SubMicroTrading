package com.rr.core.model.book;

import com.rr.core.lang.ReusableString;

/**
 * track changes to liquidity upto max levels
 * <p>
 * if side is bidSide then L0 is the best bid price
 * if side is askSide then L0 is the best ask price
 * <p>
 * note at each level the current quantity can be zero indicating price level not in current L2 book at time the tracker was snapped
 */
public interface LiquidityTracker {

    /**
     * iterate from top down counting number of active levels ... should be same as get
     */
    int countActive();

    void dump( ReusableString dest );

    /**
     * @return
     */
    int getActiveLevels(); // number of active levels .. should be same as countActive

    void setActiveLevels( int numActive );

    /**
     * @param level
     * @param entry
     */
    void getEntry( int level, LiquidityDeltaEntry entry );

    int getMaxLevels();

    /**
     * change the number of levels in tracker
     *
     * @param numLevels
     */
    void setMaxLevels( int numLevels );

    /**
     * @param lvl
     * @return price at specified level or null if none or lvl out of bounds
     */
    double getPrice( int lvl );

    double getQty( int lvl );

    /**
     * @param lvl
     * @return true if price at specified level is not null, false otherwise, also false if lvl is out of bounds
     */
    boolean hasPrice( int lvl );

    /**
     * @return if side of this LiquidityTracker is bid
     */
    boolean isBidSide();

    /**
     * @param startIdx   - index to start looking for price entry from ... should be zero for first call, use returned value for applying batch of changes
     * @param currentQty - the latest / current qty for price
     * @param price      - the price thats changing
     * @return index of price entry or Constants.UNSET_INT if out of max level
     */
    int liquidityChange( int startIdx, double currentQty, double price );

    void removeInactiveLevels();

    void removeLvlsWithNoAction();

    /**
     * clear out all data including current price / qty
     */
    void reset();

    void reset( int lvl );

    /**
     * set the added/removed liquidity to zero AND remove any levels with zero qty
     */
    void resetLiquidityCounters();

    void resetTradeCounters();

    /**
     * set the liquidity delta entry currently should only be used by snapTo
     *
     * @param lvl
     * @param entry
     */
    void set( int lvl, LiquidityDeltaEntry entry );

    void snapTo( LiquidityTracker dest );

    /**
     * volume traded ie agressor crossed spread and hit this price
     *
     * @param price
     * @param qty
     */
    void traded( double qty, double price );

    boolean verifyEndOfBlock();

    boolean verifyPostOp();
}
