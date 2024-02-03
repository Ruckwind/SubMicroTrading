/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.model.book.ApiMutatableBook;
import com.rr.core.model.book.LiquidityDeltaEntry;
import com.rr.core.model.book.LiquidityTracker;

/**
 * LiquidityBook a Lbook with bid and ask level trackers
 *
 * @NOTE book is used in maps, so the equals and hashCode methods are java default
 * do NOT implement to compare prices in two books .... create new compare methods for that .. or use isBookIdentical
 */
public interface LiquidityBook extends InstRefDataWrite<Instrument>, SnapableMktData<LiquidityBook>, Identifiable {

    /**
     * deltaFromBook applies the latest book to the liquidity book, compares difference to current L2 book generating liq tracker adjustments
     * <p>
     * for use in L2 book updates
     *
     * @param latestBook
     * @param resetCounters
     * @param event
     */
    void deltaFromBook( Book latestBook, boolean resetCounters, final Object event );

    /**
     * @return the maximum of the ask/bid tracker active levels
     */
    int getActiveLiqLevels();

    boolean getAskLiqEntry( int lvl, LiquidityDeltaEntry dest );

    LiquidityTracker getAskTracker();

    boolean getBidLiqEntry( int lvl, LiquidityDeltaEntry dest );

    LiquidityTracker getBidTracker();

    ApiMutatableBook getBook();

    /**
     * setBook applies the latest L3 book to the internal L2 book, does NOT adjust trackes as assumed they are adjusted by each L3 update
     * <p>
     * for use in L3 feeds
     *
     * @param latestBook
     */
    void setBook( Book latestBook );

    long getDeltaDurationMS();

    void setDeltaDurationMS( long deltaDurationMS );

    boolean isBookIdentical( LiquidityBook o );

    boolean isSnapIncludesLiq();

    void postWrite();

    /**
     * remove all levels with zero qty
     */
    void removeInactiveLevels();

    /**
     * remove levels with no action, ie qty=0, added=0, removed=0, traded=0  which happens when no activity occurs at a level after it was previously had resetLiquidityCounters invoked
     */
    void removeLvlsWithNoAction();

    /**
     * reset the counters in the tracker info but leaving all price/qty info
     */
    void resetLiquidityCounters();

    /**
     * clear out all tracker info
     */
    void resetTrackers();

    void resetTradeCounters();

    void set( Instrument inst, int numBookLevels, int numDeltaLevels );

    boolean setSnapWithLiquidity( boolean snapIncludesLiq );

    void setUseExtremeVerify( boolean useExtremeVerify );

    void setWipeInvalidBook( boolean wipeInvalidBook );

    boolean verifyEndOfBlock();

    boolean verifyPostOp();
}
