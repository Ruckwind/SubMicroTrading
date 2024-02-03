package com.rr.core.model.book;

import com.rr.core.lang.Constants;
import com.rr.core.model.LiquidityBook;
import com.rr.core.utils.Utils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * liquidity tracker test
 * <p>
 * invoke set to change the working book
 * invoke set multiple times to represent atomic operation
 * <p>
 * invoke delta at end of each atomic operation
 * <p>
 * trades applied directly into liquidity book, requires seperate "resetTradeCounters()" between checks ... its NOT part of deltaReset cause they are needed until after the book is snapped
 */
public class LiquidityBookTest extends BaseLiqBookTest {

    private static final double NAN = Constants.UNSET_DOUBLE;

    protected LiquidityBook _liquidityBook;

    @Override @Before public void setup() throws Exception {
        super.setup();
        _liquidityBook = new UnsafeL2LiquidityBook( null, 5 );
    }

    @Test public void bidAndAskHit() {

        set( 0, 200, 15.5, 302, 15.8 );
        set( 1, 300, 15.25, 303, 15.9 );
        set( 2, 400, 15.125, 304, 16.0 );
        set( 3, 500, 15.0, 305, 16.2 );
        set( 4, 600, 14.95, 301, 16.8 );

        deltaReset();
        resetTradeCounters();

        // its exchange dependant wether trades will flow before book update
        // assume here its the trades first ... need verify on CME which it is and check lag

        hitBid( 200, 15.5 );
        hitBid( 50, 15.25 );
        hitBid( 10, 15.25 );

        hitAsk( 175, 15.8 );

        set( 0, 320, 15.25, 300, 15.8 );
        set( 1, 400, 15.125, 333, 15.9 );
        set( 2, 500, 15.0, 200, 16.1 );
        set( 3, 650, 14.95, 255, 16.2 );
        set( 4, 700, 14.75, 301, 16.8 );

        deltaReset();

        double[][] expBook = { { 320, 15.25, 300, 15.8 },
                               { 400, 15.125, 333, 15.9 },
                               { 500, 15.0, 200, 16.1 },
                               { 650, 14.95, 255, 16.2 },
                               { 700, 14.75, 301, 16.8 } };

        double[][] expAdded = { { 0, 15.5, 0, 15.8 },
                                { 20, 15.25, 30, 15.9 }, // was 300@15.25 but 50 hit and as qty now 320, the liquidity increase is 70
                                { 0, 15.125, 0, 16.0 },
                                { 0, 15.0, 200, 16.1 },
                                { 50, 14.95, 0, 16.2 } };

        double[][] expRemov = { { 200, 15.5, 2, 15.8 },
                                { 0, 15.25, 0, 15.9 },
                                { 0, 15.125, 304, 16.0 },
                                { 0, 15.0, 0, 16.1 },
                                { 0, 14.95, 50, 16.2 } };

        double[][] expTrade = { { 200, 15.5, 175, 15.8 },
                                { 60, 15.25, 0, 15.9 },
                                { 0, 15.125, 0, 16.0 },
                                { 0, 15.0, 0, 16.1 },
                                { 0, 14.95, 0, 16.2 } };

        verify( _liquidityBook, expBook, expAdded, expRemov, expTrade );
    }

