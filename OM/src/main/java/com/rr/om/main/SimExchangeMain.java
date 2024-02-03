/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.main;

import com.rr.core.codec.FixDecoder;
import com.rr.core.collections.EventQueue;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.dummy.warmup.DummyEventHandler;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.logger.*;
import com.rr.core.model.Currency;
import com.rr.core.model.EventHandler;
import com.rr.core.model.ExchangeInstrument;
import com.rr.core.model.SecurityIDSource;
import com.rr.core.persister.PersisterException;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.properties.PropertyGroup;
import com.rr.core.recovery.dma.DMARecoveryController;
import com.rr.core.session.RecoverableSession;
import com.rr.core.session.SeperateGatewaySession;
import com.rr.core.session.SessionConfig;
import com.rr.core.session.SessionException;
import com.rr.core.utils.FileException;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.core.utils.Utils;
import com.rr.core.warmup.WarmupRegistry;
import com.rr.model.generated.fix.codec.CodecId;
import com.rr.model.generated.internal.events.impl.*;
import com.rr.model.generated.internal.events.interfaces.NewOrderSingle;
import com.rr.model.generated.internal.type.OrdType;
import com.rr.model.generated.internal.type.Side;
import com.rr.om.book.sim.SimpleOrderBook;
import com.rr.om.dummy.warmup.DummyInstrumentLocator;
import com.rr.om.main.OMProps.Tags;
import com.rr.om.model.event.EventBuilder;
import com.rr.om.model.event.EventBuilderImpl;
import com.rr.om.order.OrderImpl;
import com.rr.om.order.OrderVersion;
import com.rr.om.order.collections.HashEntry;
import com.rr.om.recovery.DummyRecoveryController;
import com.rr.om.session.SessionManager;
import com.rr.om.session.fixsocket.FixSocketConfig;
import com.rr.om.warmup.ExchangeSim;
import com.rr.om.warmup.FixTestUtils;
import com.rr.om.warmup.sim.BaseFixSimProcess;
import com.rr.om.warmup.sim.WarmupExchangeSimAdapter;
import com.rr.om.warmup.sim.WarmupUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ExchangeSIM collapsed main loader
 */
public class SimExchangeMain extends BaseSMTMain {

    private static final Logger    _console = ConsoleFactory.console( SimExchangeMain.class, Level.WARN );
    private static final ErrorCode FAILED   = new ErrorCode( "SEM100", "Exception in main" );

    private final Logger                   _log       = LoggerFactory.create( SimExchangeMain.class );
    private       WarmupExchangeSimAdapter _exSimAdapter;
    private       int                      _expOrders = 1000000;

    public static void main( String[] args ) {

        try {
            prepare( args, ThreadPriority.Other );

            SimExchangeMain smt = new SimExchangeMain();
            smt.init();
            smt.warmup();

            Utils.invokeGC();
            SuperpoolManager.instance().resetPoolStats();

            smt.run();

            _console.info( "Completed" );

        } catch( Exception e ) {

            _console.error( FAILED, "", e );
        }
    }

    SimExchangeMain() {
        super();
    }

