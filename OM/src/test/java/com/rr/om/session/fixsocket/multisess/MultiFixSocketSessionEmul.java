/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.fixsocket.multisess;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.FixDecoder;
import com.rr.core.codec.FixEncoder;
import com.rr.core.collections.BlockingSyncQueue;
import com.rr.core.collections.ConcLinkedEventQueueSingle;
import com.rr.core.collections.EventQueue;
import com.rr.core.dispatch.DirectDispatcher;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.dispatch.ThreadedDispatcher;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.model.FixVersion;
import com.rr.core.model.ModelVersion;
import com.rr.core.persister.PersisterException;
import com.rr.core.recovery.dma.DMARecoveryController;
import com.rr.core.session.*;
import com.rr.core.session.socket.PortOffset;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.session.socket.SocketSession;
import com.rr.core.thread.ControlThread;
import com.rr.core.thread.DualElementControlThread;
import com.rr.core.utils.FileException;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.ThreadUtilsFactory;
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
import com.rr.om.session.fixsocket.NonBlockingFixSocketSession;
import com.rr.om.validate.EmeaDmaValidator;
import com.rr.om.validate.EventValidator;
import com.rr.om.warmup.sim.*;

import java.io.IOException;

public class MultiFixSocketSessionEmul extends BaseFixSimProcess {

    private static final Logger _log = ConsoleFactory.console( MultiFixSocketSessionEmul.class, Level.info );

    private static final long MAX_TIME_WAIT_MS = 10000;

    public static String DEFAULT_OM_UP_ID         = "W_SMTC1";
    public static String DEFAULT_CLIENT_SIM_ID    = "W_TCLT1";
    public static int    DEFAULT_OM_CLIENT_PORT   = 14006;
    public static String DEFAULT_OM_DOWN_ID       = "W_SMTE1";
    public static String DEFAULT_EXCHANGE_SIM_ID  = "W_TEXE1";
    public static int    DEFAULT_OM_EXCHANGE_PORT = 14005;
    public static String HUB_HOST                 = "127.0.0.1";
    public static int    HUB_PORT                 = 14004;

    private final FixVersion _fixVer;
    // client sim side
    private final int                      _numClients;
    private final FixSocketSession[]       _clientSess;
    private final ClientSimSender[]        _clientSimSender;
    private final WarmClientReplyHandler[] _clientReplyHandler;
    private final ClientStatsManager[]     _statsMgr;
    private final NonBlockingFixSocketSession[]  _omToClientSess;
    private ControlThread _ct;
    private       long                     _maxRunTime = MAX_TIME_WAIT_MS;
    // OM side
    private       FixSocketSession               _omToExSess;
    private       EventProcessorImpl             _proc;
    private       MultiSessionThreadedDispatcher _om2ClientDispatcher;
    private       MultiSessionThreadedReceiver   _omFromClientReciever;

    // exchange sim side
    private FixSocketSession         _exSess;
    private WarmupExchangeSimAdapter _exSimAdapter;
    private String                   _id;

    public synchronized static MultiFixSocketSessionEmul create( String id, int ordersPerClient, int numClients ) {

        int portOffset = PortOffset.getNext();

        String args[] = { "-W", "" + ordersPerClient };

        final FixSimParams config = getProcessedParams( args, portOffset );

        MultiFixSocketSessionEmul wfss = new MultiFixSocketSessionEmul( id, config, numClients );

        return wfss;
    }

    private static FixSimParams getProcessedParams( String[] args, final int portOffset ) {
        FixSimParams params = new FixSimParams( "WarmupFixSocketSession", true, true, "S" );

        params.enableClientParams();

        params.procArgs( args );

        params.setRemovePersistence( true );

        params.setDelayMicros( 100 );

        params.setUpHost( "127.0.0.1" );
        params.setUpSenderCompId( DEFAULT_OM_UP_ID );
        params.setUpTargetCompId( DEFAULT_CLIENT_SIM_ID );
        params.setUpPort( DEFAULT_OM_CLIENT_PORT + portOffset );

        params.setDownHost( "127.0.0.1" );
        params.setDownSenderCompId( DEFAULT_OM_DOWN_ID );
        params.setDownTargetCompId( DEFAULT_EXCHANGE_SIM_ID );
        params.setDownPort( DEFAULT_OM_EXCHANGE_PORT + portOffset );

        params.setNumOrders( params.getWarmupCount() );
        params.setPersistDatPageSize( 4096 );
        params.setPersistDatPreSize( 256 * params.getWarmupCount() + 1024 );
        params.setPersistIdxPreSize( 16 * params.getWarmupCount() + 1024 );

        params.setDisableEventLogging( true );

        _log.info( "WARMUP PARAMS : numOrder=" + params.getWarmupCount() +
                   ", delayMS=" + params.getDelayMicros() +
                   ", warmClientHost=" + params.getUpHost() +
                   ", warmClientPort=" + params.getUpPort() +
                   ", warmExchangeHost=" + params.getDownHost() +
                   ", warmExchagePort=" + params.getDownPort() );

        return params;
    }

