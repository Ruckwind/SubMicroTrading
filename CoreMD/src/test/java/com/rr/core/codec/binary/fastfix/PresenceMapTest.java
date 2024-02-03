/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ViewString;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PresenceMapTest extends BaseTestCase {

    private FastFixDecodeBuilder _decoder = new FastFixDecodeBuilder();
    private FastFixBuilder       _encoder;
    private PresenceMapWriter    _mapWriter;
    private PresenceMapReader    _mapReader;

    private byte[] _buf = new byte[ 8192 ];

    @Override
    public String toString() {
        return new String( _buf, 0, _decoder.getNextFreeIdx() );
    }

    @Before
    public void setUp() {
        _encoder   = new FastFixBuilder( _buf, 0 );
        _mapWriter = new PresenceMapWriter( _encoder, 0, 1 );
        _mapReader = new PresenceMapReader();
    }

    @Test
    public void test1Bit() {
        _mapWriter.setCurrentField();
        _mapWriter.end();

        assertEquals( (byte) 0xC0, _buf[ 0 ] );

        _decoder.start( _buf, 0, _encoder.getCurrentIndex() );
        _mapReader.readMap( _decoder );

        assertEquals( 1, _decoder.getCurrentIndex() );

        assertTrue( _mapReader.isNextFieldPresent() );

        for ( int i = 0; i < 64; i++ ) {
            assertFalse( _mapReader.isNextFieldPresent() );
        }
    }

    @Test
    public void testEmpty() {
        _mapWriter.end();

        assertEquals( (byte) 0x80, _buf[ 0 ] );

        _decoder.start( _buf, 0, _encoder.getCurrentIndex() );
        _mapReader.readMap( _decoder );

        assertEquals( 1, _decoder.getCurrentIndex() );

        for ( int i = 0; i < 64; i++ ) {
            assertFalse( _mapReader.isNextFieldPresent() );
        }
    }

    @Test
    public void testSet28BitsFourBytes() {
        for ( int i = 0; i < 28; i++ ) {
            _mapWriter.setCurrentField();
        }
        _mapWriter.end();

        assertEquals( (byte) 0x7F, _buf[ 0 ] );
        assertEquals( (byte) 0x7F, _buf[ 1 ] );
        assertEquals( (byte) 0x7F, _buf[ 2 ] );
        assertEquals( (byte) 0xFF, _buf[ 3 ] );

        _decoder.start( _buf, 0, _encoder.getCurrentIndex() );
        _mapReader.readMap( _decoder );

        assertEquals( 4, _decoder.getCurrentIndex() );

        for ( int i = 0; i < 28; i++ ) {
            assertTrue( _mapReader.isNextFieldPresent() );
        }

        for ( int i = 0; i < 64; i++ ) {
            assertFalse( _mapReader.isNextFieldPresent() );
        }
    }

    @Test
    public void testSet7Bit() {
        _mapWriter.setCurrentField();
        _mapWriter.setCurrentField();
        _mapWriter.setCurrentField();
        _mapWriter.setCurrentField();
        _mapWriter.setCurrentField();
        _mapWriter.setCurrentField();
        _mapWriter.setCurrentField();
        _mapWriter.end();

        _decoder.start( _buf, 0, _encoder.getCurrentIndex() );
        assertEquals( (byte) 0xFF, _buf[ 0 ] );

        _mapReader.readMap( _decoder );

        assertEquals( 1, _decoder.getCurrentIndex() );

        for ( int i = 0; i < 7; i++ ) {
            assertTrue( _mapReader.isNextFieldPresent() );
        }

        for ( int i = 0; i < 64; i++ ) {
            assertFalse( _mapReader.isNextFieldPresent() );
        }
    }

    @Test
    public void testSet8BitsGrowTwoBytes() {

        _encoder.encodeString( new ViewString( "Z1" ) );

        _mapWriter.setCurrentField();
        _mapWriter.setCurrentField();
        _mapWriter.setCurrentField();
        _mapWriter.setCurrentField();
        _mapWriter.setCurrentField();
        _mapWriter.setCurrentField();
        _mapWriter.setCurrentField();
        _mapWriter.setCurrentField();
        _mapWriter.end();

        assertEquals( (byte) 0x7F, _buf[ 0 ] );
        assertEquals( (byte) 0xC0, _buf[ 1 ] );
        assertEquals( (byte) 'Z', _buf[ 2 ] );

        _decoder.start( _buf, 0, _encoder.getCurrentIndex() );

        _mapReader.readMap( _decoder );

        assertEquals( 2, _decoder.getCurrentIndex() );

        for ( int i = 0; i < 8; i++ ) {
            assertTrue( _mapReader.isNextFieldPresent() );
        }

        for ( int i = 0; i < 64; i++ ) {
            assertFalse( _mapReader.isNextFieldPresent() );
        }
    }

    @Test
    public void testSet8BitsTwoBytes() {
        _mapWriter.setCurrentField();
        _mapWriter.setCurrentField();
        _mapWriter.setCurrentField();
        _mapWriter.setCurrentField();
        _mapWriter.setCurrentField();
        _mapWriter.setCurrentField();
        _mapWriter.setCurrentField();
        _mapWriter.setCurrentField();
        _mapWriter.end();

        assertEquals( (byte) 0x7F, _buf[ 0 ] );
        assertEquals( (byte) 0xC0, _buf[ 1 ] );

        _decoder.start( _buf, 0, _encoder.getCurrentIndex() );

        _mapReader.readMap( _decoder );

        assertEquals( 2, _decoder.getCurrentIndex() );

        for ( int i = 0; i < 8; i++ ) {
            assertTrue( _mapReader.isNextFieldPresent() );
        }

        for ( int i = 0; i < 64; i++ ) {
            assertFalse( _mapReader.isNextFieldPresent() );
        }
    }

    @Test
    public void testSetLastof14BitsTwoBytes() {
        for ( int i = 0; i < 13; i++ ) {
            _mapWriter.clearCurrentField();
        }
        _mapWriter.setCurrentField();
        _mapWriter.end();

        assertEquals( (byte) 0x00, _buf[ 0 ] );
        assertEquals( (byte) 0x81, _buf[ 1 ] );

        _decoder.start( _buf, 0, _encoder.getCurrentIndex() );
        _mapReader.readMap( _decoder );

        assertEquals( 2, _decoder.getCurrentIndex() );

        for ( int i = 0; i < 13; i++ ) {
            assertFalse( _mapReader.isNextFieldPresent() );
        }

        assertTrue( _mapReader.isNextFieldPresent() );

        for ( int i = 0; i < 64; i++ ) {
            assertFalse( _mapReader.isNextFieldPresent() );
        }
    }

    @Test
    public void testSetLastof8BitsTwoBytes() {
        _mapWriter.clearCurrentField();
        _mapWriter.clearCurrentField();
        _mapWriter.clearCurrentField();
        _mapWriter.clearCurrentField();
        _mapWriter.clearCurrentField();
        _mapWriter.clearCurrentField();
        _mapWriter.clearCurrentField();
        _mapWriter.setCurrentField();
        _mapWriter.end();

        assertEquals( (byte) 0x00, _buf[ 0 ] );
        assertEquals( (byte) 0xC0, _buf[ 1 ] );

        _decoder.start( _buf, 0, _encoder.getCurrentIndex() );
        _mapReader.readMap( _decoder );

        assertEquals( 2, _decoder.getCurrentIndex() );

        for ( int i = 0; i < 7; i++ ) {
            assertFalse( _mapReader.isNextFieldPresent() );
        }

        assertTrue( _mapReader.isNextFieldPresent() );

        for ( int i = 0; i < 64; i++ ) {
            assertFalse( _mapReader.isNextFieldPresent() );
        }
    }

    @Test
    public void testUNSet8BitsTwoBytes() {
        _mapWriter.clearCurrentField();
        _mapWriter.clearCurrentField();
        _mapWriter.clearCurrentField();
        _mapWriter.clearCurrentField();
        _mapWriter.clearCurrentField();
        _mapWriter.clearCurrentField();
        _mapWriter.clearCurrentField();
        _mapWriter.clearCurrentField();
        _mapWriter.end();

        assertEquals( (byte) 0x00, _buf[ 0 ] );
        assertEquals( (byte) 0x80, _buf[ 1 ] );

        _decoder.start( _buf, 0, _encoder.getCurrentIndex() );
        _mapReader.readMap( _decoder );

        assertEquals( 2, _decoder.getCurrentIndex() );

        for ( int i = 0; i < 8; i++ ) {
            assertFalse( _mapReader.isNextFieldPresent() );
        }

        for ( int i = 0; i < 64; i++ ) {
            assertFalse( _mapReader.isNextFieldPresent() );
        }
    }
}
