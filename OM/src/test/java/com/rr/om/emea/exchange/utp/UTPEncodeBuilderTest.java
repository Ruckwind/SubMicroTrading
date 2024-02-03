/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.utp;

import com.rr.codec.emea.exchange.utp.UTPEncodeBuilderImpl;
import com.rr.core.lang.*;
import com.rr.core.model.Currency;
import com.rr.core.model.MultiByteLookup;
import com.rr.core.model.SecurityType;
import com.rr.core.model.TwoByteLookup;
import com.rr.core.utils.NumberUtils;
import com.rr.core.utils.Utils;
import com.rr.model.generated.internal.type.OrdRejReason;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.MathContext;

import static org.junit.Assert.assertEquals;

public class UTPEncodeBuilderTest extends BaseTestCase {

    private static final long KEEP_DECIMAL_PLACE_FACTOR = 1000000;

    private byte[]               _buf     = new byte[ 8192 ];
    private UTPEncodeBuilderImpl _builder = new UTPEncodeBuilderImpl( _buf, 0, new ViewString( "2" ) );

    private MathContext _mc;

    @Override
    public String toString() {
        return new String( _buf, 0, _builder.getNextFreeIdx() );
    }

    public void doEncodePrice( double start, double max, double deltaInc ) {
        BigDecimal delta = new BigDecimal( Double.toString( deltaInc ), _mc );

        double testVal = start;

        ReusableString expStrBuf = new ReusableString( 4 );

        while( testVal < max ) {
            doEncodePrice( testVal, expStrBuf );

            BigDecimal tmp = new BigDecimal( Double.toString( testVal ) );

            tmp     = tmp.add( delta );
            testVal = tmp.doubleValue();
        }
    }

    @Before
    public void setUp() {
        _mc = new MathContext( 10 );
    }

    @Test
    public void testEncodeBool() {
        doEncodeBool( true );
        doEncodeBool( false );
    }

    @Test
    public void testEncodeByte() {
        doEncodeByte( 'A' );
        doEncodeByte( 'Z' );
        doEncodeByte( 'a' );
        doEncodeByte( 'z' );
        doEncodeByte( '0' );
        doEncodeByte( '9' );
        doEncodeByte( '-' );
        doEncodeByte( '.' );
        doEncodeByte( '?' );
        doEncodeByte( '/' );
    }

    @Test
    public void testEncodeInt() {
        doEncodeInt( -1 );
        doEncodeInt( -1000 );
        doEncodeInt( 0 );
        doEncodeInt( Integer.MAX_VALUE );
        doEncodeInt( Integer.MIN_VALUE + 1 );
        doEncodeInt( Integer.MIN_VALUE );
        doEncodeInt( 1000 );
    }

    @Test
    public void testEncodeLong() {
        doEncodeLong( -1 );
        doEncodeLong( -1000 );
        doEncodeLong( 99999 );
        doEncodeLong( 98765432101234L );
        doEncodeLong( 0 );
        doEncodeLong( Long.MAX_VALUE );
        doEncodeLong( Long.MIN_VALUE + 1 );
        doEncodeLong( Long.MIN_VALUE );
        doEncodeLong( 1000 );
    }

    @Test
    public void testEncodeMultiByte() {

        doEncodeMultiByte( SecurityType.Equity );
        doEncodeMultiByte( SecurityType.Future );
        doEncodeMultiByte( SecurityType.Unknown );

        doEncodeMultiByte( Currency.Other );
        doEncodeMultiByte( Currency.GBP );
    }

    @Test
    public void testEncodePrice() {

//        doEncodePrice( 44, 0.0001, 9999.0, 0.0001 );  // whole required range takes hours to run

        doEncodePrice( 0.0001, 2.0, 0.0001 );
        doEncodePrice( 999.0001, 1002.0, 0.0001 );
        doEncodePrice( 9999.0001, 10011.9, 0.0001 );

        doEncodeNullPrice( Constants.UNSET_DOUBLE, new ReusableString( 4 ) );
    }

    @Test
    public void testEncodeReusableString() {

        ReusableString r1 = new ReusableString( "abcde" );
        ReusableString r2 = new ReusableString( "klmnopqrstuvwxyzABCD" );
        ReusableString r3 = new ReusableString( "" );

        doEncodeString( r1, "abcde" );
        doEncodeString( r2, "klmnopqrstuvwxyzABCD" );
        doEncodeString( r3, "" );
    }