    @Test public void bigSweep() {
        _liquidityBook = new UnsafeL2LiquidityBook( null, 10 );

        set( 0, 200, 15.5, 302, 15.8 );
        set( 1, 300, 15.25, 303, 15.9 );
        set( 2, 400, 15.125, 304, 16.0 );
        set( 3, 500, 15.0, 305, 16.2 );
        set( 4, 600, 14.95, 301, 16.8 );

        deltaReset();

        set( 0, 210, 15.95, 382, 15.1 );
        set( 1, 320, 15.9, 373, 15.2 );
        set( 2, 430, 15.8, 364, 15.3 );
        set( 3, 540, 15.7, 355, 15.4 );
        set( 4, 650, 15.6, 341, 15.45 );

        deltaReset();

        double[][] expBook = { { 210, 15.95, 382, 15.1 },
                               { 320, 15.9, 373, 15.2 },
                               { 430, 15.8, 364, 15.3 },
                               { 540, 15.7, 355, 15.4 },
                               { 650, 15.6, 341, 15.45 } };

        double[][] expAdded = { { 210, 15.95, 382, 15.1 },
                                { 320, 15.9, 373, 15.2 },
                                { 430, 15.8, 364, 15.3 },
                                { 540, 15.7, 355, 15.4 },
                                { 650, 15.6, 341, 15.45 } };

        double[][] expRemov = { { 0, 15.95, 0, 15.1 },
                                { 0, 15.9, 0, 15.2 },
                                { 0, 15.8, 0, 15.3 },
                                { 0, 15.7, 0, 15.4 },
                                { 0, 15.6, 0, 15.45 },
                                { 200, 15.5, 302, 15.8 },
                                { 300, 15.25, 303, 15.9 },
                                { 400, 15.125, 304, 16.0 },
                                { 500, 15.0, 305, 16.2 },
                                { 600, 14.95, 301, 16.8 } };

        double[][] expTrade = { { 0, 15.95, 0, 15.1 },
                                { 0, 15.9, 0, 15.2 },
                                { 0, 15.8, 0, 15.3 },
                                { 0, 15.7, 0, 15.4 },
                                { 0, 15.6, 0, 15.45 },
                                { 0, 15.5, 0, 15.8 },
                                { 0, 15.25, 0, 15.9 },
                                { 0, 15.125, 0, 16.0 },
                                { 0, 15.0, 0, 16.2 },
                                { 0, 14.95, 0, 16.8 } };

        verify( _liquidityBook, expBook, expAdded, expRemov, expTrade );
    }

    @Test public void exampleProb1() {
        _liquidityBook = new UnsafeL2LiquidityBook( null, 10 );

        set( 0, 1, 1.655, 1, 1.6571 );
        set( 1, 10, 1.6546, 2, 1.6573 );
        set( 2, 10, 1.6545, 14, 1.6575 );
        set( 3, 2, 1.653, 2, 1.658 );
        set( 4, 1, 1.6525, 1, 1.6585 );

        deltaReset();

        set( 0, 1, 1.655, 1, 1.6571 );
        set( 1, 10, 1.6546, 2, 1.6573 );
        set( 2, 10, 1.6545, 14, 1.6575 );
        set( 3, 10, 1.6544, 2, 1.658 );
        set( 4, 2, 1.653, 1, 1.6585 );

        deltaReset();

        double[][] expBook = { { 1, 1.655, 1, 1.6571 },
                               { 10, 1.6546, 2, 1.6573 },
                               { 10, 1.6545, 14, 1.6575 },
                               { 10, 1.6544, 2, 1.658 },
                               { 2, 1.653, 1, 1.6585 } };

        double[][] expAdded = { { 0, 1.655, 0, 1.6571 },
                                { 0, 1.6546, 0, 1.6573 },
                                { 0, 1.6545, 0, 1.6575 },
                                { 10, 1.6544, 0, 1.658 },
                                { 0, 1.653, 0, 1.6585 } };

        double[][] expRemov = { { 0, 1.655, 0, 1.6571 },
                                { 0, 1.6546, 0, 1.6573 },
                                { 0, 1.6545, 0, 1.6575 },
                                { 0, 1.6544, 0, 1.658 },
                                { 0, 1.653, 0, 1.6585 } };

        double[][] expTrade = { { 0, 1.655, 0, 1.6571 },
                                { 0, 1.6546, 0, 1.6573 },
                                { 0, 1.6545, 0, 1.6575 },
                                { 0, 1.6544, 0, 1.658 },
                                { 0, 1.653, 0, 1.6585 } };

        verify( _liquidityBook, expBook, expAdded, expRemov, expTrade );
    }

