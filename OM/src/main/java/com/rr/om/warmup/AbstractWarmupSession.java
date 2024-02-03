/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.warmup;

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
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.Stopable;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.EventHandler;
import com.rr.core.model.ModelVersion;
import com.rr.core.persister.PersisterException;
import com.rr.core.recovery.dma.DMARecoveryController;
import com.rr.core.session.*;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.session.socket.SocketSession;
import com.rr.core.utils.*;
import com.rr.model.generated.fix.codec.CodecId;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;
import com.rr.om.dummy.warmup.ClientStatsManager;
import com.rr.om.model.event.EventBuilder;
import com.rr.om.model.event.EventBuilderImpl;
import com.rr.om.processor.EventProcessor;
import com.rr.om.processor.EventProcessorImpl;
import com.rr.om.recovery.DummyRecoveryController;
import com.rr.om.registry.FullTradeRegistry;
import com.rr.om.registry.TradeRegistry;
import com.rr.om.router.OrderRouter;
import com.rr.om.router.SingleDestRouter;
import com.rr.om.session.fixsocket.FixSocketConfig;
import com.rr.om.session.fixsocket.FixSocketSession;
import com.rr.om.validate.EmeaDmaValidator;
import com.rr.om.validate.EventValidator;
import com.rr.om.warmup.sim.*;

import java.io.IOException;

public abstract class AbstractWarmupSession extends BaseFixSimProcess {

    protected static final Logger _log = LoggerFactory.create( AbstractWarmupSession.class );

    private static final long MAX_TIME_WAIT_MS = 10000;
    private static final int  MAX_RETRY        = 3;

    public static String DEFAULT_OM_UP_ID         = "WARMUP_SMTC1";
    public static String DEFAULT_CLIENT_SIM_ID    = "WARMUP_TCLT1";
    public static int    DEFAULT_OM_CLIENT_PORT   = 14001;
    public static String DEFAULT_OM_DOWN_ID       = "WARMUP_SMTE1";
    public static String DEFAULT_EXCHANGE_SIM_ID  = "WARMUP_TEXE1";
    public static int    DEFAULT_OM_EXCHANGE_PORT = 14011;         // +1 must be free as well for recovery port
    public static String HUB_HOST                 = "127.0.0.1";
    public static int    HUB_PORT                 = 14055;

    // client sim side
    protected RecoverableSession     _clientSess;
    protected long                   _maxRunTime = MAX_TIME_WAIT_MS;
    // OM side
    protected RecoverableSession _omToExSess;
    protected RecoverableSession _omToClientSess;
    // exchange sim side
    protected RecoverableSession       _exSess;
    protected WarmupExchangeSimAdapter _exSimAdapter;
    protected CodecId _codecId;
    private   SimClient              _clientSimSender;
    private   WarmClientReplyHandler _clientReplyHandler;
    private   ClientStatsManager     _statsMgr;
    private   EventHandler       _proc;

    public static FixSimParams createParams( String appName, int portOffset, boolean spinLocks, int count ) {
        if ( spinLocks ) {
            String[] wargs = { "-u", "-F", "" + portOffset, "-W", "" + count };
            return getProcessedParams( appName, wargs );
        }

        String[] wargs = { "-F", "" + portOffset, "-W", "" + count };
        return getProcessedParams( appName, wargs );
    }

