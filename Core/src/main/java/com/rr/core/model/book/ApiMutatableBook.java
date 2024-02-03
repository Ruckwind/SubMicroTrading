/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

/**
 * Book can be threadsafe or non threadsafe
 * <p>
 * lvl is the book level index starting at zero
 *
 * @author Richard Rose
 */
public interface ApiMutatableBook extends MutableBook {

    void deleteAsk( int lvl );

    void deleteBid( int lvl );

    /**
     * delete bid and ask from specified level
     *
     * @param lvl
     */
    void deleteFrom( int lvl );

    /**
     * fix 5.0.sp2 .. delete ASK entries from TOP down to specified lvl
     */
    void deleteFromAsk( int lvl );

    /**
     * fix 5.0.sp2 .. delete BID entries from TOP down to specified lvl
     */
    void deleteFromBid( int lvl );

    /**
     * fix 5.0.sp2 .. delete all ASK entries from lvl to MAX
     *
     * @param lvl
     */
    void deleteThruAsk( int lvl );

    /**
     * fix 5.0.sp2 .. delete all BID entries from lvl to MAX
     */
    void deleteThruBid( int lvl );

    void insertAsk( int lvl, BookLevelEntry entry );

    void insertBid( int lvl, BookLevelEntry entry );

    void setAsk( int lvl, BookLevelEntry entry );

    void setAskDirty( boolean isDirty );

    void setAskNumOrders( int lvl, int numOrders );

    void setAskPrice( int lvl, double px );

    void setAskQty( int lvl, double qty );

    void setBid( int lvl, BookLevelEntry entry );

    void setBidDirty( boolean isDirty );

    void setBidNumOrders( int lvl, int numOrders );

    void setBidPrice( int lvl, double px );

    void setBidQty( int lvl, double qty );

    /**
     * set both levels in the book
     *
     * @param lvl
     */
    void setLevel( int lvl, int bidNumOrders, double bidQty, double bidPrice, boolean bidIsDirty, int askNumOrders, double askQty, double askPrice, boolean askIsDirty );

    void setLevel( int lvl, double bidQty, double bidPrice, boolean bidIsDirty, double askQty, double askPrice, boolean askIsDirty );

    void setLevel( int lvl, BookEntryImpl bid, BookEntryImpl ask );

    void setLevel( int idx, DoubleSidedBookEntryImpl ds );

    void setMaxLevels( int numLevels );

    void setNumLevels( int lvl );
}
