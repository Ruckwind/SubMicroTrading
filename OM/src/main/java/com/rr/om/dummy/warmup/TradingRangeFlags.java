/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.dummy.warmup;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;

public class TradingRangeFlags {

    private static final ZString FLAGS = new ViewString( " flags=" );

    // @NOTE write text for bit flags

    public static void write( ReusableString err, int flags ) {
        err.append( FLAGS ).append( flags );
    }
}
