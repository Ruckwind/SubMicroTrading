/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;

/**
 * used for instruments with unknown ticks
 */
public class UnknownTickSize implements TickType {

    private final ZString _id = new ViewString( "UNKNOWN" );

    public UnknownTickSize() {
        //
    }

    @Override
    public boolean isValid( double price ) {
        return true;
    }

    @Override public double tickSize( final double price ) {
        return Constants.UNSET_DOUBLE;
    }

    @Override
    public void writeError( double price, ReusableString err ) {
        //
    }

    @Override
    public boolean canVerifyPrice() {
        return false;
    }

    @Override
    public ZString getId() {
        return _id;
    }
}
