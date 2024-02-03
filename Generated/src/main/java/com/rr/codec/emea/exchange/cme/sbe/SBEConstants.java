/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.codec.emea.exchange.cme.sbe;

public interface SBEConstants {

    double KEEP_DECIMAL_PLACE_FACTOR = 10000000D;

    long SBE_NULL_CHAR = 0;

    long SBE_NULL_BYTE  = -128;
    long SBE_NULL_UBYTE = 0xFF;

    long SBE_NULL_INT16  = Short.MIN_VALUE;       // (-2 ^ 31)
    long SBE_NULL_UINT16 = 0xFFFF;

    long SBE_NULL_INT32  = Integer.MIN_VALUE;
    long SBE_NULL_UINT32 = 0xFFFFFFFF;            // (2 ^ 32)- 1

    long SBE_NULL_INT64  = Long.MIN_VALUE;        // (-2 ^ 63)
    long SBE_NULL_UINT64 = 0xFFFFFFFFFFFFFFFFL;   // (2 ^ 64)- 1
}
