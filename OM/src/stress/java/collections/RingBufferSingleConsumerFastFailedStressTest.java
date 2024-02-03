/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package collections;

import com.rr.core.collections.EventQueue;
import com.rr.core.collections.RingBufferEventQueueSingleConsumer;
import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ReusableString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.core.utils.Utils;
import com.rr.model.generated.fix.codec.Standard44DecoderOMS;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.om.warmup.FixTestUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RingBufferSingleConsumerFastFailedStressTest extends BaseTestCase {

    private static final Logger _log = LoggerFactory.create( RingBufferSingleConsumerFastFailedStressTest.class );

    private static class ConsumerRunnable implements Runnable {

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

    private static class ProducerRunnable implements Runnable {

        private final RingBufferEventQueueSingleConsumer _q;
        private final int                                _count;
        private final int                                _producerIdx;
        private final CyclicBarrier                      _cb;

        private int _errs = 0;

        private volatile int _sent;

        public ProducerRunnable( int producerIdx,
                                 int count,
                                 RingBufferEventQueueSingleConsumer q,
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

    @Ignore
    @Test
    public void testMixedThreadedE() {

        int cnt = 30;

        for ( int i = 0; i < cnt; i++ ) {
            _log.info( "Run iteration " + i );

            doTestMixedThreaded( 2500, 32 );
        }
    }

    protected void doTestMixedThreaded( int count, int threads ) {

        threads = getRestrictedThreadCountByCoresAvail( threads );

        int total = count * threads;

        RingBufferEventQueueSingleConsumer q = new RingBufferEventQueueSingleConsumer( count / threads + 1024 );

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

            ThreadUtilsFactory.get().sleep( 1000 );

            tmpConsumed = c.getConsumed();

            _log.info( "Waited another sec .... before wait consumed=" + consumed + " of expected " + total +
                       ", after wait consumed=" + tmpConsumed + ", latchOutstanding=" + cdl.getCount() +
                       ", queueSize=" + q.size() );

        } while( consumed != total );

        try {
            cdl.await( 30, TimeUnit.SECONDS );
        } catch( InterruptedException e ) {
            // ignore
        }

        // wait until all sent

        long totalSent;
        long errs;

        totalSent = count( producers );
        errs      = countErrs( producers );

        _log.info( "Waited another sec .... produced =" + totalSent + " of expected " + total + ", errs=" + errs );

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

        ThreadUtilsFactory.getLive().sleep( 100 );
    }

    protected int getRestrictedThreadCountByCoresAvail( final int requestedThreads ) {
        int threads = requestedThreads;

        int cores = Utils.getMaxCores();

        if ( threads >= cores ) threads = cores - 1;

        if ( cores <= 3 ) {
            threads = 1;
        } else if ( cores <= 4 ) {
            threads = 2;
        } else if ( cores <= 8 ) {
            threads = 6;
        }

        if ( requestedThreads != threads ) {
            _log.info( "Restricted threads from " + requestedThreads + " to " + threads + " as only " + cores + " available" );
        }

        return threads;
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
