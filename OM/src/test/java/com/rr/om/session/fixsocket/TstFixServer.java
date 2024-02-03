/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.fixsocket;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.Encoder;
import com.rr.core.codec.FixDecoder;
import com.rr.core.codec.FixEncoder;
import com.rr.core.collections.BlockingSyncQueue;
import com.rr.core.collections.ConcLinkedEventQueueSingle;
import com.rr.core.collections.EventQueue;
import com.rr.core.dispatch.DirectDispatcher;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.dispatch.ThreadedDispatcher;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.lang.stats.StatsCfgFile;
import com.rr.core.lang.stats.StatsMgr;
import com.rr.core.logger.*;
import com.rr.core.model.ModelVersion;
import com.rr.core.persister.PersisterException;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.recovery.dma.DMARecoveryController;
import com.rr.core.session.*;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.session.socket.SocketSession;
import com.rr.core.utils.FileException;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.core.utils.Utils;
import com.rr.mds.client.MDSConsumer;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;
import com.rr.model.generated.internal.events.impl.*;
import com.rr.om.dummy.warmup.DummyInstrumentLocator;
import com.rr.om.model.event.EventBuilder;
import com.rr.om.model.event.EventBuilderImpl;
import com.rr.om.order.OrderImpl;
import com.rr.om.order.OrderVersion;
import com.rr.om.order.collections.HashEntry;
import com.rr.om.processor.EventProcessor;
import com.rr.om.processor.EventProcessorImpl;
import com.rr.om.recovery.DummyRecoveryController;
import com.rr.om.registry.FullTradeRegistry;
import com.rr.om.registry.TradeRegistry;
import com.rr.om.router.OrderRouter;
import com.rr.om.router.SingleDestRouter;
import com.rr.om.validate.EmeaDmaValidator;
import com.rr.om.validate.EventValidator;
import com.rr.om.warmup.FixSimConstants;
import com.rr.om.warmup.FixTestUtils;
import com.rr.om.warmup.sim.BaseFixSimProcess;
import com.rr.om.warmup.sim.FixSimParams;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TstFixServer extends BaseFixSimProcess {

    private static final Logger    _log    = ConsoleFactory.console( TstFixServer.class, Level.info );
    private static final ErrorCode FAILED  = new ErrorCode( "TFS100", "Exception in main" );
    private static final int       MAX_AGE = 10000;

    private static int _indexIdx = 0;
    private final MDSConsumer        _mdsClient;
    private       FixSocketSession   _exSess;
    private       FixSocketSession   _client;
    private       EventProcessorImpl _proc;

    protected synchronized static int nextIdx() {
        return ++_indexIdx;
    }

    public static void main( String[] args ) {

        StatsMgr.setStats( new StatsCfgFile() );
        StatsMgr.instance().initialise();
        FixSimParams params = getProcessedParams( args );

        ThreadUtilsFactory.get().init( params.getCpuMasksFile() );

        ThreadUtilsFactory.get().setPriority( Thread.currentThread(), ThreadPriority.Main );

        LoggerFactory.setForceConsole( false );
        LoggerFactory.initLogging( "./logs/TstFixServer.log", 10000000, Level.info );

        try {
            TstFixServer tfs = new TstFixServer( params );

            tfs.init();

            Utils.invokeGC();

            SuperpoolManager.instance().resetPoolStats();

            tfs.run();

            _log.info( "Completed" );

        } catch( Exception e ) {

            _log.error( FAILED, "", e );
        }
    }

    private static FixSimParams getProcessedParams( String[] args ) {
        FixSimParams params = new FixSimParams( "TstFixServer", true, true, "S" );

        params.setUpHost( "localhost" );
        params.setUpSenderCompId( FixSimConstants.DEFAULT_OM_UP_ID );
        params.setUpTargetCompId( FixSimConstants.DEFAULT_CLIENT_SIM_ID );
        params.setUpPort( FixSimConstants.DEFAULT_OM_CLIENT_PORT );

        params.setDownHost( "localhost" );
        params.setDownSenderCompId( FixSimConstants.DEFAULT_OM_DOWN_ID );
        params.setDownTargetCompId( FixSimConstants.DEFAULT_EXCHANGE_SIM_ID );
        params.setDownPort( FixSimConstants.DEFAULT_OM_EXCHANGE_PORT );

        params.setHubHost( "localhost" );
        params.setHubSenderCompId( FixSimConstants.DEFAULT_OM_HUB_ID );
        params.setHubTargetCompId( FixSimConstants.DEFAULT_HUB_BRIDGE_ID );
        params.setHubPort( FixSimConstants.DEFAULT_HUB_PORT );

        params.procArgs( args );

        return params;
    }

    public TstFixServer( FixSimParams params ) {
        super( params );

        _mdsClient = new MDSConsumer();
    }

    @Override
    protected void init() throws SessionException, FileException, PersisterException, IOException {

        _log.info( "TstFixServer.init()" );

        super.init();

        presize();

        _log.info( "CONNECT PARAMS : clientHost=" + _params.getUpHost() +
                   ", clientPort=" + _params.getUpPort() +
                   ", exchangeHost=" + _params.getDownHost() +
                   ", exchagePort=" + _params.getDownPort() );

        DateFormat utcFormat = new SimpleDateFormat( "yyyyMMdd" );

        String today = utcFormat.format( new Date() );
        FixTestUtils.setTodayStr( today );

        RecoverableSession hub = createHub();

        _proc   = getProcesssor( hub );
        _exSess = createExchangeFacingSession( _proc, hub );
        _client = createClientFacingSession( _proc, hub );

        OrderRouter router = new SingleDestRouter( _exSess );

        _proc.setProcessorRouter( router );

        // start the sockets

        _mdsClient.init( new DummyInstrumentLocator(), _params.getMDSListenPort() );

        _exSess.init();
        _client.init();
    }

    private FixSocketSession createClientFacingSession( EventProcessor proc, RecoverableSession hub ) throws SessionException, PersisterException {

        String         name             = "TCLIENT";
        EventRouter    inboundRouter    = new PassThruRouter( "passThru", proc );
        int            logHdrOut        = AbstractSession.getDataOffset( name, false );
        byte[]         outBuf           = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        FixEncoder     encoder          = FixTestUtils.getEncoder44( outBuf, logHdrOut );
        FixDecoder     decoder          = FixTestUtils.getOMSDecoder44();
        FixDecoder     fullDecoder      = FixTestUtils.getFullDecoder44();
        ThreadPriority receiverPriority = ThreadPriority.SessionInbound1;
        ZString        senderCompId     = new ViewString( _params.getUpSenderCompId() );
        ZString        senderSubId      = new ViewString( "" );
        ZString        targetCompId     = new ViewString( _params.getUpTargetCompId() );
        ZString        targetSubId      = new ViewString( "" );
        ZString        userName         = new ViewString( "" );
        ZString        password         = new ViewString( "" );

        FixSocketConfig socketConfig = new FixSocketConfig( AllEventRecycler.class,
                                                            true,
                                                            new ViewString( _params.getUpHost() ),
                                                            new ViewString( _params.getUpAdapter() ),
                                                            _params.getUpPort(),
                                                            senderCompId,
                                                            senderSubId,
                                                            targetCompId,
                                                            targetSubId,
                                                            userName,
                                                            password );

        socketConfig.setDisconnectOnMissedHB( false );
        socketConfig.setRecoverFromLoginSeqNumTooLow( true );
        socketConfig.setLocalPort( _params.getUpLocalPort() );

        EventDispatcher dispatcher = getSessionDispatcher( "CLIENT_DISPATCHER", ThreadPriority.SessionOutbound1 );

        socketConfig.setInboundPersister( createInboundPersister( name + "_CLT_IN", ThreadPriority.MemMapAllocator ) );
        socketConfig.setOutboundPersister( createOutboundPersister( name + "_CLT_OUT", ThreadPriority.MemMapAllocator ) );

        setSocketPerfOptions( socketConfig );

        FixSocketSession sess = new FixSocketSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder,
                                                      fullDecoder, receiverPriority );

        sess.setChainSession( hub );
        dispatcher.setHandler( sess );

        if ( _params.isDisableEventLogging() ) {
            sess.setLogEvents( false );
        } else if ( _params.isDisableNanoStats() ) {
            encoder.setNanoStats( false );
            decoder.setNanoStats( false );
            sess.setLogStats( false );
        }

        return sess;
    }

    private FixSocketSession createExchangeFacingSession( EventProcessor proc, RecoverableSession hub ) throws SessionException, PersisterException {
        String      name          = "TSERVER1";
        EventRouter inboundRouter = new PassThruRouter( "passThru", proc );
        int         logHdrOut     = AbstractSession.getDataOffset( name, false );
        byte[]      outBuf        = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        FixEncoder  encoder       = FixTestUtils.getEncoder44( outBuf, logHdrOut );
        FixDecoder  decoder       = FixTestUtils.getOMSDecoder44();
        Decoder     fullDecoder   = FixTestUtils.getFullDecoder44();

        ThreadPriority receiverPriority = ThreadPriority.SessionInbound2;

        ZString senderCompId = new ViewString( _params.getDownSenderCompId() );
        ZString senderSubId  = new ViewString( "" );
        ZString targetCompId = new ViewString( _params.getDownTargetCompId() );
        ZString targetSubId  = new ViewString( "" );
        ZString userName     = new ViewString( "" );
        ZString password     = new ViewString( "" );
        FixSocketConfig socketConfig = new FixSocketConfig( AllEventRecycler.class,
                                                            false,
                                                            new ViewString( _params.getDownHost() ),
                                                            new ViewString( _params.getDownAdapter() ),
                                                            _params.getDownPort(),
                                                            senderCompId,
                                                            senderSubId,
                                                            targetCompId,
                                                            targetSubId,
                                                            userName,
                                                            password );

        socketConfig.setDisconnectOnMissedHB( false );
        socketConfig.setRecoverFromLoginSeqNumTooLow( true );
        socketConfig.setLocalPort( _params.getDownLocalPort() );
        setSocketPerfOptions( socketConfig );

        socketConfig.setInboundPersister( createInboundPersister( name + "_EXC1_IN", ThreadPriority.MemMapAllocator ) );
        socketConfig.setOutboundPersister( createOutboundPersister( name + "_EXC1_OUT", ThreadPriority.MemMapAllocator ) );

        EventDispatcher dispatcher = getSessionDispatcher( "SRV_DISPATCHER", ThreadPriority.SessionOutbound2 );

        FixSocketSession sess = new FixSocketSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder,
                                                      fullDecoder, receiverPriority );

        sess.setChainSession( hub );
        dispatcher.setHandler( sess );

        if ( _params.isDisableEventLogging() ) {
            sess.setLogEvents( false );
        } else if ( _params.isDisableNanoStats() ) {
            encoder.setNanoStats( false );
            decoder.setNanoStats( false );
            sess.setLogStats( false );
        }

        return sess;
    }

    private SocketSession createHub() {
        String      name          = "THUB";
        int         logHdrOut     = SocketSession.getDataOffset( name, false );
        byte[]      outBuf        = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        Encoder     encoder       = FixTestUtils.getEncoder44( outBuf, logHdrOut );
        Decoder     decoder       = FixTestUtils.getOMSDecoder44();
        Decoder     fullDecoder   = FixTestUtils.getFullDecoder44();
        EventRouter inboundRouter = new DummyRouter( "dummyRouter" );

        if ( !_params.isHubEnabled() ) {
            return null;
        }

        ThreadPriority receiverPriority = ThreadPriority.Other;
        SocketConfig socketConfig = new SocketConfig( AllEventRecycler.class,
                                                      false,
                                                      new ViewString( _params.getHubHost() ),
                                                      new ViewString( _params.getHubAdapter() ),
                                                      _params.getHubPort() );

        socketConfig.setSoDelayMS( 0 );
        socketConfig.setUseNIO( false );
        socketConfig.setTcpNoDelay( false );

        EventDispatcher dispatcher;
        EventQueue      queue = new BlockingSyncQueue();
        dispatcher = new ThreadedDispatcher( "HUB_DISPATCHER", queue, ThreadPriority.Other );

        SocketSession sess = new SocketSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder,
                                                fullDecoder, receiverPriority );

        dispatcher.setHandler( sess );

        return sess;
    }

    private EventProcessorImpl getProcesssor( RecoverableSession hub ) {

        ModelVersion   version   = new ModelVersion( (byte) '1', (byte) '0' );
        EventValidator validator = new EmeaDmaValidator( MAX_AGE );
        EventBuilder   builder   = new EventBuilderImpl();
        TradeRegistry  tradeReg  = new FullTradeRegistry( 2 );

        EventDispatcher dispatcher;

        if ( _params.isOptimiseForThroughPut() ) {

            EventQueue queue = new ConcLinkedEventQueueSingle();
            dispatcher = new ThreadedDispatcher( "ProcessorDispatcher", queue, ThreadPriority.Processor );

        } else if ( _params.isOptimiseForLatency() ) {
            // no dispatcher faster for straight latency .. fine providing only one client
            // tho should really lock the order in processor

            dispatcher = new DirectDispatcher();

        } else {
            EventQueue queue = new BlockingSyncQueue();
            dispatcher = new ThreadedDispatcher( "ProcessorDispatcher", queue, ThreadPriority.Processor );
        }

        EventProcessorImpl t = new EventProcessorImpl( version, _params.getNumOrders(), validator, builder, dispatcher, hub, tradeReg );

        dispatcher.start();

        return t;
    }

    private void presize() {
        int orders      = Math.max( _params.getNumOrders(), 20000 );
        int recycledMax = Math.min( _params.getNumOrders(), 50000 ); // allowing 100000 per second, assume in second get time to recycle

        int chainSize           = 100;
        int orderChains         = orders / chainSize;
        int recycledEventChains = recycledMax / chainSize;
        int logChains           = Math.min( orders, 500 );
        int extraAlloc          = 50;

        _log.info( "Presize based on " + orders + " orders will have orderChains=" + orderChains + ", chainSize=" + chainSize );

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

        presize( OrderImpl.class, orderChains, chainSize, extraAlloc );

        presize( LogEventLarge.class, logChains, chainSize, 100 );
    }

    private void run() {

        DMARecoveryController rct = new DummyRecoveryController();

        _exSess.recover( rct );
        _client.recover( rct );

        _exSess.waitForRecoveryToComplete();
        _client.waitForRecoveryToComplete();

        _exSess.connect();
        _client.connect();

        // check received fix message

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