    @Test public void fullBookFromEmpty() {
        set( 0, 200, 15.5, 302, 15.8 );
        set( 1, 300, 15.25, 303, 15.9 );
        set( 2, 400, 15.125, 304, 16.0 );
        set( 3, 500, 15.0, 305, 16.2 );
        set( 4, 0, NAN, 301, 16.8 );

        deltaNoReset();

        double[][] expBook = { { 200, 15.5, 302, 15.8 },
                               { 300, 15.25, 303, 15.9 },
                               { 400, 15.125, 304, 16.0 },
                               { 500, 15.0, 305, 16.2 },
                               { 0, NAN, 301, 16.8 } };

        double[][] expAdded = { { 200, 15.5, 302, 15.8 },
                                { 300, 15.25, 303, 15.9 },
                                { 400, 15.125, 304, 16.0 },
                                { 500, 15.0, 305, 16.2 },
                                { 0, NAN, 301, 16.8 } };

        double[][] expRemov = { { 0, 15.5, 0, 15.8 },
                                { 0, 15.25, 0, 15.9 },
                                { 0, 15.125, 0, 16.0 },
                                { 0, 15.0, 0, 16.2 },
                                { 0, NAN, 0, 16.8 } };

        double[][] expTrade = { { 0, 15.5, 0, 15.8 },
                                { 0, 15.25, 0, 15.9 },
                                { 0, 15.125, 0, 16.0 },
                                { 0, 15.0, 0, 16.2 },
                                { 0, NAN, 0, 16.8 } };

        verify( _liquidityBook, expBook, expAdded, expRemov, expTrade );
    }

    @Test public void lvlRemoved() {

        _liquidityBook = new UnsafeL2LiquidityBook( null, 5 );
        _workBook      = new UnsafeL2Book( null, 5 );

        _workBook.setNumLevels( 5 );
        _liquidityBook.getBook().setNumLevels( 5 );

        _liquidityBook = new UnsafeL2LiquidityBook( null, 5 );

        set( 0, 1, 16280, 6, 16280 );
        set( 1, 15, 16235, 15, 16285 );
        set( 2, 15, 16230, 2, 16370 );
        set( 3, 2, 16210, 50, 16385 );
        set( 4, 50, 16205, 50, 16395 );

        deltaReset();

        set( 0, 1, 16280, 6, 16280 );
        set( 1, 15, 16235, 15, 16285 );
        set( 2, 15, 16230, 15, 16290 );
        set( 3, 15, 16225, 15, 16295 );
        set( 4, 15, 16220, 15, 16300 );

        deltaReset();

        double[][] expBook = { { 1, 16280, 6, 16280 },
                               { 15, 16235, 15, 16285 },
                               { 15, 16230, 15, 16290 },
                               { 15, 16225, 15, 16295 },
                               { 15, 16220, 15, 16300 } };

        double[][] expAdded = { { 0, 16280, 0, 16280 },
                                { 0, 16235, 0, 16285 },
                                { 0, 16230, 15, 16290 },
                                { 15, 16225, 15, 16295 },
                                { 15, 16220, 15, 16300 } };

        double[][] expRemov = { { 0, 16280, 0, 16280 },
                                { 0, 16235, 0, 16285 },
                                { 0, 16230, 0, 16290 },
                                { 0, 16225, 0, 16295 },
                                { 0, 16220, 0, 16300 } };

        verify( _liquidityBook, expBook, expAdded, expRemov, null );
    }

