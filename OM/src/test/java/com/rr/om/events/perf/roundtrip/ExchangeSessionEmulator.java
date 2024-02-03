/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.events.perf.roundtrip;

import com.rr.core.collections.EventQueue;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.core.utils.Utils;
import com.rr.model.generated.fix.codec.Standard44DecoderOMS;
import com.rr.model.generated.fix.codec.Standard44Encoder;
import com.rr.model.generated.internal.events.impl.MarketNewOrderAckImpl;
import com.rr.model.generated.internal.events.impl.MarketNewOrderSingleImpl;
import com.rr.model.generated.internal.events.recycle.MarketNewOrderSingleRecycler;
import com.rr.om.processor.EventProcessor;
import com.rr.om.processor.EventProcessorImpl;
import com.rr.om.warmup.FixTestUtils;

public class ExchangeSessionEmulator implements EventHandler {

    private static class ExchangeSessionThread extends Thread {

        private static final long MAX_CONSUME_WAIT_MS = 1000;

        private          int        _tot;
        private          EventQueue _sessQ;
        private volatile boolean    _finished = false;
        private          int        _count    = 0;
        private          long       _totQty   = 0;
        private          long[]     _stats;

        private int _ackDelayMS;

        private EventProcessor _proc;

        private ViewString _baseOrderId = new ReusableString( "ORDID" );
        private ViewString _baseExecId  = new ReusableString( "EXECID" );

        public ExchangeSessionThread( int tot, EventQueue sessQ, int ackDelayMS ) {

            super( "EXSESS" );

            _tot        = tot;
            _sessQ      = sessQ;
            _ackDelayMS = ackDelayMS;
            _stats      = new long[ _tot ];
        }

        @Override
        public void run() {
            long last = ClockFactory.get().currentTimeMillis();
            long now;

            Standard44DecoderOMS decoder = FixTestUtils.getOMSDecoder44();

            SuperpoolManager             spm         = SuperpoolManager.instance();
            MarketNewOrderSingleRecycler nosRecycler = spm.getRecycler( MarketNewOrderSingleRecycler.class, MarketNewOrderSingleImpl.class );

            MarketNewOrderSingleImpl nos;
            MarketNewOrderAckImpl    ack;

            ReusableString buffer  = new ReusableString( 150 );
            ReusableString orderId = new ReusableString( SizeConstants.DEFAULT_MARKETORDERID_LENGTH );
            ReusableString execId  = new ReusableString( SizeConstants.DEFAULT_EXECID_LENGTH );

            final byte[]      buf     = new byte[ 512 ];
            Standard44Encoder encoder = new Standard44Encoder( (byte) '4', (byte) '4', buf );
            encoder.setNanoStats( false );

            Event m;

            boolean poll = Utils.getMaxCores() > 3;

            while( _count < _tot ) {

                do {
                    if ( poll ) {
                        m = _sessQ.poll();
                    } else {
                        m = _sessQ.next();
                    }

                    now = ClockFactory.get().currentTimeMillis();

                    if ( now - last > MAX_CONSUME_WAIT_MS ) {
                        _finished = true;
                    }

                } while( m == null && !_finished );

                if ( m != null ) {

                    nos = (MarketNewOrderSingleImpl) m;
                    encoder.encode( nos );
                    long nowNano = Utils.nanoTime();
                    nos.getSrcEvent().setOrderSent( nowNano );

                    long delay = nowNano - nos.getOrderReceived();
                    _stats[ _count ] = delay;

                    _totQty += nos.getOrderQty();

                    ++_count;

                    orderId.copy( _baseOrderId );
                    orderId.append( _count );
                    execId.copy( _baseExecId );
                    execId.append( _count );

                    if ( _ackDelayMS > 0 ) {
                        ThreadUtilsFactory.get().sleep( _ackDelayMS );
                    }

                    ack = FixTestUtils.getMarketACK( buffer, decoder, nos.getClOrdId(), nos.getOrderQty(), nos.getPrice(), orderId, execId );

                    _proc.handle( ack );

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

        public void setProc( EventProcessorImpl proc ) {
            _proc = proc;
        }
    }

    private final EventQueue            _outQ;
    private final ExchangeSessionThread _exSimulator;
    private final String                _name = "ExSimEmul";

    public ExchangeSessionEmulator( int tot, EventQueue sessQ, int ackDelayMS ) {
        _exSimulator = new ExchangeSessionThread( tot, sessQ, ackDelayMS );
        _exSimulator.setDaemon( true );
        _outQ = sessQ;

        _exSimulator.start();
    }

    @Override
    public boolean canHandle() {
        return true;
    }

    @Override
    public void handle( Event msg ) {
        _outQ.add( msg );
    }

    @Override
    public void handleNow( Event msg ) {
        _outQ.add( msg );
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
        return _exSimulator.finished();
    }

    public int getConsumed() {
        return _exSimulator.getConsumed();
    }

    public long[] getTimes() {
        return _exSimulator.getTimes();
    }

    public void setProcessor( EventProcessorImpl proc ) {
        _exSimulator.setProc( proc );
    }
}
