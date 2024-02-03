/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.order.collections;

import com.rr.core.collections.ConcLinkedEventQueueSingle;
import com.rr.core.lang.*;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.model.Event;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.model.generated.fix.codec.Standard44DecoderOMS;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.model.generated.internal.events.impl.MarketNewOrderAckImpl;
import com.rr.om.warmup.FixTestUtils;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * slow producer due to creating decoder for every message
 * <p>
 * leave in as a good different test to the fast one
 *
 * @author Richard Rose
 */
public class ConcLinkedMsgQueueSingleSlowTest extends BaseTestCase {

    private static final Logger _log = ConsoleFactory.console( ConcLinkedMsgQueueSingleSlowTest.class, Level.WARN );

    @SuppressWarnings( "NonAtomicOperationOnVolatileField" ) private static class ConsumerRunnable implements Runnable {

        private static final long MAX_CONSUME_WAIT_MS = 10000;

        private          int                        _tot;
        private          ConcLinkedEventQueueSingle _q;
        private volatile boolean                    _finished = false;
        private volatile int                        _count    = 0;
        private volatile long                       _totQty   = 0;
        private          CountDownLatch             _cdl;

        public ConsumerRunnable( int tot, ConcLinkedEventQueueSingle q, CountDownLatch cdl ) {
            _tot = tot;
            _q   = q;
            _cdl = cdl;
        }

        @Override
        public void run() {
            long last = ClockFactory.get().currentTimeMillis();
            long now;

            while( _count < _tot ) {
                Event m;

                do {
                    m = _q.poll();

                    now = ClockFactory.get().currentTimeMillis();

                    if ( now - last > MAX_CONSUME_WAIT_MS ) {
                        _finished = true;
                    }

                } while( m == null && !_finished );

                if ( m != null ) {

                    _cdl.countDown();
                    ++_count;

                    ClientNewOrderSingleImpl nos = (ClientNewOrderSingleImpl) m;

                    _totQty += nos.getOrderQty();
                }

                last = now;
            }

            _finished = true;
        }

        public boolean finished() {
            return _finished;
        }

        public int getConsumed() {
            return _count;
        }

        public long getTotalQty() {
            return _totQty;
        }
    }

    @SuppressWarnings( "NonAtomicOperationOnVolatileField" ) private static class ProducerRunnable implements Runnable {

        private          ConcLinkedEventQueueSingle _q;
        private          int                        _count;
        private          int                        _producerIdx;
        private volatile int                        _errs = 0;
        private volatile int                        _sent;

        public ProducerRunnable( int producerIdx,
                                 int count,
                                 ConcLinkedEventQueueSingle q ) {
            _q           = q;
            _count       = count;
            _sent        = 0;
            _producerIdx = producerIdx;
        }

        @Override
        public void run() {
            int baseQty = _producerIdx * _count;

            Standard44DecoderOMS decoder = FixTestUtils.getOMSDecoder44();

            for ( int i = 0; i < _count; ++i ) {
                Event msg = FixTestUtils.getClientNOS( decoder, mkKey( true, i ) + _producerIdx, baseQty + i, 1 );
                if ( msg != null ) {
                    if ( _q.add( msg ) == false ) {
                        ++_errs;
                    } else {
                        ++_sent;
                    }
                } else {
                    ++_errs;
                }
            }
        }

        public long getErrs() {
            return _errs;
        }

        public long sent() {
            return _sent;
        }
    }

    static String mkKey( boolean isClient, int i ) {
        return ((isClient) ? "C" : "M") + "SOMEKEY" + (1000000 + i);
    }

    @Test
    public void testAdd() {

        Standard44DecoderOMS decoder = FixTestUtils.getOMSDecoder44();

        ConcLinkedEventQueueSingle q = new ConcLinkedEventQueueSingle();

        int numOrders = 1000;

        ZString mktOrdId = new ViewString( "ORDID" );
        ZString execId   = new ViewString( "EXEID" );

        for ( int i = 0; i < numOrders; ++i ) {
            Event msg = FixTestUtils.getClientNOS( decoder, mkKey( true, i ), i, i );
            Event ack = FixTestUtils.getMarketACK( decoder, mkKey( false, i ), i, i, mktOrdId, execId );

            q.add( msg );
            q.add( ack );
        }

        assertEquals( numOrders * 2, q.size() );

        for ( int i = 0; i < numOrders; ++i ) {
            String ckey = mkKey( true, i );
            String mkey = mkKey( false, i );

            Event nos = q.poll();
            Event ack = q.poll();

            assertSame( ClientNewOrderSingleImpl.class, nos.getClass() );
            assertSame( MarketNewOrderAckImpl.class, ack.getClass() );

            ClientNewOrderSingleImpl cnos = (ClientNewOrderSingleImpl) nos;
            MarketNewOrderAckImpl    mack = (MarketNewOrderAckImpl) ack;

            assertEquals( ckey, cnos.getClOrdId().toString() );
            assertEquals( mkey, mack.getClOrdId().toString() );
            assertEquals( i, cnos.getOrderQty(), Constants.TICK_WEIGHT );
        }

        assertNull( q.poll() );
    }