    public static FixSimParams getProcessedParams( String appName, String[] args ) {
        FixSimParams params = new FixSimParams( "Warmup" + appName, true, true, "S" );

        params.enableClientParams();

        params.setWarmupCount( 3000 );
        params.setNumOrders( 3000 );

        params.procArgs( args );

        params.setRemovePersistence( true );

        params.setDelayMicros( 100 );

        params.setUpHost( "127.0.0.1" );
        params.setUpSenderCompId( DEFAULT_OM_UP_ID );
        params.setUpTargetCompId( DEFAULT_CLIENT_SIM_ID );
        params.setUpPort( DEFAULT_OM_CLIENT_PORT + params.getWarmupPortOffset() );

        params.setDownHost( "127.0.0.1" );
        params.setDownSenderCompId( DEFAULT_OM_DOWN_ID );
        params.setDownTargetCompId( DEFAULT_EXCHANGE_SIM_ID );
        params.setDownPort( DEFAULT_OM_EXCHANGE_PORT + params.getWarmupPortOffset() );

        params.setNumOrders( params.getWarmupCount() );
        params.setPersistDatPageSize( 4096 );
        params.setPersistDatPreSize( 256 * params.getWarmupCount() + 1024 );
        params.setPersistIdxPreSize( 16 * params.getWarmupCount() + 1024 );

        params.setDisableEventLogging( true );

        return params;
    }

    public AbstractWarmupSession( FixSimParams params, CodecId id ) {
        super( params );

        _codecId = id;
    }

    @Override
    protected void init() throws SessionException, FileException, PersisterException, IOException {

        super.init();

        // no super pool presizing

        _statsMgr           = new ClientStatsManager( _params.getWarmupCount() );
        _clientReplyHandler = new WarmClientReplyHandler( _statsMgr );

        EventQueue      queue      = getQueue( "WarmupExchangeSimProcessorQueue" );
        EventDispatcher dispatcher = new ThreadedDispatcher( "WarmupExchangeSimAdapter", queue, ThreadPriority.Other );

        _exSimAdapter = new WarmupExchangeSimAdapter( dispatcher, _params.getWarmupCount(), queue );

        RecoverableSession hub = createOmToHub();

        _proc           = getProcesssor( hub );
        _clientSess     = createClientSession( _clientReplyHandler );
        _omToExSess     = createOmToExchangeSession( _proc, hub );
        _omToClientSess = createOmToClientSession( _proc, hub );
        _exSess         = createExchangeSession( _exSimAdapter );

        postCreateInit();

        // init the sockets

        _exSess.init();
        _omToExSess.init();
        _omToClientSess.init();
        _clientSess.init();

        _clientSimSender = createSimSender();
    }

    public FixSimParams getParams() {
        return _params;
    }

    public int getReceived() {
        return _clientReplyHandler.getReceived();
    }

    public int getSentByClientSim() {
        return _clientSimSender.getSent();
    }

    public void logStats() {
        _log.info( "logStats: ClientInfo : " + _clientSess.info() );
        _log.info( "logStats: OM2CLT: " + _omToClientSess.info() );
        _log.info( "logStats: OM2EX : " + _omToExSess.info() );
        _log.info( "logStats: EXCH : " + _exSess.info() );
    }

    public void setEventLogging( boolean on ) {
        _params.setDisableEventLogging( !on );
    }

    public void setMaxRunTime( long maxRunTime ) {
        _maxRunTime = maxRunTime;
    }

    public void warmup() throws Exception {

        int attempt = 0;

        while( true ) {

            try {
                _log.info( getClass().getSimpleName() + " ATTEMPT #" + attempt + " WARMUP PARAMS : numOrder=" + _params.getWarmupCount() + ", delayMS=" + _params.getDelayMicros() + ", warmClientHost=" + _params.getUpHost()
                           + ", warmClientPort=" + _params.getUpPort()
                           + ", warmExchangeHost=" + _params.getDownHost() + ", warmExchagePort=" + _params.getDownPort() );

                init();
                run();

                return;

            } catch( SMTRuntimeException e ) {
                if ( e.getMessage().contains( "Failed to connect" ) && ++attempt <= MAX_RETRY ) {
                    _params.setUpPort( _params.getUpPort() + 17 );
                    _params.setDownPort( _params.getDownPort() + 17 );

                    if ( _params.getDownLocalPort() > 0 ) _params.setDownLocalPort( _params.getDownLocalPort() + 17 );
                    if ( _params.getUpLocalPort() > 0 ) _params.setUpLocalPort( _params.getUpLocalPort() + 17 );

                    continue;
                }
                throw e;
            }
        }
    }

