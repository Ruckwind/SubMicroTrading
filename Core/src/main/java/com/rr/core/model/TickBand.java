/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.utils.Utils;

public final class TickBand {

    private static final ViewString PRICE    = new ViewString( " price " );
    private static final ViewString NOT_MULT = new ViewString( " not a multiple of " );

    private final double _lower;
    private final double _upper;
    private final double _tickSize;

    public TickBand( double lower, double upper, double tickSize ) {
        super();
        _lower    = Utils.isNull( lower ) ? -Double.MAX_VALUE : lower;
        _upper    = Utils.isNull( upper ) ? Double.MAX_VALUE : upper;
        _tickSize = tickSize;
    }

    public final double getLower() {
        return _lower;
    }

    public final double getTickSize() {
        return _tickSize;
    }

    public final double getUpper() {
        return _upper;
    }

    public final boolean inBand( double price ) {
        return (price - _lower) >= -Constants.TICK_WEIGHT && (price - _upper) <= Constants.TICK_WEIGHT;
    }

    public final boolean isValid( double price ) {
        return Math.abs( Math.IEEEremainder( price, _tickSize ) ) <= Constants.TICK_WEIGHT;
    }

    public final void writeError( double price, ReusableString err ) {
        err.append( PRICE ).append( price ).append( NOT_MULT ).append( _tickSize );
    }
}
