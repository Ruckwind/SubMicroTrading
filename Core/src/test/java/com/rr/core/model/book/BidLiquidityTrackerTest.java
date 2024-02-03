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
public class BidLiquidityTrackerTest extends BaseLiqBookTest {

    private static final double NAN = Constants.UNSET_DOUBLE;

    @Override @Before public void setup() throws Exception {
        super.setup();
        _liquidityTracker = new LiquidityTrackerImpl( 5, true );
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

    @Test public void liqBug1() {
        _liquidityTracker.liquidityChange( 0, 0, -0.0125 );
        _liquidityTracker.liquidityChange( 0, 0, -0.015 );
        _liquidityTracker.liquidityChange( 0, 26, -0.0175 );
        _liquidityTracker.liquidityChange( 0, 5, -0.02 );

        _liquidityTracker.liquidityChange( 0, 26, -0.0175 );
        _liquidityTracker.liquidityChange( 3, 5, -0.02 );

        // PRICE, QTY, ADDED, REMOVED, TRADED
        double[][] exp = { { -0.0125, 0, 0, 0, 0 },
                           { -0.015, 0, 0, 0, 0 },
                           { -0.0175, 26, 26, 0, 0 },
                           { -0.02, 5, 5, 0, 0 }
        };

        verify( _liquidityTracker, exp );
    }

    @Test public void liqBug2() {
        _liquidityTracker.liquidityChange( 0, 200, 96.5 );
        _liquidityTracker.liquidityChange( 0, 9, 96.49 );
        _liquidityTracker.liquidityChange( 0, 2, 96.45 );

        _liquidityTracker.resetLiquidityCounters();

        _liquidityTracker.liquidityChange( 0, 1, 96.57 );
        _liquidityTracker.liquidityChange( 0, 72, 96.565 );
        _liquidityTracker.liquidityChange( 0, 200, 96.5 );
        _liquidityTracker.liquidityChange( 0, 9, 96.49 );
        _liquidityTracker.liquidityChange( 0, 2, 96.45 );

        // PRICE, QTY, ADDED, REMOVED, TRADED
        double[][] exp = { { 96.57, 1, 1, 0, 0 },
                           { 96.565, 72, 72, 0, 0 },
                           { 96.5, 200, 0, 0, 0 },
                           { 96.49, 9, 0, 0, 0 },
                           { 96.45, 2, 0, 0, 0 }
        };

        verify( _liquidityTracker, exp );
    }

    @Test public void liqChangeBottom() {
        _liquidityTracker.liquidityChange( 0, 100, 1050 );
        _liquidityTracker.liquidityChange( 0, 100, 1060 );
        _liquidityTracker.liquidityChange( 0, 100, 1040 );
        _liquidityTracker.liquidityChange( 0, 100, 1045 );
        _liquidityTracker.liquidityChange( 0, 100, 1046 );
        _liquidityTracker.liquidityChange( 0, 100, 1042 );

        double[][] exp = { { 1060, 100, 100, 0, 0 },
                           { 1050, 100, 100, 0, 0 },
                           { 1046, 100, 100, 0, 0 },
                           { 1045, 100, 100, 0, 0 },
                           { 1042, 100, 100, 0, 0 },
                           { 1040, 100, 100, 0, 0 },
                           };

        verify( _liquidityTracker, exp );
    }

    @Test public void liqChangeTop() {
        _liquidityTracker.liquidityChange( 0, 90, 1060 );
        _liquidityTracker.liquidityChange( 0, 100, 1050 );

        double[][] exp = { { 1060, 90, 90, 0, 0 },
                           { 1050, 100, 100, 0, 0 } };

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

    @Test public void negPxA() {
        _liquidityTracker.liquidityChange( 0, 2, -0.015 );
        _liquidityTracker.liquidityChange( 0, 22, -0.0175 );
        _liquidityTracker.liquidityChange( 0, 25, -0.0175 );
        _liquidityTracker.liquidityChange( 0, 25, -0.0175 );
        _liquidityTracker.liquidityChange( 0, 25, -0.0175 );

        double[][] exp = { { -0.015, 2, 2, 0, 0 },
                           { -0.0175, 25, 25, 0, 0 }
        };

        verify( _liquidityTracker, exp );

        _liquidityTracker.removeInactiveLevels();

        double[][] exp2 = { { -0.015, 2, 2, 0, 0 },
                            { -0.0175, 25, 25, 0, 0 }
        };

        verify( _liquidityTracker, exp2 );
    }

    @Test public void negPxB() {
        _liquidityTracker.liquidityChange( 0, 25, -0.0175 );
        _liquidityTracker.liquidityChange( 0, 2, -0.015 );
        _liquidityTracker.liquidityChange( 0, 25, -0.0175 );
        _liquidityTracker.liquidityChange( 0, 25, -0.0175 );
        _liquidityTracker.liquidityChange( 0, 22, -0.0175 );

        double[][] exp = { { -0.015, 2, 2, 0, 0 },
                           { -0.0175, 22, 25, 3, 0 }
        };

        verify( _liquidityTracker, exp );

        _liquidityTracker.removeInactiveLevels();

        double[][] exp2 = { { -0.015, 2, 2, 0, 0 },
                            { -0.0175, 22, 25, 3, 0 }
        };

        verify( _liquidityTracker, exp2 );
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

        double[][] exp = { { 1050, 100, 100, 0, 0 },
                           { 1045, 100, 100, 0, 0 }
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
        _liquidityTracker.liquidityChange( 0, 100, 1050 );
        _liquidityTracker.liquidityChange( 0, 90, 1060 );

        double[][] exp = { { 1060, 90, 90, 0, 0 },
                           { 1050, 100, 100, 0, 0 } };

        verify( _liquidityTracker, exp );
    }

    @Test public void twoLvl() {

        setLiq( 0, 15.95, 200, 100, 30, 10 );
        setLiq( 1, 15.92, 100, 20, 0, 0 );

        double[][] exp = { { 15.95, 200, 100, 30, 10 },
                           { 15.92, 100, 20, 0, 0 } };

        verify( _liquidityTracker, exp );
    }

    // PRICE, QTY, ADDED, REMOVED, TRADED
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