    protected void close() {
        if ( _proc instanceof Stopable ) {
            ((Stopable) _proc).stop();
        }
        _clientSess.stop();
        _omToClientSess.stop();
        _omToExSess.stop();
        _exSess.stop();
        _exSimAdapter.stop();
    }

    protected void connect() {
        _exSess.connect();
        _omToExSess.connect();
        _omToClientSess.connect();
        _clientSess.connect();

        long start = System.currentTimeMillis();

        while( !_exSess.isLoggedIn() && !_clientSess.isLoggedIn() && !_omToClientSess.isLoggedIn() ) {
            ThreadUtilsFactory.get().sleep( 1000 );
            long now = System.currentTimeMillis();
            if ( Math.abs( now - start ) > (_maxRunTime / 2) ) {
                throw new SMTRuntimeException( "Failed to connect in time" );
            }
        }
    }

    protected RecoverableSession createClientSession( WarmClientReplyHandler clientReplyHandler ) throws SessionException, PersisterException {

        String      name          = "WARM_CLIENTSIM";
        EventRouter inboundRouter = new PassThruRouter( "passThru", clientReplyHandler );
        int         logHdrOut     = AbstractSession.getDataOffset( name, false );
        byte[]      outBuf        = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        FixEncoder  encoder       = (FixEncoder) WarmupUtils.getEncoder( _codecId, outBuf, logHdrOut );
        FixDecoder  decoder       = (FixDecoder) WarmupUtils.getDecoder( _codecId );
        Decoder     fullDecoder   = WarmupUtils.getFullDecoder( _codecId );

        ThreadPriority receiverPriority = ThreadPriority.Other;

        ZString senderCompId = new ViewString( _params.getUpTargetCompId() );
        ZString senderSubId  = new ViewString( "" );
        ZString targetCompId = new ViewString( _params.getUpSenderCompId() );
        ZString targetSubId  = new ViewString( "" );
        ZString userName     = new ViewString( "" );
        ZString password     = new ViewString( "" );
        FixSocketConfig socketConfig = new FixSocketConfig( AllEventRecycler.class,
                                                            false,
                                                            new ViewString( _params.getUpHost() ),
                                                            null,
                                                            _params.getUpPort(),
                                                            senderCompId,
                                                            senderSubId,
                                                            targetCompId,
                                                            targetSubId,
                                                            userName,
                                                            password );

        socketConfig.setHeartBeatIntSecs( _heartbeat );
        socketConfig.setDisconnectOnMissedHB( false );
        socketConfig.setRecoverFromLoginSeqNumTooLow( true );
        setSocketPerfOptions( socketConfig );

        socketConfig.setInboundPersister( createInboundPersister( name + "_CLTSIM_IN", ThreadPriority.Other ) );
        socketConfig.setOutboundPersister( createOutboundPersister( name + "_CLTSIM_OUT", ThreadPriority.Other ) );

        EventDispatcher dispatcher = getSessionDispatcher( "WARM_CLTSIM_DISPATCHER", ThreadPriority.Other );

        FixSocketSession sess = new FixSocketSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder,
                                                      fullDecoder, receiverPriority );

        dispatcher.setHandler( sess );

        if ( _params.isDisableNanoStats() ) {
            encoder.setNanoStats( false );
            decoder.setNanoStats( false );
            sess.setLogStats( false );
        }

        if ( _params.isDisableEventLogging() ) {
            sess.setLogEvents( false );
        }

