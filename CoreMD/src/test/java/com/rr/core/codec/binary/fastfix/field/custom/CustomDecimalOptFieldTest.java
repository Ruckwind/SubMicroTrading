/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.field.custom;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.PresenceMapWriter;
import com.rr.core.codec.binary.fastfix.msgdict.DictComponentFactory;
import com.rr.core.codec.binary.fastfix.msgdict.custom.defexp.delmant.OptDefExpDeltaMantDecimalReader;
import com.rr.core.codec.binary.fastfix.msgdict.custom.defexp.delmant.OptDefExpDeltaMantDecimalWriter;
import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.Constants;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CustomDecimalOptFieldTest extends BaseTestCase {

    private FastFixDecodeBuilder _decoder = new FastFixDecodeBuilder();
    private FastFixBuilder       _encoder;
    private PresenceMapWriter    _mapWriter;
    private PresenceMapReader    _mapReader;

    private byte[]                          _buf = new byte[ 8192 ];
    private OptDefExpDeltaMantDecimalReader _fieldReader;
    private OptDefExpDeltaMantDecimalWriter _fieldWriter;

    @Override
    public String toString() {
        return new String( _buf, 0, _decoder.getNextFreeIdx() );
    }

    @SuppressWarnings( "boxing" )
    @Before
    public void setUp() {

        _encoder   = new FastFixBuilder( _buf, 0 );
        _mapWriter = new PresenceMapWriter( _encoder, 0, 1 );
        _mapReader = new PresenceMapReader();

        DictComponentFactory cf = new DictComponentFactory();

        _fieldReader = cf.getReader( OptDefExpDeltaMantDecimalReader.class, cf, "TestULong", 10018, 2, 0L );
        _fieldWriter = cf.getWriter( OptDefExpDeltaMantDecimalWriter.class, cf, "TestULong", 10018, 2, 0L );

    }

    @Test
    public void testPrevCopy() {
        _fieldWriter.write( _encoder, _mapWriter, 100.123 );
        assertEquals( 100.123, _fieldWriter.getPreviousValue(), Constants.WEIGHT );
        _fieldWriter.write( _encoder, _mapWriter, 100.00009 );
        assertEquals( 100.00009, _fieldWriter.getPreviousValue(), Constants.WEIGHT );
        _fieldWriter.write( _encoder, _mapWriter, 1000000000000.01 );
        assertEquals( 1000000000000.01, _fieldWriter.getPreviousValue(), Constants.WEIGHT );
        _fieldWriter.write( _encoder, _mapWriter, 40 );
        assertEquals( 40, _fieldWriter.getPreviousValue(), Constants.WEIGHT );
        _fieldWriter.write( _encoder, _mapWriter, Constants.UNSET_DOUBLE );
        assertEquals( 40, _fieldWriter.getPreviousValue(), Constants.WEIGHT );
        _fieldWriter.write( _encoder, _mapWriter, 100.05 );
        assertEquals( 100.05, _fieldWriter.getPreviousValue(), Constants.WEIGHT );
        _mapWriter.end();

        _decoder.start( _buf, 0, _encoder.getCurrentIndex() );

        _mapReader.readMap( _decoder );

        assertEquals( 1, _decoder.getCurrentIndex() );

        readValAndCheck( 100.123 );
        readValAndCheck( 100.00009 );
        readValAndCheck( 1000000000000.01 );
        readValAndCheck( 40 );
        readValAndCheck( Constants.UNSET_DOUBLE );
        readValAndCheck( 100.05 );
    }

    @Test
    public void testVals() {
        checkField( 1 );
        checkField( -2 );
        checkField( -1000 );
        checkField( 1000 );
        checkField( 127 );
        checkField( 128 );
        checkField( 129 );
        checkField( 0x0FFFFFFFFFFFFL );
        checkField( Long.MAX_VALUE - 1 );
        checkField( Long.MAX_VALUE );
        checkField( Constants.UNSET_DOUBLE );

        checkField( 0.1 );
        checkField( 0.999 );
        checkField( 0.0876 );
        checkField( 0.00000000000000999 );
        checkField( 54321.00098 );
        checkField( 54321.98 );
        checkField( 987654321987654321L );
    }

    private void checkField( double value ) {
        setUp(); // force reset

        _fieldWriter.write( _encoder, _mapWriter, value );
        _mapWriter.end();
        _decoder.start( _buf, 0, _encoder.getCurrentIndex() );
        _mapReader.readMap( _decoder );

        assertEquals( 1, _decoder.getCurrentIndex() );

        readValAndCheck( value );
    }

    private void readValAndCheck( double value ) {
        double readVal = _fieldReader.read( _decoder, _mapReader );

        assertEquals( value, readVal, Constants.WEIGHT );
    }
}
