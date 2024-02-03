/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.main;

import com.rr.algo.ExchangeContainerAdapter;
import com.rr.algo.t1.T1Algo;
import com.rr.core.collections.EventQueue;
import com.rr.core.dispatch.DirectDispatcherNonThreadSafe;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.logger.*;
import com.rr.core.model.*;
import com.rr.core.persister.PersisterException;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.PropertyGroup;
import com.rr.core.recovery.dma.DMARecoveryController;
import com.rr.core.session.RecoverableSession;
import com.rr.core.session.SeperateGatewaySession;
import com.rr.core.session.SessionException;
import com.rr.core.utils.*;
import com.rr.core.warmup.WarmupRegistry;
import com.rr.md.book.SingleMDSrcSubsMgr;
import com.rr.md.book.l2.L2BookDispatchAdapter;
import com.rr.md.book.l2.L2BookFactory;
import com.rr.md.us.cme.CMEBookAdapter;
import com.rr.md.us.cme.CMEMktDataController;
import com.rr.md.us.cme.WarmupCMECodec;
import com.rr.model.generated.internal.events.impl.MDEntryImpl;
import com.rr.model.generated.internal.events.impl.MDIncRefreshImpl;
import com.rr.model.generated.internal.events.impl.NewOrderSingleImpl;
import com.rr.om.main.OMProps.Tags;
import com.rr.om.recovery.DummyRecoveryController;
import com.rr.om.router.ExchangeRouter;
import com.rr.om.router.OrderRouter;
import com.rr.om.router.RoundRobinRouter;
import com.rr.om.router.SingleDestRouter;
import com.rr.om.session.SessionManager;
import com.rr.om.warmup.FixTestUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * SubMicroTrading collapsed main loader
 */
public class T1Main extends BaseSMTMain {

    private static final Logger    _console = ConsoleFactory.console( T1Main.class, Level.info );
    private static final ErrorCode FAILED   = new ErrorCode( "TIM100", "Exception in main" );

    private final Logger               _log;
    private       CMEMktDataController _proc;
    private       int                  _expOrders = 1000000;
    private       T1Algo               _t1;
    private       int                  _tickToTradeRatio;
    private       EventDispatcher      _inboundDispatcher;

    public static void main( String[] args ) {

        try {
            prepare( args, ThreadPriority.Main );

            T1Main smt = new T1Main();
            smt.init();

            smt.warmup();

            smt.run();

            _console.info( "Completed" );

        } catch( Exception e ) {

            _console.error( FAILED, "", e );
        }
    }

    T1Main() {
        super();
        _log = LoggerFactory.create( T1Main.class );
    }

    @Override
    protected void init() throws SessionException, FileException, PersisterException {

        _log.info( "SMTMain.init()" );

        super.init();

        int            warmupCount    = WarmupRegistry.instance().getWarmupCount();
        WarmupCMECodec warmupCMECodec = new WarmupCMECodec( warmupCount );
        WarmupRegistry.instance().register( warmupCMECodec );

        _expOrders = AppProps.instance().getIntProperty( OMProps.EXPECTED_ORDERS, false, 1010000 );
        presize( _expOrders );

        DateFormat utcFormat = new SimpleDateFormat( "yyyyMMdd" );

        String today = utcFormat.format( new Date() );
        FixTestUtils.setTodayStr( today );

        SessionManager     sessMgr = getSessionManager();
        RecoverableSession hub     = null;
        _proc = getProcesssor( hub );

        ExchangeContainerAdapter exchangeInboundHandler = new ExchangeContainerAdapter( "exContRouter", null );

        String[] downSessionNames = AppProps.instance().getNodes( "session.down." );
        for ( String sessName : downSessionNames ) {
            if ( !sessName.equals( "default" ) ) {
                createSession( sessName, null, true, sessMgr, exchangeInboundHandler, hub, "session.down." );
            }
        }

        String[] upSessionNames = AppProps.instance().getNodes( "session.up." );
        for ( String sessName : upSessionNames ) {
            if ( !sessName.equals( "default" ) ) {
                ClientProfile client = null;
                if ( !sessName.startsWith( "test" ) ) {
                    client = loadClientProfile( sessName );
                }
                createSession( sessName, client, false, sessMgr, _proc, hub, "session.up." );
            }
        }

        OrderRouter          router;
        RecoverableSession[] downStream = sessMgr.getDownStreamSessions();
        if ( downStream.length == 1 ) {
            router = new SingleDestRouter( downStream[ 0 ] );
        } else {
            String routerType = AppProps.instance().getProperty( OMProps.PROC_ROUTER, false, "RoundRobin" );

            if ( "RoundRobin".equalsIgnoreCase( routerType ) ) {
                router = new RoundRobinRouter( downStream );
            } else if ( "ExchangeRouter".equalsIgnoreCase( routerType ) ) {
                router = new ExchangeRouter( downStream, sessMgr );
            } else {
                throw new SMTRuntimeException( "T1Main only proc routers RoundRobin and ExchangeRouter supported" );
            }
        }

        PropertyGroup algoGroup = new PropertyGroup( "algo.", null, null );
        boolean       trace     = algoGroup.getBoolProperty( Tags.trace, false, false );

        _t1 = new T1Algo( _tickToTradeRatio, router );
        _t1.setDebug( trace );
        exchangeInboundHandler.setContainer( _t1 );
        _expOrders = AppProps.instance().getIntProperty( OMProps.EXPECTED_ORDERS, false, 1010000 );

        // start the sockets

        for ( RecoverableSession downSess : downStream ) {
            if ( downSess instanceof SeperateGatewaySession ) { // currently only ETI
                ((SeperateGatewaySession) downSess).getGatewaySession().init();
            }
            downSess.init();
        }

        RecoverableSession[] upStream = sessMgr.getUpStreamSessions();
        for ( RecoverableSession upSess : upStream ) {
            upSess.init();
        }

        _inboundDispatcher.start();
    }

