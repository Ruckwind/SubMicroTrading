/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;

public class FixedTickSize implements TickType {

    private static final ViewString PRICE    = new ViewString( " price " );
    private static final ViewString NOT_MULT = new ViewString( " not a multiple of " );
    private static final ViewString FIXED    = new ViewString( "FIXED" );

    private final double         _fixedTick;
    private final ReusableString _id;

    public FixedTickSize( double fixedTick ) {
        _fixedTick = fixedTick;

        _id = new ReusableString( FIXED );
        _id.append( fixedTick );
    }

    @Override public boolean isValid( double price ) {
        // is the price is a multiple of fixed tick size
        return Math.abs( Math.IEEEremainder( price, _fixedTick ) ) <= Constants.TICK_WEIGHT;
    }

    @Override public double tickSize( double price ) { return _fixedTick; }

    @Override public void writeError( double price, ReusableString err ) {

        err.append( PRICE ).append( price ).append( NOT_MULT ).append( _fixedTick );
    }

    @Override public boolean canVerifyPrice() {
        return true;
    }

    @Override public ZString getId() {
        return _id;
    }

    public double getFixedTick() {
        return _fixedTick;
    }
}