    public MultiFixSocketSessionEmul( String id, FixSimParams params, int numClients ) {
        super( params );

        _id                 = id;
        _numClients         = numClients;
        _clientSess         = new FixSocketSession[ numClients ];
        _clientSimSender    = new ClientSimSender[ numClients ];
        _clientReplyHandler = new WarmClientReplyHandler[ numClients ];
        _statsMgr           = new ClientStatsManager[ numClients ];
        _omToClientSess     = new NonBlockingFixSocketSession[ numClients ];

        _fixVer = FixVersion.Fix4_4;
    }

    @Override
    protected void init() throws SessionException, FileException, PersisterException, IOException {

        super.init();

        RecoverableSession hub = createOmToHub();
        _proc = getProcesssor( hub );

        for ( int curClient = 0; curClient < _numClients; ++curClient ) {
            _statsMgr[ curClient ]           = new ClientStatsManager( _params.getWarmupCount() );
            _clientReplyHandler[ curClient ] = new WarmClientReplyHandler( _statsMgr[ curClient ] );
            _clientSess[ curClient ]         = createClientSession( _proc, _clientReplyHandler[ curClient ], curClient );
        }

        EventQueue      queue      = getQueue( "WarmupMFSSExchangeSimProcessorQueue_" + _id );
        EventDispatcher dispatcher = new ThreadedDispatcher( "WarmupExchangeSimAdapter_" + _id, queue, ThreadPriority.Other );
        _exSimAdapter = new WarmupExchangeSimAdapter( dispatcher, _params.getWarmupCount(), queue );

        _ct                   = new DualElementControlThread( "MFSS_CTL_" + _id, ThreadPriority.Other );
        _om2ClientDispatcher  = new MultiSessionThreadedDispatcher( "OMtoClientMultiSessDispatcher_" + _id, _ct );
        _omFromClientReciever = new MultiSessionThreadedReceiver( "OMfromClientMultiSessReceiver_" + _id, _ct );

        for ( int i = 0; i < _numClients; ++i ) {
            _omToClientSess[ i ] = createOmToClientSession( _proc, hub, _om2ClientDispatcher, _omFromClientReciever, i );
        }

        _omToExSess = createOmToExchangeSession( _proc, hub );
        _exSess     = createExchangeSession( _proc, _exSimAdapter );

        OrderRouter router = new SingleDestRouter( _omToExSess );

        _proc.setProcessorRouter( router );

        // init the sockets

        _exSess.init();
        _omToExSess.init();

        for ( int i = 0; i < _numClients; ++i ) {
            _omToClientSess[ i ].init();
            _clientSess[ i ].init();
            FixSocketSession[] client = { _clientSess[ i ] };
            _clientSimSender[ i ] = new ClientSimSender( WarmupUtils.getTemplateRequests(), client, _statsMgr[ i ], "SIM" + _id + "_" + i );
        }
    }

    public int getExchangeSent() {
        return _exSimAdapter.getSent();
    }

    public int getReceived() {
        int tot = 0;

        for ( int i = 0; i < _clientReplyHandler.length; i++ ) {
            tot += _clientReplyHandler[ i ].getReceived();
        }

        return tot;
    }

    public int getSent() {
        int tot = 0;

        for ( int i = 0; i < _clientSimSender.length; i++ ) {
            tot += _clientSimSender[ i ].getSent();
        }

        return tot;
    }

