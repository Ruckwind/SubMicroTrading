/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.order.collections;

import com.rr.core.collections.EventQueue;
import com.rr.core.lang.*;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.core.utils.Utils;
import com.rr.model.generated.fix.codec.Standard44DecoderOMS;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.model.generated.internal.events.impl.MarketNewOrderAckImpl;
import com.rr.om.warmup.FixTestUtils;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public abstract class BaseQueueTst extends BaseTestCase {

    private static final Logger _log = LoggerFactory.create( BaseQueueTst.class );

    protected static class ConsumerRunnable implements Runnable {

        private          int            _tot;
        private          EventQueue     _q;
        private volatile boolean        _finished = false;
        private volatile int            _count    = 0;
        private          long           _totQty   = 0;
        private          CountDownLatch _cdl;
        private          CyclicBarrier  _cb;

        public ConsumerRunnable( int tot, EventQueue q, CountDownLatch cdl, CyclicBarrier cb ) {
            _tot = tot;
            _q   = q;
            _cdl = cdl;
            _cb  = cb;
        }

        @Override
        public void run() {

            try {
                _cb.await();
            } catch( Exception e ) {
                // dont care
            }

            while( !_finished && _count < _tot ) {
                Event m;

                do {
                    m = _q.poll();

                } while( m == null && !_finished );

                if ( m != null ) {

                    do {
                        ++_count;
                        _cdl.countDown();

                        ClientNewOrderSingleImpl nos = (ClientNewOrderSingleImpl) m;

                        _totQty += nos.getOrderQty();

                        m = m.getNextQueueEntry();

                    } while( m != null );
                }
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

        public void setFinished() {
            _finished = true;
        }
    }

    protected static class ProducerRunnable implements Runnable {

        private EventQueue _q;

        private int _count;

        private int _producerIdx;
        private int _errs = 0;

        private volatile int _sent;

        private CyclicBarrier _cb;

        public ProducerRunnable( int producerIdx,
                                 int count,
                                 EventQueue q,
                                 CyclicBarrier cb ) {
            _q           = q;
            _count       = count;
            _sent        = 0;
            _producerIdx = producerIdx;
            _cb          = cb;
        }

        @Override
        public void run() {

            try {
                _cb.await();
            } catch( Exception e ) {
                // dont care
            }

            int baseQty = _producerIdx * _count;

            Standard44DecoderOMS decoder = FixTestUtils.getOMSDecoder44();

            ReusableString key    = new ReusableString( 20 );
            ReusableString buffer = new ReusableString( 256 );

            while( _sent < _count ) {
                decoder.setReceived( Utils.nanoTime() );
                mkKey( key, true, _sent );
                key.append( _producerIdx );
                Event msg = FixTestUtils.getClientNOS( buffer, decoder, key, baseQty + _sent, 1, null );
                if ( msg != null ) {
                    if ( _q.add( msg ) == false ) {
                        ++_errs;
                    } else {
                        ++_sent;
                    }
                } else {
                    ++_errs;
                }
                Thread.yield();
            }

            _log.info( "Producer finished IDX [" + _producerIdx + "] sent " + _sent + ", errs=" + _errs + ", cnt=" + _count );
        }

        public long getErrs() {
            return _errs;
        }

        public long sent() {
            return _sent;
        }
    }
    protected Standard44DecoderOMS _decoder = FixTestUtils.getOMSDecoder44();

    static void mkKey( ReusableString key, boolean isClient, int i ) {
        key.reset();

        key.append( (isClient) ? 'C' : 'M' );
        key.append( "SOMEKEY" );
        key.append( 1000000 + i );
    }

    static String mkKey( boolean isClient, int i ) {
        return ((isClient) ? "C" : "M") + "SOMEKEY" + (1000000 + i);
    }

    @Test
    public void testAdd() {

        int numOrders = 1000;

        EventQueue q = getNewQueue( numOrders * 2 );

        ZString mktOrdId = new ViewString( "ORDID" );
        ZString execId   = new ViewString( "EXEID" );

        Standard44DecoderOMS decoder = FixTestUtils.getOMSDecoder44();

        for ( int i = 0; i < numOrders; ++i ) {
            Event msg = FixTestUtils.getClientNOS( _decoder, mkKey( true, i ), i, i );
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

        EventQueue q = getNewQueue( 1 );

        Event msg1 = FixTestUtils.getClientNOS( _decoder, mkKey( true, 1 ), 1, 1 );

        q.add( msg1 );

        assertEquals( 1, q.size() );

        Event pop1 = q.poll();

        assertSame( msg1, pop1 );
        assertEquals( 0, q.size() );

        assertNull( q.poll() );
        assertNull( q.poll() );

        Event msg2 = FixTestUtils.getClientNOS( _decoder, mkKey( true, 2 ), 2, 2 );

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
    public void testAddTwoElem() {

        EventQueue q = getNewQueue( 2 );

        Event msg1 = FixTestUtils.getClientNOS( _decoder, mkKey( true, 1 ), 1, 1 );
        Event msg2 = FixTestUtils.getClientNOS( _decoder, mkKey( true, 2 ), 2, 2 );

        q.add( msg1 );
        q.add( msg2 );

        assertEquals( 2, q.size() );

        Event pop1 = q.poll();

        assertSame( msg1, pop1 );
        assertEquals( 1, q.size() );

        Event pop2 = q.poll();

        assertSame( msg2, pop2 );
        assertEquals( 0, q.size() );

        assertNull( q.poll() );
        assertNull( q.poll() );

        Event msg3 = FixTestUtils.getClientNOS( _decoder, mkKey( true, 3 ), 3, 3 );

        q.add( msg3 );

        assertEquals( 1, q.size() );

        Event pop3 = q.poll();

        assertEquals( 0, q.size() );

        String ckey = mkKey( true, 3 );

        assertSame( ClientNewOrderSingleImpl.class, pop2.getClass() );

        ClientNewOrderSingleImpl cnos = (ClientNewOrderSingleImpl) pop3;

        assertEquals( ckey, cnos.getClOrdId().toString() );
    }

    @Test
    public void testIterate3Elems() {

        EventQueue q = getNewQueue( 3 );

        Event msg1 = FixTestUtils.getClientNOS( _decoder, mkKey( true, 1 ), 1, 1 );
        Event msg2 = FixTestUtils.getClientNOS( _decoder, mkKey( true, 2 ), 2, 2 );
        Event msg3 = FixTestUtils.getClientNOS( _decoder, mkKey( true, 3 ), 3, 3 );

        q.add( msg1 );
        q.add( msg2 );
        q.add( msg3 );

        Iterator<Event> it = q.iterator();

        assertTrue( it.hasNext() );
        Event m1B = it.next();
        assertSame( msg1, m1B );

        assertTrue( it.hasNext() );
        Event m2B = it.next();
        assertSame( msg2, m2B );

        assertTrue( it.hasNext() );
        Event m3B = it.next();
        assertSame( msg3, m3B );

        assertFalse( it.hasNext() );
        try {
            assertNull( it.next() );
        } catch( NoSuchElementException e ) {
            // ok
        }
    }

    @Test
    public void testIterate3ElemsAfterSomePolls() { // Ringbuffers not at start of buffer

        EventQueue q = getNewQueue( 3 );

        Event msg1 = FixTestUtils.getClientNOS( _decoder, mkKey( true, 1 ), 1, 1 );
        Event msg2 = FixTestUtils.getClientNOS( _decoder, mkKey( true, 2 ), 2, 2 );
        Event msg3 = FixTestUtils.getClientNOS( _decoder, mkKey( true, 3 ), 3, 3 );
        Event msg4 = FixTestUtils.getClientNOS( _decoder, mkKey( true, 4 ), 4, 4 );
        Event msg5 = FixTestUtils.getClientNOS( _decoder, mkKey( true, 4 ), 5, 5 );

        q.add( msg1 );
        q.add( msg2 );
        q.add( msg3 );

        q.poll();
        q.poll();

        q.add( msg4 );
        q.add( msg5 );

        Iterator<Event> it = q.iterator();

        assertTrue( it.hasNext() );
        Event m3B = it.next();
        assertSame( msg3, m3B );

        assertTrue( it.hasNext() );
        Event m4B = it.next();
        assertSame( msg4, m4B );

        assertTrue( it.hasNext() );
        Event m5B = it.next();
        assertSame( msg5, m5B );

        assertFalse( it.hasNext() );
        try {
            assertNull( it.next() );
        } catch( NoSuchElementException e ) {
            // ok
        }
    }

    @Test public void testMixedThreadedA() { doTestMixedThreaded( 3, 1 ); }

    @Test
    public void testThreaded3_1() {
        doTestThreaded( 3, 1 );
    }

    protected long count( Set<ProducerRunnable> producers ) {
        long cnt = 0;

        for ( ProducerRunnable pr : producers ) {

            cnt += pr.sent();
        }

        return cnt;
    }

    protected long countErrs( Set<ProducerRunnable> producers ) {
        long cnt = 0;

        for ( ProducerRunnable pr : producers ) {

            cnt += pr.getErrs();
        }

        return cnt;
    }

    protected void doTestMixedThreaded( int count, int threads ) {

        threads = getRestrictedThreadCountByCoresAvail( threads );

        int total = count * threads;

        EventQueue q = getNewQueue( count / 4 + 1024 );

        CountDownLatch cdl = new CountDownLatch( total );

        CyclicBarrier cb = new CyclicBarrier( threads + 1 );

        ConsumerRunnable c = new ConsumerRunnable( total, q, cdl, cb );

        Thread ct = new Thread( c, "Consumer" );

        Set<ProducerRunnable> producers = new LinkedHashSet<>();

        for ( int i = 0; i < threads; i++ ) {

            ProducerRunnable r = new ProducerRunnable( i, count, q, cb );

            producers.add( r );

            Thread rt = new Thread( r, "Producer" + i );
            rt.start();
        }

        ct.start();

        long consumed;
        long tmpConsumed;

        do {
            consumed = c.getConsumed();

            ThreadUtilsFactory.get().sleep( 100 );

            tmpConsumed = c.getConsumed();

            _log.info( "Waited another sec .... before wait consumed=" + consumed + " of expected " + total +
                       ", after wait consumed=" + tmpConsumed + ", latchOutstanding=" + cdl.getCount() );

        } while( consumed != total );

        try {
            cdl.await( 5, TimeUnit.SECONDS );
        } catch( InterruptedException e ) {
            // ignore
        }

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

        c.setFinished();
    }

    @SuppressWarnings( "deprecated" )
    protected void doTestThreaded( int count, int threads ) {

        threads = getRestrictedThreadCountByCoresAvail( threads );

        int total = count * threads;

        EventQueue q = getNewQueue( total );

        CyclicBarrier cb = new CyclicBarrier( threads + 1 );

        CountDownLatch cdl = new CountDownLatch( total );

        ConsumerRunnable c = new ConsumerRunnable( total, q, cdl, cb );

        Thread                ct              = new Thread( c, "Consumer" );
        Set<Thread>           producerThreads = new HashSet<>();
        Set<ProducerRunnable> producers       = new LinkedHashSet<>();

        for ( int i = 0; i < threads; i++ ) {

            ProducerRunnable r = new ProducerRunnable( i, count, q, cb );

            producers.add( r );

            Thread rt = new Thread( r, "Producer" + i );
            rt.start();

            producerThreads.add( rt );
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

        try {
            cdl.await( 1, TimeUnit.MINUTES );
        } catch( InterruptedException e ) {
            // ignore
        }

        do {
            consumed = c.getConsumed();

            ThreadUtilsFactory.get().sleep( 100 );

            tmpConsumed = c.getConsumed();

            _log.info( "Waited another sec .... before wait consumed=" + consumed + " of expected " + total +
                       ", after wait consumed=" + tmpConsumed );

        } while( consumed != tmpConsumed );

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

        c.setFinished();

        ThreadUtilsFactory.get().sleep( 100 );

        try {
            for ( Thread t : producerThreads ) {
                if ( t.isAlive() ) {
                    //noinspection deprecation
                    t.stop();
                }
            }

            //noinspection deprecation
            ct.stop();
        } catch( Exception e ) {
            // dont care
        }

        ThreadUtilsFactory.get().sleep( 100 );
    }

    protected abstract EventQueue getNewQueue( int minSize );

    protected int getRestrictedThreadCountByCoresAvail( final int requestedThreads ) {
        int threads = requestedThreads;

        if ( threads < 3 ) return threads;

        int cores = Utils.getMaxCores();

        if ( cores <= 2 ) {
            threads = 3;
        } else if ( cores <= 4 ) {
            threads = 4;
        } else if ( cores <= 8 ) {
            threads = 16;
        }

        if ( requestedThreads != threads ) {
            _log.info( "Restricted threads from " + requestedThreads + " to " + threads + " as only " + cores + " available" );
        }

        return threads;
    }
}