        return sess;
    }

    protected abstract RecoverableSession createExchangeSession( WarmupExchangeSimAdapter exSimAdapter ) throws FileException, SessionException, PersisterException;

    protected RecoverableSession createOmToClientSession( EventHandler proc, RecoverableSession hub ) throws SessionException, PersisterException {

        String      name          = "WARM_OM2CLT";
        EventRouter inboundRouter = new PassThruRouter( "passThrurouter", proc );
        int         logHdrOut     = AbstractSession.getDataOffset( name, false );
        byte[]      outBuf        = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        FixEncoder  encoder       = (FixEncoder) WarmupUtils.getEncoder( _codecId, outBuf, logHdrOut );
        FixDecoder  decoder       = (FixDecoder) getDecoder( _codecId );
        FixDecoder  fullDecoder   = (FixDecoder) WarmupUtils.getFullDecoder( _codecId );

        ThreadPriority receiverPriority = ThreadPriority.Other;
        ZString        senderCompId     = new ViewString( _params.getUpSenderCompId() );
        ZString        senderSubId      = new ViewString( "" );
        ZString        targetCompId     = new ViewString( _params.getUpTargetCompId() );
        ZString        targetSubId      = new ViewString( "" );
        ZString        userName         = new ViewString( "" );
        ZString        password         = new ViewString( "" );

        FixSocketConfig socketConfig = new FixSocketConfig( AllEventRecycler.class,
                                                            true,
                                                            new ViewString( _params.getUpHost() ),
                                                            null,
                                                            _params.getUpPort(),
                                                            senderCompId,
                                                            senderSubId,
                                                            targetCompId,
                                                            targetSubId,
                                                            userName,
                                                            password );

        socketConfig.setHeartBeatIntSecs( _heartbeat );
        socketConfig.setDisconnectOnMissedHB( false );
        socketConfig.setRecoverFromLoginSeqNumTooLow( true );

        EventDispatcher dispatcher = getSessionDispatcher( "WARMUP_OM2CLIENT_DISPATCHER", ThreadPriority.Other );

        socketConfig.setInboundPersister( createInboundPersister( name + "_CLT_IN", ThreadPriority.Other ) );
        socketConfig.setOutboundPersister( createOutboundPersister( name + "_CLT_OUT", ThreadPriority.Other ) );

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

        if ( _params.isDisableEventLogging() ) {
            sess.setLogEvents( false );
        }

        return sess;
    }

    protected abstract RecoverableSession createOmToExchangeSession( EventHandler proc, RecoverableSession hub ) throws SessionException, FileException, PersisterException;

    /**
     * @throws IOException
     */
    protected SimClient createSimSender() throws IOException {
        FixSocketSession[] client = { (FixSocketSession) _clientSess };
        return new ClientSimSender( WarmupUtils.getTemplateRequests(), client, _statsMgr, "WARMUP" );
    }

    protected Decoder getDecoder( CodecId codecId ) {
        Decoder decoder = WarmupUtils.getDecoder( codecId );
        return decoder;
    }

    protected EventHandler getProcesssor( RecoverableSession hub ) {

        ModelVersion   version   = new ModelVersion( (byte) '1', (byte) '0' );
        EventValidator validator = new EmeaDmaValidator( Integer.MAX_VALUE );
        EventBuilder   builder   = new EventBuilderImpl();
        TradeRegistry  tradeReg  = new FullTradeRegistry( 2 );

        EventDispatcher dispatcher;

        if ( _params.isOptimiseForThroughPut() ) {

            EventQueue queue = new ConcLinkedEventQueueSingle();
            dispatcher = new ThreadedDispatcher( "WarmupProcessorDispatcher", queue, ThreadPriority.Other );

        } else if ( _params.isOptimiseForLatency() ) {
            // no dispatcher faster for straight latency .. fine providing only one client
            // tho should really lock the order in processor

            dispatcher = new DirectDispatcher();

        } else {
            EventQueue queue = new BlockingSyncQueue();
            dispatcher = new ThreadedDispatcher( "WarmupProcessorDispatcher", queue, ThreadPriority.Other );
        }

        EventProcessorImpl t = new EventProcessorImpl( version, _params.getWarmupCount(), validator, builder, dispatcher, hub, tradeReg );

        dispatcher.start();

        return t;
    }

    protected boolean isReadyToTrade() {
        return _omToExSess.isLoggedIn() && _clientSess.isConnected();
    }

    protected void postCreateInit() {
        OrderRouter router = new SingleDestRouter( _omToExSess );
        ((EventProcessor) _proc).setProcessorRouter( router );
    }

    protected void recover() {

        // EX
        DMARecoveryController exRct = new DummyRecoveryController();
        _exSess.recover( exRct );
        _exSess.waitForRecoveryToComplete();

        // OM
        DMARecoveryController omRct = new DummyRecoveryController();
        _omToExSess.recover( omRct );
        _omToClientSess.recover( omRct );
        _omToExSess.waitForRecoveryToComplete();
        _omToClientSess.waitForRecoveryToComplete();

        // CLIENT
        DMARecoveryController clientRct = new DummyRecoveryController();
        _clientSess.recover( clientRct );
        _clientSess.waitForRecoveryToComplete();
    }

    protected void run() {

        try {
            recover();
            connect();

            int idx = 0;
            while( idx < 20 && !isReadyToTrade() ) {
                ThreadUtilsFactory.get().sleep( 100 );
            }

            sendClientMessages();
            waitForMessagesProcessed();

            _log.info( "WarmupFixSocketSession COMPLETED  : testRequests=" + _params.getWarmupCount() );

        } catch( IOException e ) {
            e.printStackTrace();
        } finally {
            close();
        }

        _statsMgr.logStats();
    }

    protected void sendClientMessages() throws IOException {
        ReflectUtils.invoke( "clear", _proc );

        _clientReplyHandler.reset();
        _clientSimSender.reset();
        _statsMgr.reset();

        _clientSimSender.dispatchEvents( _params.getWarmupCount(), _params.getBatchSize(), _params.getDelayMicros() );
    }

    private SocketSession createOmToHub() {
        String      name          = "WARM_OM2HUB";
        int         logHdrOut     = SocketSession.getDataOffset( name, false );
        byte[]      outBuf        = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        Encoder     encoder       = WarmupUtils.getEncoder( _codecId, outBuf, logHdrOut );
        Decoder     decoder       = WarmupUtils.getDecoder( _codecId );
        Decoder     fullDecoder   = WarmupUtils.getFullDecoder( _codecId );
        EventRouter inboundRouter = new DummyRouter( "dummyRouter" );

        ThreadPriority receiverPriority = ThreadPriority.Other;
        SocketConfig   socketConfig     = new SocketConfig( AllEventRecycler.class, true, new ViewString( HUB_HOST ), null, HUB_PORT );

        socketConfig.setSoDelayMS( 0 );
        socketConfig.setUseNIO( false );

        EventDispatcher dispatcher = new SessionThreadedDispatcher( "WARM_OM2HUB_DISPATCHER", new BlockingSyncQueue(), ThreadPriority.Other );

        SocketSession sess = new SocketSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder,
                                                fullDecoder, receiverPriority );

        dispatcher.setHandler( sess );

        if ( _params.isDisableEventLogging() ) {
            sess.setLogEvents( false );
        }

        return sess;
    }

    private void waitForMessagesProcessed() {

        _log.info( "Warmup waiting for expected number of events " + _clientSimSender.getSent() );

        long start = ClockFactory.get().currentTimeMillis();

        long sent       = _clientSimSender.getSent();
        long expReplies = _clientSimSender.getExpectedReplies();

        // first wait for client replay handler for client to receive
        while( _clientReplyHandler.getReceived() < expReplies ) {

            ThreadUtilsFactory.get().sleep( 200 );

            sent = _clientSimSender.getSent();

            _log.info( "AbstractWarmupSession : warmup sent " + sent );

            long timeMS = ClockFactory.get().currentTimeMillis() - start;

            if ( timeMS > _maxRunTime ) {
                _log.info( "AbstractWarmupSession : Hit max wait time for warmup complete" );

                break;
            }
        }

        _log.info( _params.getAppName() + " Warmup replies=" + _clientReplyHandler.getReceived()
                   + ", sent=" + _clientSimSender.getSent()
                   + ", expReplies=" + expReplies
                   + ", statsSeenClOrdId=" + _clientReplyHandler.getStatsBasedReplies() );
    }
}
