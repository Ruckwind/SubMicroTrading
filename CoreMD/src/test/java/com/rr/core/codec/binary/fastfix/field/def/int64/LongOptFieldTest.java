/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.field.def.int64;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.PresenceMapWriter;
import com.rr.core.codec.binary.fastfix.common.def.int64.LongOptReaderDefault;
import com.rr.core.codec.binary.fastfix.common.def.int64.LongOptWriterDefault;
import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.Constants;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LongOptFieldTest extends BaseTestCase {

    private FastFixDecodeBuilder _decoder = new FastFixDecodeBuilder();
    private FastFixBuilder       _encoder;
    private PresenceMapWriter    _mapWriter;
    private PresenceMapReader    _mapReader;

    private byte[]               _buf = new byte[ 8192 ];
    private LongOptReaderDefault _fieldReader;
    private LongOptWriterDefault _fieldWriter;

    @Override
    public String toString() {
        return new String( _buf, 0, _decoder.getNextFreeIdx() );
    }

    @Before
    public void setUp() {

        _encoder   = new FastFixBuilder( _buf, 0 );
        _mapWriter = new PresenceMapWriter( _encoder, 0, 1 );
        _mapReader = new PresenceMapReader();

        _fieldReader = new LongOptReaderDefault( "TestULong", 10018, 100 );
        _fieldWriter = new LongOptWriterDefault( "TestULong", 10018, 100 );

    }

    @Test
    public void testPrevCopy() {
        _fieldWriter.write( _encoder, _mapWriter, 100 );
        _fieldWriter.write( _encoder, _mapWriter, 100 );
        _fieldWriter.write( _encoder, _mapWriter, 100 );
        _fieldWriter.write( _encoder, _mapWriter, 40 );
        _fieldWriter.write( _encoder, _mapWriter, Constants.UNSET_LONG );
        _fieldWriter.write( _encoder, _mapWriter, 100 );
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
        checkField( -2 );
        checkField( -1000 );
        checkField( 1000 );
        checkField( 127 );
        checkField( 128 );
        checkField( 129 );
        checkField( 0x0FFFFFFFFFFFFL );
        checkField( Long.MAX_VALUE - 1 );  // MAX_VALUE will overflow when incremented to make room for null
    }

    private void checkField( long value ) {
        setUp(); // force reset

        _fieldWriter.write( _encoder, _mapWriter, value );
        _mapWriter.end();
        _decoder.start( _buf, 0, _encoder.getCurrentIndex() );
        _mapReader.readMap( _decoder );

        assertEquals( 1, _decoder.getCurrentIndex() );

        readValAndCheck( value );
    }

    private void readValAndCheck( long value ) {
        long readVal = _fieldReader.read( _decoder, _mapReader );

        assertEquals( value, readVal );
    }
}