    public void logDetails() {
        for ( int i = 0; i < _clientReplyHandler.length; i++ ) {
            _log.info( "Client " + i + " sent " + _clientSimSender[ i ].getSent() );
            _log.info( "Client " + i + " received " + _clientReplyHandler[ i ].getReceived() );
            _log.info( "Client " + i + " stats based replies " + _clientReplyHandler[ i ].getStatsBasedReplies() );
        }

        _log.info( "ExchangeSim sent " + _exSimAdapter.getSent() );
    }

    public void setEventLogging( boolean on ) {
        _params.setDisableEventLogging( !on );
    }

    public void setMaxRunTime( long maxRunTime ) {
        _maxRunTime = maxRunTime;
    }

    public void warmup() throws Exception {

        init();

        run();
    }

    protected void doClientSimSend( int curClient ) {
        _clientReplyHandler[ curClient ].reset();
        _clientSimSender[ curClient ].reset();
        _statsMgr[ curClient ].reset();

        _clientSimSender[ curClient ].dispatchEvents( _params.getWarmupCount(), _params.getBatchSize(), _params.getDelayMicros() );
    }

    private void close() {
        _proc.stop();
        for ( int i = 0; i < _numClients; ++i ) {
            _clientSess[ i ].stop();
            _omToClientSess[ i ].stop();
        }
        _omToExSess.stop();
        _exSess.stop();
        _exSimAdapter.stop();
        _ct.setStopping( true );
    }

    private void connect() {

        _om2ClientDispatcher.start();
        _omFromClientReciever.start();  // THIS WILL RUN CONNECT ON OM-CLIENT SESSIONS

        _exSess.connect();
        _omToExSess.connect();

        for ( int i = 0; i < 20; i++ ) {
            ThreadUtilsFactory.get().sleep( 100 );
            if ( _omToExSess.isLoggedIn() && _exSess.isLoggedIn() ) {
                break;
            }
        }

        for ( int i = 0; i < _numClients; ++i ) {
            _clientSess[ i ].connect();
        }
    }

    private int countClientReceived() {
        int totSent = 0;
        for ( int i = 0; i < _numClients; ++i ) {
            totSent += _clientReplyHandler[ i ].getReceived();
        }
        return totSent;
    }

    private int countClientSent() {
        int totSent = 0;
        for ( int i = 0; i < _numClients; ++i ) {
            totSent += _clientSimSender[ i ].getSent();
        }
        return totSent;
    }

    private int countClientStatsReplies() {
        int totSent = 0;
        for ( int i = 0; i < _numClients; ++i ) {
            totSent += _clientReplyHandler[ i ].getStatsBasedReplies();
        }
        return totSent;
    }

    private int countExpReplies() {
        int eotExpectedReplies = 0;
        for ( int i = 0; i < _numClients; ++i ) {
            eotExpectedReplies += _clientSimSender[ i ].getExpectedReplies();
        }
        return eotExpectedReplies;
    }

    private FixSocketSession createClientSession( EventProcessorImpl proc, WarmClientReplyHandler clientReplyHandler, int curClient ) throws SessionException, PersisterException {

        String      name          = "CLIENTSIM_" + _id + "_" + curClient;
        EventRouter inboundRouter = new PassThruRouter( "passThru", clientReplyHandler );
        int         logHdrOut     = AbstractSession.getDataOffset( name, false );
        byte[]      outBuf        = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        FixEncoder  encoder       = WarmupUtils.getEncoder( _fixVer, outBuf, logHdrOut );
        FixDecoder  decoder       = WarmupUtils.getFixOMSDecoder( _fixVer );
        Decoder     fullDecoder   = WarmupUtils.getFullDecoder( _fixVer );

        ThreadPriority receiverPriority = ThreadPriority.Other;

        ZString senderCompId = new ViewString( _params.getUpTargetCompId() + curClient );
        ZString senderSubId  = new ViewString( "" );
        ZString targetCompId = new ViewString( _params.getUpSenderCompId() + curClient );
        ZString targetSubId  = new ViewString( "" );
        ZString userName     = new ViewString( "" );
        ZString password     = new ViewString( "" );
        FixSocketConfig socketConfig = new FixSocketConfig( AllEventRecycler.class,
                                                            false,
                                                            new ViewString( _params.getUpHost() ),
                                                            null,
                                                            _params.getUpPort() + curClient,
                                                            senderCompId,
                                                            senderSubId,
                                                            targetCompId,
                                                            targetSubId,
                                                            userName,
                                                            password );

        socketConfig.setDisconnectOnMissedHB( false );
        socketConfig.setRecoverFromLoginSeqNumTooLow( true );
        socketConfig.setUseNIO( false );

        setSocketPerfOptions( socketConfig );

        socketConfig.setInboundPersister( createInboundPersister( name + "_CLTSIM_IN", ThreadPriority.Other ) );
        socketConfig.setOutboundPersister( createOutboundPersister( name + "_CLTSIM_OUT", ThreadPriority.Other ) );

        EventDispatcher dispatcher = new SessionThreadedDispatcher( "CLTSIM_DIS" + curClient, new BlockingSyncQueue(), ThreadPriority.Other );

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

        _log.info( "CLIENT session " + name + sess.info() );

        return sess;
    }

