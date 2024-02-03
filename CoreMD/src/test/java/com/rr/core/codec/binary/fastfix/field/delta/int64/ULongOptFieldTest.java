/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.field.delta.int64;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.PresenceMapWriter;
import com.rr.core.codec.binary.fastfix.msgdict.delta.int64.ULongOptReaderDelta;
import com.rr.core.codec.binary.fastfix.msgdict.delta.int64.ULongOptWriterDelta;
import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.Constants;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ULongOptFieldTest extends BaseTestCase {

    private FastFixDecodeBuilder _decoder = new FastFixDecodeBuilder();
    private FastFixBuilder       _encoder;
    private PresenceMapWriter    _mapWriter;
    private PresenceMapReader    _mapReader;

    private byte[]              _buf = new byte[ 8192 ];
    private ULongOptReaderDelta _fieldReader;
    private ULongOptWriterDelta _fieldWriter;

    @Override
    public String toString() {
        return new String( _buf, 0, _decoder.getNextFreeIdx() );
    }

    @Before
    public void setUp() {

        _encoder   = new FastFixBuilder( _buf, 0 );
        _mapWriter = new PresenceMapWriter( _encoder, 0, 1 );
        _mapReader = new PresenceMapReader();

        _fieldReader = new ULongOptReaderDelta( "TestULong", 10018, 0 );
        _fieldWriter = new ULongOptWriterDelta( "TestULong", 10018, 0 );

    }

    @Test
    public void testPrevCopy() {
        _fieldWriter.write( _encoder, 100 );
        assertEquals( 100, _fieldWriter.getPreviousValue() );
        _fieldWriter.write( _encoder, 100 );
        assertEquals( 100, _fieldWriter.getPreviousValue() );
        _fieldWriter.write( _encoder, 100 );
        assertEquals( 100, _fieldWriter.getPreviousValue() );
        _fieldWriter.write( _encoder, 40 );
        assertEquals( 40, _fieldWriter.getPreviousValue() );
        _fieldWriter.write( _encoder, Constants.UNSET_LONG );
        assertEquals( 40, _fieldWriter.getPreviousValue() );
        _fieldWriter.write( _encoder, 100 );
        assertEquals( 100, _fieldWriter.getPreviousValue() );
        _mapWriter.end();

        _decoder.start( _buf, 0, _encoder.getCurrentIndex() );

        _mapReader.readMap( _decoder );

        assertEquals( 1, _decoder.getCurrentIndex() );

        readValAndCheck( 100 );
        readValAndCheck( 100 );
        readValAndCheck( 100 );
        readValAndCheck( 40 );
        readValAndCheck( Constants.UNSET_LONG );
        readValAndCheck( 100 );
    }

    @Test
    public void testVals() {
        checkField( 1 );
        checkField( Constants.UNSET_LONG );
        checkField( Long.MAX_VALUE );
        checkField( -2 );
        checkField( -1000 );
        checkField( 1000 );
        checkField( 127 );
        checkField( 128 );
        checkField( 129 );
        checkField( 0x0FFFFFFFFFFFFL );
    }

    private void checkField( long value ) {
        setUp(); // force reset

        _fieldWriter.write( _encoder, value );
        _mapWriter.end();
        _decoder.start( _buf, 0, _encoder.getCurrentIndex() );
        _mapReader.readMap( _decoder );

        assertEquals( 1, _decoder.getCurrentIndex() );

        readValAndCheck( value );
    }

    private void readValAndCheck( long value ) {
        long readVal = _fieldReader.read( _decoder );

        assertEquals( value, readVal );
    }
}
