package com.rr.core.collections;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class TestMaxLongValueSlidingWindow {

    @Test
    public void testLarger() {
        for ( int i = 0; i < 5; i++ ) {
            doTest( 100, 1000 );
            doTest( 100, 10000 );
            doTest( 100, 100000 );
            doTest( 100, 1000000 );
        }
        for ( int i = 0; i < 3; i++ ) {
            doTest( 10000, 100000 );
            doTest( 10000, 1000000 );
            doTest( 10000, 10000000 );
        }
    }

    @SuppressWarnings( "boxing" )
    @Test
    public void testSimple() {
        MaxLongValueInSlidingWindow window = new MaxLongValueInSlidingWindow( 3 );

        check( 5, 5, window, 5 );
        check( 1, 5, window, 5, 1 );
        check( 3, 5, window, 5, 3 );
        check( 4, 4, window, 4 );
        check( 2, 4, window, 4, 2 );
        check( 1, 4, window, 4, 2, 1 );
        check( 5, 5, window, 5 );
        check( 1, 5, window, 5, 1 );
    }

    private void check( int nextVal, int expectedMax, MaxLongValueInSlidingWindow window, Integer... entries ) {

        long maxInWindow = window.add( nextVal );

        Assert.assertEquals( expectedMax, maxInWindow );

        Assert.assertEquals( entries.length, window.activeEntries() );

        System.out.println( window.toString() );

        for ( int i = 0; i < entries.length; i++ ) {
            @SuppressWarnings( "boxing" )
            int expected = entries[ i ];

            Assert.assertEquals( expected, window.getEntry( i ) );
        }
    }

    private void doTest( int windowSize, int streamSize ) {
        MaxLongValueInSlidingWindow window    = new MaxLongValueInSlidingWindow( windowSize );
        Random                      generator = new Random();

        long start = System.nanoTime();

        for ( int i = 0; i < streamSize; i++ ) {
            window.add( generator.nextLong() );
        }

        long end = System.nanoTime();

        long perEntryNano = Math.abs( end - start ) / streamSize;

        System.out.println( "Stream size " + streamSize + ", timePerEntry=" + perEntryNano );
    }
}
