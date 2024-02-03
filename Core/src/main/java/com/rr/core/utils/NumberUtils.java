/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

import com.rr.core.codec.RuntimeEncodingException;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ZString;

public class NumberUtils {

    public static int nextPowerTwo( int initialNum ) {
        // Find a power of 2 >= initialCapacity
        int num = 1;
        while( num < initialNum )
            num <<= 1;

        return num;
    }

    public static long parseLong( final ZString src ) {

        if ( src == null ) return Constants.UNSET_LONG;

        byte[] bytes  = src.getBytes();
        int    len    = src.length();
        long   num    = 0;
        int    idx    = src.getOffset();
        int    maxIdx = idx + len;

        if ( len == 0 ) return Constants.UNSET_LONG;

        byte b = bytes[ idx ];

        // skip leading space
        while( b == ' ' && ++idx < maxIdx ) {
            b = bytes[ idx ];
        }

        if ( idx >= maxIdx ) return Constants.UNSET_LONG;

        boolean negative = false;

        if ( bytes[ idx ] == '+' ) {
            if ( ++idx >= maxIdx ) throw new NumberFormatException( "E1 Bad double [" + src.toString() + "] idx=" + idx + ", len=" + len + ", offset=" + src.getOffset() );
            b = bytes[ idx ];
        } else if ( bytes[ idx ] == '-' ) {
            negative = true;
            if ( ++idx >= maxIdx ) throw new NumberFormatException( "E2 Bad double [" + src.toString() + "] idx=" + idx + ", len=" + len + ", offset=" + src.getOffset() );
            b = bytes[ idx ];
        }

        while( b >= '0' && b <= '9' ) {
            num = (num * 10) + (b - '0');

            if ( ++idx < maxIdx ) {
                b = bytes[ idx ];
            } else {
                break;
            }
        }

        // skip trailing space
        while( b == ' ' && ++idx < maxIdx ) {
            b = bytes[ idx ];
        }

        if ( idx < maxIdx && (b < '0' || b > '9') ) {
            throw new NumberFormatException( "Unexpected byte " + (char) b + " in numeric string [" + src.toString() + "]" );
        }

        return (negative) ? -num : num;
    }

    public static int parseInt( final ZString src ) throws NumberFormatException {

        if ( src == null ) return Constants.UNSET_INT;

        byte[] bytes  = src.getBytes();
        int    length = src.length();
        int    num    = 0;
        int    idx    = 0;

        if ( length == 0 ) return Constants.UNSET_INT;

        byte b = bytes[ 0 ];

        // skip leading space
        while( b == ' ' && ++idx < length ) {
            b = bytes[ idx ];
        }

        if ( idx >= length ) return Constants.UNSET_INT;

        boolean negative = false;

        if ( bytes[ idx ] == '+' ) {
            if ( ++idx >= length ) throw new NumberFormatException( "E1 Bad double [" + src.toString() + "] idx=" + idx + ", len=" + length );
            b = bytes[ idx ];
        } else if ( bytes[ idx ] == '-' ) {
            negative = true;
            if ( ++idx >= length ) throw new NumberFormatException( "E2 Bad double [" + src.toString() + "] idx=" + idx + ", len=" + length );
            b = bytes[ idx ];
        }

        while( b >= '0' && b <= '9' ) {
            num = (num * 10) + (b - '0');

            if ( ++idx < length ) {
                b = bytes[ idx ];
            } else {
                break;
            }
        }

        // skip trailing space
        while( b == ' ' && ++idx < length ) {
            b = bytes[ idx ];
        }

        if ( idx < length && (b < '0' || b > '9') ) {
            throw new NumberFormatException( "Unexpected byte " + (char) b + " in numeric string [" + src.toString() + "]" );
        }

        return (negative) ? -num : num;
    }

    public static int parseInt( byte[] bytes, int offset, int len ) throws NumberFormatException {

        int num    = 0;
        int idx    = offset;
        int maxIdx = offset + len;

        byte b = bytes[ idx ];

        // skip leading space
        while( b == ' ' && ++idx < maxIdx ) {
            b = bytes[ idx ];
        }

        if ( idx >= maxIdx ) return Constants.UNSET_INT;

        boolean negative = false;

        if ( bytes[ idx ] == '+' ) {
            if ( ++idx >= maxIdx ) throw new NumberFormatException( "E1 Bad double [" + new String( bytes, offset, len ) + "] idx=" + idx + ", maxIdx=" + maxIdx );
            b = bytes[ idx ];
        } else if ( bytes[ idx ] == '-' ) {
            negative = true;
            if ( ++idx >= maxIdx ) throw new NumberFormatException( "E2 Bad double [" + new String( bytes, offset, len ) + "] idx=" + idx + ", maxIdx=" + maxIdx );
            b = bytes[ idx ];
        }

        while( b >= '0' && b <= '9' && idx < maxIdx ) {
            num = (num * 10) + (b - '0');

            if ( ++idx < maxIdx ) {
                b = bytes[ idx ];
            } else {
                break;
            }
        }

        // skip trailing space
        while( b == ' ' && ++idx < maxIdx ) {
            b = bytes[ idx ];
        }

        if ( idx < maxIdx && (b < '0' || b > '9') ) {
            throw new NumberFormatException( "Unexpected byte " + (char) b + " in numeric string [" + new String( bytes ) + "]" );
        }

        return (negative) ? -num : num;
    }

