/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

public class StringFactory {

    public static ViewString hexToViewString( String hex ) {
        byte[] bytes = hex.getBytes();

        if ( bytes[ 0 ] == '0' && bytes[ 1 ] == 'x' ) {
            int            approxlen = (bytes.length - 2) / 2 + 1;
            ReusableString s         = new ReusableString( approxlen );

            int loopSize = bytes.length - 1;
            int i        = 2;

            while( i < loopSize ) {
                int b1 = chkHex( bytes[ i++ ] );
                int b2 = chkHex( bytes[ i++ ] );

                if ( b1 == -1 || b2 == -1 ) {
                    throw new RuntimeException( "Invalid char in hex string " + hex + ", offset=" + (i - 2) + ", b1=" + b1 + ", b2=" + b2 );
                }

                int val = (b1 << 4) + b2;

                s.append( (byte) val );
            }

            return s;

        }

        return new ViewString( bytes );
    }

    private static int chkHex( byte b ) {
        if ( b >= '0' && b <= '9' ) {
            return b - '0';
        }
        if ( b >= 'A' && b <= 'F' ) {
            return b + 10 - 'A';
        }
        if ( b >= 'a' && b <= 'f' ) {
            return b + 10 - 'a';
        }
        return -1;
    }
}
