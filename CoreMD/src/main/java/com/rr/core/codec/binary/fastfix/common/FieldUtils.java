/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common;

import com.rr.core.lang.Constants;

public class FieldUtils {

    public static double parseDouble( String init ) {
        if ( init == null || init.length() == 0 ) return Constants.UNSET_DOUBLE;
        return Double.parseDouble( init );
    }

    public static int parseInt( String init ) {
        if ( init == null || init.length() == 0 ) return Constants.UNSET_INT;
        return Integer.parseInt( init );
    }

    public static long parseLong( String init ) {
        if ( init == null || init.length() == 0 ) return Constants.UNSET_LONG;
        return Long.parseLong( init );
    }

}
