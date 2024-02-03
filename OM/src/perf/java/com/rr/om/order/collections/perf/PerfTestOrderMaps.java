/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.order.collections.perf;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.utils.OMUtils;
import com.rr.core.utils.Utils;
import com.rr.om.order.Order;
import com.rr.om.order.collections.HashEntry;
import com.rr.om.order.collections.SegmentOrderMap;
import com.rr.om.order.collections.SimpleOrderMap;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * test to determine overhead of using generics in map
 */
@SuppressWarnings( "UnusedAssignment" ) public class PerfTestOrderMaps extends BaseTestCase {

    private static final ZString SOME_KEY = new ViewString( "SOMEKEY" );

    private long _segmentOrderMapWrite;
    private long _segmentOrderMapRead;
    private long _hashOrderMapWrite;
    private long _hashOrderMapRead;
    private long _concOrderMapWrite;
    private long _concOrderMapRead;
    private long _simpleOrderMapWrite;
    private long _simpleOrderMapRead;

    @Test
    public void testMaps() {

        int runs          = 3;
        int size          = 500000;
        int numIterations = 10;

        doRun( runs, size, numIterations );
    }

    private void doRun( int runs, int size, int numIterations ) {
        System.out.println( "Write times are nano's to create key and put order in map" );
        System.out.println( "Read times are nano's to create key and get order from order" );

        for ( int idx = 0; idx < runs; idx++ ) {

            testSegmentOrderMap( size, numIterations, 1 );
            testSimpleOrderMap( size, numIterations );
            testHashMap( size, numIterations );

            System.out.println( "Run " + idx +
                                ", simpleOrderMap read=" + _simpleOrderMapRead + ", write=" + _simpleOrderMapWrite +
                                ", segmentOrderMap read=" + _segmentOrderMapRead + ", write=" + _segmentOrderMapWrite +
                                ", concOrderMap read=" + _concOrderMapRead + ", write=" + _concOrderMapWrite +
                                ", hashMap read=" + _hashOrderMapRead + ", write=" + _hashOrderMapWrite );
        }

        testSegmentOrderMap( size, numIterations, 1 );
        System.out.println( " SegmentOrderMap Segments " + 1 + ", GET =" + _segmentOrderMapRead + ", write=" + _segmentOrderMapWrite );

        testSegmentOrderMap( size, numIterations, 4 );
        System.out.println( " SegmentOrderMap Segments " + 4 + ", GET =" + _segmentOrderMapRead + ", write=" + _segmentOrderMapWrite );

        testSegmentOrderMap( size, numIterations, 16 );
        System.out.println( " SegmentOrderMap Segments " + 16 + ", GET =" + _segmentOrderMapRead + ", write=" + _segmentOrderMapWrite );

        testSegmentOrderMap( size, numIterations, 64 );
        System.out.println( " SegmentOrderMap Segments " + 64 + ", GET =" + _segmentOrderMapRead + ", write=" + _segmentOrderMapWrite );

        testSegmentOrderMap( size, numIterations, 128 );
        System.out.println( " SegmentOrderMap Segments " + 128 + ", GET =" + _segmentOrderMapRead + ", write=" + _segmentOrderMapWrite );

        testSegmentOrderMap( size, numIterations, 512 );
        System.out.println( " SegmentOrderMap Segments " + 512 + ", GET =" + _segmentOrderMapRead + ", write=" + _segmentOrderMapWrite );

        testSegmentOrderMap( size, numIterations, 16384 );
        System.out.println( " SegmentOrderMap Segments " + 16384 + ", GET =" + _segmentOrderMapRead + ", write=" + _segmentOrderMapWrite );
    }

    private ReusableString mkKey( ReusableString buf, int i ) {
        buf.copy( SOME_KEY ).append( 1000000 + i );

        return buf;
    }

