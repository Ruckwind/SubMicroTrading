/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

import com.rr.core.codec.binary.BinaryBigEndianDecoderUtils;
import com.rr.core.codec.binary.BinaryBigEndianEncoderUtils;
import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.Constants;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestBinaryCodecUtils extends BaseTestCase {

    @Test
    public void testBoolean() {
        byte[]                      buf = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklnopqrstuvwxyz0123456789".getBytes();
        BinaryBigEndianEncoderUtils beu = new BinaryBigEndianEncoderUtils( buf, 3 );

        beu.start();
        beu.encodeBool( true );
        beu.encodeBool( false );
        beu.end();

        assertEquals( 2, beu.getLength() );

        BinaryBigEndianDecoderUtils bdu = new BinaryBigEndianDecoderUtils();
        bdu.start( buf, 3, buf.length );
        assertEquals( true, bdu.decodeBool() );
        assertEquals( false, bdu.decodeBool() );
        bdu.end();

        assertEquals( 'C', buf[ 2 ] );
        assertEquals( 'B', buf[ 1 ] );
    }

    @Test
    public void testInteger() {
        byte[]                      buf = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklnopqrstuvwxyz0123456789".getBytes();
        BinaryBigEndianEncoderUtils beu = new BinaryBigEndianEncoderUtils( buf, 3 );

        beu.start();
        beu.encodeInt( 0 );
        beu.encodeInt( Integer.MIN_VALUE );
        beu.encodeInt( Integer.MAX_VALUE );
        beu.encodeInt( 123 );
        beu.end();

        assertEquals( 16, beu.getLength() );

        BinaryBigEndianDecoderUtils bdu = new BinaryBigEndianDecoderUtils();
        bdu.start( buf, 3, buf.length );
        assertEquals( 0, bdu.decodeInt() );
        assertEquals( 0, bdu.decodeInt() ); // Integer.MIN_VALUE used as UNSET_VALUE which is encoded as zero
        assertEquals( Integer.MAX_VALUE, bdu.decodeInt() );
        assertEquals( 123, bdu.decodeInt() );
        bdu.end();
    }

    @Test
    public void testLong() {
        byte[]                      buf = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklnopqrstuvwxyz0123456789".getBytes();
        BinaryBigEndianEncoderUtils beu = new BinaryBigEndianEncoderUtils( buf, 3 );

        beu.start();
        beu.encodeLong( 0L );
        beu.encodeLong( Long.MIN_VALUE );
        beu.encodeLong( Long.MAX_VALUE );
        beu.encodeLong( 123456789054321L );
        beu.end();

        assertEquals( 32, beu.getLength() );

        BinaryBigEndianDecoderUtils bdu = new BinaryBigEndianDecoderUtils();
        bdu.start( buf, 3, buf.length );
        assertEquals( 0L, bdu.decodeLong() );
        assertEquals( 0L, bdu.decodeLong() ); // MIN_VALUE used as UNSET_VALUE which is encoded as zero
        assertEquals( Long.MAX_VALUE, bdu.decodeLong() );
        assertEquals( 123456789054321L, bdu.decodeLong() );
        bdu.end();
    }

    @Test
    public void testPrice() {
        byte[]                      buf = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789ABCDFGH".getBytes();
        BinaryBigEndianEncoderUtils beu = new BinaryBigEndianEncoderUtils( buf, 3 );

        beu.start();
        beu.encodePrice( 0.0 );
        beu.encodePrice( -Double.MAX_VALUE );
        beu.encodePrice( Double.MAX_VALUE );
        beu.encodePrice( Constants.UNSET_DOUBLE );
        beu.encodePrice( 0.000009 );
        beu.encodePrice( 123456789.000009 );
        beu.encodePrice( 123456789.0 );
        beu.encodePrice( 12345.6789 );
        beu.end();

        assertEquals( 64, beu.getLength() );

        BinaryBigEndianDecoderUtils bdu = new BinaryBigEndianDecoderUtils();
        bdu.start( buf, 3, buf.length );
        assertEquals( 0.0, bdu.decodePrice(), Constants.WEIGHT );
        assertEquals( -Double.MAX_VALUE, bdu.decodePrice(), Constants.WEIGHT ); //MIN_VALUE used as UNSET_VALUE which is encoded as zero
        assertEquals( Double.MAX_VALUE, bdu.decodePrice(), Constants.WEIGHT );
        assertEquals( 0.0, bdu.decodePrice(), Constants.WEIGHT );              // UNSET encoded as 0.0
        assertEquals( 0.000009, bdu.decodePrice(), Constants.WEIGHT );
        assertEquals( 123456789.000009, bdu.decodePrice(), Constants.WEIGHT );
        assertEquals( 123456789.0, bdu.decodePrice(), Constants.WEIGHT );
        assertEquals( 12345.6789, bdu.decodePrice(), Constants.WEIGHT );
        bdu.end();

        assertEquals( 'G', buf[ 67 ] );
    }
}
