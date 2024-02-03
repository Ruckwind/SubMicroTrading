/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.fastfix.cme;

import com.rr.core.collections.ConcLinkedEventQueueSingle;
import com.rr.core.collections.EventQueue;
import com.rr.core.dispatch.DirectDispatcher;
import com.rr.core.dispatch.DirectDispatcherNonThreadSafe;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.dispatch.ThreadedDispatcher;
import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.Reusable;
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
import com.rr.model.generated.internal.events.impl.MDEntryImpl;
import com.rr.model.generated.internal.events.impl.MDIncRefreshImpl;
import com.rr.om.dummy.warmup.DummyInstrumentLocator;
import com.rr.om.main.BaseSMTMain;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PerfTestTickDecodeBookUpdate extends BaseTestCase {

    private static final Logger _log = ConsoleFactory.console( PerfTestTickDecodeBookUpdate.class );
    private final List<byte[]>                  _templateRequests = new ArrayList<>();
    private int    _iter  = 5;
    private int    _count = 1000000;
    private int    _curIdx;
    private long[] _time;
    private       SimFastFixMDGenWithMsgHandler _sender;
    private       CMEMktDataController          _mktDataController;

    private boolean _nanoTiming;

    private static <T extends Reusable<T>> void presize( Class<T> aclass, int chains, int chainSize, int extraAlloc ) {
        SuperPool<T> sp = SuperpoolManager.instance().getSuperPool( aclass );
        sp.init( chains, chainSize, extraAlloc );
    }

    public PerfTestTickDecodeBookUpdate() throws IOException {
        // nothing

        BaseSMTMain.loadSampleData( "./data/cme/sampleMD.dat", _templateRequests, 30 );

    }

    public void sendNext( int idx ) throws IOException {
        _sender.sendNext( idx );
    }

    @Test
    public void testSync() throws IOException {
        _time = new long[ _count * 2 ];

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

    protected void store( int durationNanos ) {
        if ( _nanoTiming && _curIdx < _time.length ) {
            _time[ _curIdx++ ] = durationNanos;
        }
    }

    private void doTest( int iter, int max, boolean nanoTiming, int numMdEntries, int pMapInitSize, int chainSize ) throws IOException {
        int chains     = max / chainSize;
        int extraAlloc = 10;

        presize( MDIncRefreshImpl.class, chains, chainSize, extraAlloc );
        presize( MDEntryImpl.class, chains, chainSize, extraAlloc );

        _mktDataController = getMktDataController( false, ThreadPriority.Other );

        _sender = new SimFastFixMDGenWithMsgHandler( _templateRequests, nanoTiming, this::processEvent );

        for ( int i = 0; i < iter; ++i ) {
            runTest( max, nanoTiming, numMdEntries, pMapInitSize, chainSize );
        }
    }

    private CMEMktDataController getMktDataController( boolean async, ThreadPriority priority ) {

        EventDispatcher inboundDispatcher;

        if ( async ) {
            EventQueue queue = new ConcLinkedEventQueueSingle();
            inboundDispatcher = new ThreadedDispatcher( "SimMktDataDispatcher", queue, priority );
        } else {
            inboundDispatcher = new DirectDispatcher();
        }

        final DummyInstrumentLocator  instrumentLocator = new DummyInstrumentLocator();
        L2BookFactory<CMEBookAdapter> bookFactory       = new L2BookFactory<>( CMEBookAdapter.class, false, instrumentLocator, 10 );

        EventDispatcher algoDispatcher = new DirectDispatcherNonThreadSafe();

        algoDispatcher.setHandler( new EventHandler() {

            @Override public String getComponentId() { return null; }

            @Override
            public void handle( Event event ) {
                handleNow( event );
            }

            @Override public void handleNow( Event event ) {
                Book book = (Book) event;

// @TODO REVISIT

//                long time = Utils.nanoTime() - book.getLastTickInNanos();
//
//                store( (int) time );
            }

            @Override public boolean canHandle() { return true; }

            @Override public void threadedInit() { /* nothing */ }
        } );

        L2BookDispatchAdapter<CMEBookAdapter> asyncListener = new L2BookDispatchAdapter<>( algoDispatcher );

        CMEMktDataController ctlr = new CMEMktDataController( "TestController", "2", inboundDispatcher, bookFactory, asyncListener, instrumentLocator, false );

        algoDispatcher.start();
        inboundDispatcher.start();

        return ctlr;
    }

    private void runTest( int max, boolean nanoTiming, int numMDEntries, int initPMapSize, int chainSize ) throws IOException {

        _nanoTiming = nanoTiming;

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

        System.out.println( "Tick Decode cnt=" + max + ", ave=" + ((timeMicros) / max) );

        if ( nanoTiming ) {
            Percentiles p = new Percentiles( _time );

            System.out.println( "Tick Decode NanoSecond stats " + " count=" + max +
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
