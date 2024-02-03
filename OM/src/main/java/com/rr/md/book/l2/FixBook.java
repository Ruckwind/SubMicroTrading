/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.book.l2;

import com.rr.core.model.Book;
import com.rr.core.recycler.EventRecycler;
import com.rr.model.generated.internal.events.interfaces.MDEntry;
import com.rr.model.generated.internal.events.interfaces.MDSnapshotFullRefresh;

/**
 * In FastFix a single event may be applicable to multiple books
 * <p>
 * This means the event processing needs to be pushed up a level
 *
 * @author Richard Rose
 */
public interface FixBook extends Book {

    @Override void reset();

    /**
     * apply SINGLE incremental update
     *
     * @param entry
     * @return true if book updated or trade occurred
     */
    boolean applyIncrementalEntry( int eventSeqNum, MDEntry entry );

    /**
     * Apply the snapshot event
     * Replay any enqueued incremental updates checking the bookSeqNum (T83)
     *
     * @param msg
     * @return true if event applied and book ok
     */
    boolean applySnapshot( MDSnapshotFullRefresh msg, EventRecycler entryRecycler );

    double getLastTradePrice();

    int getLastTradeQty();

    int getTotalTradeVol();

    double getTotalTraded();
}