    @Override
    protected void init() throws SessionException, FileException, PersisterException {

        _log.info( "SimExchangeMain.init()" );

        super.init();

        _expOrders = AppProps.instance().getIntProperty( OMProps.EXPECTED_ORDERS, false, 1010000 );
        presize( _expOrders );

        PropertyGroup simGroup      = new PropertyGroup( "sim.warmup.", null, null );
        int           warmHeartbeat = simGroup.getIntProperty( Tags.heartBeatIntSecs, false, 30 );
        BaseFixSimProcess.setHeartbeat( warmHeartbeat );

        DateFormat utcFormat = new SimpleDateFormat( "yyyyMMdd" );

        String today = utcFormat.format( new Date() );
        FixTestUtils.setTodayStr( today );

        PropertyGroup procGroup = new PropertyGroup( "sim.", null, null );
        EventQueue    queue     = getQueue( procGroup, "ExchangeSimProcessorQueue" );

        EventDispatcher dispatcher = getProcessorDispatcher( procGroup, queue, "WarmupExchangeSimAdapter", ThreadPriority.ExchangeSimProcessor );

        _exSimAdapter = new WarmupExchangeSimAdapter( dispatcher, _expOrders, queue );

        boolean logOrderInTS = procGroup.getBoolProperty( OMProps.Tags.logOrderInTS, false, false );
        _exSimAdapter.setLogOrderInTS( logOrderInTS );

        SessionManager sessMgr        = getSessionManager();
        String[]       upSessionNames = AppProps.instance().getNodes( "session.up." );
        for ( String sessName : upSessionNames ) {
            if ( !sessName.equals( "default" ) ) {
                createSession( sessName, null, false, sessMgr, _exSimAdapter, null, "session.up." );
            }
        }

        // start the sockets

        RecoverableSession[] upStream = sessMgr.getUpStreamSessions();
        for ( RecoverableSession upSess : upStream ) {
            if ( upSess instanceof SeperateGatewaySession ) {
                ((SeperateGatewaySession) upSess).getGatewaySession().init();
            }
            upSess.init();
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
        int recycledMax = Math.min( expOrders, 50000 ); // allowing 20000 per second, assume in second get time to recycle

        PropertyGroup simGroup            = new PropertyGroup( "sim.", null, null );
        int           chainSize           = simGroup.getIntProperty( CoreProps.Tags.chainSize, false, 100 );
        int           orderChains         = orders / chainSize;
        int           recycledEventChains = recycledMax / chainSize;
        int           extraAlloc          = 50;

        presize( ClientNewOrderSingleImpl.class, orderChains, chainSize, extraAlloc );
        presize( MarketNewOrderSingleImpl.class, recycledEventChains, chainSize, extraAlloc );
        presize( ClientNewOrderAckImpl.class, recycledEventChains, chainSize, extraAlloc );
        presize( MarketNewOrderAckImpl.class, recycledEventChains, chainSize, extraAlloc );
        presize( ClientCancelRequestImpl.class, recycledEventChains, chainSize, extraAlloc );
        presize( MarketCancelRequestImpl.class, recycledEventChains, chainSize, extraAlloc );
        presize( ClientCancelledImpl.class, recycledEventChains, chainSize, extraAlloc );
        presize( MarketCancelledImpl.class, recycledEventChains, chainSize, extraAlloc );

        presize( ReusableString.class, 10 * orderChains, chainSize, extraAlloc );
        presize( HashEntry.class, 2 * orderChains, chainSize, extraAlloc );

        presize( OrderImpl.class, orderChains, chainSize, extraAlloc );
        presize( OrderVersion.class, 2 * orderChains, chainSize, extraAlloc );

        presize( LogEventSmall.class, 200, 100, 50 );
    }

    @Override
    protected void warmup() {

        SimpleOrderBook ob = new SimpleOrderBook();

        ViewString id          = new ViewString( "DUMMYID" );
        int        warmupCount = WarmupRegistry.instance().getWarmupCount();
        for ( int i = 0; i < 3000; i++ ) {
            ob.add( id, warmupCount, i, OrdType.Limit, Side.Buy );
        }

        super.warmup();

        CodecId codecId = CodecId.Standard44;

        try {
            SessionManager       sessMgr  = getSessionManager();
            RecoverableSession[] upStream = sessMgr.getUpStreamSessions();
            for ( RecoverableSession upSess : upStream ) {
                SessionConfig cfg = upSess.getConfig();

                if ( cfg instanceof FixSocketConfig ) {
                    codecId = ((FixSocketConfig) cfg).getCodecId();
                }
            }

            warmExchangeSim( codecId );
        } catch( Exception e ) {
            _log.warn( "Exception in warmup exchange SIM : " + e.getMessage() );
        }
    }

    void start() {
        DMARecoveryController rct = new DummyRecoveryController();

        RecoverableSession[] upStream = getSessionManager().getUpStreamSessions();

        for ( RecoverableSession upSess : upStream ) {
            upSess.recover( rct );
        }

        for ( RecoverableSession upSess : upStream ) {
            upSess.waitForRecoveryToComplete();
        }

        Utils.invokeGC();

        commonSessionConnect();

        for ( RecoverableSession upSess : upStream ) {
            if ( upSess instanceof SeperateGatewaySession ) {
                ((SeperateGatewaySession) upSess).getGatewaySession().connect();
            }
            upSess.connect();
        }
    }

    private ExchangeInstrument getWarmInst( NewOrderSingle nos ) {
        final Currency         clientCcy = nos.getCurrency();
        final SecurityIDSource src       = nos.getSecurityIDSource();

        ExchangeInstrument instr;

        DummyInstrumentLocator locator = new DummyInstrumentLocator();

        instr = locator.getExchInst( nos.getSymbol(), SecurityIDSource.ExchangeSymbol, nos.getSecurityExchange() );

        if ( instr == null ) instr = locator.getExchInst( nos.getSecurityId(), src, nos.getSecurityExchange() );

        if ( instr == null && src == SecurityIDSource.ISIN ) {
            instr = locator.getExchInstByIsin( nos.getSecurityId(), nos.getSecurityExchange(), clientCcy );
        }

        return instr;
    }

    private void run() {

        start();

        SuperpoolManager.instance().resetPoolStats();

        System.out.flush();
        System.err.flush();

        _log.info( "ENTERING MAIN LOOP - ctrl-C to stop program" );

        int idx = 0;

        while( true ) {

            if ( ++idx % 10 == 0 ) {
                System.out.flush();
                System.err.flush();
            }

            ThreadUtilsFactory.get().sleep( 1000 );
        }
    }

    private void warmExchangeSim( CodecId codecId ) {
        FixDecoder     decoder = (FixDecoder) WarmupUtils.getDecoder( codecId );
        ReusableString buf     = new ReusableString( 1000 );

        int          warmupCount  = WarmupRegistry.instance().getWarmupCount();
        EventBuilder eventBuilder = new EventBuilderImpl();
        ExchangeSim  exchangeSim  = new ExchangeSim( warmupCount, eventBuilder );

        eventBuilder.initPools();

        EventHandler handler = new DummyEventHandler();

        ReusableString clOrdId     = new ReusableString();
        ReusableString origClOrdId = new ReusableString();
        ReusableString execId      = new ReusableString();

        for ( int i = 1; i < warmupCount; i++ ) {

            clOrdId.copy( "CLORDID" ).append( i );
            origClOrdId.copy( "CLORDID" ).append( i - 1 );
            execId.copy( "EXECID" ).append( i );

            ClientNewOrderSingleImpl       cnos    = FixTestUtils.getClientNOS( buf, decoder, clOrdId, 1, 1.0, handler );
            ClientCancelReplaceRequestImpl camend  = FixTestUtils.getClientCancelReplaceRequest( buf, decoder, clOrdId, origClOrdId, 1, 1.1, handler );
            ClientCancelRequestImpl        ccancel = FixTestUtils.getClientCancelRequest( buf, decoder, clOrdId, origClOrdId, handler );

            exchangeSim.handle( cnos );
            exchangeSim.handle( camend );
            exchangeSim.handle( ccancel );
            _exSimAdapter.handleNow( cnos );
            _exSimAdapter.handleNow( camend );
            _exSimAdapter.handleNow( ccancel );

            getWarmInst( cnos );
        }

        _exSimAdapter.reset();
    }
}
