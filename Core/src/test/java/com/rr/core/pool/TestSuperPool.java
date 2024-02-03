/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.pool;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.Constants;
import com.rr.core.lang.Reusable;
import com.rr.core.lang.ReusableType;
import com.rr.core.utils.ThreadUtilsFactory;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import static org.junit.Assert.*;

public class TestSuperPool extends BaseTestCase {

    static class TestReusable implements Reusable<TestReusable> {

        int _val = 0;
        private TestReusable _next = null;

        @Override
        public TestReusable getNext() {
            return _next;
        }

        @Override
        public void setNext( TestReusable nxt ) {
            _next = nxt;
        }

        @Override
        public ReusableType getReusableType() {
            return null;
        }

        @Override
        public void reset() {
            _val = -1;
        }
    }

    static TestReusable add( PoolFactory<TestReusable> factory, Set<TestReusable> set, int i ) {
        TestReusable s;
        s = factory.get();

        assertNotNull( s );
        assertTrue( !set.contains( s ) );

        s._val = i;
        set.add( s );

        return s;
    }

    @Test
    public void testLargePool() {

        SuperPool<TestReusable>   pool    = new SuperPool<>( TestReusable.class, 3, 5, 10 );
        PoolFactory<TestReusable> factory = pool.getPoolFactory();
        Set<TestReusable>         set     = new HashSet<>();

        for ( int i = 0; i < 2000; ++i ) {

            add( factory, set, i );
        }

        assertEquals( 199, pool.getCountExtraChains() );
    }

    @Test
    public void testNoExtraPool() {

        SuperPool<TestReusable>   pool    = new SuperPool<>( TestReusable.class, 3, 5, 10 );
        PoolFactory<TestReusable> factory = pool.getPoolFactory();
        Set<TestReusable>         set     = new HashSet<>();

        for ( int i = 0; i < 15; ++i ) {

            add( factory, set, i );
        }

        assertEquals( 0, pool.getCountExtraChains() );
    }

    @Test
    public void testSmallPool() {

        SuperPool<TestReusable>   pool    = new SuperPool<>( TestReusable.class, 3, 5, 10 );
        PoolFactory<TestReusable> factory = pool.getPoolFactory();
        Set<TestReusable>         set     = new HashSet<>();

        for ( int i = 0; i < 20; ++i ) {

            add( factory, set, i );
        }

        assertEquals( 1, pool.getCountExtraChains() );
    }

    @Test
    public void testSmallPoolRecycle() {

        if ( Constants.DISABLE_RECYCLING ) return;

        SuperPool<TestReusable>   pool     = new SuperPool<>( TestReusable.class, 3, 5, 10 );
        PoolFactory<TestReusable> factory  = pool.getPoolFactory();
        Recycler<TestReusable>    recycler = pool.getRecycleFactory();
        Set<TestReusable>         set      = new HashSet<>();

        for ( int i = 0; i < 20; ++i ) {

            TestReusable s = add( factory, set, i );
            recycle( s, set, recycler );
        }

        assertEquals( 0, pool.getCountExtraChains() );
        assertEquals( 4, pool.getCountRecycledChains() );
    }

    @Test
    public void testSmallPoolRecycleAll() {

        if ( Constants.DISABLE_RECYCLING ) return;

        SuperPool<TestReusable>   pool     = new SuperPool<>( TestReusable.class, 3, 5, 10 );
        PoolFactory<TestReusable> factory  = pool.getPoolFactory();
        Recycler<TestReusable>    recycler = pool.getRecycleFactory();
        Set<TestReusable>         set      = new HashSet<>();

        for ( int i = 0; i < 20; ++i ) {

            add( factory, set, i );
        }

        assertEquals( 1, pool.getCountExtraChains() );

        for ( TestReusable r : set ) {

            recycler.recycle( r );
        }

        set.clear();

        assertEquals( 4, pool.getCountRecycledChains() );

        for ( int i = 0; i < 20; ++i ) {

            add( factory, set, i );
        }

        assertEquals( 1, pool.getCountExtraChains() );
        assertEquals( 4, pool.getCountRecycledChains() );
    }

    @Test
    public void testSmallPoolResize() {

        SuperPool<TestReusable>   pool    = new SuperPool<>( TestReusable.class, 3, 5, 10 );
        PoolFactory<TestReusable> factory = pool.getPoolFactory();
        Set<TestReusable>         set     = new HashSet<>();

        pool.init( 5, 7, 10 );

        for ( int i = 0; i < 35; ++i ) {

            add( factory, set, i );
        }

        assertEquals( 0, pool.getCountExtraChains() );
    }

    protected void doTestConcurrant( final SuperPool<TestReusable> pool, int numThreads, final int delay, final int allocs ) {

        System.out.println( "Start threads=" + numThreads + ", allocs=" + allocs + ", delay=" + delay );

        final CountDownLatch cdl = new CountDownLatch( numThreads );
        final CyclicBarrier  cb  = new CyclicBarrier( numThreads );

        for ( int t = 0; t < numThreads; ++t ) {
            new Thread( () -> {
                Set<TestReusable> set = new HashSet<>();

                Recycler<TestReusable>    recycler = pool.getRecycleFactory();
                PoolFactory<TestReusable> factory  = pool.getPoolFactory();

                try {
                    cb.await();
                } catch( Exception e ) {
                    //  dont care
                }

                int runSize = 35;
                int runs    = allocs / runSize;
                int cnt     = 0;

                for ( int i = 0; i < runs; i++ ) {
                    for ( int j = 0; j < runSize; j++ ) {
                        add( factory, set, ++cnt );
                    }

                    ThreadUtilsFactory.get().sleep( delay );

                    for ( TestReusable r : set ) {
                        recycler.recycle( r );
                    }

                    set.clear();
                }

                cdl.countDown();

            } ).start();
        }

        try {
            cdl.await();
        } catch( InterruptedException e ) {
            fail( "Wait interrupted" );
        }

        System.out.println( "Done threads=" + numThreads + ", allocs=" + allocs + ", delay=" + delay );
    }

    private void recycle( TestReusable s, Set<TestReusable> set, Recycler<TestReusable> recycler ) {

        set.remove( s );
        recycler.recycle( s );
    }
}
