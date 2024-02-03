package com.rr.core.model.book;

import com.rr.core.lang.BaseTestCase;
import org.junit.Before;

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
public class BaseLiqBookTest extends BaseTestCase {

    protected UnsafeL2Book        _workBook;
    protected BookLevelEntry      _entry    = new BookEntryImpl();
    protected LiquidityTracker    _liquidityTracker;
    protected LiquidityDeltaEntry _liqEntry = new LiquidityDeltaEntryImpl();

    @Before public void setup() throws Exception {
        _workBook = new UnsafeL2Book( null, 5 );
        _entry.setNumOrders( 0 );
    }

    protected void set( int lvlIdx, double bidQty, double bidPx, double askQty, double askPx ) {
        set( lvlIdx, bidQty, bidPx, askQty, askPx, false );
    }

    protected void set( int lvlIdx, double bidQty, double bidPx, double askQty, double askPx, boolean removeIfQtyZero ) {
        setBid( lvlIdx, bidQty, bidPx, removeIfQtyZero );
        setAsk( lvlIdx, askQty, askPx, removeIfQtyZero );

        int lvl = lvlIdx + 1;

        if ( lvl > _workBook.getActiveLevels() ) _workBook.setNumLevels( lvl );
    }

    protected void setAsk( final int idx, final double askQty, final double askPx ) {
        setAsk( idx, askQty, askPx, false );
    }

    protected void setAsk( final int idx, final double askQty, final double askPx, boolean removeIfQtyZero ) {
        _entry.set( 0, askQty, askPx );
        _workBook.setAsk( idx, _entry );
        if ( removeIfQtyZero && askQty <= 0 ) {
            _workBook.deleteAsk( idx );
        }
    }

    protected void setBid( final int idx, final double bidQty, final double bidPx ) {
        setBid( idx, bidQty, bidPx, false );
    }

    protected void setBid( final int idx, final double bidQty, final double bidPx, boolean removeIfQtyZero ) {
        _entry.set( 0, bidQty, bidPx );
        _workBook.setBid( idx, _entry );
        if ( removeIfQtyZero && bidQty <= 0 ) {
            _workBook.deleteBid( idx );
        }
    }

    protected void setLiq( int idx, double price, double qty, double added, double removed, double traded ) {
        _liqEntry.clear();
        _liqEntry.set( qty, price );
        _liqEntry.setAddedLiquidity( added );
        _liqEntry.setRemovedLiquidity( removed );
        _liqEntry.setTradedQty( traded );
        _liquidityTracker.set( idx, _liqEntry );
    }
}