    private void testHashMap( int numOrders, int iterations ) {

        Map<ViewString, Order> map   = new HashMap<>( (int) (numOrders * 1.5), 0.75f );
        Order                  order = OMUtils.mkOrder( mkKey( new ReusableString(), 1 ), 1 );

        SuperPool<ReusableString> spRS = SuperpoolManager.instance().getSuperPool( ReusableString.class );
        spRS.init( numOrders / 1000 + 1, 1000, 50 );
        PoolFactory<ReusableString> rsf = spRS.getPoolFactory();
        Utils.invokeGC();

        long total = 0;

        for ( int i = 0; i < numOrders; ++i ) {
            ReusableString key = rsf.get();
            mkKey( key, i );
            long start = Utils.nanoTime();
            map.put( key, order );
            long end = Utils.nanoTime();
            total += Math.abs( end - start );
        }

        _hashOrderMapWrite = total / numOrders;

        ReusableString buf = new ReusableString();

        total = 0;

        for ( int j = 0; j < iterations; ++j ) {
            for ( int i = 0; i < numOrders; ++i ) {
                mkKey( buf, i );
                long start = Utils.nanoTime();
                order = map.get( buf );
                long end = Utils.nanoTime();
                total += Math.abs( end - start );
            }
        }

        map.clear();

        _hashOrderMapRead = total / (numOrders * iterations);
    }

    private void testSegmentOrderMap( int numOrders, int iterations, int segments ) {

        SegmentOrderMap map   = new SegmentOrderMap( (int) (numOrders * 1.5), 0.75f, segments );
        Order           order = OMUtils.mkOrder( mkKey( new ReusableString(), 1 ), 1 );

        SuperPool<ReusableString> spRS = SuperpoolManager.instance().getSuperPool( ReusableString.class );
        spRS.init( numOrders / 1000 + 1, 1000, 50 );
        PoolFactory<ReusableString> rsf  = spRS.getPoolFactory();
        SuperPool<HashEntry>        spHE = SuperpoolManager.instance().getSuperPool( HashEntry.class );
        spHE.init( numOrders / 1000 + 1, 1000, 50 );
        Utils.invokeGC();

        long total = 0;

        for ( int i = 0; i < numOrders; ++i ) {
            ReusableString key = rsf.get();
            mkKey( key, i );
            long start = Utils.nanoTime();
            map.put( key, order );
            long end = Utils.nanoTime();
            total += Math.abs( end - start );
        }

        _segmentOrderMapWrite = total / numOrders;

        ReusableString buf = new ReusableString();

        total = 0;

        for ( int j = 0; j < iterations; ++j ) {
            for ( int i = 0; i < numOrders; ++i ) {
                mkKey( buf, i );
                long start = Utils.nanoTime();
                order = map.get( buf );
                long end = Utils.nanoTime();
                total += Math.abs( end - start );
            }
        }

        map.clear();

        _segmentOrderMapRead = total / (numOrders * iterations);
    }

    private void testSimpleOrderMap( int numOrders, int iterations ) {

        SimpleOrderMap map   = new SimpleOrderMap( (int) (numOrders * 1.5), 0.75f );
        Order          order = OMUtils.mkOrder( mkKey( new ReusableString(), 1 ), 1 );

        SuperPool<ReusableString> spRS = SuperpoolManager.instance().getSuperPool( ReusableString.class );
        spRS.init( numOrders / 1000 + 1, 1000, 50 );
        PoolFactory<ReusableString> rsf  = spRS.getPoolFactory();
        SuperPool<HashEntry>        spHE = SuperpoolManager.instance().getSuperPool( HashEntry.class );
        spHE.init( numOrders / 1000 + 1, 1000, 50 );
        Utils.invokeGC();

        long total = 0;

        for ( int i = 0; i < numOrders; ++i ) {
            ReusableString key = rsf.get();
            mkKey( key, i );
            long start = Utils.nanoTime();
            map.put( key, order );
            long end = Utils.nanoTime();
            total += Math.abs( end - start );
        }

        _simpleOrderMapWrite = total / numOrders;

        ReusableString buf = new ReusableString();

        total = 0;

        for ( int j = 0; j < iterations; ++j ) {
            for ( int i = 0; i < numOrders; ++i ) {
                mkKey( buf, i );
                long start = Utils.nanoTime();
                order = map.get( buf );
                long end = Utils.nanoTime();
                total += Math.abs( end - start );
            }
        }

        map.clear();

        _simpleOrderMapRead = total / (numOrders * iterations);
    }
}
