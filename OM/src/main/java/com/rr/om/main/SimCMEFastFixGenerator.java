/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.main;

import com.rr.core.codec.FixDecoder;
import com.rr.core.codec.FixEncoder;
import com.rr.core.collections.EventQueue;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableString;
import com.rr.core.logger.*;
import com.rr.core.persister.PersisterException;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.properties.PropertyGroup;
import com.rr.core.recovery.dma.DMARecoveryController;
import com.rr.core.session.*;
import com.rr.core.utils.*;
import com.rr.core.warmup.WarmupRegistry;
import com.rr.md.us.cme.CMEFastFixSession;
import com.rr.model.generated.internal.events.impl.MDEntryImpl;
import com.rr.model.generated.internal.events.impl.MDIncRefreshImpl;
import com.rr.model.generated.internal.events.impl.NewOrderSingleImpl;
import com.rr.om.main.OMProps.Tags;
import com.rr.om.recovery.DummyRecoveryController;
import com.rr.om.session.SessionManager;
import com.rr.om.session.fixsocket.FixSocketConfig;
import com.rr.om.session.fixsocket.NonBlockingFixSocketSession;
import com.rr.om.warmup.FixTestUtils;
import com.rr.om.warmup.sim.BaseFixSimProcess;
import com.rr.om.warmup.sim.SimCMEFastFixSender;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * SubMicroTrading T1 Simulator
 * <p>
 * publish market data to SMT
 */
public class SimCMEFastFixGenerator extends BaseSMTMain {

    private static final Logger    _console   = ConsoleFactory.console( SimCMEFastFixGenerator.class, Level.WARN );
    private static final ErrorCode FAILED     = new ErrorCode( "T1S100", "Exception in main" );
    private static final ErrorCode FAIL_LOGIN = new ErrorCode( "T1S200", "Max logIn wait exceeded" );
    protected final List<byte[]>        _templateRequests = new ArrayList<>();
    private final   Logger              _log              = LoggerFactory.create( SimCMEFastFixGenerator.class );
    private         SimCMEFastFixSender _fastFixSender;
    private         int                 _expOrders        = 1000000;

    public static void main( String[] args ) {

        try {
            prepare( args, ThreadPriority.ClientSimulatorOut );

            _console.info( "T1Simulator Started" );

            SimCMEFastFixGenerator smt = new SimCMEFastFixGenerator();
            smt.init();

            smt.warmup();
            smt.run();

            _console.info( "T1Simulator Completed" );

        } catch( Exception e ) {

            _console.error( FAILED, "", e );
        }
    }

    SimCMEFastFixGenerator() {
        super();
    }

    @Override
    protected NonBlockingFixSocketSession createNonBlockingFixSession( FixSocketConfig socketConfig,
                                                                       EventRouter inboundRouter,
                                                                       FixEncoder encoder,
                                                                       FixDecoder decoder,
                                                                       FixDecoder fullDecoder,
                                                                       String name,
                                                                       MultiSessionThreadedDispatcher dispatcher,
                                                                       MultiSessionThreadedReceiver receiver,
                                                                       EventQueue dispatchQueue ) {

        throw new SMTRuntimeException( "Misconfiguration cannot use non blocking fix sessions with this Main (use SimMultiClientMain)" );
    }

    @Override
    protected void init() throws SessionException, FileException, PersisterException {

        _log.info( "T1Simulator.init()" );

        super.init();

        _expOrders = AppProps.instance().getIntProperty( OMProps.EXPECTED_ORDERS, false, 1010000 );

        presize( _expOrders );

        PropertyGroup simGroup      = new PropertyGroup( "sim.warmup.", null, null );
        int           warmHeartbeat = simGroup.getIntProperty( Tags.heartBeatIntSecs, false, 30 );
        BaseFixSimProcess.setHeartbeat( warmHeartbeat );

        DateFormat utcFormat = new SimpleDateFormat( "yyyyMMdd" );

        String today = utcFormat.format( new Date() );
        FixTestUtils.setTodayStr( today );

        SessionManager sessMgr          = getSessionManager();
        String[]       downSessionNames = AppProps.instance().getNodes( "session.down." );

        for ( String sessName : downSessionNames ) {
            if ( !sessName.equals( "default" ) ) {
                createSession( sessName, null, true, sessMgr, null, null, "session.down." );
            }
        }

        // start the sockets

        RecoverableSession[] downStream = sessMgr.getDownStreamSessions();
        for ( RecoverableSession downSess : downStream ) {
            downSess.init();
        }
    }