    @Test
    public void testAddOneElem() {

        Standard44DecoderOMS decoder = FixTestUtils.getOMSDecoder44();

        ConcLinkedEventQueueSingle q = new ConcLinkedEventQueueSingle();

        Event msg1 = FixTestUtils.getClientNOS( decoder, mkKey( true, 1 ), 1, 1 );

        q.add( msg1 );

        assertEquals( 1, q.size() );

        Event pop1 = q.poll();

        assertSame( msg1, pop1 );
        assertEquals( 0, q.size() );

        assertNull( q.poll() );
        assertNull( q.poll() );

        Event msg2 = FixTestUtils.getClientNOS( decoder, mkKey( true, 2 ), 2, 2 );

        q.add( msg2 );

        assertEquals( 1, q.size() );

        Event pop2 = q.poll();

        assertEquals( 0, q.size() );

        String ckey = mkKey( true, 2 );

        assertSame( ClientNewOrderSingleImpl.class, pop2.getClass() );

        ClientNewOrderSingleImpl cnos = (ClientNewOrderSingleImpl) pop2;

        assertEquals( ckey, cnos.getClOrdId().toString() );
    }

    @Test
    public void testThreadedT1M500() {
        doTestThreaded( 500, 1 );
    }

    protected void doTestThreaded( int count, int threads ) {

        // block multipe unit test in same JVM

        synchronized( ConcLinkedMsgQueueSingleSlowTest.class ) {

            int total = count * threads;

            ConcLinkedEventQueueSingle q = new ConcLinkedEventQueueSingle();

            CountDownLatch cdl = new CountDownLatch( total );

            ConsumerRunnable c = new ConsumerRunnable( total, q, cdl );

            Thread ct = new Thread( c, "Consumer" );

            Set<ProducerRunnable> producers = new LinkedHashSet<>();

            for ( int i = 0; i < threads; i++ ) {

                ProducerRunnable r = new ProducerRunnable( i, count, q );

                producers.add( r );

                Thread rt = new Thread( r, "Thread" + i );
                rt.start();
            }

            ct.start();

            // wait until all sent

            long totalSent;
            long errs;

            do {
                ThreadUtilsFactory.get().sleep( 100 );

                totalSent = count( producers );
                errs      = countErrs( producers );

                _log.info( "Waited another sec .... produced =" + totalSent + " of expected " + total + ", errs=" + errs );

            } while( totalSent + errs < total );

            assertEquals( 0, errs );
            assertEquals( total, totalSent );

            long consumed;
            long tmpConsumed;

            do {
                consumed = c.getConsumed();

                ThreadUtilsFactory.get().sleep( 500 );

                tmpConsumed = c.getConsumed();

                _log.info( "Waited another sec .... before wait consumed=" + consumed + " of expected " + total +
                           ", after wait consumed=" + tmpConsumed );

            } while( consumed != tmpConsumed );

            try {
                cdl.await( 5, TimeUnit.SECONDS );
            } catch( InterruptedException e ) {
                // ignore
            }

            assertTrue( "Consumed " + c.getConsumed() + " of " + total + ", threads=" + threads, c.finished() );
            assertEquals( total, c.getConsumed() );

            long expectedQty = 0;

            for ( int i = 0; i < threads; i++ ) {
                int baseQty = i * count;
                for ( int j = 0; j < count; ++j ) {
                    int qty = baseQty + j;
                    expectedQty += qty;
                }
            }

            assertEquals( expectedQty, c.getTotalQty() );
        }
    }

    private long count( Set<ProducerRunnable> producers ) {
        long cnt = 0;

        for ( ProducerRunnable pr : producers ) {

            cnt += pr.sent();
        }

        return cnt;
    }

    private long countErrs( Set<ProducerRunnable> producers ) {
        long cnt = 0;

        for ( ProducerRunnable pr : producers ) {

            cnt += pr.getErrs();
        }

        return cnt;
    }
}