    private FixSocketSession createExchangeSession( EventProcessorImpl proc, WarmupExchangeSimAdapter exSimAdapter ) throws SessionException, PersisterException {
        String         name             = "EXSIM_" + _id;
        EventRouter    inboundRouter    = new PassThruRouter( "passThru", exSimAdapter );
        int            logHdrOut        = AbstractSession.getDataOffset( name, false );
        byte[]         outBuf           = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        FixEncoder     encoder          = WarmupUtils.getEncoder( _fixVer, outBuf, logHdrOut );
        FixDecoder     decoder          = WarmupUtils.getFixOMSDecoder( _fixVer );
        FixDecoder     fullDecoder      = WarmupUtils.getFullDecoder( _fixVer );
        ThreadPriority receiverPriority = ThreadPriority.Other;
        ZString        senderCompId     = new ViewString( _params.getDownTargetCompId() );
        ZString        senderSubId      = new ViewString( "" );
        ZString        targetCompId     = new ViewString( _params.getDownSenderCompId() ); // perspective of params is OM in this case
        ZString        targetSubId      = new ViewString( "" );
        ZString        userName         = new ViewString( "" );
        ZString        password         = new ViewString( "" );

        FixSocketConfig socketConfig = new FixSocketConfig( AllEventRecycler.class,
                                                            true,
                                                            new ViewString( _params.getDownHost() ),
                                                            null,
                                                            _params.getDownPort(),
                                                            senderCompId,
                                                            senderSubId,
                                                            targetCompId,
                                                            targetSubId,
                                                            userName,
                                                            password );

        socketConfig.setDisconnectOnMissedHB( false );
        socketConfig.setRecoverFromLoginSeqNumTooLow( true );

        EventDispatcher dispatcher = new SessionThreadedDispatcher( "EXSIM_DIS", new BlockingSyncQueue(), ThreadPriority.Other );

        socketConfig.setInboundPersister( createInboundPersister( name + "_EXSIM_IN", ThreadPriority.Other ) );
        socketConfig.setOutboundPersister( createOutboundPersister( name + "_EXSIM_OUT", ThreadPriority.Other ) );
        socketConfig.setUseNIO( false );

        setSocketPerfOptions( socketConfig );

        FixSocketSession sess = new FixSocketSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder,
                                                      fullDecoder, receiverPriority );

        dispatcher.setHandler( sess );

        encoder.setNanoStats( false );
        decoder.setNanoStats( false );
        sess.setLogStats( false );
        if ( _params.isDisableEventLogging() ) {
            sess.setLogEvents( false );
        }

        _log.info( "EXCHANGE session " + name + sess.info() );

