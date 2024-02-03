/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.book.l3;

import com.rr.core.collections.LongHashEntry;
import com.rr.core.collections.LongHashEntryFactory;
import com.rr.core.collections.SMTHashMap;
import com.rr.core.lang.Reusable;
import com.rr.core.lang.ZString;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.SuperpoolManager;
import com.rr.model.generated.internal.events.impl.BookAddOrderImpl;
import com.rr.model.generated.internal.events.impl.BookDeleteOrderImpl;
import com.rr.model.generated.internal.events.impl.BookModifyOrderImpl;

public final class BookPoolMgr<T extends Reusable<T>> {

    private final PoolFactory<OrderBookEntry>     _orderBookEntryFactory;
    private final PoolFactory<FullBookLevelEntry> _bookLevelEntryFactory;

    private final Recycler<OrderBookEntry>      _orderBookEntryRecycler;
    private final Recycler<FullBookLevelEntry>  _bookLevelRecycler;
    private final Recycler<BookAddOrderImpl>    _orderBookAddRecycler;
    private final Recycler<BookModifyOrderImpl> _orderBookModifyRecycler;
    private final Recycler<BookDeleteOrderImpl> _orderBookDeleteRecycler;

    private final LongHashEntryFactory<T>    _entryLongFactory;
    private final Recycler<LongHashEntry<T>> _entryLongRecycler;

    private final SMTHashMap.HashEntryFactory<ZString, T>    _entryFactory;
    private final Recycler<SMTHashMap.HashEntry<ZString, T>> _entryRecycler;

    @SuppressWarnings( "unchecked" )
    public BookPoolMgr() {
        Recycler<?> entryRecycler;

        _orderBookEntryFactory  = SuperpoolManager.instance().getPoolFactory( OrderBookEntry.class );
        _orderBookEntryRecycler = SuperpoolManager.instance().getRecycler( OrderBookEntry.class );

        _bookLevelEntryFactory = SuperpoolManager.instance().getPoolFactory( FullBookLevelEntry.class );
        _bookLevelRecycler     = SuperpoolManager.instance().getRecycler( FullBookLevelEntry.class );

        _entryLongFactory  = SuperpoolManager.instance().getFactory( LongHashEntryFactory.class, LongHashEntry.class );
        entryRecycler      = SuperpoolManager.instance().getRecycler( LongHashEntry.class );
        _entryLongRecycler = (Recycler<LongHashEntry<T>>) entryRecycler;

        _entryFactory  = SuperpoolManager.instance().getFactory( SMTHashMap.HashEntryFactory.class, SMTHashMap.HashEntry.class );
        entryRecycler  = SuperpoolManager.instance().getRecycler( SMTHashMap.HashEntry.class );
        _entryRecycler = (Recycler<SMTHashMap.HashEntry<ZString, T>>) entryRecycler;

        _orderBookAddRecycler    = SuperpoolManager.instance().getRecycler( BookAddOrderImpl.class );
        _orderBookModifyRecycler = SuperpoolManager.instance().getRecycler( BookModifyOrderImpl.class );
        _orderBookDeleteRecycler = SuperpoolManager.instance().getRecycler( BookDeleteOrderImpl.class );
    }

    public PoolFactory<FullBookLevelEntry> getBookLevelEntryFactory() {
        return _bookLevelEntryFactory;
    }

    public Recycler<FullBookLevelEntry> getBookLevelRecycler() {
        return _bookLevelRecycler;
    }

    public SMTHashMap.HashEntryFactory<ZString, T> getEntryFactory() {
        return _entryFactory;
    }

    public Recycler<SMTHashMap.HashEntry<ZString, T>> getEntryRecycler() {
        return _entryRecycler;
    }

    public LongHashEntryFactory<T> getLongEntryFactory() {
        return _entryLongFactory;
    }

    public Recycler<LongHashEntry<T>> getLongEntryRecycler() {
        return _entryLongRecycler;
    }

    public PoolFactory<OrderBookEntry> getOrderBookEntryFactory() {
        return _orderBookEntryFactory;
    }

    public Recycler<OrderBookEntry> getOrderBookEntryRecycler() {
        return _orderBookEntryRecycler;
    }

    public void recycle( BookDeleteOrderImpl event ) {
        _orderBookDeleteRecycler.recycle( event );
    }

    public void recycle( BookModifyOrderImpl event ) {
        _orderBookModifyRecycler.recycle( event );
    }

    public void recycle( BookAddOrderImpl event ) {
        _orderBookAddRecycler.recycle( event );
    }
}
