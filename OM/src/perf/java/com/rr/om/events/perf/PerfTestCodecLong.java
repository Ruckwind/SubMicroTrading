/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.events.perf;

import com.rr.core.codec.AbstractFixDecoder;
import com.rr.core.codec.FixDecoder;
import com.rr.core.codec.FixEncodeBuilder;
import com.rr.core.codec.FixEncodeBuilderImpl;
import com.rr.core.lang.BaseTestCase;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Logger;
import com.rr.core.model.Event;
import com.rr.core.utils.Utils;
import com.rr.om.dummy.warmup.DummyInstrumentLocator;
import org.junit.Test;

/**
 * perf test using long instead of int for codec
 *
 * @author Richard Rose
 */
public class PerfTestCodecLong extends BaseTestCase {

    private static final Logger _log = ConsoleFactory.console( PerfTestCodecLong.class );

    private static class DummyDecoder extends AbstractFixDecoder {

        public DummyDecoder( byte major, byte minor ) {
            super( major, minor );
            setInstrumentLocator( new DummyInstrumentLocator() );
        }

        @Override protected Event doMessageDecode() {
            return null;
        }

        @Override public String getComponentId()  { return null; }

        @Override public FixDecoder newInstance() { return null; }

        public int decodeInt() {
            return getIntVal();
        }

        public long decodeLong() {
            return getLongVal();
        }

        public int decodeTag() {
            return getTag();
        }

        public void setup( final byte[] fixMsg, final int offset, final int maxIdx ) {
            _fixMsg = fixMsg;
            _offset = offset;
            _idx    = offset;
            _maxIdx = maxIdx;
        }
    }

    private int count = 1000000;

    public PerfTestCodecLong() {
        // nothing
    }

    @Test
    public void testEncoding() {
        doTest( 5, count, 1 );
        doTest( 5, count, 100 );
        doTest( 5, count, 54321 );
        doTest( 5, count, 987654321 );
    }

    private void doTest( int iter, int max, int num ) {
        for ( int i = 0; i < iter; ++i ) {
            runTest( max, num );
        }
    }

    private void runTest( int max, int num ) {

        final byte[] bufInt  = new byte[ 512 ];
        final byte[] bufLong = new byte[ 512 ];

        FixEncodeBuilder encoderInt  = new FixEncodeBuilderImpl( bufInt, 0, (byte) '4', (byte) '4' );
        FixEncodeBuilder encoderLong = new FixEncodeBuilderImpl( bufLong, 0, (byte) '4', (byte) '4' );

        DummyDecoder decoder = new DummyDecoder( (byte) '4', (byte) '4' );

        for ( int i = 0; i < 10; i++ ) {
            encoderInt.encodeInt( 38, num );
            encoderLong.encodeLong( 38, num );
        }

        _log.info( "START TEST ==================" );

        @SuppressWarnings( "unused" )
        long tmp;
        @SuppressWarnings( "unused" )
        int tag;

        long start = Utils.nanoTime();
        for ( int i = 0; i < max; i++ ) {
            decoder.setup( bufInt, 0, bufInt.length );
            for ( int j = 0; j < 10; ++j ) {
                //noinspection UnusedAssignment
                tag = decoder.decodeTag();
                //noinspection UnusedAssignment
                tmp = decoder.decodeInt();
            }
        }
        long end = Utils.nanoTime();
        System.out.println( "DECODE INT " + num + ", cnt=" + max + ", time=" + ((end - start) / max) );

        start = Utils.nanoTime();
        for ( int i = 0; i < max; i++ ) {
            decoder.setup( bufLong, 0, bufLong.length );
            for ( int j = 0; j < 10; ++j ) {
                //noinspection UnusedAssignment
                tag = decoder.decodeTag();
                //noinspection UnusedAssignment
                tmp = decoder.decodeLong();
            }
        }
        end = Utils.nanoTime();
        System.out.println( "DECODE LONG " + num + ", cnt=" + max + ", time=" + ((end - start) / max) );

        start = Utils.nanoTime();
        for ( int i = 0; i < max; i++ ) {
            encoderInt.start();
            for ( int j = 0; j < 10; ++j ) {
                encoderInt.encodeInt( 38, num );
            }
        }
        end = Utils.nanoTime();
        System.out.println( "ENCODE INT " + num + ", cnt=" + max + ", time=" + ((end - start) / max) );

        start = Utils.nanoTime();
        for ( int i = 0; i < max; i++ ) {
            encoderLong.start();
            for ( int j = 0; j < 10; ++j ) {
                encoderLong.encodeLong( 38, num );
            }
        }
        end = Utils.nanoTime();
        System.out.println( "ENCODE LONG, cnt=" + max + ", time=" + ((end - start) / max) );
    }
}
