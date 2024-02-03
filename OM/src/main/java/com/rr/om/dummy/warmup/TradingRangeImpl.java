/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.dummy.warmup;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.TradingRange;

/**
 * note there is NO sync here
 * <p>
 * worst case is on next mem barrier the cached values will be updated
 * dont worry about not atomically updating the upper and  lower values as only 1 of them is used in each path of the valid method
 */

public class TradingRangeImpl implements TradingRange {

    private static final Logger _log = LoggerFactory.create( TradingRangeImpl.class );

    private static final ViewString INVALID_BUY_PRICE  = new ViewString( "Invalid BUY price of " );
    private static final ViewString INVALID_SELL_PRICE = new ViewString( "Invalid SELL price of " );
    private static final ViewString LOW                = new ViewString( ", minSell=" );
    private static final ViewString HIGH               = new ViewString( ", maxBuy=" );
    private static final ViewString TICK_ID            = new ViewString( ", tickID=" );

    private double _lower      = 0;
    private double _upper      = Double.MAX_VALUE;
    private long   _lowerId    = 0L;
    private long   _upperId    = 0L;
    private int    _lowerFlags = 0;
    private int    _upperFlags = 0;

    /**
     * set the id second
     * worst case upperId value is cached by the proc, both updated here and then proc reads uncached upper from mem
     * and picks up the upper ... ie the gives us the previous upperId ... easy to check by looking at the tick logs
     */
    @Override
    public void setMaxBuy( long tickId, double upper, int flags ) {
        _upperId    = tickId;
        _upper      = upper;
        _upperFlags = flags;
    }

    @Override
    public void setMinSell( long tickId, double lower, int flags ) {
        _lowerId    = tickId;
        _lower      = lower;
        _lowerFlags = flags;
    }

    @Override
    public boolean valid( double price, boolean isBuySide, ReusableString err ) {
        boolean valid;

        if ( isBuySide ) {
            final double upper = _upper; // ensure the price logged is the price used in validation

            valid = (price >= 0.0 && price <= upper);

            if ( !valid ) {
                err.append( INVALID_BUY_PRICE ).append( price ).append( HIGH ).append( upper ).append( TICK_ID ).append( _upperId );

                TradingRangeFlags.write( err, _upperFlags );

                _log.info( "Invalid buy price " );
            }

        } else {
            final double lower = _lower;

            valid = price >= lower;

            if ( !valid ) {
                err.append( INVALID_SELL_PRICE ).append( price ).append( LOW ).append( lower ).append( TICK_ID ).append( _lowerId );

                TradingRangeFlags.write( err, _lowerFlags );
            }
        }

        return valid;
    }

    public double getLower()   { return _lower; }

    public int getLowerFlags() { return _lowerFlags; }

    public long getLowerId()   { return _lowerId; }

    public double getUpper()   { return _upper; }

    public int getUpperFlags() { return _upperFlags; }

    public long getUpperId()   { return _upperId; }
}
