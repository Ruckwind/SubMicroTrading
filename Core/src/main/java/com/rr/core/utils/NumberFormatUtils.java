/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

import com.rr.core.lang.Constants;
import com.rr.core.lang.ZString;

public class NumberFormatUtils {

    public static final int MAX_DOUBLE_DIGITS = 18;
    public static final int MAX_DP            = 10; // THIS IS ONLY TRUE IN LIMITED CIRCUMSTANCES ... ONLY UPTO 6 IS SAFE FOR IMPORT/EXPORT
    public static final byte[] _dig10 = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    public static final int[] _hundreds = { 0, 100, 200, 300, 400, 500, 600, 700, 800, 900 };
    public static final int[] _tens     = { 0, 10, 20, 30, 40, 50, 60, 70, 80, 90 };
    public static int[] _dig100 = makeTwoDigs(); // 1 to 100 encoded as short upperByte = char for tens, lowerbyte = char for units

    public static void addLong( byte[] buffer, int idx, long value, int length ) {
        long q;
        int  r;
        int  charPos = idx + length;
        byte sign    = 0;

        if ( value < 0 ) {
            sign  = '-';
            value = -value;
        }

        if ( value == 0 ) {
            buffer[ --charPos ] = '0';
        } else {                        // encoding right to left
            while( value > 65536 ) {
                q     = value / 100;
                r     = (int) (value - (q * 100));
                value = q;
                final int t = _dig100[ r ];

                buffer[ --charPos ] = (byte) (t & 0xFF);   // units
                buffer[ --charPos ] = (byte) (t >> 8);     // tens
            }

            do {
                q                   = value / 10;
                r                   = (int) (value - (q * 10));
                buffer[ --charPos ] = _dig10[ r ];
                value               = q;
            } while( value != 0 );
        }

        if ( sign != 0 ) {
            buffer[ --charPos ] = sign;
        }
    }

    public static int toInteger( ZString strInt ) {

        int val = 0;

        byte[] b = strInt.getBytes();

        for ( int i = 0; i < b.length; ++i ) {
            byte x = b[ i ];

            if ( x >= '0' && x <= '9' ) {
                val = (val * 10) + (x - '0');
            } else {
                break;
            }
        }

        return val;
    }

    public static long toLong( ZString strLong ) {

        long val = 0;

        byte[] b = strLong.getBytes();

        for ( int i = 0; i < b.length; ++i ) {
            byte x = b[ i ];

            if ( x >= '0' && x <= '9' ) {
                val = (val * 10) + (x - '0');
            } else {
                break;
            }
        }

        return val;
    }

    public static void addInt( byte[] buffer, int idx, int value, int length ) {

        int  r;
        int  charPos = idx + length;
        byte sign    = 0;

        if ( value < 0 ) {
            sign  = '-';
            value = -value;
        }

        // Do the integer part now
        if ( value == 0 ) {
            buffer[ --charPos ] = '0';
        } else {
            // Get 2 digits/iteration using ints
            int q;
            while( value > 65536 ) {
                q     = value / 100;
                r     = value - (q * 100);
                value = q;
                final int t = _dig100[ r ];

                buffer[ --charPos ] = (byte) (t & 0xFF);   // units
                buffer[ --charPos ] = (byte) (t >> 8);     // tens
            }

            do {
                q                   = value / 10;
                r                   = value - (q * 10);
                buffer[ --charPos ] = _dig10[ r ];
                value               = q;
            } while( value != 0 );
        }
        if ( sign != 0 ) {
            buffer[ --charPos ] = sign;
        }
    }

    public static void addPositiveIntFixedLength( byte[] buffer, int idx, int value, int length ) {

        int r;
        int charPos = idx + length;

        // Do the integer part now
        if ( value == 0 ) {
            buffer[ --charPos ] = '0';
        } else {
            // Get 2 digits/iteration using ints
            int q;
            while( value > 65536 ) {
                q     = value / 100;
                r     = (value - (q * 100));
                value = q;
                final int t = _dig100[ r ];

                buffer[ --charPos ] = (byte) (t & 0xFF);   // units
                buffer[ --charPos ] = (byte) (t >> 8);     // tens
            }

            do {
                q                   = value / 10;
                r                   = (value - (q * 10));
                buffer[ --charPos ] = _dig10[ r ];
                value               = q;
            } while( value != 0 );
        }

        int padLen = charPos - idx;
        while( padLen > 0 ) {
            buffer[ --charPos ] = '0';
            padLen--;
        }
    }

