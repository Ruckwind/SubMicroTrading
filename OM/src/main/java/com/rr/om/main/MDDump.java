/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.main;

import com.rr.core.collections.EventQueue;
import com.rr.core.dispatch.DirectDispatcherNonThreadSafe;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableString;
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
import com.rr.om.session.SessionManager;
import com.rr.om.warmup.FixTestUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * SubMicroTrading collapsed main loader
 */
public class MDDump extends BaseSMTMain {

    private static final Logger    _console = ConsoleFactory.console( MDDump.class, Level.WARN );
    private static final ErrorCode FAILED   = new ErrorCode( "MDD100", "Exception in main" );

    final   Logger               _log;
    private CMEMktDataController _proc;
    private int                  _expOrders = 1000000;
    private EventDispatcher      _inboundDispatcher;

    public static void main( String[] args ) {

        try {
            prepare( args, ThreadPriority.Main );

            MDDump smt = new MDDump();
            smt.init();

            smt.warmup();

            smt.run();

            _console.info( "Completed" );

        } catch( Exception e ) {

            _console.error( FAILED, "", e );
        }
    }

    MDDump() {
        super();
        _log = LoggerFactory.create( MDDump.class );
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

        // could load balance market data events across number of processors
        EventHandler[] handlers = { _proc };

        createCMEFastFixSessionsViaBuilder( handlers );

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

    private CMEMktDataController getProcesssor( RecoverableSession hub ) throws SessionException {

        PropertyGroup procGroup = new PropertyGroup( "proc.", null, null );

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

            private final ReusableString _debugMsg = new ReusableString();

            @Override public boolean canHandle() { return true; }

            @Override
            public void handle( Event event ) {
                handleNow( event );
            }

            @Override public void handleNow( Event event ) {
                Book book = (Book) event;
                _debugMsg.reset();
                book.dump( _debugMsg );
                _log.info( _debugMsg );
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

            @SuppressWarnings( "unchecked" )
            SingleMDSrcSubsMgr subMgr = new SingleMDSrcSubsMgr( "SubMgr", sc, () -> new BookContextImpl() );

            try {
                subMgr.addSubscriptions( _instrumentStore, subscriptionFile, 0 );
            } catch( IOException e ) {
                throw new SessionException( "Error subscribing to file " + subscriptionFile, e );
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

        ReflectUtils.setProperty( p, "_overrideSubscribeSet", "true" );

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