    @Override
    protected void initWarmup() {
        super.initWarmup();
    }

    @Override
    protected Logger log() {
        return _log;
    }

    @Override
    protected void presize( int expOrders ) {
        int orders      = expOrders;
        int recycledMax = Math.min( orders, 50000 ); // allowing 20000 per second, assume in second get time to recycle

        PropertyGroup simGroup = new PropertyGroup( "sim.", null, null );

        int chainSize           = simGroup.getIntProperty( CoreProps.Tags.chainSize, false, 100 );
        int orderChains         = orders / chainSize;
        int recycledEventChains = (recycledMax / chainSize) + 100;
        int extraAlloc          = 50;

        presize( MDEntryImpl.class, recycledEventChains, chainSize, extraAlloc );
        presize( MDIncRefreshImpl.class, recycledEventChains, chainSize, extraAlloc );
        presize( NewOrderSingleImpl.class, recycledEventChains, chainSize, extraAlloc );
        presize( LogEventSmall.class, orderChains, chainSize, extraAlloc );
        presize( ReusableString.class, 4 * orderChains, chainSize, extraAlloc );
    }

    @Override
    public void warmup() {

        super.warmup();

        int warmupCount = WarmupRegistry.instance().getWarmupCount();

        org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger( "JITWARMUP" );
        log.addAppender( org.apache.log4j.varia.NullAppender.getNullAppender() );

        for ( int i = 0; i < warmupCount; i++ ) {
            log.info( "Force Apache JIT line " + i );
        }
    }

    protected SimCMEFastFixSender createSender( CMEFastFixSession sess, boolean nanoStats ) {
        return new SimCMEFastFixSender( _templateRequests, nanoStats, sess );
    }

    protected void run() throws IOException {

        recoverAndConnect();

        PropertyGroup simGroup = new PropertyGroup( "sim.", null, null );

        int    postConnectWait  = simGroup.getIntProperty( Tags.postConnectWaitSecs, false, 1 );
        int    sendEvents       = simGroup.getIntProperty( Tags.sendEvents, false, _expOrders );
        int    batchSize        = simGroup.getIntProperty( Tags.batchSize, false, 1 );
        int    batchDelayMicros = simGroup.getIntProperty( Tags.batchDelayMicros, false, 1000 );
        String templateFile     = simGroup.getProperty( Tags.eventTemplateFile );

        loadSampleData( templateFile, _templateRequests, 30 );

        RecoverableSession[] clients = getSessionManager().getDownStreamSessions();

        _fastFixSender = createSender( (CMEFastFixSession) clients[ 0 ], false );

        _console.info( "Waiting for fully connected" );
        int waitIdx = 0;
        for ( int i = 0; i < clients.length; i++ ) {
            RecoverableSession client = clients[ i ];
            while( !client.isConnected() && ++waitIdx < 10 ) {
                ThreadUtilsFactory.get().sleep( 1000 );
            }
        }

        if ( waitIdx >= 10 ) {
            _log.error( FAIL_LOGIN, "Abort" );
            ShutdownManager.instance().shutdown( -1 );
        }

        _console.info( "Sleeping post connect for " + postConnectWait + " secs" );
        ThreadUtilsFactory.get().sleep( postConnectWait * 1000 ); // wait for any inflight recovery messages

        SuperpoolManager.instance().resetPoolStats();

        _console.info( "ready to dispatch" );

        System.out.flush();
        System.err.flush();

        _fastFixSender.dispatchEvents( sendEvents, batchSize, batchDelayMicros );

        _console.info( "postDispatch wait of 5secs" );

        ThreadUtilsFactory.get().sleep( 5000 );

        for ( RecoverableSession downSess : getSessionManager().getDownStreamSessions() ) {
            downSess.stop();
        }
    }

    private void recoverAndConnect() {
        DMARecoveryController rct = new DummyRecoveryController();

        RecoverableSession[] downStream = getSessionManager().getDownStreamSessions();

        for ( RecoverableSession downSess : downStream ) {
            downSess.recover( rct );
        }

        for ( RecoverableSession downSess : downStream ) {
            downSess.waitForRecoveryToComplete();
        }

        SuperpoolManager.instance().resetPoolStats();
        Utils.invokeGC();

        commonSessionConnect();

        for ( RecoverableSession downSess : downStream ) {
            downSess.connect();
        }
    }
}
