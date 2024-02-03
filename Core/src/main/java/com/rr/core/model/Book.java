/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.ReusableString;
import com.rr.core.model.book.ApiMutatableBook;
import com.rr.core.model.book.BookLevelEntry;
import com.rr.core.model.book.DoubleSidedBookEntry;
import com.rr.core.utils.lock.OptimisticReadWriteLock;

/**
 * Book interface - a book with levels (1/2/3)
 *
 * @NOTE book is used in maps, so the equals and hashCode methods are java default
 * do NOT implement to compare prices in two books .... create new compare methods for that
 */
public interface Book extends MktDataWithContext<ApiMutatableBook, BookContext> {

    enum Level {L1, L2, L3}

    @Override Instrument getInstrument();

    /**
     * @return the lock which will be either proxy to StampedLock or a Dummy lock depending on wether book has safe or unsafe concurrency threading requirements
     */
    @Override OptimisticReadWriteLock getLock();

    /**
     * @return event id causing last update
     */
    @Override int getMsgSeqNum();

    @Override void dump( ReusableString dest );

    /**
     * @return reference price for book, currently midPx, may change to lastTradedPx
     */
    @Override double getRefPrice();

    @Override String id();

    /**
     * @return true if bid and ask populated, and prices not crossed, ie ask > bid
     */
    @Override boolean isValid();

    /**
     * mark book as potentially out of date
     */
    @Override void setDirty( boolean isDirty );

    /**
     * write copy of book to destination upto max levels as supported in the destination book
     * <p>
     * if source book has 20 levels but dest book only supports 5 then only top 5 copied
     * <p>
     * if book is threadsafe may spinlock against mutating thread
     * <p>
     * only copies the book, lastTickId and lastTickInNanos
     *
     * @param dest
     */
    @Override void snapTo( ApiMutatableBook dest );

    int getActiveLevels();

    /**
     * get the requested ask entry
     *
     * @param lvl  (0+)
     * @param dest
     * @return true if level supported by book, false if not
     */
    boolean getAskEntry( int lvl, BookLevelEntry dest );

    /**
     * get the requested bid entry
     *
     * @param lvl  (0+)
     * @param dest
     * @return true if level supported by book, false if not
     */
    boolean getBidEntry( int lvl, BookLevelEntry dest );

    Level getLevel();

    /**
     * get both entries for a particular level
     *
     * @param lvl  (0+)
     * @param dest
     * @return false if requested level not available
     */
    boolean getLevel( int lvl, DoubleSidedBookEntry dest );

    /**
     * @return maxiumum number of levels in book
     */
    int getMaxLevels();

    /**
     * @return number of tick updates (snapshot counts as 1 only)
     */
    long getTickCount();
}
