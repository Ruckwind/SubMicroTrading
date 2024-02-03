/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.threading;

import com.rr.core.collections.*;
import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ReusableString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.pool.SuperPool;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.utils.Percentiles;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.core.utils.Utils;
import com.rr.model.generated.fix.codec.Standard44DecoderOMS;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.model.generated.internal.events.recycle.ClientNewOrderSingleRecycler;
import com.rr.om.warmup.FixTestUtils;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PerfThreadPassMsg extends BaseTestCase {

    private static final Logger _log = LoggerFactory.create( PerfThreadPassMsg.class );

    private static class ConsumerRunnable implements Runnable {

        private static final long MAX_CONSUME_WAIT_MS = 1000;

        private          int            _tot;
        private          EventQueue     _q;
        private volatile boolean        _finished = false;
        private          int            _count    = 0;
        private          long           _totQty   = 0;
        private          long[]         _stats;
        private          ThreadPriority _priority;

        public ConsumerRunnable( int tot, EventQueue q, ThreadPriority priority ) {
            _tot      = tot;
            _q        = q;
            _stats    = new long[ _tot ];
            _priority = priority;
        }

        @Override
        public void run() {
            ThreadUtilsFactory.get().setPriority( Thread.currentThread(), _priority );

            long last = ClockFactory.get().currentTimeMillis();
            long now;

            SuperPool<ClientNewOrderSingleImpl> nosSP       = SuperpoolManager.instance().getSuperPool( ClientNewOrderSingleImpl.class );
            ClientNewOrderSingleRecycler        nosRecycler = new ClientNewOrderSingleRecycler( nosSP.getChainSize(), nosSP );

            ClientNewOrderSingleImpl nos;

            Event m;

            while( _count < _tot ) {

                long received;

                do {
                    m = _q.poll();

                    received = Utils.nanoTime();

                    now = ClockFactory.get().currentTimeMillis();

                    if ( now - last > MAX_CONSUME_WAIT_MS ) {
                        _finished = true;
                    }

                } while( m == null && !_finished );

                if ( m != null ) {

                    nos = (ClientNewOrderSingleImpl) m;

                    long delay = received - nos.getOrderReceived();
                    _stats[ _count ] = delay;

                    _totQty += nos.getOrderQty();

                    ++_count;

                    nosRecycler.recycle( nos );
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

        public long[] getTimes() {
            return _stats;
        }

        @SuppressWarnings( "unused" )
        public long getTotalQty() {
            return _totQty;
        }
    }

    private static class ProducerRunnable implements Runnable {

        private final EventQueue     _q;
        private final int            _producerIdx;
        private final int            _producerDelayMS;
        private       int            _count;
        private       int            _errs   = 0;
        private       int            _sent;
        private       ReusableString _buffer = new ReusableString( 256 );
        private       ThreadPriority _priority;

        public ProducerRunnable( int producerIdx,
                                 int count,
                                 EventQueue q,
                                 int producerDelayMS,
                                 ThreadPriority p ) {
            _q               = q;
            _count           = count;
            _sent            = 0;
            _producerIdx     = producerIdx;
            _producerDelayMS = producerDelayMS;
            _priority        = p;
        }

        @Override
        public void run() {
            ThreadUtilsFactory.get().setPriority( Thread.currentThread(), _priority );
            int baseQty = _producerIdx * _count;

            Standard44DecoderOMS decoder = FixTestUtils.getOMSDecoder44();
            ReusableString       key     = new ReusableString( 20 );

            for ( int i = 0; i < _count; ++i ) {
                decoder.setReceived( Utils.nanoTime() );
                mkKey( key, true, i );
                key.append( _producerIdx );
                Event msg = FixTestUtils.getClientNOS( _buffer, decoder, key, baseQty + i, 1, null );
                if ( msg != null ) {
                    ClientNewOrderSingleImpl nos = (ClientNewOrderSingleImpl) msg;
                    nos.setOrderReceived( Utils.nanoTime() );
                    if ( _q.add( msg ) == false ) {
                        ++_errs;
                    } else {
                        ++_sent;
                    }
                } else {
                    ++_errs;
                }
                if ( _producerDelayMS != 0 ) {
                    ThreadUtilsFactory.get().sleep( _producerDelayMS );
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

    static {
        // dont use the default thread config unless test requires the thread isolation of main SMT process
        ThreadUtilsFactory.get().init( ThreadUtilsFactory.get().DEFAULT_THREAD_CONFIG );
    }

    static void mkKey( ReusableString key, boolean isClient, int i ) {
        key.reset();

        key.append( (isClient) ? 'C' : 'M' );
        key.append( "SOMEKEY" );
        key.append( 1000000 + i );
    }

    @Test
    public void testThreaded() {

        doTestThreaded( 1000, 1, 0 );
        doTestThreaded( 5000000, 1, 0 );
        doTestThreaded( 1000, 1, 0 );
        doTestThreaded( 1000, 2, 10 );
        doTestThreaded( 1000, 4, 10 );
        doTestThreaded( 100000, 2, 1 );
        doTestThreaded( 100, 4, 100 );
        doTestThreaded( 100, 4, 100 );
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

    private void doTestThreaded( int count, int threads, int prodDelayMS ) {

        int total = count * threads;

        _log.info( "\n===========================================================================\n" );

        _log.info( "\nCONCURRENT LINKED QUEUE tot=" + total );
        EventQueue qDoug = new JavaConcEventQueue();
        long       dougQ = perf( "SUN", count, threads, total, qDoug, prodDelayMS );

        _log.info( "\nNEW LINKED QUEUE tot=" + total );
        ConcLinkedEventQueueSingle q     = new ConcLinkedEventQueueSingle();
        long                       concQ = perf( "RRCAS", count, threads, total, q, prodDelayMS );

        _log.info( "\nNEW SYNC LINKED QUEUE tot=" + total );
        NonBlockingSyncQueue n     = new NonBlockingSyncQueue();
        long                 multQ = perf( "RRSYNC", count, threads, total, n, prodDelayMS );

        _log.info( "\nNEW BLOCK LINKED QUEUE tot=" + total );
        BlockingSyncQueue b      = new BlockingSyncQueue();
        long              blockQ = perf( "RRBLOCK", count, threads, total, b, prodDelayMS );

        _log.info( "RRCASQ   delay=" + prodDelayMS + ", totCnt=" + total + ", over threads=" + threads + ", time=" + concQ / 1000 + " secs" );
        _log.info( "SUNQ     delay=" + prodDelayMS + ", totCnt=" + total + ", over threads=" + threads + ", time=" + dougQ / 1000 + " secs" );
        _log.info( "RRSYNC   delay=" + prodDelayMS + ", totCnt=" + total + ", over threads=" + threads + ", time=" + multQ / 1000 + " secs" );
        _log.info( "RRBLOCK  delay=" + prodDelayMS + ", totCnt=" + total + ", over threads=" + threads + ", time=" + blockQ / 1000 + " secs" );
    }

    private long perf( String comment, int count, int threads, int total, EventQueue q, int producerDelayMS ) {
        ConsumerRunnable c = new ConsumerRunnable( total, q, ThreadPriority.Processor );

        Thread ct = new Thread( c, "Consumer" );

        Set<ProducerRunnable> producers = new LinkedHashSet<>();

        SuperPool<ClientNewOrderSingleImpl> nosSP = SuperpoolManager.instance().getSuperPool( ClientNewOrderSingleImpl.class );
        nosSP.init( 200, 1000, 50 );

        ThreadPriority[] priorities = { ThreadPriority.SessionInbound1, ThreadPriority.SessionInbound2,
                                        ThreadPriority.SessionOutbound1, ThreadPriority.SessionOutbound2 };

        if ( threads > priorities.length ) throw new RuntimeException( "too many threads" );

        for ( int i = 0; i < threads; i++ ) {

            ProducerRunnable r = new ProducerRunnable( i, count, q, producerDelayMS, priorities[ i ] );

            producers.add( r );
        }

        Utils.invokeGC();

        long start = ClockFactory.get().currentTimeMillis();

        int inst = 0;

        ct.start();

        for ( ProducerRunnable prod : producers ) {

            Thread rt = new Thread( prod, "Thread" + (inst++) );
            rt.start();
        }

        // wait until all sent

        long totalSent;
        long errs;

        do {
            ThreadUtilsFactory.get().sleep( 1000 );

            totalSent = count( producers );
            errs      = countErrs( producers );

            // _log.info( "Waited another sec .... produced =" + totalSent + " of expected " + total + ", errs=" + errs );

        } while( totalSent + errs < total );

        assertEquals( 0, errs );
        assertEquals( total, totalSent );

        long consumed;
        long tmpConsumed;

        do {
            consumed = c.getConsumed();

            ThreadUtilsFactory.get().sleep( 2000 );
            // two waits incase GC occurs during delay
            ThreadUtilsFactory.get().sleep( 2000 );

            tmpConsumed = c.getConsumed();

            // _log.info( "Waited another sec .... before wait consumed=" + consumed + " of expected " + total +
            //           ", after wait consumed=" + tmpConsumed );

        } while( consumed != tmpConsumed );

        assertTrue( "Consumed " + c.getConsumed() + " of " + total + ", threads=" + threads, c.finished() );
        assertEquals( total, c.getConsumed() );

        long end = ClockFactory.get().currentTimeMillis();

        long[] stats = c.getTimes();

        Percentiles p = new Percentiles( stats );

        System.out.println( "[" + comment + "]  NanoSecond stats " + " count=" + total + ", delay=" + producerDelayMS +
                            ", med=" + p.median() +
                            ", ave=" + p.getAverage() +
                            ", min=" + p.getMinimum() +
                            ", max=" + p.getMaximum() +
                            "\n                 " +
                            ", p99=" + p.calc( 99 ) +
                            ", p95=" + p.calc( 95 ) +
                            ", p90=" + p.calc( 90 ) +
                            ", p80=" + p.calc( 80 ) +
                            ", p70=" + p.calc( 70 ) +
                            ", p50=" + p.calc( 50 ) + "\n" );

        nosSP.logStats();

        return end - start;
    }
}
