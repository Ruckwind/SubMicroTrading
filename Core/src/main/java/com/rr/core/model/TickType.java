/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;

public interface TickType {

    /**
     * @return true if the tick type is setup properly aand can verify prices
     */
    boolean canVerifyPrice();

    /**
     * @return id/name of the tick type
     */
    ZString getId();

    /**
     * @param price
     * @return true if the price is valid
     */
    boolean isValid( double price );

    /**
     * @param price to find tick size for
     * @return tick price for the specified price OR Constants.UNSET_DOUBLE  if unknown
     */
    double tickSize( double price );

    /**
     * when price is not valid APPEND logger details to the supplied buffer
     *
     * @param price
     */
    void writeError( double price, ReusableString err );
}
