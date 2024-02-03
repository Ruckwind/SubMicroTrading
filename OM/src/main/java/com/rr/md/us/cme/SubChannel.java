/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.us.cme;

import com.rr.core.utils.SMTRuntimeException;

public enum SubChannel {

    // note the ordinal IS used

    Zero,
    One,
    Two,
    Three,
    Four,
    Five,
    Six,
    Seven,
    Eight,
    Nine;

    public static SubChannel get( int subChannel ) {
        switch( subChannel ) {
        case 1:
            return One;
        case 2:
            return Two;
        case 3:
            return Three;
        case 4:
            return Four;
        case 5:
            return Five;
        case 6:
            return Six;
        case 7:
            return Seven;
        case 8:
            return Eight;
        case 9:
            return Nine;
        }

        throw new SMTRuntimeException( "Invalid sub channel " + subChannel );
    }
}
