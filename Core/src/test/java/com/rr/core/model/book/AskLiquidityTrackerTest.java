package com.rr.core.model.book;

import com.rr.core.lang.Constants;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
public class AskLiquidityTrackerTest extends BaseLiqBookTest {

    private static final double NAN = Constants.UNSET_DOUBLE;

    @Override @Before public void setup() throws Exception {
        super.setup();
        _liquidityTracker = new LiquidityTrackerImpl( 5, false );
    }

    @Test public void growDown() {
        double startPx = 1000;

        for ( int i = 0; i < 1000; i++ ) {
            _liquidityTracker.liquidityChange( 0, 10000 + i, startPx + i );
        }

        assertEquals( 1000, _liquidityTracker.getActiveLevels() );

        for ( int i = 0; i < 1000; i++ ) {
            _liquidityTracker.getEntry( i, _liqEntry );

            assertEquals( 10000 + i, _liqEntry.getQty(), Constants.TICK_WEIGHT );
            assertEquals( startPx + i, _liqEntry.getPrice(), Constants.TICK_WEIGHT );
        }
    }

    @Test public void growUp() {
        double startPx = 2000;

        final int cnt = 1000;
        for ( int i = 0; i < cnt; i++ ) {
            _liquidityTracker.liquidityChange( 0, 10000 - i, startPx - i );
        }

        assertEquals( cnt, _liquidityTracker.getActiveLevels() );

        final double baseQty = 10000 - cnt + 1;
        final double basePx  = startPx - cnt + 1;

        for ( int i = 0; i < cnt; i++ ) {
            _liquidityTracker.getEntry( i, _liqEntry );

            assertEquals( baseQty + i, _liqEntry.getQty(), Constants.TICK_WEIGHT );
            assertEquals( basePx + i, _liqEntry.getPrice(), Constants.TICK_WEIGHT );
        }
    }

    @Test public void liqAddRemove() {
        _liquidityTracker.liquidityChange( 0, 100, 1050 );
        _liquidityTracker.liquidityChange( 0, 90, 1050 );
        _liquidityTracker.liquidityChange( 0, 100, 1050 );
        _liquidityTracker.liquidityChange( 0, 80, 1050 );
        _liquidityTracker.liquidityChange( 0, 100, 1050 );

        double[][] exp = { { 1050, 100, 130, 30, 0 } };

        verify( _liquidityTracker, exp );
    }

    @Test public void liqChangeBottom() {
        _liquidityTracker.liquidityChange( 0, 100, 1050 );
        _liquidityTracker.liquidityChange( 0, 100, 1060 );
        _liquidityTracker.liquidityChange( 0, 100, 1040 );
        _liquidityTracker.liquidityChange( 0, 100, 1045 );
        _liquidityTracker.liquidityChange( 0, 100, 1046 );
        _liquidityTracker.liquidityChange( 0, 100, 1042 );

        double[][] exp = { { 1040, 100, 100, 0, 0 },
                           { 1042, 100, 100, 0, 0 },
                           { 1045, 100, 100, 0, 0 },
                           { 1046, 100, 100, 0, 0 },
                           { 1050, 100, 100, 0, 0 },
                           { 1060, 100, 100, 0, 0 },
                           };

        verify( _liquidityTracker, exp );
    }

    @Test public void liqChangeTop() {
        _liquidityTracker.liquidityChange( 0, 90, 1050 );
        _liquidityTracker.liquidityChange( 0, 100, 1060 );

        double[][] exp = { { 1050, 90, 90, 0, 0 },
                           { 1060, 100, 100, 0, 0 } };

        verify( _liquidityTracker, exp );
    }

    @Test public void liqRemove() {
        _liquidityTracker.liquidityChange( 0, 100, 1050 );
        _liquidityTracker.liquidityChange( 0, 97, 1050 );
        _liquidityTracker.liquidityChange( 0, 94, 1050 );
        _liquidityTracker.liquidityChange( 0, 90, 1050 );

        double[][] exp = { { 1050, 90, 100, 10, 0 } };

        verify( _liquidityTracker, exp );
    }

    @Test public void oneLiqChange() {
        _liquidityTracker.liquidityChange( 0, 100, 1050 );

        double[][] exp = { { 1050, 100, 100, 0, 0 } };

        verify( _liquidityTracker, exp );
    }

