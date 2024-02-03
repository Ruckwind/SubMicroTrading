/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.fixsocket;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.Encoder;
import com.rr.core.codec.FixDecoder;
import com.rr.core.codec.FixEncoder;
import com.rr.core.collections.BlockingSyncQueue;
import com.rr.core.collections.EventQueue;
import com.rr.core.dispatch.DirectDispatcher;
import com.rr.core.dispatch.DirectDispatcherNonThreadSafe;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.dispatch.ThreadedDispatcher;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.lang.stats.StatsCfgFile;
import com.rr.core.lang.stats.StatsMgr;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.ModelVersion;
import com.rr.core.persister.PersisterException;
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
import com.rr.om.order.collections.OrderMap;
import com.rr.om.order.collections.SegmentOrderMap;
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

/**
 * a fix server that has one pipe from client to proc to exchange
 * <p>
 * and one pipe from exchange thru proc to client
 * <p>
 * need two proc instances sharing concurrent map
 * the upstream session dispatcher must have a sync direct dispatcher
 * the downstream dispatcher does not require sync
 *
 * @author Richard Rose
 */
public class TstSyncFixServer extends BaseFixSimProcess {

    private static final Logger    _log    = ConsoleFactory.console( TstSyncFixServer.class, Level.info );
    private static final ErrorCode FAILED  = new ErrorCode( "TFS100", "Exception in main" );
    private static final int       MAX_AGE = 10000;

    private static int _indexIdx = 0;
    private final MDSConsumer        _mdsClient;
    private       FixSocketSession   _exSess;
    private       FixSocketSession   _client;
    private       EventProcessorImpl _procUp;
    private       EventProcessorImpl _procDown;

    protected synchronized static int nextIdx() {
        return ++_indexIdx;
    }

    public static void main( String[] args ) {

        StatsMgr.setStats( new StatsCfgFile() );
        StatsMgr.instance().initialise();
        ThreadUtilsFactory.get().init( ThreadUtilsFactory.get().DEFAULT_THREAD_CONFIG );

        LoggerFactory.setForceConsole( false );
        LoggerFactory.initLogging( "./logs/TstSyncFixServer.log", 10000000, Level.info );

        FixSimParams params = getProcessedParams( args );

        try {
            TstSyncFixServer tfs = new TstSyncFixServer( params );

            tfs.init();

            Utils.invokeGC();

            tfs.run();

            _log.info( "Completed" );

        } catch( Exception e ) {

            _log.error( FAILED, "", e );
        }
    }

    private static FixSimParams getProcessedParams( String[] args ) {
        FixSimParams params = new FixSimParams( "TstSyncFixServer", true, true, "S" );

        params.setOptimiseForLatency( true );

        params.setUpHost( "localhost" );
        params.setUpSenderCompId( FixSimConstants.DEFAULT_OM_UP_ID );
        params.setUpTargetCompId( FixSimConstants.DEFAULT_CLIENT_SIM_ID );
        params.setUpPort( FixSimConstants.DEFAULT_OM_CLIENT_PORT );

        params.setDownHost( "localhost" );
        params.setDownSenderCompId( FixSimConstants.DEFAULT_OM_DOWN_ID );
        params.setDownTargetCompId( FixSimConstants.DEFAULT_EXCHANGE_SIM_ID );
        params.setDownPort( FixSimConstants.DEFAULT_OM_EXCHANGE_PORT );

        params.procArgs( args );

        _log.info( "CONNECT PARAMS : clientHost=" + params.getUpHost() +
                   ", clientPort=" + params.getUpPort() +
                   ", exchangeHost=" + params.getDownHost() +
                   ", exchagePort=" + params.getDownPort() );

        return params;
    }

    public TstSyncFixServer( FixSimParams params ) {
        super( params );

        _mdsClient = new MDSConsumer();
    }

    @Override
    protected void init() throws SessionException, FileException, PersisterException, IOException {

        super.init();

        presize();

        DateFormat utcFormat = new SimpleDateFormat( "yyyyMMdd" );

        String today = utcFormat.format( new Date() );
        FixTestUtils.setTodayStr( today );

        RecoverableSession hub = createHub();

        OrderMap orderMap = new SegmentOrderMap( _params.getNumOrders(), 0.75f, 1 );
        _procDown = getProcesssor( hub, orderMap );
        _procUp   = getProcesssor( hub, orderMap );
        _client   = createClientFacingSession( _procDown, hub );
        _exSess   = createExchangeFacingSession( _procUp, hub );

        OrderRouter router = new SingleDestRouter( _exSess );

        _procDown.setProcessorRouter( router );
        _procUp.setProcessorRouter( router );

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

        EventDispatcher dispatcher = new DirectDispatcher();

        socketConfig.setInboundPersister( createInboundPersister( name + "_CLT_IN", ThreadPriority.MemMapAllocator ) );
        socketConfig.setOutboundPersister( createOutboundPersister( name + "_CLT_OUT", ThreadPriority.MemMapAllocator ) );

        setSocketPerfOptions( socketConfig );

        FixSocketSession sess = new FixSocketSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder,
                                                      fullDecoder, receiverPriority );

