/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.book.l3;

import com.rr.core.factories.Factory;
import com.rr.core.lang.ZString;
import com.rr.core.model.*;
import com.rr.core.utils.SMTRuntimeException;

// @TODO refactor out the hardcoded LSE ref

public class L3BookFactoryLSE implements Factory<ZString, Book> {

    private static final int DEFAULT_PRESIZE_ORDERS = 100;
    private static final ThreadLocal<BookPoolMgr<OrderBookEntry>> _poolMgrLocal = ThreadLocal.withInitial( BookPoolMgr::new );
    private static InstrumentLocator _instrumentLocator;
    private final int _presizeOrders; // @TODO persist orders per symbol basis

    public static void setInstrumentLocator( InstrumentLocator locator ) {
        _instrumentLocator = locator;
    }

    public L3BookFactoryLSE() {
        this( DEFAULT_PRESIZE_ORDERS );
    }

    public L3BookFactoryLSE( int presizeOrders ) {
        _presizeOrders = presizeOrders;
    }

    @Override public Book create( ZString key ) {
        ExchangeInstrument inst = _instrumentLocator.getExchInst( key, SecurityIDSource.ExchangeSymbol, ExchangeCode.XLON );

        throw new SMTRuntimeException( "DEPRECATED - REGEN THE ITCH L3Book from PITCH" );

//        return new FullL3OrderBook( inst, _presizeOrders, _poolMgrLocal.get() );
    }
}