    @Test public void oneLvl() {

        setLiq( 0, 15.95, 200, 100, 30, 10 );

        double[][] exp = { { 15.95, 200, 100, 30, 10 } };

        verify( _liquidityTracker, exp );
    }

    @Test public void removeInactiveA() {
        _liquidityTracker.liquidityChange( 0, 100, 1050 );
        _liquidityTracker.liquidityChange( 0, 0, 1060 );
        _liquidityTracker.liquidityChange( 0, 0, 1040 );
        _liquidityTracker.liquidityChange( 0, 100, 1045 );
        _liquidityTracker.liquidityChange( 0, 0, 1046 );
        _liquidityTracker.liquidityChange( 0, 0, 1042 );

        _liquidityTracker.removeInactiveLevels();

        double[][] exp = { { 1045, 100, 100, 0, 0 },
                           { 1050, 100, 100, 0, 0 }
        };

        verify( _liquidityTracker, exp );
    }

    @Test public void removeInactiveB() {
        _liquidityTracker.liquidityChange( 0, 0, 1050 );
        _liquidityTracker.liquidityChange( 0, 100, 1060 );
        _liquidityTracker.liquidityChange( 0, 0, 1040 );
        _liquidityTracker.liquidityChange( 0, 0, 1045 );
        _liquidityTracker.liquidityChange( 0, 0, 1046 );
        _liquidityTracker.liquidityChange( 0, 0, 1042 );

        _liquidityTracker.removeInactiveLevels();

        double[][] exp = { { 1060, 100, 100, 0, 0 } };

        verify( _liquidityTracker, exp );
    }

    @Test public void removeInactiveC() {
        _liquidityTracker.liquidityChange( 0, 0, 1050 );
        _liquidityTracker.liquidityChange( 0, 0, 1060 );
        _liquidityTracker.liquidityChange( 0, 0, 1040 );
        _liquidityTracker.liquidityChange( 0, 0, 1045 );
        _liquidityTracker.liquidityChange( 0, 0, 1046 );
        _liquidityTracker.liquidityChange( 0, 100, 1042 );

        _liquidityTracker.removeInactiveLevels();

        double[][] exp = { { 1042, 100, 100, 0, 0 } };

        verify( _liquidityTracker, exp );
    }

    @Test public void removeInactiveD() {
        _liquidityTracker.liquidityChange( 0, 0, 1050 );
        _liquidityTracker.liquidityChange( 0, 0, 1060 );
        _liquidityTracker.liquidityChange( 0, 0, 1040 );
        _liquidityTracker.liquidityChange( 0, 0, 1045 );
        _liquidityTracker.liquidityChange( 0, 0, 1046 );
        _liquidityTracker.liquidityChange( 0, 0, 1042 );

        _liquidityTracker.removeInactiveLevels();

        assertEquals( 0, _liquidityTracker.getActiveLevels() );
    }

    @Test public void twoLiqChange() {
        _liquidityTracker.liquidityChange( 0, 100, 1060 );
        _liquidityTracker.liquidityChange( 0, 90, 1050 );

        double[][] exp = { { 1050, 90, 90, 0, 0 },
                           { 1060, 100, 100, 0, 0 } };

        verify( _liquidityTracker, exp );
    }

    @Test public void twoLvl() {

        setLiq( 0, 15.92, 200, 100, 30, 10 );
        setLiq( 1, 15.95, 100, 20, 0, 0 );

        double[][] exp = { { 15.92, 200, 100, 30, 10 },
                           { 15.95, 100, 20, 0, 0 } };

        verify( _liquidityTracker, exp );
    }

    private void verify( LiquidityTracker t, double[][] exp ) {

        int numLevels = t.getActiveLevels();

        assertEquals( exp.length, numLevels );

        final LiquidityDeltaEntry entry = new LiquidityDeltaEntryImpl();

        for ( int l = 0; l < numLevels; l++ ) {

            t.getEntry( l, entry );

            assertEquals( exp[ l ][ 0 ], entry.getPrice(), Constants.TICK_WEIGHT );
            assertEquals( exp[ l ][ 1 ], entry.getQty(), Constants.TICK_WEIGHT );
            assertEquals( exp[ l ][ 2 ], entry.getAddedLiquidity(), Constants.TICK_WEIGHT );
            assertEquals( exp[ l ][ 3 ], entry.getRemovedLiquidity(), Constants.TICK_WEIGHT );
            assertEquals( exp[ l ][ 4 ], entry.getTradedQty(), Constants.TICK_WEIGHT );
        }
    }
}
