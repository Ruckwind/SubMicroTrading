/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.admin;

import com.rr.core.lang.ZString;
import com.rr.core.model.Currency;

public interface AdminReply {

    void add( ZString val );

    void add( String val );

    void add( boolean val );

    void add( long val );

    void add( int val );

    void add( double val );

    void add( double val, int dp );

    void add( double val, boolean cashFmt );

    void add( double val, boolean cashFmt, Currency ccy );

    /**
     * reply is complete, perform end of reply processing
     *
     * @return the final formatted String
     */
    String end();
}
