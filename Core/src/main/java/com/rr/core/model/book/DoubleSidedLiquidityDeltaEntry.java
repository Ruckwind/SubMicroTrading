/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

import com.rr.core.lang.ReusableString;

public interface DoubleSidedLiquidityDeltaEntry {

    void applyDelta( LiquidityDeltaEntry bid, LiquidityDeltaEntry ask );

    void dump( ReusableString dest );

    LiquidityDeltaEntry getAskSide();

    LiquidityDeltaEntry getBidSide();

    boolean isDirty();

    /**
     * @return true if bid and ask not dirty
     */
    boolean isValid();

    /**
     * reset values back to zero
     */
    void reset();

    void set( LiquidityDeltaEntry bid, LiquidityDeltaEntry ask );
}