    public static long parseLong( byte[] bytes, int offset, int len ) throws NumberFormatException {

        long num    = 0;
        int  idx    = offset;
        int  maxIdx = offset + len;

        byte b = bytes[ idx ];

        // skip leading space
        while( b == ' ' && ++idx < maxIdx ) {
            b = bytes[ idx ];
        }

        if ( idx >= maxIdx ) return Constants.UNSET_LONG;

        boolean negative = false;

        if ( bytes[ idx ] == '+' ) {
            if ( ++idx >= maxIdx ) throw new NumberFormatException( "E1 Bad double [" + new String( bytes, offset, len ) + "] idx=" + idx + ", maxIdx=" + maxIdx );
            b = bytes[ idx ];
        } else if ( bytes[ idx ] == '-' ) {
            negative = true;
            if ( ++idx >= maxIdx ) throw new NumberFormatException( "E2 Bad double [" + new String( bytes, offset, len ) + "] idx=" + idx + ", maxIdx=" + maxIdx );
            b = bytes[ idx ];
        }

        while( b >= '0' && b <= '9' && idx < maxIdx ) {
            num = (num * 10) + (b - '0');

            if ( ++idx < maxIdx ) {
                b = bytes[ idx ];
            } else {
                break;
            }
        }

        // skip trailing space
        while( b == ' ' && ++idx < maxIdx ) {
            b = bytes[ idx ];
        }

        if ( idx < maxIdx && (b < '0' || b > '9') ) {
            throw new NumberFormatException( "Unexpected byte " + (char) b + " in numeric string [" + new String( bytes ) + "]" );
        }

        return (negative) ? -num : num;
    }

    public static final double parseDouble( byte[] bytes, int offset, int len ) {
        int idx    = offset;
        int maxIdx = offset + len;

        while( idx < maxIdx && bytes[ idx ] == ' ' ) {
            idx++;
        }

        if ( idx >= maxIdx ) return Constants.UNSET_DOUBLE;

        boolean negative = false;

        if ( bytes[ idx ] == '+' ) {
            idx++;
            if ( idx >= maxIdx ) throw new NumberFormatException( "E1 Bad double [" + new String( bytes, offset, len ) + "] idx=" + idx + ", maxIdx=" + maxIdx );
        } else if ( bytes[ idx ] == '-' ) {
            idx++;
            negative = true;
            if ( idx >= maxIdx ) throw new NumberFormatException( "E2 Bad double [" + new String( bytes, offset, len ) + "] idx=" + idx + ", maxIdx=" + maxIdx );
        }

        // Find the integer part
        long    wholePart = 0;
        boolean endPart   = false;

        byte digit = 0;

        while( !endPart && idx < maxIdx ) {
            digit = bytes[ idx++ ];

            if ( digit >= '0' && digit <= '9' ) {

                wholePart *= 10;
                wholePart += (digit - '0');

            } else if ( digit == '.' || digit == ' ' ) {
                endPart = true;
            } else {
                throw new NumberFormatException( "E3 Bad double [" + new String( bytes, offset, len ) + "] idx=" + idx + ", maxIdx=" + maxIdx );
            }

            if ( wholePart < 0 ) throw new NumberFormatException( "E4 Bad double ... too big [" + new String( bytes, offset, len ) + "] idx=" + idx + ", maxIdx=" + maxIdx );
            ;
        }

        if ( digit == ' ' && idx < maxIdx ) {
            while( bytes[ idx++ ] == ' ' ) {
                // consume space
            }

            throw new NumberFormatException( "E5 Bad double .. unexpected byte [" + new String( bytes, offset, len ) + "] idx=" + idx + ", maxIdx=" + maxIdx );
        }

        // only support prices to Constants.PRICE_DP
        long priceFractionAsLong = 0;
        endPart = false;

        int dp = 0;

        while( !endPart && idx < maxIdx ) {
            digit = bytes[ idx++ ];

            if ( digit >= '0' && digit <= '9' ) {

                if ( dp++ < Constants.PRICE_DP_H ) {
                    priceFractionAsLong *= 10;
                    priceFractionAsLong += (digit - '0');
                }
            } else if ( digit == ' ' ) {
                endPart = true;
            } else {
                throw new NumberFormatException( "E6 Bad double has non numberic chars [" + new String( bytes, offset, len ) + "] idx=" + idx + ", maxIdx=" + maxIdx );
            }
        }

        for ( int i = dp; i < Constants.PRICE_DP_H; i++ ) {
            priceFractionAsLong *= 10;
        }

        double v = negative ? -(wholePart + (priceFractionAsLong / Constants.PRICE_DP_H_DFACTOR)) : (wholePart + (priceFractionAsLong / Constants.PRICE_DP_H_DFACTOR));

        if ( !Double.isFinite( v ) ) {
            System.out.println( new String( bytes, offset, len ) + " -infinity" );
        }

        if ( Double.isNaN( v ) ) {
            System.out.println( new String( bytes, offset, len ) + " -infinity" );
        }

        return v;
    }

    public static int priceToExternalInt6DP( double price ) {

        if ( price >= 0 ) {
            if ( price == Double.MAX_VALUE ) {
                return Integer.MAX_VALUE;
            }
            return (int) ((price + Constants.WEIGHT) * 1000000L);
        }

        if ( Utils.isNull( price ) ) { // NULL
            return 0;
        } else if ( price == -Double.MAX_VALUE ) {
            return Integer.MIN_VALUE;
        }

        return (int) ((price - Constants.WEIGHT) * 1000000L);
    }

    public static long priceToExternalLong6DP( double price ) {

        if ( price >= 0 ) {
            if ( price == Double.MAX_VALUE ) {
                return Long.MAX_VALUE;
            }
            return (long) ((price + Constants.WEIGHT) * 1000000L);
        }

        if ( Utils.isNull( price ) ) { // NULL
            return 0;
        } else if ( price == -Double.MAX_VALUE ) {
            return Long.MIN_VALUE;
        }

        return (long) ((price - Constants.WEIGHT) * 1000000L);
    }
}