    @Test public void modDecreaseBook2Lvls() {

        _liquidityBook = new UnsafeL2LiquidityBook( null, 10 );
        _workBook      = new UnsafeL2Book( null, 10 );

        _workBook.setNumLevels( 7 );
        _liquidityBook.getBook().setNumLevels( 7 );

        _liquidityBook = new UnsafeL2LiquidityBook( null, 10 );

        set( 0, 42, 3040, 42, 3053 );
        set( 1, 63, 3039, 10, 3155 );
        set( 2, 41, 3036, 10, 3180 );

        deltaReset();

        set( 0, 42, 3040, 42, 3053 );
        set( 1, 63, 3039, 10, 3155 );
        set( 2, 41, 3038, 10, 3180 );
        set( 3, 41, 3036, 10, 3181 );

        setBid( 4, 1, 3034 );
        setBid( 5, 10, 2934 );
        setBid( 6, 10, 2910 );

        deltaReset();

        double[][] expBook = { { 42, 3040, 42, 3053 },
                               { 63, 3039, 10, 3155 },
                               { 41, 3038, 10, 3180 },
                               { 41, 3036, 10, 3181 },
                               { 1, 3034, 0, NAN },
                               { 10, 2934, 0, NAN },
                               { 10, 2910, 0, NAN } };

        double[][] expAdded = { { 0, 3040, 0, 3053 },
                                { 0, 3039, 0, 3155 },
                                { 41, 3038, 0, 3180 },
                                { 0, 3036, 10, 3181 },
                                { 1, 3034, 0, NAN },
                                { 10, 2934, 0, NAN },
                                { 10, 2910, 0, NAN } };

        double[][] expRemov = { { 0, 3040, 0, 0 },
                                { 0, 3039, 0, 0 },
                                { 0, 3038, 0, 0 },
                                { 0, 3036, 0, 0 },
                                { 0, 3034, 0, 0 },
                                { 0, 2934, 0, 0 },
                                { 0, 2910, 0, 0 } };

        verify( _liquidityBook, expBook, expAdded, expRemov, null );
    }

    @Test public void modQtyAndPrice() {

        set( 0, 200, 15.5, 150, 15.8 );
        deltaNoReset();
        set( 0, 250, 15.5, 140, 15.8 );
        deltaNoReset();
        set( 0, 245, 15.5, 142, 15.8 );
        deltaNoReset();

        set( 0, 125, 15.6, 100, 15.7 );
        set( 1, 210, 15.5, 192, 15.8 );
        deltaNoReset();

        set( 0, 145, 15.6, 95, 15.7 );
        deltaNoReset();

        double[][] expBook = { { 145, 15.6, 95, 15.7 },
                               { 210, 15.5, 192, 15.8 } };

        double[][] expAdded = { { 145, 15.6, 100, 15.7 },
                                { 250, 15.5, 202, 15.8 } };

        double[][] expRemov = { { 0, 15.6, 5, 15.7 },
                                { 40, 15.5, 10, 15.8 } };

        double[][] expTrade = { { 0, 15.6, 0, 15.7 },
                                { 0, 15.5, 0, 15.8 } };

        _liquidityBook.getBook().setNumLevels( 2 );

        verify( _liquidityBook, expBook, expAdded, expRemov, expTrade );
    }

    @Test public void modQtyAndPriceWithBothBooks2Lvls() {

        _workBook.setNumLevels( 2 );
        _liquidityBook.getBook().setNumLevels( 2 );

        set( 0, 200, 15.5, 150, 15.8 );
        deltaNoReset();
        set( 0, 250, 15.5, 140, 15.8 );
        deltaNoReset();
        set( 0, 245, 15.5, 142, 15.8 );
        deltaNoReset();

        set( 0, 125, 15.6, 100, 15.7 );
        set( 1, 210, 15.5, 192, 15.8 );
        deltaNoReset();

        set( 0, 145, 15.6, 95, 15.7 );
        deltaNoReset();

        double[][] expBook = { { 145, 15.6, 95, 15.7 },
                               { 210, 15.5, 192, 15.8 } };

        double[][] expAdded = { { 145, 15.6, 100, 15.7 },
                                { 250, 15.5, 202, 15.8 } };

        double[][] expRemov = { { 0, 15.6, 5, 15.7 },
                                { 40, 15.5, 10, 15.8 } };

        double[][] expTrade = { { 0, 15.6, 0, 15.7 },
                                { 0, 15.5, 0, 15.8 } };

        verify( _liquidityBook, expBook, expAdded, expRemov, expTrade );
    }