    @Test
    public void testEncodeTwoByte() {
        doEncodeTwoByte( OrdRejReason.ExchangeClosed );
        doEncodeTwoByte( OrdRejReason.Other );
        doEncodeTwoByte( OrdRejReason.Unknown );

        doEncodeTwoByte( OrdRejReason.UnknownAccounts );
    }

    @Test
    public void testEncodeUTCTimestamp() {
        doEncodeUTCTimestamp( 0, 0, 0, 60 );
        doEncodeUTCTimestamp( 0, 0, 0, 123 );
        doEncodeUTCTimestamp( 0, 0, 0, 0 );
        doEncodeUTCTimestamp( 0, 0, 0, 1 );
        doEncodeUTCTimestamp( 0, 0, 1, 0 );
        doEncodeUTCTimestamp( 1, 2, 3, 4 );
        doEncodeUTCTimestamp( 12, 0, 0, 1 );
        doEncodeUTCTimestamp( 23, 59, 59, 998 );
        doEncodeUTCTimestamp( 23, 59, 59, 999 );
    }

    @Test
    public void testEncodeViewString() {

        byte[] buf = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-?=+/.\"\'\\0123456789".getBytes();

        ViewString vStart = new ViewString( buf, 0, 5 );
        ViewString vMid   = new ViewString( buf, 10, 20 );
        ViewString vAll   = new ViewString( buf, 0, buf.length );

        ViewString vEnd   = new ViewString( buf, buf.length - 5, 5 );
        ViewString vEmpty = new ViewString( buf, 5, 0 );

        doEncodeString( vStart, "abcde" );
        doEncodeString( vMid, "klmnopqrstuvwxyzABCD" );
        doEncodeString( vEnd, "56789" );
        doEncodeString( vEmpty, "" );
        doEncodeString( vAll, new String( buf ) );
    }

    private void chk( byte[] expected ) {
        assertEquals( _builder.getOffset() + expected.length, _builder.getNextFreeIdx() );

        byte[] res = _builder.getBuffer();
        for ( int i = 0; i < expected.length; ++i ) {
            byte exp   = expected[ i ];
            byte found = res[ i + _builder.getOffset() ];
            assertEquals( "fail at idx " + i + " expected=" + exp + ", found=" + found, exp, found );
        }
    }

    private void doEncodeBool( boolean val ) {
        _builder.start();

        char cVal = (val) ? '1' : '0';

        byte[] expected = { (byte) cVal };

        _builder.encodeBool( val );

        chk( expected );
    }

    private void doEncodeByte( char cVal ) {
        _builder.start();

        byte[] expected = { (byte) cVal };

        _builder.encodeByte( (byte) cVal );

        chk( expected );
    }

    private void doEncodeInt( int val ) {
        _builder.start();

        byte b1 = (byte) (val >>> 24);
        byte b2 = (byte) ((val >>> 16) & 0xFF);
        byte b3 = (byte) ((val >>> 8) & 0xFF);
        byte b4 = (byte) (val & 0xFF);

        byte[] expected = { b1, b2, b3, b4 };

        if ( val == Constants.UNSET_INT ) {
            expected = new byte[] { 0x00, 0x00, 0x00, 0x00 };
        }

        _builder.encodeInt( val );

        chk( expected );
    }

    private void doEncodeLong( long val ) {
        _builder.start();

        byte b1 = (byte) (val >>> 56);
        byte b2 = (byte) ((val >>> 48) & 0xFF);
        byte b3 = (byte) ((val >>> 40) & 0xFF);
        byte b4 = (byte) ((val >>> 32) & 0xFF);
        byte b5 = (byte) ((val >>> 24) & 0xFF);
        byte b6 = (byte) ((val >>> 16) & 0xFF);
        byte b7 = (byte) ((val >>> 8) & 0xFF);
        byte b8 = (byte) (val & 0xFF);

        byte[] expected = { b1, b2, b3, b4, b5, b6, b7, b8 };

        if ( val == Constants.UNSET_LONG ) {
            expected = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
        }

        _builder.encodeLong( val );

        chk( expected );
    }

    private void doEncodeMultiByte( MultiByteLookup code ) {
        _builder.start();

        String expected = new String( code.getVal() );

        _builder.encodeStringFixedWidth( code.getVal(), 0, expected.length() );

        chk( expected.getBytes() );
    }

    private void doEncodeNullPrice( double val, ReusableString expStrBuf ) {
        _builder.start();
        _builder.encodePrice( val );
        getExpectedPrice( val, expStrBuf );
        chk( expStrBuf.getBytes() );
    }

