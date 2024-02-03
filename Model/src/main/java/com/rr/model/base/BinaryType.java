/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

/**
 * BinaryType represents the encoding type of a binary external format
 * Most binary formats dont distinguish between signed and unsigned numbers
 * ETI does differentiate and its important because NULL is encoded differently
 * Other exchange protocols dont use NULL and in this case sInt encoding is the same as uInt encoding
 * the exchange will decode into appropriate sign/unsigned format
 *
 * @author Richard Rose
 */
public enum BinaryType {
    data,
    fstr, /* fixed string not null terminated */
    zstr, /* fixed string null terminated */
    str,
    ch,
    decimal,
    qty,
    price,
    bool,
    sByte,
    sShort,
    sInt,
    sLong,
    uByte,      /* unsigned byte */
    uShort,     /* unsigned short */
    uInt,       /* unsigned int */
    uLong,      /* unsigned long */
    base36,     /* long in base36 [A-Z][0-9] */
    nowUTC,
    timestampLocal,
    timestampUTC,
    timeUTC,
    timeLocal;

    public static boolean isUnsignedNumber( BinaryType extType ) {
        return extType == BinaryType.uByte || extType == BinaryType.uShort ||
               extType == BinaryType.uInt || extType == BinaryType.uLong;

    }

    public static boolean isWholeNumber( final BinaryType extType ) {
        switch( extType ) {
        case qty:
        case sShort:
        case sInt:
        case sLong:
        case uByte:
        case uShort:
        case uInt:
        case uLong:
        case sByte:
            return true;

        case data:
        case fstr:
        case zstr:
        case str:
        case ch:
        case decimal:
        case price:
        case bool:
        case base36:
        case nowUTC:
        case timestampLocal:
        case timestampUTC:
        case timeUTC:
        case timeLocal:
            break;
        }

        return false;
    }
}