    @Test public void modQtyUpAndDown() {

        set( 0, 200, 15.5, 150, 15.8 );
        deltaNoReset();
        set( 0, 250, 15.5, 140, 15.8 );
        deltaNoReset();
        set( 0, 245, 15.5, 142, 15.8 );
        deltaNoReset();

        double[][] expBook = { { 245, 15.5, 142, 15.8 } };

        double[][] expAdded = { { 250, 15.5, 152, 15.8 } };

        double[][] expRemov = { { 5, 15.5, 10, 15.8 } };

        double[][] expTrade = { { 0, 15.5, 0, 15.8 } };

        _liquidityBook.getBook().setNumLevels( 1 );

        verify( _liquidityBook, expBook, expAdded, expRemov, expTrade );
    }

    @Test public void removeUnusedLvls() {
        _liquidityBook = new UnsafeL2LiquidityBook( null, 10 );

        set( 0, 200, 15.5, 302, 15.8 );
        set( 1, 300, 15.25, 303, 15.9 );
        set( 2, 400, 15.125, 304, 16.0 );
        set( 3, 500, 15.0, 305, 16.2 );
        set( 4, 600, 14.95, 301, 16.8 );

        deltaReset();

        set( 0, 210, 15.95, 382, 15.1 );
        set( 1, 320, 15.9, 373, 15.2 );
        set( 2, 430, 15.8, 364, 15.3 );
        set( 3, 540, 15.7, 355, 15.4 );
        set( 4, 101, 15.5, 301, 15.8 );

        deltaReset();

        _liquidityBook.removeInactiveLevels();

        double[][] expBook = { { 210, 15.95, 382, 15.1 },
                               { 320, 15.9, 373, 15.2 },
                               { 430, 15.8, 364, 15.3 },
                               { 540, 15.7, 355, 15.4 },
                               { 101, 15.5, 301, 15.8 } };

        double[][] expAdded = { { 210, 15.95, 382, 15.1 },
                                { 320, 15.9, 373, 15.2 },
                                { 430, 15.8, 364, 15.3 },
                                { 540, 15.7, 355, 15.4 },
                                { 0, 15.5, 0, 15.8 } };

        double[][] expRemov = { { 0, 15.95, 0, 15.1 },
                                { 0, 15.9, 0, 15.2 },
                                { 0, 15.8, 0, 15.3 },
                                { 0, 15.7, 0, 15.4 },
                                { 99, 15.5, 1, 15.8 } };

        double[][] expTrade = { { 0, 15.95, 0, 15.1 },
                                { 0, 15.9, 0, 15.2 },
                                { 0, 15.8, 0, 15.3 },
                                { 0, 15.7, 0, 15.4 },
                                { 0, 15.5, 0, 15.8 } };

        verify( _liquidityBook, expBook, expAdded, expRemov, expTrade );
    }

    @Test public void testIndicPriceQtyZero() {
        _workBook.setNumLevels( 5 );
        _liquidityBook.getBook().setNumLevels( 5 );

        set( 0, 0, 15.5, 0, 16.0 );
        deltaNoReset();
        set( 0, 0, 15.6, 0, 16.1 );
        deltaNoReset();

        double[][] expBook = { { 0, 15.6, 0, 16.1 } };

        double[][] expAdded = { { 0, NAN, 0, NAN },
                                { 0, NAN, 0, NAN } };

        double[][] expRemov = { { 0, NAN, 0, NAN },
                                { 0, NAN, 0, NAN } };

        _liquidityBook.getBook().setNumLevels( 1 );

        verify( _liquidityBook, expBook, expAdded, expRemov, null );
    }

    @Test public void testRemoveBadLevels() {
        set( 0, 1, 15435, 0, NAN );
        set( 1, 1, 14550, 0, NAN );
        set( 2, 1, 13500, 0, NAN );
        set( 3, 1, 15435, 0, NAN );
        set( 4, 1, 14550, 0, NAN );

        _workBook.markDirtyIfInvalid();

        deltaNoReset();

        assertFalse( _liquidityBook.getBook().isValid() );
    }

