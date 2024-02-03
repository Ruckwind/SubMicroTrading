/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.events.perf.roundtrip;

import com.rr.core.collections.EventQueue;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ReusableString;
import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.core.utils.Utils;
import com.rr.model.generated.fix.codec.Standard44DecoderOMS;
import com.rr.model.generated.fix.codec.Standard44Encoder;
import com.rr.model.generated.internal.events.impl.ClientNewOrderAckImpl;
import com.rr.model.generated.internal.events.recycle.ClientNewOrderAckRecycler;
import com.rr.om.processor.EventProcessor;
import com.rr.om.warmup.FixTestUtils;

/**
 * emulate a client session, has an in thread which generates NOS
 * and an out thread which encodes the client ACK
 * measures time from CNOS decoded to ACK encoded
 *
 * @author Richard Rose
 */
public class ClientSessionEmulator implements EventHandler {

    private static class ClientNOSGenerator extends Thread {

        private final EventProcessor _proc;
        private final int            _producerIdx;
        private final int            _producerDelayMS;
        private       int            _count;
        private       int            _errs   = 0;
        private       int            _sent;
        private       ReusableString _buffer = new ReusableString( 256 );
        private       long[]         _stats;
        private       EventHandler   _emulator;

        public ClientNOSGenerator( int producerIdx,
                                   int count,
                                   EventProcessor proc,
                                   int producerDelayMS,
                                   EventHandler emulator ) {

            super( "CLIENT_IN_" + producerIdx );

            _proc            = proc;
            _count           = count;
            _sent            = 0;
            _producerIdx     = producerIdx;
            _producerDelayMS = producerDelayMS;
            _stats           = new long[ _count ];
            _emulator        = emulator;
        }

        @Override
        public void run() {

            Standard44DecoderOMS decoder = FixTestUtils.getOMSDecoder44();

            ReusableString key = new ReusableString( 20 );

            for ( int i = 0; i < _count; ++i ) {
                Thread.yield();
                decoder.setReceived( Utils.nanoTime() );
                mkKey( key, true, i, _producerIdx );
                key.append( _producerIdx );
                Event msg = FixTestUtils.getClientNOS( _buffer, decoder, key, 1, 1, _emulator );
                if ( msg != null ) {
                    _proc.handle( msg );
                    ++_sent;
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

        public long[] stats() {
            return _stats;
        }
    }

    private static class ClientSenderEmulator extends Thread {

        private static final long MAX_CONSUME_WAIT_MS = 1000;

        private          int        _tot;
        private          EventQueue _q;
        private volatile boolean    _finished = false;
        private          int        _count    = 0;
        private          long       _totQty   = 0;
        private          long[]     _stats;

        private ClientNewOrderAckRecycler ackRecycler = SuperpoolManager.instance().getRecycler( ClientNewOrderAckRecycler.class,
                                                                                                 ClientNewOrderAckImpl.class );

        public ClientSenderEmulator( int sessionNum, int count, EventQueue q ) {
            super( "CLIENT_OUT_" + sessionNum );

            _tot   = count;
            _q     = q;
            _stats = new long[ _tot ];
        }

        @Override
        public void run() {
            long last = ClockFactory.get().currentTimeMillis();
            long now;

            ClientNewOrderAckImpl ack;

            final byte[]      buf     = new byte[ 512 ];
            Standard44Encoder encoder = new Standard44Encoder( (byte) '4', (byte) '4', buf );
            encoder.setNanoStats( false );

            Event m;

            boolean poll = Utils.getMaxCores() > 3;

            while( _count < _tot ) {

                do {
                    if ( poll ) {
                        m = _q.poll();
                    } else {
                        m = _q.next();
                    }

                    now = ClockFactory.get().currentTimeMillis();

                    if ( now - last > MAX_CONSUME_WAIT_MS ) {
                        _finished = true;
                    }

                } while( m == null && !_finished );

                if ( m != null ) {

                    ack = (ClientNewOrderAckImpl) m;
                    encoder.encode( ack );
                    long nowNano = Utils.nanoTime();
                    long nosLat  = ack.getOrderSent() - ack.getOrderReceived();
                    long ackLat  = nowNano - ack.getAckReceived();

                    _stats[ _count ] = (nosLat + ackLat);

                    _totQty += ack.getOrderQty();

                    ++_count;

                    ackRecycler.recycle( ack );
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
    private final EventQueue           _outboundQ;
    private final ClientNOSGenerator   _clientInEmulation;
    private final ClientSenderEmulator _clientOutEmulation;
    private final String               _name;

    static void mkKey( ReusableString key, boolean isClient, int i, int producerIdx ) {
        key.reset();

        key.append( (isClient) ? 'C' : 'M' );
        key.append( "SOMEKEY" );
        key.append( producerIdx );
        key.append( 1000000 + i );
    }

    public ClientSessionEmulator( EventQueue outboundQ, int producerIdx, int count, EventProcessor proc, int producerDelayMS ) {

        _name = "CLIENT" + producerIdx;

        _outboundQ = outboundQ;

        _clientInEmulation = new ClientNOSGenerator( producerIdx, count, proc, producerDelayMS, this );
        _clientInEmulation.setDaemon( true );

        _clientOutEmulation = new ClientSenderEmulator( producerIdx, count, _outboundQ );
        _clientOutEmulation.setDaemon( false );
    }

    @Override
    public boolean canHandle() {
        return true;
    }

    @Override
    public void handle( Event msg ) {
        _outboundQ.add( msg );
    }

    @Override
    public void handleNow( Event msg ) {
        _outboundQ.add( msg );
    }

    @Override
    public String getComponentId() {
        return _name;
    }

    @Override
    public void threadedInit() {
        // nothing
    }

    public boolean finished() {
        return _clientOutEmulation.finished();
    }

    public int getConsumed() {
        return _clientOutEmulation.getConsumed();
    }

    public long getErrs() {
        return _clientInEmulation.getErrs();
    }

    public long[] getInboundStats() {
        return _clientInEmulation.stats();
    }

    public long[] getRoundTripTimes() {
        return _clientOutEmulation.getTimes();
    }

    public long sent() {
        return _clientInEmulation.sent();
    }

    public void start() {
        _clientOutEmulation.start();
        _clientInEmulation.start();
    }
}