    public static void addPositiveLongFixedLength( byte[] buffer, int idx, long value, int length ) {
        long q;
        int  r;
        int  charPos = idx + length;

        if ( value == 0 ) {
            buffer[ --charPos ] = '0';
        } else {
            while( value > 65536 ) {
                q     = value / 100;
                r     = (int) (value - (q * 100));
                value = q;
                final int t = _dig100[ r ];
                buffer[ --charPos ] = (byte) (t & 0xFF);   // units
                buffer[ --charPos ] = (byte) (t >> 8);     // tens
            }

            do {
                q                   = value / 10;
                r                   = (int) (value - (q * 10));
                buffer[ --charPos ] = _dig10[ r ];
                value               = q;
            } while( value != 0 );
        }

        int padLen = charPos - idx;
        while( padLen > 0 ) {
            buffer[ --charPos ] = '0';
            padLen--;
        }
    }

    /**
     * append a double truncated at 6 dp to supplied buffer caller must ensure truncates decimal places for numbers bigger than 99,999,999,999 the buffer is big
     * enough
     */
    public static int addPrice( byte[] buffer, int idx, double doubleValue, int len ) {
        final long base = (long) doubleValue;

        long absVal = Math.abs( base );

        if ( (absVal & Constants.PRICE_DP_THRESHOLD_MASK_8DP) != 0 ) { // not enough space for 8DP to be added to long

            if ( (absVal & Constants.PRICE_DP_THRESHOLD_MASK_NODP) != 0 ) {
                len = getLongLen( base );
                addLong( buffer, idx, base, len );
                return len;
            }

            final long value = (long) (doubleValue * Constants.PRICE_DP_S_LFACTOR); // use short form ie only 6dp
            return addPriceLong( buffer, idx, value, len, Constants.PRICE_DP_S );
        }

        final long value = (long) (doubleValue * Constants.PRICE_DP_L_LFACTOR); // use long form 8dp
        return addPriceLong( buffer, idx, value, len, Constants.PRICE_DP_L );
    }

    /**
     * encode a double with fixed number of decimal places ... round up only a trailing fractional 9 beyond the dp requested
     *
     * @param buffer
     * @param idx
     * @param doubleValue
     * @param requestedDP
     * @return
     */
    public static int addPriceFixedDP( byte[] buffer, int idx, double doubleValue, int requestedDP ) {

        int fixedDP = requestedDP;

        long value = (long) doubleValue;

        if ( Utils.isNull( value ) || value == Long.MAX_VALUE || requestedDP <= 0 ) {
            final int len = NumberFormatUtils.getLongLen( value );
            NumberFormatUtils.addLong( buffer, idx, value, len );
            return len;
        }

        final boolean wasPos = doubleValue >= 0;

        int len = 0;

        if ( !wasPos ) {
            ++len;
            buffer[ idx++ ] = '-';

            doubleValue = Math.abs( doubleValue );
            value       = -value;
        }

        if ( fixedDP > MAX_DP ) fixedDP = MAX_DP;

        double dpFactor = getDPFactor( fixedDP + 1 );

        double fraction = doubleValue - value;

        long fractionAsLong    = (long) (fraction * dpFactor);
        long tmpFractionAsLong = (fractionAsLong + 1); // ROUND UP ONLY IF .9 or over

        int fracDigits    = getLongLen( fractionAsLong );
        int tmpFracDigits = getLongLen( tmpFractionAsLong );

        if ( tmpFracDigits > fracDigits && tmpFracDigits > (fixedDP + 1) ) { // carry over from fractional to decimal part
            ++value;
            tmpFractionAsLong -= carryValue( fixedDP + 1 );
        }

        fractionAsLong = tmpFractionAsLong / 10;

        int numSigDig = getLongLen( value );

        addLong( buffer, idx, value, numSigDig );
        len += numSigDig;
        idx += numSigDig;

        ++len;
        buffer[ idx++ ] = '.';

        addPositiveLongFixedLength( buffer, idx, fractionAsLong, fixedDP );
        idx += fixedDP;
        len += fixedDP;

        return len;
    }

