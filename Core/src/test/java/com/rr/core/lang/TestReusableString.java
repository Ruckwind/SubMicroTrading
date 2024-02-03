/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

// original ReusableString is lost !!
// @NOTE add back all the lost tests

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestReusableString extends BaseTestCase {

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
        ReusableString.setFastMode( true );
    }

    @After
    public void tearDown() {
        ReusableString.setFastMode( ReusableString.DEFAULT_DBL_FAST_MODE );
    }

    @Test
    public void testContains() {
        ReusableString s        = new ReusableString( "ICAD.XPAR" );
        boolean        contains = s.contains( "XPAR" );

        assertTrue( contains );
    }

    @Test
    public void testContainsViewString() {
        ReusableString rs       = new ReusableString( "ABCDEFICAD.XPARGHIJKL" );
        ViewString     s        = new ViewString( rs.getBytes(), 6, 9 );
        boolean        contains = s.contains( "XPAR" );

        assertTrue( contains );
    }

    @Test
    public void testCopy() {
        ReusableString s   = new ReusableString( "abc.def" );
        int            idx = s.indexOf( '.' );
        assertEquals( 3, idx );
        ReusableString out = new ReusableString();
        s.substring( out, idx + 1 );
        assertEquals( "def", out.toString() );
    }

    @Test
    public void testDelete() {
        checkDelete( "A", (byte) 'B', "A" );
        checkDelete( "AB", (byte) 'B', "A" );
        checkDelete( "ABC", (byte) 'B', "AC" );
        checkDelete( "BABBCBBBDBBBB", (byte) 'B', "ACD" );
        checkDelete( "AC", (byte) 'B', "AC" );
    }

    @Test
    public void testDouble() {

        checkDouble( 123456789.987654, "123456789.987654" );
        checkDouble( 0, "0.0" );

        checkDouble( Constants.UNSET_DOUBLE, "" );
        checkDouble( Constants.UNSET_LONG, "" );
        checkDouble( 12345678901.123456, "12345678901.123456" );
        checkDouble( 1234567890123.123456, "1234567890123" );
        checkDouble( Long.MAX_VALUE, "9223372036854775807" );

        checkDouble( 12345, "12345.0" );
        checkDouble( 12345.123, "12345.123" );
        checkDouble( 12345.765432, "12345.765432" );
        checkDouble( 123456789.000001, "123456789.000001" );
        checkDouble( 0.000001, "0.000001" );
        checkDouble( 1.000001, "1.000001" );
    }

    @Test
    public void testDoubleEightDP() {

        checkDoubleDP( 8, 1234567890123.123456, "1234567890123.12353515" );
        checkDoubleDP( 8, 123456789.000001, "123456789.00000099" ); // decimal fractional error
        checkDoubleDP( 8, 0, "0.00000000" );
        checkDoubleDP( 8, 12345, "12345.00000000" );
        checkDoubleDP( 8, 12345.123, "12345.12300000" );
        checkDoubleDP( 8, 12345.765432, "12345.76543200" );
        checkDoubleDP( 8, 123456789.987654, "123456789.98765400" );
        checkDoubleDP( 8, 123456789.123456789, "123456789.12345679" );
        checkDoubleDP( 8, 123456789.987654321, "123456789.98765432" );
        checkDoubleDP( 8, 1234567890.123456789, "1234567890.12345671" );
        checkDoubleDP( 8, 1234567890.987654321, "1234567890.98765421" );
        checkDoubleDP( 8, 123456789098765.123456789, "123456789098765.12500000" );
        checkDoubleDP( 8, 123456789098765.987654321, "123456789098765.98437500" );
        checkDoubleDP( 8, 0.000001, "0.00000100" );
        checkDoubleDP( 8, 1.000001, "1.00000100" );
        checkDoubleDP( 8, 0.0000001, "0.00000010" );
        checkDoubleDP( 8, 1.0000001, "1.00000010" );
        checkDoubleDP( 8, 0.00000001, "0.00000001" );
        checkDoubleDP( 8, 1.00000001, "1.00000001" );
        checkDoubleDP( 8, 0.000000001, "0.00000000" );
        checkDoubleDP( 8, 1.000000001, "1.00000000" );
        checkDoubleDP( 8, 0.0000000001, "0.00000000" );
        checkDoubleDP( 8, 1.0000000001, "1.00000000" );
        checkDoubleDP( 8, 0.00000000001, "0.00000000" );
        checkDoubleDP( 8, 1.00000000001, "1.00000000" );
    }

    @Test
    public void testDoubleFourDP() {

        checkDoubleDP( 4, 1234567890123.123456, "1234567890123.1235" );
        checkDoubleDP( 4, 0, "0.0000" );
        checkDoubleDP( 4, 12345, "12345.0000" );
        checkDoubleDP( 4, 12345.123, "12345.1230" );
        checkDoubleDP( 4, 12345.765432, "12345.7654" );
        checkDoubleDP( 4, 123456789.987654, "123456789.9876" );
        checkDoubleDP( 4, 123456789.000001, "123456789.0000" );
        checkDoubleDP( 4, 123456789.123456789, "123456789.1234" );
        checkDoubleDP( 4, 123456789.987654321, "123456789.9876" );
        checkDoubleDP( 4, 1234567890.123456789, "1234567890.1234" );
        checkDoubleDP( 4, 1234567890.987654321, "1234567890.9876" );
        checkDoubleDP( 4, 123456789098765.123456789, "123456789098765.1250" );
        checkDoubleDP( 4, 123456789098765.987654321, "123456789098765.9843" );
        checkDoubleDP( 4, 0.000001, "0.0000" );
        checkDoubleDP( 4, 1.000001, "1.0000" );
        checkDoubleDP( 4, 0.0000001, "0.0000" );
    }

    @Test
    public void testDoubleNineDP() {

        checkDoubleDP( 9, 1234567890123.123456, "1234567890123.123535156" );
        checkDoubleDP( 9, 123456789.000001, "123456789.000000998" ); // decimal fractional error
        checkDoubleDP( 9, 0, "0.000000000" );
        checkDoubleDP( 9, 12345, "12345.000000000" );
        checkDoubleDP( 9, 12345.123, "12345.123000000" );
        checkDoubleDP( 9, 12345.765432, "12345.765432000" );
        checkDoubleDP( 9, 123456789.987654, "123456789.987654000" );
        checkDoubleDP( 9, 123456789.123456789, "123456789.123456791" );
        checkDoubleDP( 9, 123456789.987654321, "123456789.987654328" );
        checkDoubleDP( 9, 1234567890.123456789, "1234567890.123456716" );
        checkDoubleDP( 9, 1234567890.987654321, "1234567890.987654209" );
        checkDoubleDP( 9, 123456789098765.123456789, "123456789098765.125000000" );
        checkDoubleDP( 9, 123456789098765.987654321, "123456789098765.984375000" );
        checkDoubleDP( 9, 0.000001, "0.000001000" );
        checkDoubleDP( 9, 1.000001, "1.000001000" );
        checkDoubleDP( 9, 0.0000001, "0.000000100" );
        checkDoubleDP( 9, 1.0000001, "1.000000100" );
        checkDoubleDP( 9, 0.00000001, "0.000000010" );
        checkDoubleDP( 9, 1.00000001, "1.000000010" );
        checkDoubleDP( 9, 0.000000001, "0.000000001" );
        checkDoubleDP( 9, 1.000000001, "1.000000001" );
        checkDoubleDP( 9, 0.0000000001, "0.000000000" );
        checkDoubleDP( 9, 1.0000000001, "1.000000000" );
        checkDoubleDP( 9, 0.00000000001, "0.000000000" );
        checkDoubleDP( 9, 1.00000000001, "1.000000000" );
    }

    @Test
    public void testDoubleOneDP() {
        checkDoubleDP( 1, 123456789.987654, "123456789.9" );

        checkDoubleDP( 1, 1234567890123.123456, "1234567890123.1" );
        checkDoubleDP( 1, 0, "0.0" );
        checkDoubleDP( 1, 12345, "12345.0" );
        checkDoubleDP( 1, 12345.123, "12345.1" );
        checkDoubleDP( 1, 12345.765432, "12345.7" );
        checkDoubleDP( 1, 123456789.000001, "123456789.0" );
        checkDoubleDP( 1, 123456789.123456789, "123456789.1" );
        checkDoubleDP( 1, 123456789.987654321, "123456789.9" );
        checkDoubleDP( 1, 1234567890.123456789, "1234567890.1" );
        checkDoubleDP( 1, 1234567890.987654321, "1234567890.9" );
        checkDoubleDP( 1, 123456789098765.123456789, "123456789098765.1" );
        checkDoubleDP( 1, 123456789098765.987654321, "123456789098765.9" );
        checkDoubleDP( 1, 0.000001, "0.0" );
        checkDoubleDP( 1, 1.000001, "1.0" );
        checkDoubleDP( 1, 0.0000001, "0.0" );
        checkDoubleDP( 1, 1.0000001, "1.0" );
        checkDoubleDP( 1, 0.00000001, "0.0" );
        checkDoubleDP( 1, 1.00000001, "1.0" );
        checkDoubleDP( 1, 0.000000001, "0.0" );
        checkDoubleDP( 1, 1.000000001, "1.0" );
        checkDoubleDP( 1, 0.0000000001, "0.0" );
        checkDoubleDP( 1, 1.0000000001, "1.0" );
        checkDoubleDP( 1, 0.00000000001, "0.0" );
        checkDoubleDP( 1, 1.00000000001, "1.0" );
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

    @Test
    public void testDoubleSixDP() {

        checkDoubleDP( 6, 1234567890123.123456, "1234567890123.123535" );
        checkDoubleDP( 6, 123456789.000001, "123456789.000001" ); // decimal fractional error
        checkDoubleDP( 6, 0, "0.000000" );
        checkDoubleDP( 6, 12345, "12345.000000" );
        checkDoubleDP( 6, 12345.123, "12345.123000" );
        checkDoubleDP( 6, 12345.765432, "12345.765432" );
        checkDoubleDP( 6, 123456789.987654, "123456789.987654" );
        checkDoubleDP( 6, 123456789.123456789, "123456789.123456" );
        checkDoubleDP( 6, 123456789.987654321, "123456789.987654" );
        checkDoubleDP( 6, 1234567890.123456789, "1234567890.123456" );
        checkDoubleDP( 6, 1234567890.987654321, "1234567890.987654" );
        checkDoubleDP( 6, 123456789098765.123456789, "123456789098765.125000" );
        checkDoubleDP( 6, 123456789098765.987654321, "123456789098765.984375" );
        checkDoubleDP( 6, 0.000001, "0.000001" );
        checkDoubleDP( 6, 1.000001, "1.000001" );
        checkDoubleDP( 6, 0.0000001, "0.000000" );
        checkDoubleDP( 6, 1.0000001, "1.000000" );
        checkDoubleDP( 6, 0.00000001, "0.000000" );
        checkDoubleDP( 6, 1.00000001, "1.000000" );
        checkDoubleDP( 6, 0.000000001, "0.000000" );
        checkDoubleDP( 6, 1.000000001, "1.000000" );
        checkDoubleDP( 6, 0.0000000001, "0.000000" );
        checkDoubleDP( 6, 1.0000000001, "1.000000" );
        checkDoubleDP( 6, 0.00000000001, "0.000000" );
        checkDoubleDP( 6, 1.00000000001, "1.000000" );

    }

    @Test
    public void testDoubleThreeDP() {

        checkDoubleDP( 3, 1234567890123.123456, "1234567890123.123" );
        checkDoubleDP( 3, 12345.123, "12345.123" );

        checkDoubleDP( 3, 123456789098765.123, "123456789098765.125" );
        checkDoubleDP( 3, 123456789098765.123456789, "123456789098765.125" );
        checkDoubleDP( 3, 0, "0.000" );
        checkDoubleDP( 3, 12345, "12345.000" );
        checkDoubleDP( 3, 12345.765432, "12345.765" );
        checkDoubleDP( 3, 123456789.987654, "123456789.987" );
        checkDoubleDP( 3, 123456789.000001, "123456789.000" );
        checkDoubleDP( 3, 123456789.123456789, "123456789.123" );
        checkDoubleDP( 3, 123456789.987654321, "123456789.987" );
        checkDoubleDP( 3, 1234567890.123456789, "1234567890.123" );
        checkDoubleDP( 3, 1234567890.987654321, "1234567890.987" );
        checkDoubleDP( 3, 12345678909876.987654321, "12345678909876.988" );
        checkDoubleDP( 3, 123456789098765.987654321, "123456789098765.984" );
        checkDoubleDP( 3, 0.000001, "0.000" );
        checkDoubleDP( 3, 1.000001, "1.000" );
        checkDoubleDP( 3, 0.0000001, "0.000" );
        checkDoubleDP( 3, 1.0000001, "1.000" );
        checkDoubleDP( 3, 0.00000001, "0.000" );
        checkDoubleDP( 3, 1.00000001, "1.000" );
        checkDoubleDP( 3, 0.000000001, "0.000" );
        checkDoubleDP( 3, 1.000000001, "1.000" );
        checkDoubleDP( 3, 0.0000000001, "0.000" );
        checkDoubleDP( 3, 1.0000000001, "1.000" );
        checkDoubleDP( 3, 0.00000000001, "0.000" );
        checkDoubleDP( 3, 1.00000000001, "1.000" );
    }

    @Test
    public void testDoubleTwoDP() {

        checkDoubleDP( 2, 0, "0.00" );
        checkDoubleDP( 2, 1234567890123.123456, "1234567890123.12" );
        checkDoubleDP( 2, 12345, "12345.00" );
        checkDoubleDP( 2, 12345.123, "12345.12" );
        checkDoubleDP( 2, 12345.765432, "12345.76" );
        checkDoubleDP( 2, 123456789.987654, "123456789.98" );
        checkDoubleDP( 2, 123456789.000001, "123456789.00" );
        checkDoubleDP( 2, 123456789.123456789, "123456789.12" );
        checkDoubleDP( 2, 123456789.987654321, "123456789.98" );
        checkDoubleDP( 2, 1234567890.123456789, "1234567890.12" );
        checkDoubleDP( 2, 1234567890.987654321, "1234567890.98" );
        checkDoubleDP( 2, 123456789098765.123456789, "123456789098765.12" );
        checkDoubleDP( 2, 123456789098765.987654321, "123456789098765.98" );
        checkDoubleDP( 2, 0.000001, "0.00" );
        checkDoubleDP( 2, 1.000001, "1.00" );
        checkDoubleDP( 2, 0.0000001, "0.00" );
        checkDoubleDP( 2, 1.0000001, "1.00" );
        checkDoubleDP( 2, 0.00000001, "0.00" );
        checkDoubleDP( 2, 1.00000001, "1.00" );
        checkDoubleDP( 2, 0.000000001, "0.00" );
        checkDoubleDP( 2, 1.000000001, "1.00" );
        checkDoubleDP( 2, 0.0000000001, "0.00" );
        checkDoubleDP( 2, 1.0000000001, "1.00" );
        checkDoubleDP( 2, 0.00000000001, "0.00" );
        checkDoubleDP( 2, 1.00000000001, "1.00" );
    }

    @Test
    public void testDoubleZ() {

        checkDoubleZ( -425147808028867D, "-425147808028867" );
        checkDoubleZ( 0, "0.0" );

        checkDoubleZ( Constants.UNSET_DOUBLE, "null" );
        checkDoubleZ( Constants.UNSET_LONG, "null" );
        checkDoubleZ( 1234567890123456.123456, "1234567890123456" );
        checkDoubleZ( 12345678901.123456, "12345678901.123456" );
        checkDoubleZ( Long.MAX_VALUE, "9223372036854775807" );

        checkDoubleZ( 12345, "12345.0" );
        checkDoubleZ( 12345.123, "12345.123" );
        checkDoubleZ( 12345.765432, "12345.765432" );
        checkDoubleZ( 123456789.000001, "123456789.000001" );
        checkDoubleZ( 0.000001, "0.000001" );
        checkDoubleZ( 1.000001, "1.000001" );
    }

    @Test
    public void testDoubleZeroDP() {
        checkDoubleDP( 0, 1234567890123.123456, "1234567890123" );

        checkDoubleDP( 0, Long.MAX_VALUE, "9223372036854775807" );
        checkDoubleDP( 0, Constants.UNSET_DOUBLE, "" );
        checkDoubleDP( 0, Constants.UNSET_LONG, "" );

        checkDoubleDP( 0, 0, "0" );
        checkDoubleDP( 0, 12345, "12345" );
        checkDoubleDP( 0, 12345.123, "12345" );
        checkDoubleDP( 0, 12345.765432, "12345" );
        checkDoubleDP( 0, 123456789.987654, "123456789" );
        checkDoubleDP( 0, 123456789.000001, "123456789" );
        checkDoubleDP( 0, 123456789.123456789, "123456789" );
        checkDoubleDP( 0, 123456789.987654321, "123456789" );
        checkDoubleDP( 0, 1234567890.123456789, "1234567890" );
        checkDoubleDP( 0, 1234567890.987654321, "1234567890" );
        checkDoubleDP( 0, 123456789098765.123456789, "123456789098765" );
        checkDoubleDP( 0, 123456789098765.987654321, "123456789098765" );
        checkDoubleDP( 0, 0.000001, "0" );
        checkDoubleDP( 0, 1.000001, "1" );
        checkDoubleDP( 0, 0.0000001, "0" );
        checkDoubleDP( 0, 1.0000001, "1" );
        checkDoubleDP( 0, 0.00000001, "0" );
        checkDoubleDP( 0, 1.00000001, "1" );
        checkDoubleDP( 0, 0.000000001, "0" );
        checkDoubleDP( 0, 1.000000001, "1" );
        checkDoubleDP( 0, 0.0000000001, "0" );
        checkDoubleDP( 0, 1.0000000001, "1" );
        checkDoubleDP( 0, 0.00000000001, "0" );
        checkDoubleDP( 0, 1.00000000001, "1" );
    }

    @Test
    public void testHex() {
        ReusableString s = new ReusableString();

        String testString = "ABCDEFGHIJ";
        byte[] buf        = testString.getBytes();

        s.appendReadableHEX( buf, 1, 3 );

        assertEquals( "  B  C  D", s.toString() );
    }

    @Test
    public void testInt() {

        checkInt( 0 );
        checkInt( 12345 );
        checkInt( Integer.MAX_VALUE );
    }

    @Test
    public void testReverse() {
        checkReverse( "A", "A" );
        checkReverse( "AB", "BA" );
        checkReverse( "ABC", "CBA" );
        checkReverse( "ABCD", "DCBA" );
        checkReverse( "ABCDE", "EDCBA" );
    }

    @Test public void testStrEq() {
        ReusableString str = new ReusableString( "XYLD.USD.XLON.CHIX" );

        assertTrue( str.equals( "XYLD.USD.XLON.CHIX" ) );
    }

    @Test
    public void testSubstring() {
        ReusableString s   = new ReusableString( "abc.def" );
        int            idx = s.indexOf( '.' );
        assertEquals( 3, idx );
        ReusableString out = new ReusableString();

        s.substring( out, idx + 1 );
        assertEquals( "def", out.toString() );

        s.substring( out, 6, 7 );
        assertEquals( "f", out.toString() );

        s.substring( out, 7, 7 );
        assertEquals( "", out.toString() );

        try {
            s.substring( out, 8, 9 );
            fail( "expected exception" );
        } catch( Exception e ) { /* expected */ }

        try {
            s.substring( out, 6, 9 );
            fail( "expected exception" );
        } catch( Exception e ) { /* expected */ }

        try {
            s.substring( out, 3, 2 );
            fail( "expected exception" );
        } catch( Exception e ) { /* expected */ }
    }

    private void checkDelete( String val, byte delChar, String expected ) {
        ReusableString s = new ReusableString();
        s.append( val );
        s.delete( delChar );

        assertEquals( expected, s.toString() );
    }

    private void checkDouble( double val, String exp ) {
        ReusableString s = new ReusableString();
        s.append( val );
        assertEquals( exp, s.toString() );
    }

    private void checkDoubleDP( int dp, double val, String exp ) {
        ReusableString s = new ReusableString();
        s.append( val, dp );
        assertEquals( exp, s.toString() );
    }

    private void checkDoubleZ( double val, String exp ) {
        ReusableString s = new ReusableString();
        s.appendZ( val, true );
        assertEquals( exp, s.toString() );
    }

    private void checkInt( int val ) {
        ReusableString s = new ReusableString();
        s.append( val );

        assertEquals( "" + val, s.toString() );
    }

    private void checkReverse( String val, String expected ) {
        ReusableString s = new ReusableString();
        s.append( val );
        s.reverse();

        assertEquals( expected, s.toString() );
    }

    private void perfDoubleDP( int dp, double val ) {
        _s.reset();
        _s.append( val, dp );
    }
}
