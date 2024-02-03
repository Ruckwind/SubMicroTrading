/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

import com.rr.core.lang.BaseTestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SafeBookReserverTest extends BaseTestCase {

    @Test
    public void testMultiThreads() {
        // doesnt test concurrency, but as we know when threadcount hits two a sync wrapper is unsed internally
        // just really need check that threadcount can hit two

        final SafeBookReserver sbr = new SafeBookReserver();

        sbr.attachReserveWorkerThread( Thread.currentThread() );
        assertEquals( 1, sbr.getAttachedWorkerThreads() );

        sbr.attachReserveWorkerThread( new Thread() );
        assertEquals( 2, sbr.getAttachedWorkerThreads() );

        assertEquals( 0, sbr.getReserved() );
        assertEquals( 4, sbr.grabQty( 10, 4, 100 * 1000 * 1000 ) );
        assertEquals( 4, sbr.getReserved() );

        assertEquals( 6, sbr.grabQty( 10, 6, 100 * 1000 * 1000 + SafeBookReserver.MIN_RESET_DELAY_NANOS + 1 ) );
        assertEquals( 6, sbr.getReserved() );

        sbr.completed( 10 );
        assertEquals( 0, sbr.getReserved() );

        assertEquals( 10, sbr.grabQty( 10, 10, 100 * 1000 * 1000 + 300 ) );
        assertEquals( 10, sbr.getReserved() );
        sbr.completed( 10 );
        assertEquals( 0, sbr.getReserved() );
    }

    @Test
    public void testSingleWorkerAging() {
        SafeBookReserver sbr = new SafeBookReserver();

        sbr.attachReserveWorkerThread( Thread.currentThread() );

        assertEquals( 0, sbr.getReserved() );
        assertEquals( 4, sbr.grabQty( 10, 4, 100 * 1000 * 1000 ) );
        assertEquals( 4, sbr.getReserved() );

        assertEquals( 6, sbr.grabQty( 10, 6, 100 * 1000 * 1000 + SafeBookReserver.MIN_RESET_DELAY_NANOS + 1 ) );
        assertEquals( 6, sbr.getReserved() );

        sbr.completed( 10 );
        assertEquals( 0, sbr.getReserved() );

        assertEquals( 10, sbr.grabQty( 10, 10, 100 * 1000 * 1000 + 300 ) );
        assertEquals( 10, sbr.getReserved() );
        sbr.completed( 10 );
        assertEquals( 0, sbr.getReserved() );
    }

    @Test
    public void testSingleWorkerThreadSimple() {
        SafeBookReserver sbr = new SafeBookReserver();

        sbr.attachReserveWorkerThread( Thread.currentThread() );

        assertEquals( 0, sbr.getReserved() );
        assertEquals( 4, sbr.grabQty( 10, 4, 100 * 1000 * 1000 ) );
        assertEquals( 4, sbr.getReserved() );

        sbr.completed( 1 );
        assertEquals( 3, sbr.getReserved() );

        assertEquals( 1, sbr.grabQty( 1, 6, 100 * 1000 * 1000 + 100 ) );
        assertEquals( 4, sbr.getReserved() );

        assertEquals( 2, sbr.grabQty( 10, 6, 100 * 1000 * 1000 + 200 ) );
        assertEquals( 6, sbr.getReserved() );

        sbr.completed( 6 );
        assertEquals( 0, sbr.getReserved() );

        assertEquals( 10, sbr.grabQty( 10, 10, 100 * 1000 * 1000 + 300 ) );
        assertEquals( 10, sbr.getReserved() );
        sbr.completed( 10 );
        assertEquals( 0, sbr.getReserved() );

        assertEquals( 1, sbr.getAttachedWorkerThreads() );
    }
}
