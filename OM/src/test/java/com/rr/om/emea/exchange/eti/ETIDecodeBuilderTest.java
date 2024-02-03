/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.eti;

import com.rr.codec.emea.exchange.eti.ETIDecodeBuilderImpl;
import com.rr.codec.emea.exchange.eti.ETIEncodeBuilderImpl;
import com.rr.core.lang.*;
import com.rr.core.model.Currency;
import com.rr.core.model.MultiByteLookup;
import com.rr.core.model.SecurityType;
import com.rr.core.utils.Utils;
import com.rr.om.emea.exchange.eti.gateway.ETIGatewayController;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.MathContext;

import static org.junit.Assert.assertEquals;

public class ETIDecodeBuilderTest extends BaseTestCase {

    private ETIDecodeBuilderImpl _builder = new ETIDecodeBuilderImpl();
    private byte[]               _buf     = new byte[ 8192 ];

    private MathContext _mc;

    @Override
    public String toString() {
        return new String( _buf, 0, _builder.getNextFreeIdx() );
    }

    public void doDecodePrice( double start, double max, double deltaInc ) {
        BigDecimal delta = new BigDecimal( Double.toString( deltaInc ), _mc );

        double testVal = start;

        ReusableString expStrBuf = new ReusableString( 4 );

        while( testVal < max ) {
            doDecodePrice( testVal, expStrBuf );

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
    public void testDecodeByte() {
        doDecodeByte( (byte) 'A' );
        doDecodeByte( (byte) 'Z' );
        doDecodeByte( (byte) 'a' );
        doDecodeByte( (byte) 'z' );
        doDecodeByte( (byte) '0' );
        doDecodeByte( (byte) '9' );
        doDecodeByte( (byte) '-' );
        doDecodeByte( (byte) '.' );
        doDecodeByte( (byte) '?' );
        doDecodeByte( (byte) '/' );
        doDecodeByte( Constants.UNSET_BYTE );
    }

    @Test
    public void testDecodeChar() {
        doDecodeChar( 'A' );
        doDecodeChar( 'Z' );
        doDecodeChar( 'a' );
        doDecodeChar( 'z' );
        doDecodeChar( '0' );
        doDecodeChar( '9' );
        doDecodeChar( '-' );
        doDecodeChar( '.' );
        doDecodeChar( '?' );
        doDecodeChar( '/' );
        doDecodeChar( Constants.UNSET_CHAR );
    }

    @Test
    public void testDecodeInt() {
        doDecodeInt( -1 );
        doDecodeInt( -1000 );
        doDecodeInt( 0 );
        doDecodeInt( Integer.MAX_VALUE );
        doDecodeInt( Integer.MIN_VALUE + 1 );
        doDecodeInt( Constants.UNSET_INT );
        doDecodeInt( 1000 );
    }

    @Test
    public void testDecodeLong() {
        doDecodeLong( -1 );
        doDecodeLong( -1000 );
        doDecodeLong( 99999 );
        doDecodeLong( 98765432101234L );
        doDecodeLong( 0 );
        doDecodeLong( Long.MAX_VALUE );
        doDecodeLong( Long.MIN_VALUE + 1 );
        doDecodeLong( Constants.UNSET_LONG );
        doDecodeLong( 1000 );
    }

    @Test
    public void testDecodeMultiByte() {

        doDecodeMultiByte( SecurityType.Equity );
        doDecodeMultiByte( SecurityType.Future );
        doDecodeMultiByte( SecurityType.Unknown );

        doDecodeMultiByte( Currency.Other );
        doDecodeMultiByte( Currency.GBP );
    }

    @Test
    public void testDecodePrice() {

        doDecodePrice( 0.2148, new ReusableString( 8 ) );

        doDecodePrice( 0.0001, 2.0, 0.0001 );
        doDecodePrice( 999.0001, 1002.0, 0.0001 );
        doDecodePrice( 9999.0001, 10011.9, 0.0001 );

        byte[] expected = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x80 };
        doDecodeNullPrice( Constants.UNSET_DOUBLE, expected );
    }

    @Test
    public void testDecodeReusableString() {

        ReusableString r1 = new ReusableString( "abcde" );
        ReusableString r2 = new ReusableString( "klmnopqrst\000uvwxyzABCD\000\000" );
        ReusableString r3 = new ReusableString( "" );

        doDecodeString( r1, "abcde" );
        doDecodeString( r2, "klmnopqrst\000uvwxyzABCD" );
        doDecodeString( r3, "" );
    }

    @Test
    public void testDecodeShort() {
        doDecodeShort( (short) -1 );
        doDecodeShort( (short) -1000 );
        doDecodeShort( (short) 0 );
        doDecodeShort( Short.MAX_VALUE );
        doDecodeShort( (short) (Short.MIN_VALUE + 1) );
        doDecodeShort( Constants.UNSET_SHORT );
        doDecodeShort( (short) 1000 );
    }

    @Test
    public void testDecodeUByte() {
        doDecodeUByte( (byte) 'A' );
        doDecodeUByte( (byte) 'Z' );
        doDecodeUByte( (byte) 'a' );
        doDecodeUByte( (byte) 'z' );
        doDecodeUByte( (byte) '0' );
        doDecodeUByte( (byte) '9' );
        doDecodeUByte( (byte) '-' );
        doDecodeUByte( (byte) '.' );
        doDecodeUByte( (byte) '?' );
        doDecodeUByte( (byte) '/' );
        doDecodeUByte( Constants.UNSET_BYTE );
    }

    @Test
    public void testDecodeUInt() {
        doDecodeUInt( 0 );
        doDecodeUInt( Integer.MAX_VALUE );
        doDecodeUInt( Integer.MIN_VALUE + 1 );
        doDecodeUInt( Constants.UNSET_INT );
        doDecodeUInt( 1000 );
    }

    @Test
    public void testDecodeULong() {
        doDecodeULong( 99999 );
        doDecodeULong( 98765432101234L );
        doDecodeULong( 0 );
        doDecodeULong( Long.MAX_VALUE );
        doDecodeULong( Constants.UNSET_LONG );
        doDecodeULong( 1000 );
    }

    @Test
    public void testDecodeUShort() {
        doDecodeUShort( (short) 0 );
        doDecodeUShort( Short.MAX_VALUE );
        doDecodeUShort( (short) (Short.MIN_VALUE + 1) );
        doDecodeUShort( Constants.UNSET_SHORT );
        doDecodeUShort( (short) 1000 );
    }

    @Test
    public void testDecodeUTCTimestamp() {
        doDecodeUTCTimestamp( 0, 0, 0, 60 );
        doDecodeUTCTimestamp( 0, 0, 0, 123 );
        doDecodeUTCTimestamp( 0, 0, 0, 0 );
        doDecodeUTCTimestamp( 0, 0, 0, 1 );
        doDecodeUTCTimestamp( 0, 0, 1, 0 );
        doDecodeUTCTimestamp( 1, 2, 3, 4 );
        doDecodeUTCTimestamp( 12, 0, 0, 1 );
        doDecodeUTCTimestamp( 23, 59, 59, 998 );
        doDecodeUTCTimestamp( 23, 59, 59, 999 );
    }

    @Test
    public void testDecodeViewString() {

        byte[] buf = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-?=+/.\"\'\\0123456789".getBytes();

        ViewString vStart = new ViewString( buf, 0, 5 );
        ViewString vMid   = new ViewString( buf, 10, 20 );
        ViewString vAll   = new ViewString( buf, 0, buf.length );

        ViewString vEnd   = new ViewString( buf, buf.length - 5, 5 );
        ViewString vEmpty = new ViewString( buf, 5, 0 );

        doDecodeString( vStart, "abcde" );
        doDecodeString( vMid, "klmnopqrstuvwxyzABCD" );
        doDecodeString( vEnd, "56789" );
        doDecodeString( vEmpty, "" );
        doDecodeString( vAll, new String( buf ) );
    }

    @Test
    public void testIPAddress() {
        int addr = -1055041150;

        ReusableString dest = new ReusableString();

        ETIGatewayController.intIPtoFullAddr( dest, addr );

        assertEquals( "193.29.89.130", dest.toString() );
    }

    private void doDecodeByte( byte cVal ) {

        if ( cVal == Constants.UNSET_BYTE ) {
            byte[] expected = { (byte) 0x80 };
            _builder.start( expected, 0, expected.length );
            byte val = _builder.decodeByte();
            assertEquals( cVal, val );
        } else {
            byte[] expected = { cVal };
            _builder.start( expected, 0, expected.length );
            byte val = _builder.decodeByte();
            assertEquals( cVal, val );
        }
    }

    private void doDecodeChar( char cVal ) {

        if ( cVal == Constants.UNSET_CHAR ) {
            byte[] expected = { (byte) 0x00 };
            _builder.start( expected, 0, expected.length );
            byte ch = _builder.decodeChar();
            assertEquals( cVal, ch );
        } else {
            byte[] expected = { (byte) cVal };
            _builder.start( expected, 0, expected.length );
            byte ch = _builder.decodeChar();
            assertEquals( cVal, ch );
        }
    }

    private void doDecodeInt( int val ) {

        byte b1 = (byte) (val >>> 24);
        byte b2 = (byte) ((val >>> 16) & 0xFF);
        byte b3 = (byte) ((val >>> 8) & 0xFF);
        byte b4 = (byte) (val & 0xFF);

        byte[] expected = { b4, b3, b2, b1 };
        _builder.start( expected, 0, expected.length );

        int decoded = _builder.decodeInt();

        assertEquals( val, decoded );
    }

    private void doDecodeLong( long val ) {

        byte b1 = (byte) (val >>> 56);
        byte b2 = (byte) ((val >>> 48) & 0xFF);
        byte b3 = (byte) ((val >>> 40) & 0xFF);
        byte b4 = (byte) ((val >>> 32) & 0xFF);
        byte b5 = (byte) ((val >>> 24) & 0xFF);
        byte b6 = (byte) ((val >>> 16) & 0xFF);
        byte b7 = (byte) ((val >>> 8) & 0xFF);
        byte b8 = (byte) (val & 0xFF);

        byte[] expected = { b8, b7, b6, b5, b4, b3, b2, b1 };
        _builder.start( expected, 0, expected.length );

        long decoded = _builder.decodeLong();

        assertEquals( val, decoded );
    }

    private void doDecodeMultiByte( MultiByteLookup code ) {

        byte[] expected = new String( code.getVal() ).getBytes();
        _builder.start( expected, 0, expected.length );

        ReusableString dest = new ReusableString();
        _builder.decodeStringFixedWidth( dest, expected.length );

        assertEquals( new String( code.getVal() ), dest.toString() );
    }

    private void doDecodeNullPrice( double val, byte[] expected ) {
        _builder.start( expected, 0, expected.length );

        double decoded = _builder.decodePrice();

        assertEquals( val, decoded, 0.0000000005 );
    }

    private void doDecodePrice( double val, ReusableString expStrBuf ) {

        getExpectedPrice( val, expStrBuf );
        _builder.start( expStrBuf.getBytes(), 0, expStrBuf.getBytes().length );
        double decoded = _builder.decodePrice();
        assertEquals( val, decoded, 0.000000005 );

        double multUp = (val + Constants.WEIGHT) * 100;
        getExpectedPrice( val, 100, expStrBuf );
        _builder.start( expStrBuf.getBytes(), 0, expStrBuf.getBytes().length );
        decoded = _builder.decodePrice();
        assertEquals( multUp, decoded, 0.000000005 );

        double multDown = (val + Constants.WEIGHT) * 0.01;
        getExpectedPrice( val, 0.01, expStrBuf );
        _builder.start( expStrBuf.getBytes(), 0, expStrBuf.getBytes().length );
        decoded = _builder.decodePrice();
        assertEquals( multDown, decoded, 0.000000005 );
    }

    private void doDecodeShort( short val ) {

        byte b1 = (byte) ((val >>> 8) & 0xFF);
        byte b2 = (byte) (val & 0xFF);

        byte[] expected = { b2, b1 };
        _builder.start( expected, 0, expected.length );

        short decoded = _builder.decodeShort();

        assertEquals( val, decoded );
    }

    private void doDecodeString( ZString src, String expectedVal ) {
        byte[] expected = expectedVal.getBytes();

        int offset = src.getOffset();

        _builder.start( src.getBytes(), offset, src.length() + offset );

        ReusableString dest = new ReusableString();
        _builder.decodeStringFixedWidth( dest, expected.length );

        assertEquals( expectedVal, dest.toString() );
    }

    private void doDecodeUByte( byte cVal ) {

        if ( cVal == Constants.UNSET_BYTE ) {
            byte[] expected = { (byte) 0xFF };
            _builder.start( expected, 0, expected.length );
            byte val = _builder.decodeUByte();
            assertEquals( cVal, val );
        } else {
            byte[] expected = { cVal };
            _builder.start( expected, 0, expected.length );
            byte val = _builder.decodeUByte();
            assertEquals( cVal, val );
        }
    }

    private void doDecodeUInt( int val ) {

        byte b1 = (byte) (val >>> 24);
        byte b2 = (byte) ((val >>> 16) & 0xFF);
        byte b3 = (byte) ((val >>> 8) & 0xFF);
        byte b4 = (byte) (val & 0xFF);

        byte[] expected = { b4, b3, b2, b1 };
        _builder.start( expected, 0, expected.length );

        int decoded = _builder.decodeUInt();

        assertEquals( val, decoded );
    }

    private void doDecodeULong( long val ) {

        byte b1 = (byte) (val >>> 56);
        byte b2 = (byte) ((val >>> 48) & 0xFF);
        byte b3 = (byte) ((val >>> 40) & 0xFF);
        byte b4 = (byte) ((val >>> 32) & 0xFF);
        byte b5 = (byte) ((val >>> 24) & 0xFF);
        byte b6 = (byte) ((val >>> 16) & 0xFF);
        byte b7 = (byte) ((val >>> 8) & 0xFF);
        byte b8 = (byte) (val & 0xFF);

        byte[] expected = { b8, b7, b6, b5, b4, b3, b2, b1 };
        _builder.start( expected, 0, expected.length );

        long decoded = _builder.decodeULong();

        assertEquals( val, decoded );
    }

    private void doDecodeUShort( short val ) {

        byte b1 = (byte) ((val >>> 8) & 0xFF);
        byte b2 = (byte) (val & 0xFF);

        byte[] expected = { b2, b1 };
        _builder.start( expected, 0, expected.length );

        short decoded = _builder.decodeUShort();

        assertEquals( val, decoded );
    }

    private void doDecodeUTCTimestamp( int hh, int mm, int sec, int ms ) {

        int msFromStartOfDayUTC = (((((hh * 60) + mm) * 60) + sec) * 1000) + ms;

        TimeUtils tzCalc = TimeUtilsFactory.createTimeUtils();
        tzCalc.setTodayAsNow();

        long now         = ClockFactory.get().currentTimeMillis();
        long time        = CommonTimeUtils.unixTimeToInternalTime( now );
        long midnightUTC = now - time;

        long expTime = (midnightUTC + msFromStartOfDayUTC);
        _builder.setTimeUtils( tzCalc );

        byte b8 = (byte) (expTime >>> 56);
        byte b7 = (byte) ((expTime >>> 48) & 0xFF);
        byte b6 = (byte) ((expTime >>> 40) & 0xFF);
        byte b5 = (byte) ((expTime >>> 32) & 0xFF);
        byte b4 = (byte) ((expTime >>> 24) & 0xFF);
        byte b3 = (byte) ((expTime >>> 16) & 0xFF);
        byte b2 = (byte) ((expTime >>> 8) & 0xFF);
        byte b1 = (byte) (expTime & 0xFF);

        byte[] expected = { b1, b2, b3, b4, b5, b6, b7, b8 };
        _builder.start( expected, 0, expected.length );

        long decoded = _builder.decodeTimestampUTC();

        assertEquals( msFromStartOfDayUTC, decoded );
    }

    private void getExpectedPrice( double dval, ReusableString expStrBuf ) {
        if ( Utils.isNull( dval ) ) {
            expStrBuf.reset();
            expStrBuf.append( (byte) 0x00 );
            expStrBuf.append( (byte) 0x00 );
            expStrBuf.append( (byte) 0x00 );
            expStrBuf.append( (byte) 0x00 );
            expStrBuf.append( (byte) 0x00 );
            expStrBuf.append( (byte) 0x00 );
            expStrBuf.append( (byte) 0x00 );
            expStrBuf.append( (byte) 0x00 );
            return;
        }

        long val = ETIEncodeBuilderImpl.priceToExternalLong( dval );

        expStrBuf.reset();

        byte b8 = (byte) (val >>> 56);
        byte b7 = (byte) ((val >>> 48) & 0xFF);
        byte b6 = (byte) ((val >>> 40) & 0xFF);
        byte b5 = (byte) ((val >>> 32) & 0xFF);
        byte b4 = (byte) ((val >>> 24) & 0xFF);
        byte b3 = (byte) ((val >>> 16) & 0xFF);
        byte b2 = (byte) ((val >>> 8) & 0xFF);
        byte b1 = (byte) (val & 0xFF);

        byte[] expected = { b1, b2, b3, b4, b5, b6, b7, b8 };

        expStrBuf.append( expected );
    }

    private void getExpectedPrice( double dval, double factor, ReusableString expStrBuf ) {
        if ( Utils.isNull( dval ) ) {
            expStrBuf.reset();
            expStrBuf.append( (byte) 0x00 );
            expStrBuf.append( (byte) 0x00 );
            expStrBuf.append( (byte) 0x00 );
            expStrBuf.append( (byte) 0x00 );
            expStrBuf.append( (byte) 0x00 );
            expStrBuf.append( (byte) 0x00 );
            expStrBuf.append( (byte) 0x00 );
            expStrBuf.append( (byte) 0x00 );
            return;
        }

        long val;
        if ( dval > 0 ) {
            val = (long) ((dval + Constants.WEIGHT) * ETIDecodeBuilderImpl.KEEP_DECIMAL_PLACE_FACTOR * factor);
        } else {
            val = (long) ((dval - Constants.WEIGHT) * ETIDecodeBuilderImpl.KEEP_DECIMAL_PLACE_FACTOR * factor);
        }

        expStrBuf.reset();

        byte b8 = (byte) (val >>> 56);
        byte b7 = (byte) ((val >>> 48) & 0xFF);
        byte b6 = (byte) ((val >>> 40) & 0xFF);
        byte b5 = (byte) ((val >>> 32) & 0xFF);
        byte b4 = (byte) ((val >>> 24) & 0xFF);
        byte b3 = (byte) ((val >>> 16) & 0xFF);
        byte b2 = (byte) ((val >>> 8) & 0xFF);
        byte b1 = (byte) (val & 0xFF);

        byte[] expected = { b1, b2, b3, b4, b5, b6, b7, b8 };

        if ( val == Constants.UNSET_LONG ) {
            expStrBuf.append( "\000\000\000\000\000\000\000\000" );
        } else {
            expStrBuf.append( expected );
        }
    }
}
