/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TLC;
import com.rr.core.utils.SMTRuntimeException;

import java.util.Map;

public class DecoderUtils {

    public static void populate( Map<ReusableString, ReusableString> map, byte[] fixMsg, int offset, int maxIdx ) {

        int startKey = 0;
        int endKey   = -1;
        int startVal = -1;
        int endVal;

        if ( maxIdx > fixMsg.length ) maxIdx = fixMsg.length;

        for ( int i = 0; i < maxIdx; ++i ) {

            byte b = fixMsg[ i ];

            if ( b == '=' ) {
                endKey   = i - 1;
                startVal = i + 1;
            } else if ( b == FixField.FIELD_DELIMITER ) {
                endVal = i - 1;
                if ( endKey > 0 ) {
                    ReusableString key = TLC.instance().pop();
                    ReusableString val = TLC.instance().pop();
                    key.setValue( fixMsg, startKey, endKey - startKey + 1 );
                    val.setValue( fixMsg, startVal, endVal - startVal + 1 );
                    map.put( key, val );
                }
                startKey = i + 1;
                endKey   = -1;
            }
        }
    }

    // get date in same format as data
    public static byte[] getYYYYMMDD( final String dateStr ) {

        if ( dateStr.length() < 8 ) return new byte[ 0 ];

        String base = dateStr.substring( 0, 8 );

        int dateInt = Integer.parseInt( base );

        if ( dateInt < 19700000 || dateInt > 21000000 ) throw new SMTRuntimeException( "Bad date filter of " + base + " expected between 19700000 and 2100000 ie 1970 to 2100" );

        String yyyymmdd = dateStr.substring( 0, 4 ) + "-" + dateStr.substring( 4, 6 ) + "-" + dateStr.substring( 6, 8 );

        return yyyymmdd.getBytes();
    }
}
