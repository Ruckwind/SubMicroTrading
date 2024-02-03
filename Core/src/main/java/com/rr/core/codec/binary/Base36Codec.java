package com.rr.core.codec.binary;

import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.codec.RuntimeEncodingException;
import com.rr.core.lang.Constants;
import com.rr.core.utils.Utils;

import java.util.Arrays;

public final class Base36Codec {

    public static final int NULL = 255;
    public static final byte[] _bytesBase36 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes();
    public static final int[] _charToDigit = buildCharToDigit();
    private static final int MAX_BYTES = 13;

    private static int[] buildCharToDigit() {
        int[] c2d = new int[ 256 ];

        Arrays.fill( c2d, NULL );

        for ( int idx = 0; idx < _bytesBase36.length; ++idx ) {

            byte b = _bytesBase36[ idx ];

            c2d[ b ] = idx;
        }

        return c2d;
    }

    public static long decode( final byte[] buf, final int offset, final int len ) {
        if ( len == 0 ) {
            return Constants.UNSET_LONG;
        }

        if ( len > MAX_BYTES ) {
            throw new RuntimeDecodingException( "Base36Codec len of " + len + " is too big, max " + MAX_BYTES );
        }

        final int bufEnd = offset + len;

        if ( bufEnd > buf.length ) throw new RuntimeDecodingException( "Base36Codec buffer not big enough, maxSize " + buf.length + " requires " + bufEnd + " bytes" );

        long result = 0;
        int  i      = offset;

        while( i < bufEnd ) {
            final byte b     = buf[ i++ ];
            final int  digit = _charToDigit[ b ];

            if ( digit == NULL ) throw new RuntimeDecodingException( "Base36Codec invalid character '" + (char) b + "' on decoding" );

            result *= 36;

            result += digit;
        }

        return result;
    }

    public static void encode( final long val, final byte[] dest, final int offset, int len ) {

        long tVal = (Utils.isNull( val )) ? 0 : val;

        if ( tVal < 0 ) throw new RuntimeEncodingException( "Base36Codec cannot encode negative numbers " + val );

        int destIdx = offset + len;

        while( destIdx > offset && tVal > 0 ) {
            final int digit = (int) (tVal % 36);

            dest[ --destIdx ] = _bytesBase36[ digit ];

            tVal /= 36;
        }

        while( --destIdx >= offset ) {
            dest[ destIdx ] = '0';
        }
    }
}
