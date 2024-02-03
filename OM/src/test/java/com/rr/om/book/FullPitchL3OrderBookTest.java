/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.book;

import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Currency;
import com.rr.core.model.ExchangeCode;
import com.rr.core.model.SecurityIDSource;
import com.rr.core.model.book.DoubleSidedBookEntry;
import com.rr.core.model.book.DoubleSidedBookEntryImpl;
import com.rr.core.model.book.UnsafeL2Book;
import com.rr.md.book.l3.L3BookStats;
import com.rr.md.book.l3.PitchBookPoolMgr;
import com.rr.md.book.l3.PitchL3OrderBook;
import com.rr.model.generated.internal.events.impl.PitchBookAddOrderImpl;
import com.rr.model.generated.internal.events.impl.PitchBookCancelOrderImpl;
import com.rr.model.generated.internal.events.impl.PitchBookOrderExecutedImpl;
import com.rr.model.generated.internal.events.impl.PitchSymbolClearImpl;
import com.rr.model.generated.internal.events.interfaces.PitchBookAddOrderWrite;
import com.rr.model.generated.internal.events.interfaces.PitchBookCancelOrderWrite;
import com.rr.model.generated.internal.events.interfaces.PitchBookOrderExecutedWrite;
import com.rr.model.generated.internal.events.interfaces.PitchSymbolClear;
import com.rr.model.generated.internal.type.Side;
import com.rr.om.BaseOMTestCase;
import com.rr.om.dummy.warmup.DummyInstrumentLocator;
import com.rr.om.model.instrument.InstrumentWrite;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FullPitchL3OrderBookTest extends BaseOMTestCase {

    private static final Logger _log = LoggerFactory.create( FullPitchL3OrderBookTest.class );

    private InstrumentWrite _inst;
    private ReusableString  _logMsg = new ReusableString();

    @Before
    public void setInst() {
        _inst = new DummyInstrumentLocator().getExchInst( new ViewString( "BT.TST" ), SecurityIDSource.ExchangeSymbol, ExchangeCode.UNKNOWN );

        _inst.setCurrency( Currency.EUR );

        L3BookStats.instance().reset();
    }

    @Test
    public void testBuyInsertMiddle() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        applyAddEvent( book, 1050, Side.Buy, 300, 15.25 );
        applyAddEvent( book, 1041, Side.Buy, 500, 15.0 );
        applyAddEvent( book, 1032, Side.Buy, 400, 15.125 );
        applyAddEvent( book, 1023, Side.Buy, 200, 15.5 );
        applyAddEvent( book, 1014, Side.Buy, 100, 15.75 );

        double[][] results = { { 100, 15.75, 0, Constants.UNSET_DOUBLE },
                               { 200, 15.5, 0, Constants.UNSET_DOUBLE },
                               { 300, 15.25, 0, Constants.UNSET_DOUBLE },
                               { 400, 15.125, 0, Constants.UNSET_DOUBLE },
                               { 500, 15.0, 0, Constants.UNSET_DOUBLE } };

        verify( book, results );
    }

    @Test
    public void testDelBestBBO() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        applyAddEvent( book, 1050, Side.Buy, 300, 15.25 );
        applyAddEvent( book, 1041, Side.Buy, 500, 15.0 );
        applyAddEvent( book, 1032, Side.Buy, 400, 15.125 );
        applyAddEvent( book, 1023, Side.Buy, 200, 15.5 );
        applyAddEvent( book, 1014, Side.Buy, 100, 15.75 );
        applyAddEvent( book, 1060, Side.Sell, 300, 15.8 );

        applyDelEvent( book, 1014 );
        applyDelEvent( book, 1060 );

        double[][] results = { { 200, 15.5, 0, Constants.UNSET_DOUBLE },
                               { 300, 15.25, 0, Constants.UNSET_DOUBLE },
                               { 400, 15.125, 0, Constants.UNSET_DOUBLE },
                               { 500, 15.0, 0, Constants.UNSET_DOUBLE } };

        verify( book, results );
    }

    @Test
    public void testDelBestBuy() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        applyAddEvent( book, 1050, Side.Buy, 300, 15.25 );
        applyAddEvent( book, 1041, Side.Buy, 500, 15.0 );
        applyAddEvent( book, 1032, Side.Buy, 400, 15.125 );
        applyAddEvent( book, 1023, Side.Buy, 200, 15.5 );
        applyAddEvent( book, 1014, Side.Buy, 100, 15.75 );

        applyDelEvent( book, 1014 );

        double[][] results = { { 200, 15.5, 0, Constants.UNSET_DOUBLE },
                               { 300, 15.25, 0, Constants.UNSET_DOUBLE },
                               { 400, 15.125, 0, Constants.UNSET_DOUBLE },
                               { 500, 15.0, 0, Constants.UNSET_DOUBLE } };

        verify( book, results );
    }

    @Test
    public void testDelHalfInMiddleSellMoves() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        applyAddEvent( book, 1050, Side.Buy, 300, 15.25 );
        applyAddEvent( book, 1041, Side.Buy, 500, 15.0 );
        applyAddEvent( book, 1032, Side.Buy, 400, 15.125 );
        applyAddEvent( book, 1023, Side.Buy, 200, 15.5 );
        applyAddEvent( book, 1014, Side.Buy, 100, 15.75 );

        applyAddEvent( book, 1060, Side.Sell, 301, 16.8 );
        applyAddEvent( book, 1070, Side.Sell, 302, 15.8 );
        applyAddEvent( book, 1080, Side.Sell, 303, 15.9 );
        applyAddEvent( book, 1090, Side.Sell, 304, 16.0 );
        applyAddEvent( book, 1095, Side.Sell, 305, 16.2 );

        applyDelEvent( book, 1014 );

        double[][] results = { { 200, 15.5, 302, 15.8 },
                               { 300, 15.25, 303, 15.9 },
                               { 400, 15.125, 304, 16.0 },
                               { 500, 15.0, 305, 16.2 },
                               { 0, Constants.UNSET_DOUBLE, 301, 16.8 } };

        verify( book, results );
    }

    @Test
    public void testDelHalfLevelBestSellMoves() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        applyAddEvent( book, 1050, Side.Buy, 300, 15.25 );
        applyAddEvent( book, 1041, Side.Buy, 500, 15.0 );
        applyAddEvent( book, 1032, Side.Buy, 400, 15.125 );
        applyAddEvent( book, 1023, Side.Buy, 200, 15.5 );
        applyAddEvent( book, 1014, Side.Buy, 100, 15.75 );
        applyAddEvent( book, 1060, Side.Sell, 300, 15.8 );

        applyDelEvent( book, 1014 );

        double[][] results = { { 200, 15.5, 300, 15.8 },
                               { 300, 15.25, 0, Constants.UNSET_DOUBLE },
                               { 400, 15.125, 0, Constants.UNSET_DOUBLE },
                               { 500, 15.0, 0, Constants.UNSET_DOUBLE } };

        verify( book, results );
    }

    @Test
    public void testFullFill() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        applyAddEvent( book, 1050, Side.Buy, 300, 15.25 );
        applyAddEvent( book, 1041, Side.Buy, 500, 15.0 );
        applyAddEvent( book, 1032, Side.Buy, 400, 15.125 );
        applyAddEvent( book, 1023, Side.Buy, 200, 15.5 );
        applyAddEvent( book, 1014, Side.Buy, 100, 15.75 );
        applyAddEvent( book, 1060, Side.Sell, 900, 15.8 );

        applyFillEvent( book, 1014, 100 );
        applyFillEvent( book, 1060, 300 );

        double[][] results = { { 200, 15.5, 600, 15.8 },
                               { 300, 15.25, 0, Constants.UNSET_DOUBLE },
                               { 400, 15.125, 0, Constants.UNSET_DOUBLE },
                               { 500, 15.0, 0, Constants.UNSET_DOUBLE } };

        verify( book, results );
    }

    @Test
    public void testFullFillLeavesLine() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        applyAddEvent( book, 1050, Side.Buy, 300, 15.25 );
        applyAddEvent( book, 1041, Side.Buy, 500, 15.0 );
        applyAddEvent( book, 1032, Side.Buy, 400, 15.125 );
        applyAddEvent( book, 1014, Side.Buy, 100, 15.75 );
        applyAddEvent( book, 1070, Side.Buy, 150, 15.75 );
        applyAddEvent( book, 1060, Side.Sell, 900, 15.8 );

        applyFillEvent( book, 1014, 100 );

        double[][] results = { { 150, 15.75, 900, 15.8 },
                               { 300, 15.25, 0, Constants.UNSET_DOUBLE },
                               { 400, 15.125, 0, Constants.UNSET_DOUBLE },
                               { 500, 15.0, 0, Constants.UNSET_DOUBLE } };

        verify( book, results );
    }

    @Test
    public void testInsertDelete() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        applyAddEvent( book, 1000, Side.Buy, 100, 15.25 );
        applyDelEvent( book, 1000 );

        double[][] results = {};

        verify( book, results );
    }

    @Test
    public void testLotsMultiFill() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        int iter = 10000;

        for ( int i = 0; i < iter; i++ ) {
            int offset = i * 10000;

            applyAddEvent( book, 1032 + offset, Side.Buy, 400, 15.125 );
            applyAddEvent( book, 1050 + offset, Side.Buy, 300, 15.25 );
            applyAddEvent( book, 1041 + offset, Side.Buy, 500, 15.0 );
            applyAddEvent( book, 1060 + offset, Side.Sell, 900, 15.8 );
        }

        double[][] results = { { 300 * iter, 15.25, 900 * iter, 15.8 },
                               { 400 * iter, 15.125, 0, Constants.UNSET_DOUBLE },
                               { 500 * iter, 15.0, 0, Constants.UNSET_DOUBLE } };

        verify( book, results );

        assertEquals( 3, book.getActiveLevels() );
        assertEquals( 4 * iter, book.getActiveOrders() );

        log( book );
    }

    @Test
    public void testLotsMultiFillMatched() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        for ( int i = 0; i < 1000; i++ ) {
            int offset = i * 1000;

            applyAddEvent( book, 1032 + offset, Side.Buy, 400, 15.125 );
            applyAddEvent( book, 1050 + offset, Side.Buy, 300, 15.25 );
            applyAddEvent( book, 1041 + offset, Side.Buy, 500, 15.0 );
        }

        for ( int i = 0; i < 1000; i++ ) {
            int offset = i * 1000;

            applyFillEvent( book, 1050 + offset, 300 );
            applyFillEvent( book, 1041 + offset, 500 );
            applyFillEvent( book, 1032 + offset, 400 );
        }

        double[][] results = {};

        verify( book, results );
    }

    @Test
    public void testLotsMultiLevel() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        final int iters = 10000;

        for ( long i = 0; i < iters; i++ ) {
            long   offset   = i * iters;
            double pxOffset = offset * -0.000001;

            applyAddEvent( book, 1032 + offset, Side.Buy, 400, 15.125 + pxOffset );
            applyAddEvent( book, 1050 + offset, Side.Buy, 300, 15.25 + pxOffset );
            applyAddEvent( book, 1041 + offset, Side.Buy, 500, 15.0 + pxOffset );
        }

        log( book, 10 );

        for ( long i = 0; i < iters; i++ ) {
            long offset = i * iters;

            applyFillEvent( book, 1050 + offset, 300 );
            applyFillEvent( book, 1041 + offset, 500 );
            applyFillEvent( book, 1032 + offset, 400 );
        }

        log( book );

        double[][] results = {};

        verify( book, results );
    }

    @Test
    public void testLotsMultiLevelOneSetLeftOpen() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        int    iters   = 1000;
        double pxDelta = -0.000001;

        for ( long i = 0; i < iters; i++ ) {
            long   offset   = i * iters;
            double pxOffset = i * pxDelta;

            applyAddEvent( book, 1032 + offset, Side.Buy, 400, 15.125 + pxOffset );
            applyAddEvent( book, 1050 + offset, Side.Buy, 300, 15.25 + pxOffset );
            applyAddEvent( book, 1041 + offset, Side.Buy, 500, 15.0 + pxOffset );
        }

        for ( long i = 0; i < 999; i++ ) {
            long offset = i * iters;

            applyFillEvent( book, 1050 + offset, 300 );
            applyFillEvent( book, 1041 + offset, 500 );
            applyFillEvent( book, 1032 + offset, 400 );
        }

        log( book );

        final double lastPxOffset = (iters - 1) * pxDelta;

        double[][] results = { { 300, 15.25 + lastPxOffset, 0, Constants.UNSET_DOUBLE },
                               { 400, 15.125 + lastPxOffset, 0, Constants.UNSET_DOUBLE },
                               { 500, 15.0 + lastPxOffset, 0, Constants.UNSET_DOUBLE } };

        verify( book, results );
    }

    @Test
    public void testMisc() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        applyAddEvent( book, 1050, Side.Buy, 300, 15.25 );
        applyAddEvent( book, 1041, Side.Buy, 500, 15.0 );
        applyAddEvent( book, 1032, Side.Buy, 400, 15.125 );
        applyAddEvent( book, 1023, Side.Buy, 200, 15.5 );
        applyAddEvent( book, 1014, Side.Buy, 100, 15.75 );

        applyAddEvent( book, 1060, Side.Sell, 301, 16.8 );
        applyAddEvent( book, 1070, Side.Sell, 302, 15.8 );
        applyAddEvent( book, 1080, Side.Sell, 303, 15.9 );
        applyAddEvent( book, 1090, Side.Sell, 304, 16.0 );
        applyAddEvent( book, 1095, Side.Sell, 305, 16.2 );

        applyDelEvent( book, 1041 );
        applyDelEvent( book, 1080 );

        double[][] results = { { 100, 15.75, 302, 15.8 },
                               { 200, 15.5, 304, 16.0 },
                               { 300, 15.25, 305, 16.2 },
                               { 400, 15.125, 301, 16.8 } };

        verify( book, results );
    }

    @Test
    public void testMultiClear() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        for ( int i = 0; i < 1000; i++ ) {
            int offset = i * 1000;

            applyAddEvent( book, 1032 + offset, Side.Buy, 400, 15.125 );
            applyAddEvent( book, 1050 + offset, Side.Buy, 300, 15.25 );
            applyAddEvent( book, 1041 + offset, Side.Buy, 500, 15.0 );

            applyClearEvent( book );

            applyAddEvent( book, 1032 + offset, Side.Buy, 400, 15.125 );
            applyAddEvent( book, 1050 + offset, Side.Buy, 300, 15.25 );
            applyAddEvent( book, 1041 + offset, Side.Buy, 500, 15.0 );

            applyFillEvent( book, 1050 + offset, 300 );
            applyFillEvent( book, 1041 + offset, 500 );
            applyFillEvent( book, 1032 + offset, 400 );
        }

        assertEquals( 0, book.getActiveLevels() );
        assertEquals( 0, book.getActiveOrders() );

        log( book );

        double[][] results = {};

        verify( book, results );
    }

    @Test
    public void testMultiFillClear() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        for ( int i = 0; i < 1000; i++ ) {
            int offset = i * 1000;

            applyAddEvent( book, 1032 + offset, Side.Buy, 400, 15.125 );
            applyAddEvent( book, 1050 + offset, Side.Buy, 300, 15.25 );
            applyAddEvent( book, 1041 + offset, Side.Buy, 500, 15.0 );

            applyFillEvent( book, 1050 + offset, 300 );
            applyFillEvent( book, 1041 + offset, 500 );
            applyFillEvent( book, 1032 + offset, 400 );
        }

        double[][] results = {};

        verify( book, results );
    }

    @Test
    public void testMultiFillMissingSide() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        applyAddEvent( book, 1050, Side.Sell, 4275, 225.4 );
        applyAddEvent( book, 1041, Side.Buy, 5276, 225.3 );
        applyAddEvent( book, 1032, Side.Sell, 1277, 225.35 );
        applyAddEvent( book, 1014, Side.Buy, 4278, 225.31 );

        log( book );

        double[][] results = { { 4278, 225.31, 1277, 225.35 },
                               { 5276, 225.3, 4275, 225.4 } };

        verify( book, results );

        applyFillEvent( book, 1014, 4278 );

        log( book );

        double[][] results2 = { { 5276, 225.3, 1277, 225.35 },
                                { 0, Constants.UNSET_DOUBLE, 4275, 225.4 } };

        verify( book, results2 );

        applyFillEvent( book, 1032, 1277 );

        log( book );

        double[][] results3 = { { 5276, 225.3, 4275, 225.4 } };

        verify( book, results3 );
    }

    @Test
    public void testMultiFullFillTakesBB() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        applyAddEvent( book, 1050, Side.Buy, 300, 15.25 );
        applyAddEvent( book, 1041, Side.Buy, 500, 15.0 );
        applyAddEvent( book, 1032, Side.Buy, 400, 15.125 );
        applyAddEvent( book, 1014, Side.Buy, 100, 15.75 );
        applyAddEvent( book, 1070, Side.Buy, 150, 15.75 );
        applyAddEvent( book, 1060, Side.Sell, 900, 15.8 );

        applyFillEvent( book, 1014, 100 );
        applyFillEvent( book, 1070, 150 );

        double[][] results = { { 300, 15.25, 900, 15.8 },
                               { 400, 15.125, 0, Constants.UNSET_DOUBLE },
                               { 500, 15.0, 0, Constants.UNSET_DOUBLE } };

        verify( book, results );
    }

    @Test
    public void testSimpleConsolidateBuyOnly() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        applyAddEvent( book, 1050, Side.Buy, 300, 15.25 );
        applyAddEvent( book, 1041, Side.Buy, 500, 15.0 );
        applyAddEvent( book, 1032, Side.Buy, 400, 15.25 );
        applyAddEvent( book, 1023, Side.Buy, 200, 15.0 );
        applyAddEvent( book, 1014, Side.Buy, 100, 15.0 );

        double[][] results = { { 700, 15.25, 0, Constants.UNSET_DOUBLE },
                               { 800, 15.0, 0, Constants.UNSET_DOUBLE } };

        verify( book, results );
    }

    @Test
    public void testSimpleL1BothSides() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        applyAddEvent( book, 1000, Side.Buy, 100, 15.25 );
        applyAddEvent( book, 1001, Side.Sell, 55, 15.5 );

        double[][] results = { { 100, 15.25, 55, 15.5 } };

        verify( book, results );
    }

    @Test
    public void testSimpleL1BuyOnly() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        applyAddEvent( book, 1000, Side.Buy, 100, 15.25 );

        double[][] results = { { 100, 15.25, 0, Constants.UNSET_DOUBLE } };

        verify( book, results );
    }

    @Test
    public void testSimpleL1Mod() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        applyAddEvent( book, 1000, Side.Buy, 100, 15.25 );

        applyAddEvent( book, 1000, Side.Buy, 75, 15.25 );

        double[][] results = { { 75, 15.25, 0, Constants.UNSET_DOUBLE } };

        verify( book, results );
    }

    @Test
    public void testSimpleL1Mod2Empty() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        applyAddEvent( book, 1000, Side.Buy, 100, 15.25 );
        applyAddEvent( book, 1000, Side.Buy, 0, 15.25, false );

        double[][] results = {};

        verify( book, results );
    }

    @Test
    public void testSimpleL1Mod2ShiftLevel() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        applyAddEvent( book, 1000, Side.Buy, 100, 15.25 );
        applyAddEvent( book, 1000, Side.Buy, 70, 15.5 );

        double[][] results = { { 70, 15.5, 0, Constants.UNSET_DOUBLE } };

        verify( book, results );
    }

    @Test
    public void testSimpleL1SellOnly() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        applyAddEvent( book, 1000, Side.Sell, 100, 15.25 );

        double[][] results = { { 0, Constants.UNSET_DOUBLE, 100, 15.25 } };

        verify( book, results );
    }

    @Test
    public void testSimpleL2BothSides() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        applyAddEvent( book, 1000, Side.Buy, 100, 15.25 );
        applyAddEvent( book, 1001, Side.Sell, 55, 15.5 );

        applyAddEvent( book, 1002, Side.Buy, 200, 15.0 );
        applyAddEvent( book, 1003, Side.Sell, 110, 15.75 );

        double[][] results = { { 100, 15.25, 55, 15.5 },
                               { 200, 15.0, 110, 15.75 } };

        verify( book, results );
    }

    @Test
    public void testSimpleL5BuyOnly() {
        PitchL3OrderBook book = new PitchL3OrderBook( _inst, 0, new PitchBookPoolMgr<>() );

        applyAddEvent( book, 1050, Side.Buy, 300, 15.25 );
        applyAddEvent( book, 1041, Side.Buy, 500, 15.0 );
        applyAddEvent( book, 1032, Side.Buy, 400, 15.125 );
        applyAddEvent( book, 1023, Side.Buy, 200, 15.5 );
        applyAddEvent( book, 1014, Side.Buy, 100, 15.75 );

        double[][] results = { { 100, 15.75, 0, Constants.UNSET_DOUBLE },
                               { 200, 15.5, 0, Constants.UNSET_DOUBLE },
                               { 300, 15.25, 0, Constants.UNSET_DOUBLE },
                               { 400, 15.125, 0, Constants.UNSET_DOUBLE },
                               { 500, 15.0, 0, Constants.UNSET_DOUBLE } };

        verify( book, results );
    }

    private void applyAddEvent( PitchL3OrderBook book, long orderId, Side side, int qty, double px ) {
        applyAddEvent( book, orderId, side, qty, px, true );
    }

    private void applyAddEvent( PitchL3OrderBook book, long orderId, Side side, int qty, double px, boolean isExpTrue ) {
        PitchBookAddOrderWrite event = new PitchBookAddOrderImpl();
        event.getSecurityIdForUpdate().copy( _inst.getSecurityID( SecurityIDSource.ExchangeSymbol ) );
        event.setSide( side );
        event.setOrderQty( qty );
        event.setPrice( px );
        event.setOrderId( orderId );

        if ( isExpTrue ) {
            assertTrue( book.apply( event ) );
        } else {
            assertFalse( book.apply( event ) );
        }
    }

    private void applyClearEvent( PitchL3OrderBook book ) {
        PitchSymbolClear event = new PitchSymbolClearImpl();
        assertTrue( book.apply( event ) );
    }

    private void applyDelEvent( PitchL3OrderBook book, long orderId ) {
        PitchBookCancelOrderWrite event = new PitchBookCancelOrderImpl();
        event.setOrderId( orderId );

        assertTrue( book.apply( event ) );
    }

    private void applyFillEvent( final PitchL3OrderBook book, final long orderId, final int lastQty ) {
        PitchBookOrderExecutedWrite event = new PitchBookOrderExecutedImpl();
        event.setOrderId( orderId );
        event.setLastQty( lastQty );

        book.apply( event );
    }

    private void log( final PitchL3OrderBook book, int maxLevels ) {
        _logMsg.reset();

        UnsafeL2Book tmp = new UnsafeL2Book( _inst, maxLevels );
        book.snapTo( tmp );
        tmp.dump( _logMsg );

        _logMsg.append( "\n" );

        book.eodStats();

        L3BookStats.instance().dump( _logMsg );

        _log.info( _logMsg );
    }

    private void log( final PitchL3OrderBook book ) {
        _logMsg.reset();
        book.dump( _logMsg );

        _logMsg.append( "\n" );

        book.eodStats();

        L3BookStats.instance().dump( _logMsg );

        _log.info( _logMsg );
    }

    private void verify( PitchL3OrderBook book, double[][] results ) {
        UnsafeL2Book snappedBook = new UnsafeL2Book( _inst, 10 );
        book.snapTo( snappedBook );

        int numLevels = results.length;

        assertEquals( numLevels, book.getActiveLevels() );

        DoubleSidedBookEntry entry = new DoubleSidedBookEntryImpl();

        for ( int l = 0; l < numLevels; l++ ) {
            boolean ok = snappedBook.getLevel( l, entry );

            assertTrue( ok );
            assertTrue( results[ l ].length == 4 );

            assertEquals( results[ l ][ 0 ], entry.getBidQty(), Constants.TICK_WEIGHT );
            assertEquals( results[ l ][ 1 ], entry.getBidPx(), Constants.TICK_WEIGHT );
            assertEquals( results[ l ][ 2 ], entry.getAskQty(), Constants.TICK_WEIGHT );
            assertEquals( results[ l ][ 3 ], entry.getAskPx(), Constants.TICK_WEIGHT );
        }
    }
}