    private static long carryValue( final int dp ) {
        switch( dp ) {
        case 0:
            return 1;
        case 1:
            return 10;
        case 2:
            return 100;
        case 3:
            return 1000;
        case 4:
            return 10000;
        case 5:
            return 100000;
        case 6:
            return 1000000;
        case 7:
            return 10000000;
        case 8:
            return 100000000;
        case 9:
            return 1000000000;
        case 10:
            return 10000000000L;
        case 11:
            return 100000000000L;
        case 12:
            return 1000000000000L;
        default:
            return 10000000000000L;
        }
    }

    private static int addPriceLong( byte[] buffer, int idx, long value, int length, int numDP ) {
        long q;
        int  r;
        int  charPos    = idx + length;
        int  nxtFreeIdx = idx;
        byte sign       = 0;

        if ( value < 0 ) {
            sign  = '-';
            value = -value;
        }

        int dpLoops = numDP >> 1;

        // Ok, we know that the input value will be at least 7 digits long due
        // to 1M multiplier, so do 3 iterations (double digits)
        boolean digitsWritten = false;
        for ( int i = 0; i < dpLoops; i++ ) {
            q = value / 100;
            r = (int) (value - ((q << 6) + (q << 5) + (q << 2)));

            final int t     = _dig100[ r ];
            final int units = (t & 0xFF);

            value = q;
            if ( digitsWritten ) {
                buffer[ --charPos ] = (byte) units;   // units
                buffer[ --charPos ] = (byte) (t >> 8);     // tens
            } else {
                if ( r == 0 ) {
                    charPos -= 2;
                } else {
                    digitsWritten = true;
                    if ( units == 0x30 ) {
                        charPos--;
                        nxtFreeIdx = charPos;
                    } else {
                        nxtFreeIdx          = charPos;
                        buffer[ --charPos ] = (byte) units;
                    }

                    buffer[ --charPos ] = (byte) (t >> 8);
                }
            }
        }

        if ( !digitsWritten ) {

            nxtFreeIdx = charPos + 1;

            // charPos already decremented by 6

            buffer[ charPos ] = '0';
        }

        buffer[ --charPos ] = '.';

        // Do the integer part now
        if ( value == 0 ) {
            buffer[ --charPos ] = '0';
        } else {
            while( value > 65536 ) {
                q     = value / 100;
                r     = (int) (value - (q * 100));
                value = q;
                final int t = _dig100[ r ];

                buffer[ --charPos ] = (byte) (t & 0xFF);   // units
                buffer[ --charPos ] = (byte) (t >> 8);     // tens
            }

            do {
                q                   = value / 10;
                r                   = (int) (value - (q * 10));
                buffer[ --charPos ] = _dig10[ r ];
                value               = q;
            } while( value != 0 );
        }

        if ( sign != 0 ) {
            buffer[ --charPos ] = sign;
        }

        return (nxtFreeIdx - idx);
    }

    public static int getLongLen( long value ) {
        int len;
        if ( value < 0 ) {
            len = getNegLongLen( value );
        } else {
            len = getPosLongLen( value );
        }
        return len;
    }

    public static int getPosShortLen( final short v ) {
        if ( v < 10 )
            return 1;
        if ( v < 100 )
            return 2;
        if ( v < 1000 )
            return 3;
        if ( v < 10000 )
            return 4;
        return 5;
    }

    public static int getPosIntLen( final int v ) {
        if ( v < 10 )
            return 1;
        if ( v < 100 )
            return 2;
        if ( v < 1000 )
            return 3;
        if ( v < 10000 )
            return 4;
        if ( v < 100000 )
            return 5;
        if ( v < 1000000 )
            return 6;
        if ( v < 10000000 )
            return 7;
        if ( v < 100000000 )
            return 8;
        if ( v < 1000000000 )
            return 9;
        return 10;
    }

    public static int getNegIntLen( final int v ) {
        if ( v > -10 )
            return 2;
        if ( v > -100 )
            return 3;
        if ( v > -1000 )
            return 4;
        if ( v > -10000 )
            return 5;
        if ( v > -100000 )
            return 6;
        if ( v > -1000000 )
            return 7;
        if ( v > -10000000 )
            return 8;
        if ( v > -100000000 )
            return 9;
        if ( v > -1000000000 )
            return 10;
        return 11;
    }

