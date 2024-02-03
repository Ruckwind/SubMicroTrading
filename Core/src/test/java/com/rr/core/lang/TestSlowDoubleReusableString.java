/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

// original ReusableString is lost !!
// @NOTE add back all the lost tests

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestSlowDoubleReusableString extends BaseTestCase {

    private ReusableString _s = new ReusableString();

    @Test
    public void perfDoubleSixDP() {

        perfDoubleDP( 6, 1234567890123.123456 );
        perfDoubleDP( 6, 123456789.000001 ); // decimal fractional error
        perfDoubleDP( 6, 0 );
        perfDoubleDP( 6, 12345 );
        perfDoubleDP( 6, 12345.123 );
        perfDoubleDP( 6, 12345.765432 );
        perfDoubleDP( 6, 123456789.987654 );
        perfDoubleDP( 6, 123456789.123456789 );
        perfDoubleDP( 6, 123456789.987654321 );
        perfDoubleDP( 6, 1234567890.123456789 );
        perfDoubleDP( 6, 1234567890.987654321 );
        perfDoubleDP( 6, 123456789098765.123456789 );
        perfDoubleDP( 6, 123456789098765.987654321 );
        perfDoubleDP( 6, 0.000001 );
        perfDoubleDP( 6, 1.000001 );
        perfDoubleDP( 6, 0.0000001 );
        perfDoubleDP( 6, 1.0000001 );
        perfDoubleDP( 6, 0.0000000 );
        perfDoubleDP( 6, 1.00000001 );
        perfDoubleDP( 6, 0.000000001 );
        perfDoubleDP( 6, 1.000000001 );
        perfDoubleDP( 6, 0.0000000001 );
        perfDoubleDP( 6, 1.0000000001 );
        perfDoubleDP( 6, 0.00000000001 );
        perfDoubleDP( 6, 1.00000000001 );
    }

    @Before
    public void setUp() {
        ReusableString.setFastMode( false );
    }

    @After
    public void tearDown() {
        ReusableString.setFastMode( ReusableString.DEFAULT_DBL_FAST_MODE );
    }

    @Test
    public void testDouble() {

        checkDouble( Constants.UNSET_DOUBLE, "" );
        checkDouble( Constants.UNSET_LONG, "" );
        checkDouble( 1234567890123.123456, "1234567890123.1235" );
        checkDouble( Long.MAX_VALUE, "9223372036854775807" );

        checkDouble( 0, "0" );
        checkDouble( 12345, "12345" );
        checkDouble( 12345.123, "12345.123" );
        checkDouble( 12345.765432, "12345.765432" );
        checkDouble( 123456789.987654, "123456789.987654" );
        checkDouble( 123456789.000001, "123456789.000001" );
        checkDouble( 0.000001, "0.000001" );
        checkDouble( 1.000001, "1.000001" );

        checkDouble( 1234567890123.123456, "1234567890123.1235" );
        checkDouble( 123456789.000001, "123456789.000001" ); // decimal fractional error
        checkDouble( 0, "0" );
        checkDouble( 12345, "12345" );
        checkDouble( 12345.123, "12345.123" );
        checkDouble( 12345.765432, "12345.765432" );
        checkDouble( 123456789.987654, "123456789.987654" );
        checkDouble( 123456789.123456789, "123456789.123457" );
        checkDouble( 123456789.987654321, "123456789.987654" );
        checkDouble( 1234567890.123456789, "1234567890.123457" );
        checkDouble( 1234567890.987654321, "1234567890.987654" );
        checkDouble( 123456789098765.123456789, "123456789098765.12" );
        checkDouble( 123456789098765.987654321, "123456789098765.98" );
        checkDouble( 0.000001, "0.000001" );
        checkDouble( 1.000001, "1.000001" );
        checkDouble( 0.0000001, "0.0000001" );
        checkDouble( 1.0000001, "1.0000001" );
        checkDouble( 0.00000001, "0.00000001" );
        checkDouble( 1.00000001, "1.00000001" );
        checkDouble( 0.000000001, "0" );
        checkDouble( 1.000000001, "1" );
        checkDouble( 0.0000000001, "0" );
        checkDouble( 1.0000000001, "1" );
        checkDouble( 0.00000000001, "0" );
        checkDouble( 1.00000000001, "1" );

    }

    @Test
    public void testDoublePerf() {
        for ( int j = 0; j < 5; j++ ) {
            long start = ClockFactory.getLiveClock().nanoTime();
            int  cnt   = 10000;
            for ( int i = 0; i < cnt; i++ ) {
                perfDoubleSixDP();
            }
            long end = ClockFactory.getLiveClock().nanoTime();

            System.out.println( "Iter " + j + " TIME " + (Math.abs( end - start ) / (cnt * 24)) + " nanos" );
        }
    }

    private void checkDouble( double val, String exp ) {
        ReusableString s = new ReusableString();
        s.append( val );
        assertEquals( exp, s.toString() );
    }

    private void perfDoubleDP( int dp, double val ) {
        _s.reset();
        _s.append( val, dp );
    }
}