        sess.setChainSession( hub );
        dispatcher.setHandler( sess );

        if ( _params.isDisableNanoStats() ) {
            encoder.setNanoStats( false );
            decoder.setNanoStats( false );
            sess.setLogStats( false );
        }

        return sess;
    }

    private FixSocketSession createExchangeFacingSession( EventProcessor proc, RecoverableSession hub ) throws SessionException, PersisterException {
        String      name          = "TSERVER";
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
        setSocketPerfOptions( socketConfig );

        socketConfig.setInboundPersister( createInboundPersister( name + "_EXC1_IN", ThreadPriority.MemMapAllocator ) );
        socketConfig.setOutboundPersister( createOutboundPersister( name + "_EXC1_OUT", ThreadPriority.MemMapAllocator ) );

        EventDispatcher dispatcher = new DirectDispatcher();

        FixSocketSession sess = new FixSocketSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder,
                                                      fullDecoder, receiverPriority );

        sess.setChainSession( hub );
        dispatcher.setHandler( sess );

        if ( _params.isDisableNanoStats() ) {
            encoder.setNanoStats( false );
            decoder.setNanoStats( false );
            sess.setLogStats( false );
        }

        return sess;
    }

    private SocketSession createHub() {

        if ( !_params.isHubEnabled() ) {
            return null;
        }

        String      name          = "THUB";
        int         logHdrOut     = SocketSession.getDataOffset( name, false );
        byte[]      outBuf        = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        Encoder     encoder       = FixTestUtils.getEncoder44( outBuf, logHdrOut );
        Decoder     decoder       = FixTestUtils.getOMSDecoder44();
        Decoder     fullDecoder   = FixTestUtils.getFullDecoder44();
        EventRouter inboundRouter = new DummyRouter( "dummyRouter" );

        ThreadPriority receiverPriority = ThreadPriority.Other;
        SocketConfig socketConfig = new SocketConfig( AllEventRecycler.class,
                                                      false,
                                                      new ViewString( _params.getHubHost() ),
                                                      new ViewString( _params.getHubAdapter() ),
                                                      _params.getHubPort() );

        socketConfig.setSoDelayMS( 0 );
        socketConfig.setUseNIO( false );

        EventDispatcher dispatcher;
        EventQueue      queue = new BlockingSyncQueue();
        dispatcher = new ThreadedDispatcher( "HUB_DISPATCHER", queue, ThreadPriority.Other );

        SocketSession sess = new SocketSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder,
                                                fullDecoder, receiverPriority );

        dispatcher.setHandler( sess );

        return sess;
    }

    private EventProcessorImpl getProcesssor( RecoverableSession hub, OrderMap orderMap ) {

        ModelVersion   version   = new ModelVersion( (byte) '1', (byte) '0' );
        EventValidator validator = new EmeaDmaValidator( MAX_AGE );
        EventBuilder   builder   = new EventBuilderImpl();
        TradeRegistry  tradeReg  = new FullTradeRegistry( 2 );

        EventDispatcher dispatcher = new DirectDispatcherNonThreadSafe();

        EventProcessorImpl t = new EventProcessorImpl( orderMap, version, _params.getNumOrders(), validator, builder, dispatcher, hub, tradeReg );

        dispatcher.start();

        return t;
    }

    private void presize() {
        int orders      = _params.getNumOrders();
        int recycledMax = Math.min( _params.getNumOrders(), 20000 ); // allowing 20000 per second, assume in second get time to recycle

        int chainSize           = 1000;
        int orderChains         = orders / chainSize;
        int recycledEventChains = recycledMax / chainSize;
        int extraAlloc          = 50;

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

        int expCount = _params.getNumOrders();

        while( true ) {

            ThreadUtilsFactory.get().sleep( 1000 );

            if ( expCount >= 100 ) {
                if ( _procDown.size() >= expCount ) {

                    _log.info( "SEEN CONFIGURED EXPECTED NUMBER OF ORDERS " + expCount + " - QUITING" );

                    break;
                }
            }
        }
    }
}