    public static int getPosLongLen( final long v ) {
        if ( v < 10L )
            return 1;
        if ( v < 100L )
            return 2;
        if ( v < 1000L )
            return 3;
        if ( v < 10000L )
            return 4;
        if ( v < 100000L )
            return 5;
        if ( v < 1000000L )
            return 6;
        if ( v < 10000000L )
            return 7;
        if ( v < 100000000L )
            return 8;
        if ( v < 1000000000L )
            return 9;
        if ( v < 10000000000L )
            return 10;
        if ( v < 100000000000L )
            return 11;
        if ( v < 1000000000000L )
            return 12;
        if ( v < 10000000000000L )
            return 13;
        if ( v < 100000000000000L )
            return 14;
        if ( v < 1000000000000000L )
            return 15;
        if ( v < 10000000000000000L )
            return 16;
        if ( v < 100000000000000000L )
            return 17;
        if ( v < 1000000000000000000L )
            return 18;
        return 19;
    }

    public static int getNegLongLen( final long v ) {
        if ( v > -10L )
            return 2;
        if ( v > -100L )
            return 3;
        if ( v > -1000L )
            return 4;
        if ( v > -10000L )
            return 5;
        if ( v > -100000L )
            return 6;
        if ( v > -1000000L )
            return 7;
        if ( v > -10000000L )
            return 8;
        if ( v > -100000000L )
            return 9;
        if ( v > -1000000000L )
            return 10;
        if ( v > -10000000000L )
            return 11;
        if ( v > -100000000000L )
            return 12;
        if ( v > -1000000000000L )
            return 13;
        if ( v > -10000000000000L )
            return 14;
        if ( v > -100000000000000L )
            return 15;
        if ( v > -1000000000000000L )
            return 16;
        if ( v > -10000000000000000L )
            return 17;
        if ( v > -100000000000000000L )
            return 18;
        if ( v > -1000000000000000000L )
            return 19;
        return 20;
    }

    public static int getPriceLen( final double doubleValue ) {
        long absVal = (long) Math.abs( doubleValue );

        int  dp;
        long factor;

        if ( (absVal & Constants.PRICE_DP_THRESHOLD_MASK_8DP) == 0 ) {
            dp     = Constants.PRICE_DP_L;
            factor = Constants.PRICE_DP_L_LFACTOR;
        } else {
            dp     = Constants.PRICE_DP_S;
            factor = Constants.PRICE_DP_S_LFACTOR;
        }

        long value = (long) (doubleValue * factor);
        int  len;
        if ( value < 0 ) {
            len = getNegLongLen( value );
            if ( len < dp + 2 ) // if length is less than 7 it means that the original values was 0.something
                len = dp + 2; // 6 for the fractional + 1 for the leading integer part
        } else {
            len = getPosLongLen( value );
            if ( len < dp + 1 ) // if length is less than 7 it means that the original values was 0.something
                len = dp + 1; // 6 for the fractional + 1 for the leading integer part
        }
        len++; // adjust for the dot '.'
        return len;
    }

    private static double getDPFactor( final int maxDP ) {
        switch( maxDP ) {
        case 0:
            return 1;
        case 1:
            return 10;
        case 2:
            return 100;
        case 3:
            return 1000;
        case 4:
            return 10000;
        case 5:
            return 100000;
        case 6:
            return 1000000;
        case 7:
            return 10000000;
        case 8:
            return 100000000;
        case 9:
            return 1000000000;
        case 10:
            return 10000000000D;
        case 11:
            return 100000000000D;
        case 12:
            return 1000000000000D;
        default:
            return 10000000000000D;
        }
    }

    private static int[] makeTwoDigs() {
        int[] t = new int[ 100 ];

        for ( int tens = 0; tens < 10; tens++ ) {
            int tenCH = tens + '0';

            for ( int units = 0; units < 10; units++ ) {
                int unitCH = units + '0';
                int v      = (tenCH << 8) + unitCH;
                t[ tens * 10 + units ] = v;
            }
        }

        return t;
    }
}