    @Override
    protected Logger log() {
        return _log;
    }

    @Override
    protected void presize( int expOrders ) {
        super.presize( expOrders );

        int recycledMax = Math.min( expOrders, 50000 ); // allowing 100000 per second, assume in second get time to recycle

        int chainSize           = SizeConstants.DEFAULT_CHAIN_SIZE;
        int recycledEventChains = (recycledMax / chainSize) + 100;
        int extraAlloc          = 50;

        presize( MDEntryImpl.class, recycledEventChains, chainSize, extraAlloc );
        presize( MDIncRefreshImpl.class, recycledEventChains, chainSize, extraAlloc );
        presize( NewOrderSingleImpl.class, recycledEventChains, chainSize, extraAlloc );

        presize( LogEventSmall.class, 200, 100, 50 );
        presize( LogEventLarge.class, 10000, 100, 100 );
    }

    T1Algo getAlgo() {
        return _t1;
    }

    @SuppressWarnings( "unchecked" )
    private CMEMktDataController getProcesssor( RecoverableSession hub ) {

        PropertyGroup procGroup = new PropertyGroup( "proc.", null, null );

        _tickToTradeRatio = procGroup.getIntProperty( Tags.tickToTradeRatio, false, 10 );
        int bookLevels = procGroup.getIntProperty( Tags.bookLevels, false, 10 );

        // enqueue incremental updates pending next snapshot event
        boolean enqueueIncTicksOnGap = procGroup.getBoolProperty( Tags.enqueueIncTicksOnGap, false, false );
        String  rec                  = procGroup.getProperty( Tags.MIC, false, "XCME" ); // CME

        EventQueue queue = getQueue( procGroup, "Processor" );

        _inboundDispatcher = getProcessorDispatcher( procGroup, queue, "ProcessorDispatcher", ThreadPriority.Processor );

        _log.info( "PROCESSOR Using " + _inboundDispatcher.getClass().getSimpleName() + " with " + queue.getClass().getSimpleName() + " for Processsor" );

        L2BookFactory<CMEBookAdapter> bookFactory = new L2BookFactory<>( CMEBookAdapter.class, false, _instrumentStore, bookLevels );

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

        String subscriptionFile = procGroup.getProperty( Tags.subscriptionFile, false, null );

        CMEMktDataController p;

        if ( subscriptionFile != null ) {
            CMEMktDataController sc = new CMEMktDataController( "TestController",
                                                                rec,
                                                                _inboundDispatcher,
                                                                bookFactory,
                                                                asyncListener,
                                                                _instrumentStore,
                                                                enqueueIncTicksOnGap );

            SingleMDSrcSubsMgr subMgr = new SingleMDSrcSubsMgr( "SubMgr", sc, () -> new BookContextImpl() );

            try {
                subMgr.addSubscriptions( _instrumentStore, subscriptionFile, 0 );

            } catch( IOException e ) {
                throw new SMTRuntimeException( "Error subscribing to file " + subscriptionFile, e );
            }

            p = sc;

        } else {
            p = new CMEMktDataController( "TestController",
                                          rec,
                                          _inboundDispatcher,
                                          bookFactory,
                                          asyncListener,
                                          _instrumentStore,
                                          enqueueIncTicksOnGap );
        }

        algoDispatcher.start();

        return p;
    }

    private void run() {

        RecoverableSession[] downStream = getSessionManager().getDownStreamSessions();
        RecoverableSession[] upStream   = getSessionManager().getUpStreamSessions();

        DMARecoveryController rct = new DummyRecoveryController();

        rct.start();

        for ( RecoverableSession upSess : upStream ) {
            upSess.recover( rct );
        }

        for ( RecoverableSession downSess : downStream ) {
            downSess.recover( rct );
        }

        for ( RecoverableSession upSess : upStream ) {
            upSess.waitForRecoveryToComplete();
        }

        for ( RecoverableSession downSess : downStream ) {
            downSess.waitForRecoveryToComplete();
        }

        rct.reconcile();
        rct.commit();

        Utils.invokeGC();
        SuperpoolManager.instance().resetPoolStats();

        commonSessionConnect();

        for ( RecoverableSession downSess : downStream ) {
            if ( downSess instanceof SeperateGatewaySession ) {
                ((SeperateGatewaySession) downSess).getGatewaySession().connect();
            } else {
                downSess.connect();
            }
        }

        for ( RecoverableSession upSess : upStream ) {
            upSess.connect();
        }

        // check received fix message

        _console.info( "ENTERING MAIN LOOP - ctrl-C to stop program" );

        System.out.flush();
        System.err.flush();

        _log.info( "ENTERING MAIN LOOP - ctrl-C to stop program" );

        while( true ) {
            try {
                ThreadUtilsFactory.get().sleep( 1000 );
            } catch( Throwable t ) {
                System.out.println( t.getMessage() );
            }
        }
    }
}
