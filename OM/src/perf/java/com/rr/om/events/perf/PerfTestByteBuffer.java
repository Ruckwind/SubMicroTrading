/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.events.perf;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ClockFactory;
import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * test the performance difference between using ReusableStrings in a NOS and ViewStrings
 *
 * @author Richard Rose
 */
// TODO check impact of replacing all the small (<=8 byte) with SmallString
public class PerfTestByteBuffer extends BaseTestCase {

    @Test
    public void testMultTen() {

        int runs       = 5;
        int iterations = 10000000;

        doRun( runs, iterations );
    }

    @SuppressWarnings( "MismatchedReadAndWriteOfArray" ) private long byteArray( int iterations, int size ) {
        byte[] buf = new byte[ size ];

        byte[] bytes = "11=CLORDID_1234".getBytes();
        int    max   = size - bytes.length;

        long startTime = ClockFactory.get().currentTimeMillis();

        int idx = 0;

        for ( int i = 0; i < iterations; ++i ) {
            if ( idx >= max ) {
                idx = 0;
            }

            System.arraycopy( bytes, 0, buf, idx, bytes.length );

            idx += bytes.length;
        }

        long endTime = ClockFactory.get().currentTimeMillis();

        return endTime - startTime;
    }

    private long direct( int iterations, int size ) {

        ByteBuffer buf = ByteBuffer.allocateDirect( size );

        byte[] bytes = "11=CLORDID_1234".getBytes();
        int    max   = size - bytes.length;

        long startTime = ClockFactory.get().currentTimeMillis();

        for ( int i = 0; i < iterations; ++i ) {
            if ( buf.position() >= max ) {
                buf.clear();
            }
            buf.put( bytes );
        }

        long endTime = ClockFactory.get().currentTimeMillis();

        return endTime - startTime;
    }

    private void doRun( int runs, int iterations ) {
        for ( int idx = 0; idx < runs; idx++ ) {

            long heapTime   = heap( iterations, 400 );
            long directTime = direct( iterations, 400 );
            long byteArr    = byteArray( iterations, 400 );

            System.out.println( "Run " + idx + " heap=" + heapTime + ", direct=" + directTime + ", byteArr=" + byteArr );
        }
    }

    private long heap( int iterations, int size ) {

        ByteBuffer buf = ByteBuffer.allocate( size );

        byte[] bytes = "11=CLORDID_1234".getBytes();
        int    max   = size - bytes.length;

        long startTime = ClockFactory.get().currentTimeMillis();

        for ( int i = 0; i < iterations; ++i ) {
            if ( buf.position() >= max ) {
                buf.clear();
            }
            buf.put( bytes );
        }

        long endTime = ClockFactory.get().currentTimeMillis();

        return endTime - startTime;
    }

}