    protected void deltaReset() {
        _liquidityBook.deltaFromBook( _workBook, true, null );
    }

    protected void hitAsk( final int qty, final double px ) {
        _liquidityBook.getAskTracker().traded( qty, px );
    }

    protected void hitBid( final int qty, final double px ) {
        _liquidityBook.getBidTracker().traded( qty, px );
    }

    protected void resetTradeCounters() {
        _liquidityBook.resetTradeCounters();
    }

    private void deltaNoReset() {
        _liquidityBook.deltaFromBook( _workBook, false, null );
    }

    private void resetLiquidityCounters() {
        _liquidityBook.resetLiquidityCounters();
    }

    private void verify( LiquidityBook book, double[][] expBook, double[][] expAdded, double[][] expRemove, double[][] expTrade ) {
        UnsafeL2LiquidityBook snappedBook = new UnsafeL2LiquidityBook( null, book.getBook().getMaxLevels() );
        book.snapTo( snappedBook );
        book.resetTradeCounters();

        int numLevels = expBook.length;

        assertEquals( numLevels, book.getBook().getActiveLevels() );

        DoubleSidedBookEntry entry = new DoubleSidedBookEntryImpl();

        for ( int l = 0; l < numLevels; l++ ) {
            boolean ok = snappedBook.getBook().getLevel( l, entry );
            assertTrue( ok );

            assertEquals( expBook[ l ][ 0 ], entry.getBidQty(), Constants.TICK_WEIGHT );
            assertEquals( expBook[ l ][ 1 ], entry.getBidPx(), Constants.TICK_WEIGHT );
            assertEquals( expBook[ l ][ 2 ], entry.getAskQty(), Constants.TICK_WEIGHT );
            assertEquals( expBook[ l ][ 3 ], entry.getAskPx(), Constants.TICK_WEIGHT );
        }

        if ( expAdded != null ) {
            numLevels = expAdded.length;

            for ( int l = 0; l < numLevels; l++ ) {
                LiquidityDeltaEntry bidEntry = new LiquidityDeltaEntryImpl();
                LiquidityDeltaEntry askEntry = new LiquidityDeltaEntryImpl();

                boolean ok = snappedBook.getAskLiqEntry( l, askEntry );
                if ( Utils.isNull( expAdded[ l ][ 3 ] ) ) {
                    assertFalse( ok );
                } else {
                    assertTrue( ok );
                }

                ok = snappedBook.getBidLiqEntry( l, bidEntry );
                if ( Utils.isNull( expAdded[ l ][ 1 ] ) ) {
                    assertFalse( ok );
                } else {
                    assertTrue( ok );
                }

                assertTrue( expAdded[ l ].length == 4 );
                assertTrue( expRemove[ l ].length == 4 );

                assertEquals( expAdded[ l ][ 0 ], bidEntry.getAddedLiquidity(), Constants.TICK_WEIGHT );
                assertEquals( expAdded[ l ][ 1 ], bidEntry.getPrice(), Constants.TICK_WEIGHT );
                assertEquals( expAdded[ l ][ 2 ], askEntry.getAddedLiquidity(), Constants.TICK_WEIGHT );
                assertEquals( expAdded[ l ][ 3 ], askEntry.getPrice(), Constants.TICK_WEIGHT );

                assertEquals( expRemove[ l ][ 0 ], bidEntry.getRemovedLiquidity(), Constants.TICK_WEIGHT );
                assertEquals( expRemove[ l ][ 2 ], askEntry.getRemovedLiquidity(), Constants.TICK_WEIGHT );

                if ( expTrade != null ) {
                    assertTrue( expTrade[ l ].length == 4 );
                    assertEquals( expTrade[ l ][ 0 ], bidEntry.getTradedQty(), Constants.TICK_WEIGHT );
                    assertEquals( expTrade[ l ][ 2 ], askEntry.getTradedQty(), Constants.TICK_WEIGHT );
                }
            }
        }
    }
}
