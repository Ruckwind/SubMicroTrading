/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.pool;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SuperPoolStressTest extends TestSuperPool {

    @Test public void testConcurrent() {
        final SuperPool<TestReusable> pool = new SuperPool<>( TestReusable.class, 10, 10, 10 );

        try {
            doTestConcurrant( pool, 1, 1, 1000 );
            doTestConcurrant( pool, 2, 1, 1000 );
            doTestConcurrant( pool, 4, 1, 1000 );
            doTestConcurrant( pool, 8, 1, 10000 );
            doTestConcurrant( pool, 32, 1, 10000 );
            doTestConcurrant( pool, 128, 1, 10000 );
            doTestConcurrant( pool, 1024, 1, 1000 );
        } catch( Exception e ) {
            fail( "exception " + e.getMessage() );
        }

        pool.logStats();
    }

    @Test public void testMultiPoolSingleRecycler() {

        SuperPool<TestReusable>   pool     = new SuperPool<>( TestReusable.class, 6, 5, 3 );
        PoolFactory<TestReusable> factoryA = pool.getPoolFactory();
        PoolFactory<TestReusable> factoryB = pool.getPoolFactory();
        PoolFactory<TestReusable> factoryC = pool.getPoolFactory();
        PoolFactory<TestReusable> factoryD = pool.getPoolFactory();
        Recycler<TestReusable>    recycler = pool.getRecycleFactory();

        for ( int i = 0; i < 1000000; ++i ) {

            TestReusable ta = factoryA.get();
            TestReusable tb = factoryB.get();
            TestReusable tc = factoryC.get();
            TestReusable td = factoryD.get();

            recycler.recycle( ta );
            recycler.recycle( tb );
            recycler.recycle( tc );
            recycler.recycle( td );
        }

        assertEquals( 0, pool.getCountExtraChains() );
    }

    @Test public void testMultiPoolSingleRecyclerBigPools() {

        SuperPool<TestReusable>   pool     = new SuperPool<>( TestReusable.class, 100, 100, 100 );
        PoolFactory<TestReusable> factoryA = pool.getPoolFactory();
        PoolFactory<TestReusable> factoryB = pool.getPoolFactory();
        PoolFactory<TestReusable> factoryC = pool.getPoolFactory();
        PoolFactory<TestReusable> factoryD = pool.getPoolFactory();
        Recycler<TestReusable>    recycler = pool.getRecycleFactory();

        for ( int i = 0; i < 1000000; ++i ) {

            TestReusable ta = factoryA.get();
            TestReusable tb = factoryB.get();
            TestReusable tc = factoryC.get();
            TestReusable td = factoryD.get();

            recycler.recycle( ta );
            recycler.recycle( tb );
            recycler.recycle( tc );
            recycler.recycle( td );
        }

        assertEquals( 0, pool.getCountExtraChains() );
    }

    @Test public void testSuperLargePool() {

        SuperPool<TestReusable>   pool    = new SuperPool<>( TestReusable.class, 10000, 100, 100 );
        PoolFactory<TestReusable> factory = pool.getPoolFactory();
        Set<TestReusable>         set     = new HashSet<>();

        for ( int i = 0; i < 1000000; ++i ) {

            add( factory, set, i );
        }

        assertEquals( 0, pool.getCountExtraChains() );
    }
}