    private void doEncodePrice( double val, ReusableString expStrBuf ) {

        _builder.start();
        _builder.encodePrice( val );
        getExpectedPrice( val, expStrBuf );
        chk( expStrBuf.getBytes() );

        double multUp = (val + Constants.WEIGHT) * 100;
        _builder.start();
        _builder.encodePrice( multUp );
        getExpectedPrice( val, 100, expStrBuf );
        chk( expStrBuf.getBytes() );

        double multDown = (val + Constants.WEIGHT) * 0.01;
        _builder.start();
        _builder.encodePrice( multDown );
        getExpectedPrice( val, 0.01, expStrBuf );
        chk( expStrBuf.getBytes() );
    }

    private void doEncodeString( ZString zstr, String expectedVal ) {
        _builder.start();

        String expected = expectedVal + (char) 0;

        _builder.encodeStringFixedWidth( zstr, expectedVal.length() + 1 );

        chk( expected.getBytes() );

        _builder.start();

        _builder.encodeStringFixedWidth( zstr.getBytes(), zstr.getOffset(), expectedVal.length() );

        chk( expectedVal.getBytes() );
    }

    private void doEncodeTwoByte( TwoByteLookup code ) {
        _builder.start();

        byte[] val      = code.getVal();
        byte[] expected = { val[ 0 ], (val.length == 1) ? (byte) 0 : val[ 1 ] };

        _builder.encodeStringFixedWidth( code.getVal(), 0, 2 );

        chk( expected );
    }

    private void doEncodeUTCTimestamp( int hh, int mm, int sec, int ms ) {

        int msFromStartOfDayUTC = (((((hh * 60) + mm) * 60) + sec) * 1000) + ms;

        TimeUtils tzCalc = TimeUtilsFactory.createTimeUtils();
        tzCalc.setTodayAsNow();

        long now         = ClockFactory.get().currentTimeMillis();
        long time        = CommonTimeUtils.unixTimeToInternalTime( now );
        long midnightUTC = now - time;

        int unixTime = (int) ((midnightUTC + msFromStartOfDayUTC) / 1000);
        _builder.setTimeUtils( tzCalc );
        _builder.start();

        byte b1 = (byte) (unixTime >>> 24);
        byte b2 = (byte) ((unixTime >>> 16) & 0xFF);
        byte b3 = (byte) ((unixTime >>> 8) & 0xFF);
        byte b4 = (byte) (unixTime & 0xFF);

        byte[] expected = { b1, b2, b3, b4 };

        _builder.encodeTimestampUTC( msFromStartOfDayUTC );

        chk( expected );
    }

    private void getExpectedPrice( double dval, ReusableString expStrBuf ) {
        if ( Utils.isNull( dval ) ) {
            expStrBuf.reset();
            expStrBuf.append( (byte) 0x00 );
            expStrBuf.append( (byte) 0x00 );
            expStrBuf.append( (byte) 0x00 );
            expStrBuf.append( (byte) 0x00 );
            return;
        }

        int val = NumberUtils.priceToExternalInt6DP( dval );

        expStrBuf.reset();

        byte b1 = (byte) (val >>> 24);
        byte b2 = (byte) ((val >>> 16) & 0xFF);
        byte b3 = (byte) ((val >>> 8) & 0xFF);
        byte b4 = (byte) val;

        byte[] expected = { b1, b2, b3, b4 };

        expStrBuf.append( expected );
    }

    private void getExpectedPrice( double dval, double factor, ReusableString expStrBuf ) {
        if ( Utils.isNull( dval ) ) {
            expStrBuf.reset();
            expStrBuf.append( (byte) 0x00 );
            expStrBuf.append( (byte) 0x00 );
            expStrBuf.append( (byte) 0x00 );
            expStrBuf.append( (byte) 0x00 );
            return;
        }

        int val;
        if ( dval > 0 ) {
            val = (int) ((dval + Constants.WEIGHT) * KEEP_DECIMAL_PLACE_FACTOR * factor);
        } else {
            val = (int) ((dval - Constants.WEIGHT) * KEEP_DECIMAL_PLACE_FACTOR * factor);
        }

        expStrBuf.reset();

        char b1 = (char) (val >>> 24);
        char b2 = (char) ((val >>> 16) & 0xFF);
        char b3 = (char) ((val >>> 8) & 0xFF);
        char b4 = (char) (val & 0xFF);

        String expected = "" + b1 + b2 + b3 + b4;

        if ( val == Constants.UNSET_INT ) {
            expected = "\000\000\000\000";
        }

        expStrBuf.append( expected );
    }
}
