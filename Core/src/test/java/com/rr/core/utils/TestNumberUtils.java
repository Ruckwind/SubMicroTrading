/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestNumberUtils extends BaseTestCase {

    @Test public void testStringToDouble() {
        doStringToDouble( 726.1546442213725, new ViewString( "726.1546442213725" ) );
        doStringToDouble( 123456789.45678, new ViewString( "123456789.45678" ) );
        doStringToDouble( 45.4, new ViewString( "45.4" ) );
        doStringToDouble( 45.0, new ViewString( "45.0" ) );
        doStringToDouble( 45, new ViewString( "45" ) );
        doStringToDouble( 0.12345, new ViewString( "0.12345" ) );
        doStringToDouble( -1.2345, new ViewString( "-1.2345" ) );
        doStringToDouble( Constants.UNSET_DOUBLE, new ViewString( "" ) );
    }

    @Test public void testStringToInt() {
        assertEquals( 987654321, NumberUtils.parseInt( new ViewString( "987654321" ) ) );
        assertEquals( 987654321, NumberUtils.parseInt( new ViewString( "   987654321" ) ) );
        assertEquals( 987654321, NumberUtils.parseInt( new ViewString( " 987654321" ) ) );
        assertEquals( 987654321, NumberUtils.parseInt( new ViewString( "987654321   " ) ) );
        assertEquals( 987654321, NumberUtils.parseInt( new ViewString( "987654321 " ) ) );
        assertEquals( 0, NumberUtils.parseInt( new ViewString( "0" ) ) );
        assertEquals( -123, NumberUtils.parseInt( new ViewString( "-123" ) ) );
        assertEquals( Constants.UNSET_INT, NumberUtils.parseInt( new ViewString( "" ) ) );
    }

    @Test public void testStringToLong() {
        assertEquals( 1413185975413001003L, NumberUtils.parseLong( new ViewString( "1413185975413001003" ) ) );
        assertEquals( 34870005, NumberUtils.parseLong( new ViewString( "34870005" ) ) );
        assertEquals( 0, NumberUtils.parseLong( new ViewString( "0" ) ) );
        assertEquals( Constants.UNSET_LONG, NumberUtils.parseLong( new ViewString( "" ) ) );
    }

    @Test public void testViewStringToLong() {

        ReusableString r  = new ReusableString( "abc11223344def" );
        ViewString     vs = new ViewString( r.getBytes(), 3, 8 );

        assertEquals( 11223344L, NumberUtils.parseLong( vs ) );
    }

    private void doStringToDouble( final double exp, final ViewString val ) {
        double v = NumberUtils.parseDouble( val.getBytes(), val.getOffset(), val.length() );

        assertEquals( exp, v, Constants.TICK_WEIGHT );
    }
}
