/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.fastfix.cme;

import com.rr.algo.t1.T1Algo;
import com.rr.core.codec.FixEncoder;
import com.rr.core.dispatch.DirectDispatcherNonThreadSafe;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.Reusable;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Logger;
import com.rr.core.model.Book;
import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;
import com.rr.core.pool.SuperPool;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.utils.Percentiles;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.Utils;
import com.rr.md.book.l2.L2BookDispatchAdapter;
import com.rr.md.book.l2.L2BookFactory;
import com.rr.md.us.cme.CMEBookAdapter;
import com.rr.md.us.cme.CMEMktDataController;
import com.rr.model.generated.fix.codec.CMEEncoder;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;
import com.rr.model.generated.internal.events.impl.MDEntryImpl;
import com.rr.model.generated.internal.events.impl.MDIncRefreshImpl;
import com.rr.model.generated.internal.events.interfaces.NewOrderSingle;
import com.rr.om.dummy.warmup.DummyInstrumentLocator;
import com.rr.om.main.BaseSMTMain;
import com.rr.om.router.OrderRouter;
import com.rr.om.router.SingleDestRouter;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PerfTestTickToTrade extends BaseTestCase {

    private static final Logger _log = ConsoleFactory.console( PerfTestTickToTrade.class );
    private final List<byte[]>                  _templateRequests = new ArrayList<>();
    private int    _iter  = 5;
    private int    _count = 1000000;
    private int    _curIdx;
    private long[] _time;
    private       SimFastFixMDGenWithMsgHandler _sender;
    private       CMEMktDataController          _mktDataController;
    private       T1Algo                        _t1;

    private boolean _nanoTiming;

    private EventHandler _simSession = new EventHandler() {

        byte[] _outBuf = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        FixEncoder _encoder = new CMEEncoder( _outBuf, 0 );
        private AllEventRecycler _recycler = new AllEventRecycler();

        @Override
        public boolean canHandle() {
            return true;
        }

        @Override
        public void handle( Event msg ) {
            handleNow( msg );
        }

        @Override
        public void handleNow( Event msg ) {
            if ( msg.getReusableType().getSubId() == EventIds.ID_NEWORDERSINGLE ) {

                _encoder.encode( msg );

                long time = Utils.nanoTime() - ((NewOrderSingle) msg).getOrderReceived();

                store( (int) time );
            }

            _recycler.recycle( msg );
        }

        @Override public String getComponentId() { return null; }

        @Override public void threadedInit() { /* nothing */ }
    };

    private static <T extends Reusable<T>> void presize( Class<T> aclass, int chains, int chainSize, int extraAlloc ) {
        SuperPool<T> sp = SuperpoolManager.instance().getSuperPool( aclass );
        sp.init( chains, chainSize, extraAlloc );
    }

    public PerfTestTickToTrade() throws IOException {
        // nothing

        BaseSMTMain.loadSampleData( "./data/cme/sampleMD.dat", _templateRequests, 30 );

    }

    public void sendNext( int idx ) throws IOException {
        _sender.sendNext( idx );
    }

    @Test
    public void testSync() throws IOException {
        doTest( _iter, _count, true, 10, 1, 100 );
        doTest( _iter, _count, false, 10, 1, 100 );
    }

    /**
     * invoked when tick has been decoded
     *
     * @param msg
     * @param templateId
     */
    protected void processEvent( Event msg, int templateId ) {
        _mktDataController.handle( msg );
    }

    protected void store( int durationNANOS ) {
        if ( _nanoTiming && _curIdx < _time.length ) {
            _time[ _curIdx++ ] = durationNANOS;
        }
    }

    T1Algo getAlgo() {
        return _t1;
    }

    private void doTest( int iter, int max, boolean nanoTiming, int numMdEntries, int pMapInitSize, int chainSize ) throws IOException {
        int chains     = max / chainSize;
        int extraAlloc = 10;

        presize( MDIncRefreshImpl.class, chains, chainSize, extraAlloc );
        presize( MDEntryImpl.class, chains, chainSize, extraAlloc );

        OrderRouter router = new SingleDestRouter( _simSession );
        _t1 = new T1Algo( 2, router );

        _mktDataController = getMktDataController( ThreadPriority.ExchangeSimProcessor );

        _sender = new SimFastFixMDGenWithMsgHandler( _templateRequests, nanoTiming, this::processEvent );

        for ( int i = 0; i < iter; ++i ) {
            runTest( max, nanoTiming, numMdEntries, pMapInitSize, chainSize );
        }
    }

    private CMEMktDataController getMktDataController( ThreadPriority priority ) {

        EventDispatcher inboundDispatcher = new DirectDispatcherNonThreadSafe();

        final DummyInstrumentLocator  locator     = new DummyInstrumentLocator();
        L2BookFactory<CMEBookAdapter> bookFactory = new L2BookFactory<>( CMEBookAdapter.class, false, locator, 10 );

        EventDispatcher algoDispatcher = new DirectDispatcherNonThreadSafe();

        algoDispatcher.setHandler( new EventHandler() {

            @Override public boolean canHandle() { return true; }

            @Override
            public void handle( Event event ) {
                handleNow( event );
            }

            @Override public void handleNow( Event event ) {
                Book book = (Book) event;
                getAlgo().changed( book );
            }

            @Override public String getComponentId() { return null; }

            @Override public void threadedInit() { /* nothing */ }
        } );

        L2BookDispatchAdapter<CMEBookAdapter> asyncListener = new L2BookDispatchAdapter<>( algoDispatcher );

        CMEMktDataController ctlr = new CMEMktDataController( "TestController", "2", inboundDispatcher, bookFactory, asyncListener, locator, false );

        algoDispatcher.start();
        inboundDispatcher.start();

        return ctlr;
    }

    private void runTest( int max, boolean nanoTiming, int numMDEntries, int initPMapSize, int chainSize ) throws IOException {

        _nanoTiming = nanoTiming;

        _time = new long[ max ];

        _curIdx = 0;

        _log.info( "Forcing gc START" );
        System.gc();
        _log.info( "Forcing gc COMPLETE" );

        _log.info( "START TEST ==================" );

        long start = Utils.nanoTime();

        for ( int i = 0; i < max; i++ ) {
            sendNext( i );
        }

        long end = Utils.nanoTime();

        long timeMicros = (end - start) / 1000;

        System.out.println( "Tick Decode ticks=" + max + ", order=" + _curIdx + ", aveTickUSEC=" + ((timeMicros) / max) );

        if ( nanoTiming ) {
            Percentiles p = new Percentiles( _time, _curIdx );

            System.out.println( "TickToTrade Decode NanoSecond stats " + " count=" + _curIdx +
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
        }

        _log.info( "Forcing gc START" );
        System.gc();
        _log.info( "Forcing gc COMPLETE" );
    }
}