        return sess;
    }

    private NonBlockingFixSocketSession createOmToClientSession( EventProcessor proc,
                                                                 RecoverableSession hub,
                                                                 MultiSessionThreadedDispatcher dispatcher,
                                                                 MultiSessionThreadedReceiver receiver,
                                                                 int curClient )

            throws SessionException, PersisterException {

        String      name          = "OM2CLT_" + _id + "_" + curClient;
        EventRouter inboundRouter = new PassThruRouter( "passThru", proc );
        int         logHdrOut     = AbstractSession.getDataOffset( name, false );
        byte[]      outBuf        = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        FixEncoder  encoder       = WarmupUtils.getEncoder( _fixVer, outBuf, logHdrOut );
        FixDecoder  decoder       = WarmupUtils.getFixOMSDecoder( _fixVer );
        Decoder     fullDecoder   = WarmupUtils.getFullDecoder( _fixVer );

        ZString senderCompId = new ViewString( _params.getUpSenderCompId() + curClient );
        ZString senderSubId  = new ViewString( "" );
        ZString targetCompId = new ViewString( _params.getUpTargetCompId() + curClient );
        ZString targetSubId  = new ViewString( "" );
        ZString userName     = new ViewString( "" );
        ZString password     = new ViewString( "" );

        FixSocketConfig socketConfig = new FixSocketConfig( AllEventRecycler.class,
                                                            true,
                                                            new ViewString( _params.getUpHost() ),
                                                            null,
                                                            _params.getUpPort() + curClient,
                                                            senderCompId,
                                                            senderSubId,
                                                            targetCompId,
                                                            targetSubId,
                                                            userName,
                                                            password );
        socketConfig.setDisconnectOnMissedHB( false );
        socketConfig.setRecoverFromLoginSeqNumTooLow( true );
        setSocketPerfOptions( socketConfig );
        socketConfig.setUseNIO( true );

        socketConfig.setInboundPersister( createInboundPersister( name + "_CLT_IN", ThreadPriority.Other ) );
        socketConfig.setOutboundPersister( createOutboundPersister( name + "_CLT_OUT", ThreadPriority.Other ) );

        EventQueue queue = new ConcLinkedEventQueueSingle();
        NonBlockingFixSocketSession sess = new NonBlockingFixSocketSession( name,
                                                                            inboundRouter,
                                                                            socketConfig,
                                                                            dispatcher,
                                                                            receiver,
                                                                            encoder,
                                                                            decoder,
                                                                            fullDecoder,
                                                                            queue );

        sess.setChainSession( hub );
        dispatcher.addSession( sess );

        if ( _params.isDisableNanoStats() ) {
            encoder.setNanoStats( false );
            decoder.setNanoStats( false );
            sess.setLogStats( false );
        }

        if ( _params.isDisableEventLogging() ) {
            sess.setLogEvents( false );
        }

        _log.info( "OM2CLIENT session " + name + sess.info() );

        return sess;
    }

    private FixSocketSession createOmToExchangeSession( EventProcessor proc, RecoverableSession hub ) throws SessionException, PersisterException {
        String      name          = "OM_2_SRV_" + _id;
        EventRouter inboundRouter = new PassThruRouter( "passThru", proc );
        int         logHdrOut     = AbstractSession.getDataOffset( name, false );
        byte[]      outBuf        = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        FixEncoder  encoder       = WarmupUtils.getEncoder( _fixVer, outBuf, logHdrOut );
        FixDecoder  decoder       = WarmupUtils.getFixOMSDecoder( _fixVer );
        Decoder     fullDecoder   = WarmupUtils.getFullDecoder( _fixVer );

        ThreadPriority receiverPriority = ThreadPriority.Other;

        ZString senderCompId = new ViewString( _params.getDownSenderCompId() );
        ZString senderSubId  = new ViewString( "" );
        ZString targetCompId = new ViewString( _params.getDownTargetCompId() );
        ZString targetSubId  = new ViewString( "" );
        ZString userName     = new ViewString( "" );
        ZString password     = new ViewString( "" );
        FixSocketConfig socketConfig = new FixSocketConfig( AllEventRecycler.class,
                                                            false,
                                                            new ViewString( _params.getDownHost() ),
                                                            null,
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
        socketConfig.setUseNIO( true );

        socketConfig.setInboundPersister( createInboundPersister( name + "_EXC1_IN", ThreadPriority.Other ) );
        socketConfig.setOutboundPersister( createOutboundPersister( name + "_EXC1_OUT", ThreadPriority.Other ) );

        EventDispatcher dispatcher = getSessionDispatcher( "WARM_OM2EX_DISPATCHER", ThreadPriority.Other );

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

        _log.info( "OM2EXCHANGE session " + name + sess.info() );

        return sess;
    }

    private SocketSession createOmToHub() {
        String      name          = "OM2HUB_" + _id;
        int         logHdrOut     = SocketSession.getDataOffset( name, false );
        byte[]      outBuf        = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        FixEncoder  encoder       = WarmupUtils.getEncoder( _fixVer, outBuf, logHdrOut );
        FixDecoder  decoder       = WarmupUtils.getFixOMSDecoder( _fixVer );
        Decoder     fullDecoder   = WarmupUtils.getFullDecoder( _fixVer );
        EventRouter inboundRouter = new DummyRouter( "dummyRouter" );

        ThreadPriority receiverPriority = ThreadPriority.Other;
        SocketConfig   socketConfig     = new SocketConfig( AllEventRecycler.class, true, new ViewString( HUB_HOST ), null, HUB_PORT );

        socketConfig.setSoDelayMS( 0 );
        socketConfig.setUseNIO( true );

        EventDispatcher dispatcher = new SessionThreadedDispatcher( "WARM_OM2HUB_DISPATCHER", new BlockingSyncQueue(), ThreadPriority.Other );

        SocketSession sess = new SocketSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder,
                                                fullDecoder, receiverPriority );

        dispatcher.setHandler( sess );

        if ( _params.isDisableEventLogging() ) {
            sess.setLogEvents( false );
        }

        return sess;
    }

    private EventProcessorImpl getProcesssor( RecoverableSession hub ) {

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

        EventProcessorImpl t = new EventProcessorImpl( version, _params.getWarmupCount() * _numClients, validator, builder, dispatcher, hub, tradeReg );

        dispatcher.start();

        return t;
    }

    private void recover() {

        // OM
        DMARecoveryController omRct = new DummyRecoveryController();
        _omToExSess.recover( omRct );
        _omToExSess.waitForRecoveryToComplete();
        for ( int i = 0; i < _numClients; ++i ) {
            _omToClientSess[ i ].recover( omRct );
        }
        for ( int i = 0; i < _numClients; ++i ) {
            _omToClientSess[ i ].waitForRecoveryToComplete();
        }

        // EX
        DMARecoveryController exRct = new DummyRecoveryController();
        _exSess.recover( exRct );
        _exSess.waitForRecoveryToComplete();

        // CLIENT
        DMARecoveryController clientRct = new DummyRecoveryController();
        for ( int i = 0; i < _numClients; ++i ) {
            _clientSess[ i ].recover( clientRct );
        }
        for ( int i = 0; i < _numClients; ++i ) {
            _clientSess[ i ].waitForRecoveryToComplete();
        }
    }

    private void run() {

        try {
            recover();
            connect();

            sendClientMessages();
            waitForMessagesProcessed();

            _log.info( "WarmupFixSocketSession COMPLETED  : testRequests=" + _params.getWarmupCount() );

        } finally {
            close();
        }

        for ( int i = 0; i < _numClients; ++i ) {
            _log.info( "CLIENTSTATS " + i );
            _statsMgr[ i ].logStats();
        }
    }

    private void sendClientMessages() {
        _proc.clear();

        Thread[] clientSimThreads = new Thread[ _numClients ];

        for ( int i = 0; i < _numClients; ++i ) {
            final int curClient = i;
            clientSimThreads[ i ] = new Thread( () -> doClientSimSend( curClient ), "CLIENTSIM_SENDER_" + curClient );

            clientSimThreads[ i ].start();
        }

        for ( int i = 0; i < _numClients; ++i ) {
            _log.info( "Waiting for clientSim " + i + " to finish" );
            try {
                clientSimThreads[ i ].join();
            } catch( InterruptedException e ) { /* dont care */ }
        }
    }

    private void waitForMessagesProcessed() {

        int totSent = countClientSent();

        _log.info( "MultiFixSocketSessionEmul() " + _params.getAppName() + " Warmup waiting for expected number of events " + totSent );

        long start = ClockFactory.get().currentTimeMillis();

        long totExpReplies = countExpReplies();

        // first wait for client replay handler for client to receive
        while( countClientReceived() < totExpReplies ) {

            ThreadUtilsFactory.get().sleep( 200 );

            long timeMS = ClockFactory.get().currentTimeMillis() - start;

            if ( timeMS > _maxRunTime ) {
                _log.info( "MultiFixSocketSessionEmul() " + _params.getAppName() + " Hit max wait time for warmup complete" );

                break;
            }
        }

        _log.info( "MultiFixSocketSessionEmul() " + _params.getAppName() + " Warmup replies=" + countClientReceived() + ", statsSeenClOrdId=" + countClientStatsReplies() );
    }
}
