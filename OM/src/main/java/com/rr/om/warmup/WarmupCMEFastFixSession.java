/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.warmup;

import com.rr.algo.ExchangeContainerAdapter;
import com.rr.algo.t1.T1Algo;
import com.rr.core.codec.Decoder;
import com.rr.core.codec.Encoder;
import com.rr.core.codec.FixDecoder;
import com.rr.core.codec.FixEncoder;
import com.rr.core.collections.BlockingSyncQueue;
import com.rr.core.collections.ConcLinkedEventQueueSingle;
import com.rr.core.collections.EventQueue;
import com.rr.core.dispatch.DirectDispatcher;
import com.rr.core.dispatch.DirectDispatcherNonThreadSafe;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.dispatch.ThreadedDispatcher;
import com.rr.core.dummy.warmup.DummyEventHandler;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.model.Book;
import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;
import com.rr.core.persister.PersisterException;
import com.rr.core.session.*;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.session.socket.SocketSession;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.core.warmup.JITWarmup;
import com.rr.core.warmup.WarmupRegistry;
import com.rr.inst.InstrumentStore;
import com.rr.md.book.l2.L2BookDispatchAdapter;
import com.rr.md.book.l2.L2BookFactory;
import com.rr.md.fastfix.FastSocketConfig;
import com.rr.md.us.cme.CMEBookAdapter;
import com.rr.md.us.cme.CMEFastFixSession;
import com.rr.md.us.cme.CMEMktDataController;
import com.rr.md.us.cme.reader.CMEFastFixDecoder;
import com.rr.md.us.cme.writer.CMEFastFixEncoder;
import com.rr.model.generated.fix.codec.CMEEncoder;
import com.rr.model.generated.fix.codec.CodecId;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;
import com.rr.model.generated.internal.events.impl.ClientNewOrderAckImpl;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.model.generated.internal.events.impl.MarketNewOrderAckImpl;
import com.rr.model.generated.internal.events.impl.MarketNewOrderSingleImpl;
import com.rr.om.main.BaseSMTMain;
import com.rr.om.router.OrderRouter;
import com.rr.om.router.SingleDestRouter;
import com.rr.om.session.fixsocket.FixSocketConfig;
import com.rr.om.session.fixsocket.FixSocketSession;
import com.rr.om.warmup.sim.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @NOTE is get multicast dropped packets it possible the decoder will have the wrong value in the stateful templateId reader this will generate errors
 */
public class WarmupCMEFastFixSession extends AbstractWarmupSession implements JITWarmup {

    private static final String DEFAULT_TEMPLATES = "./data/cme/sampleMD.dat";
    private final List<byte[]>         _templateRequests = new ArrayList<>();
    private final InstrumentStore _instLocator;
    T1Algo _t1;
    private       String               _templateDataFile;
    private       CMEMktDataController _ctlr;
    private ExchangeContainerAdapter _exchangeInboundHandler;
    private String                   _mcastGroups = "224.0.0.1";

    public static WarmupCMEFastFixSession create( String appName, String[] args, InstrumentStore locator ) {

        FixSimParams params = getProcessedParams( appName, args );

        WarmupCMEFastFixSession wfss = new WarmupCMEFastFixSession( params, locator );

        return wfss;
    }

    public static WarmupCMEFastFixSession create( String appName, int portOffset, boolean spinLocks, int count, InstrumentStore locator ) {

        FixSimParams params = AbstractWarmupSession.createParams( appName, portOffset, spinLocks, count );

        WarmupCMEFastFixSession wfss = new WarmupCMEFastFixSession( params, locator );

        return wfss;
    }

    public WarmupCMEFastFixSession( String appName, String args[], InstrumentStore locator ) {
        super( getProcessedParams( appName, args ), CodecId.CME );
        _instLocator = locator;
        commonInit();
    }

    public WarmupCMEFastFixSession( FixSimParams params, InstrumentStore locator ) {
        super( params, CodecId.CME );
        _instLocator = locator;

        commonInit();
    }

    @Override
    public String getName() {
        return "WarmCMEFastFixSession";
    }

    @Override
    public void warmup() throws Exception {
//        warmSession();
        super.warmup();
    }

    @Override
    protected void postCreateInit() {
        OrderRouter router = new SingleDestRouter( _omToExSess );
        _t1 = new T1Algo( 2, router );
        _t1.setDebug( _params.isDebug() );
        _exchangeInboundHandler.setContainer( _t1 );
    }

    @Override
    protected SimClient createSimSender() throws IOException {
        BaseSMTMain.loadSampleData( _templateDataFile, _templateRequests, 30 );

        return new SimCMEFastFixSender( _templateRequests, false, (CMEFastFixSession) _clientSess );
    }

    @Override
    protected RecoverableSession createOmToExchangeSession( EventHandler proc, RecoverableSession hub ) throws SessionException, PersisterException {
        String     name        = "WARM_OM_2_SRV";
        int        logHdrOut   = AbstractSession.getDataOffset( name, false );
        byte[]     outBuf      = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        FixEncoder encoder     = new CMEEncoder( _codecId.getFixVersion()._major, _codecId.getFixVersion()._minor, outBuf, logHdrOut );
        FixDecoder decoder     = WarmupUtils.getCMEDecoder();
        Decoder    fullDecoder = WarmupUtils.getFullDecoder( _codecId );

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

        socketConfig.setHeartBeatIntSecs( _heartbeat );
        socketConfig.setDisconnectOnMissedHB( false );
        socketConfig.setRecoverFromLoginSeqNumTooLow( true );
        setSocketPerfOptions( socketConfig );

        socketConfig.setInboundPersister( createInboundPersister( name + "_EXC1_IN", ThreadPriority.Other ) );
        socketConfig.setOutboundPersister( createOutboundPersister( name + "_EXC1_OUT", ThreadPriority.Other ) );

        EventDispatcher dispatcher = getSessionDispatcher( "WARM_OM2EX_DISPATCHER", ThreadPriority.Other );

        FixSocketSession sess = new FixSocketSession( name, _exchangeInboundHandler, socketConfig, dispatcher, encoder, decoder,
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

    @Override
    protected RecoverableSession createExchangeSession( WarmupExchangeSimAdapter exSimAdapter ) throws SessionException, PersisterException {
        String         name             = "WARM_EXSIM";
        EventRouter    inboundRouter    = new PassThruRouter( "passThru", exSimAdapter );
        int            logHdrOut        = AbstractSession.getDataOffset( name, false );
        byte[]         outBuf           = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        FixEncoder     encoder          = new CMEEncoder( _codecId.getFixVersion()._major, _codecId.getFixVersion()._minor, outBuf, logHdrOut );
        FixDecoder     decoder          = WarmupUtils.getCMEDecoder();
        FixDecoder     fullDecoder      = (FixDecoder) WarmupUtils.getFullDecoder( _codecId );
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
        socketConfig.setHeartBeatIntSecs( _heartbeat );
        socketConfig.setDisconnectOnMissedHB( false );
        socketConfig.setRecoverFromLoginSeqNumTooLow( true );

        EventDispatcher dispatcher = getSessionDispatcher( "WARMUP_EXSIM_DISPATCHER", ThreadPriority.Other );

        socketConfig.setInboundPersister( createInboundPersister( name + "_EXSIM_IN", ThreadPriority.Other ) );
        socketConfig.setOutboundPersister( createOutboundPersister( name + "_EXSIM_OUT", ThreadPriority.Other ) );

        setSocketPerfOptions( socketConfig );

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

    @Override
    protected void run() {

        try {
            int expectedOrders = _params.getNumOrders() / _t1.getNOSMod();

            _log.info( "CMEFastFix Warmup expected number of events " + expectedOrders );

            recover();
            connect();

            int idx = 0;
            while( idx < 20 && !isReadyToTrade() ) {
                ThreadUtilsFactory.get().sleep( 100 );
            }

            sendClientMessages();

            _log.info( "CMEFastFix Warmup waiting for expected number of events " );

            long start = ClockFactory.get().currentTimeMillis();

            // first wait for client replay handler for client to receive
            while( _exSimAdapter.getRequests() < expectedOrders ) {

                ThreadUtilsFactory.get().sleep( 200 );

                _log.info( "WarmupCMEFastFixSession replies=" + _exSimAdapter.getRequests() + ", expected=" + expectedOrders );

                long timeMS = ClockFactory.get().currentTimeMillis() - start;

                if ( timeMS > _maxRunTime ) {
                    _log.info( "WarmupCMEFastFixSession : Hit max wait time for warmup complete" );

                    break;
                }
            }

            _log.info( "CMEFastFix Warmup replies=" + _exSimAdapter.getRequests() + ", expected=" + expectedOrders );

            _log.info( "CMEFastFix WarmupFixSocketSession COMPLETED  : testRequests=" + _params.getWarmupCount() );

        } catch( IOException e ) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    @Override
    protected EventHandler getProcesssor( RecoverableSession hub ) {

        _exchangeInboundHandler = new ExchangeContainerAdapter( "exContainerRouter", null );

        EventDispatcher inboundDispatcher;

        if ( _params.isOptimiseForThroughPut() ) {

            EventQueue queue = new ConcLinkedEventQueueSingle();
            inboundDispatcher = new ThreadedDispatcher( "WarmupMktDataDispatcher", queue, ThreadPriority.Other );

        } else if ( _params.isOptimiseForLatency() ) {
            // no dispatcher faster for straight latency .. fine providing only one client
            // tho should really lock the order in processor

            inboundDispatcher = new DirectDispatcher();

        } else {
            EventQueue queue = new BlockingSyncQueue();
            inboundDispatcher = new ThreadedDispatcher( "WarmupMktDataDispatcher", queue, ThreadPriority.Other );
        }

        L2BookFactory<CMEBookAdapter> bookFactory = new L2BookFactory<>( CMEBookAdapter.class, false, _instLocator, 10 );

        EventDispatcher algoDispatcher = new DirectDispatcherNonThreadSafe();

        algoDispatcher.setHandler( new EventHandler() {

            @Override public String getComponentId() { return null; }

            @Override
            public void handle( Event event ) {
                handleNow( event );
            }

            @Override public void handleNow( Event event ) {
                Book book = (Book) event;
                getAlgo().changed( book );
            }

            @Override public boolean canHandle() { return true; }

            @Override public void threadedInit() { /* nothing */ }
        } );

        L2BookDispatchAdapter<CMEBookAdapter> asyncListener = new L2BookDispatchAdapter<>( algoDispatcher );

        _ctlr = new CMEMktDataController( "TestController", "2", inboundDispatcher, bookFactory, asyncListener, _instLocator, false );

        _ctlr.setOverrideSubscribeSet( true );

        algoDispatcher.start();
        inboundDispatcher.start();

        return _ctlr;
    }

    @Override
    protected RecoverableSession createOmToClientSession( EventHandler proc, RecoverableSession hub ) {

        String      name          = "WARM_OM_FASTFIX_IN";
        EventRouter inboundRouter = new PassThruRouter( "passThru", proc );

        ThreadPriority receiverPriority = ThreadPriority.Other;

        EventDispatcher dispatcher = getSessionDispatcher( "WARMUP_OM2CLIENT_DISPATCHER", ThreadPriority.Other );

        int              port         = _params.getUpPort();
        ZString[]        grps         = BaseSMTMain.formMulticastGroups( _mcastGroups );
        FastSocketConfig socketConfig = new FastSocketConfig( AllEventRecycler.class, false, grps[ 0 ], null, port );

        setSocketPerfOptions( socketConfig );

        socketConfig.setDisableLoopback( false );
        socketConfig.setQOS( 2 );
        socketConfig.setTTL( 0 );
        socketConfig.setNic( new ViewString( "127.0.0.1" ) );
        socketConfig.setMulticast( true );
        socketConfig.setMulticastGroups( grps );

        socketConfig.validate();

        long subChannelMask = -1;

        CMEFastFixEncoder encoder = new CMEFastFixEncoder( "CMEOM2ClientWriter" + name, "data/cme/templates.xml", _params.isDebug() );
        Decoder           decoder = new CMEFastFixDecoder( "CMEOM2ClientReader" + name, "data/cme/templates.xml", subChannelMask, _params.isDebug() );

        CMEFastFixSession sess = new CMEFastFixSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder, receiverPriority );

        dispatcher.setHandler( sess );

        if ( _params.isDebug() ) {
            sess.setLogEvents( true );
            sess.setLogPojos( true );
        } else {

            if ( _params.isDisableNanoStats() ) {
                encoder.setNanoStats( false );
                decoder.setNanoStats( false );
                sess.setLogStats( false );
            }

            if ( _params.isDisableEventLogging() ) {
                sess.setLogEvents( false );
            }
        }

        return sess;
    }

    @Override
    protected RecoverableSession createClientSession( WarmClientReplyHandler unused ) {

        String      name          = "WARM_FASTFIX_CLIENTSIM";
        EventRouter inboundRouter = new PassThruRouter( "passThru", new DummyEventHandler() );

        ThreadPriority receiverPriority = ThreadPriority.Other;

        EventDispatcher dispatcher = getSessionDispatcher( "WARM_CLTSIM_DISPATCHER", ThreadPriority.Other );

        int              port         = _params.getUpPort();
        ZString[]        grps         = BaseSMTMain.formMulticastGroups( _mcastGroups );
        FastSocketConfig socketConfig = new FastSocketConfig( AllEventRecycler.class, true, grps[ 0 ], null, port );

        setSocketPerfOptions( socketConfig );

        socketConfig.setDisableLoopback( false );
        socketConfig.setQOS( 2 );
        socketConfig.setTTL( 0 );
        socketConfig.setNic( new ViewString( "127.0.0.1" ) );
        socketConfig.setMulticast( true );
        socketConfig.setMulticastGroups( grps );

        socketConfig.validate();

        long subChannelMask = -1;

        CMEFastFixEncoder encoder = new CMEFastFixEncoder( "CMEClientWriter" + name, "data/cme/templates.xml", _params.isDebug() );
        CMEFastFixDecoder decoder = new CMEFastFixDecoder( "CMEClientReader" + name, "data/cme/templates.xml", subChannelMask, _params.isDebug() );

        CMEFastFixSession sess = new CMEFastFixSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder, receiverPriority );

        dispatcher.setHandler( sess );

        if ( _params.isDebug() ) {
            sess.setLogEvents( true );
        } else {

            if ( _params.isDisableNanoStats() ) {
                encoder.setNanoStats( false );
                decoder.setNanoStats( false );
                sess.setLogStats( false );
            }

            if ( _params.isDisableEventLogging() ) {
                sess.setLogEvents( false );
            }
        }

        return sess;
    }

    public int getOrdersReceived() {
        return _exSimAdapter.getRequests();
    }

    protected void warmSession() {
        String      name          = "WARMSESS";
        int         logHdrOut     = SocketSession.getDataOffset( name, false );
        byte[]      outBuf        = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        Encoder     encoder       = FixTestUtils.getEncoder44( outBuf, logHdrOut );
        Decoder     decoder       = FixTestUtils.getOMSDecoder44();
        Decoder     fullDecoder   = FixTestUtils.getFullDecoder44();
        EventRouter inboundRouter = new DummyRouter( "dummyRouter" );

        ThreadPriority receiverPriority = ThreadPriority.HubSimulator;
        SocketConfig socketConfig = new SocketConfig( AllEventRecycler.class, true, new ViewString( FixSimConstants.DEFAULT_HUB_HOST ),
                                                      null, FixSimConstants.DEFAULT_HUB_PORT );

        socketConfig.setSoDelayMS( 0 );
        socketConfig.setUseNIO( false );

        EventDispatcher dispatcher;
        EventQueue      queue = new ConcLinkedEventQueueSingle();
        dispatcher = new SessionThreadedDispatcher( "WARMDISP", queue, ThreadPriority.Other );

        SocketSession sess = new SocketSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder,
                                                fullDecoder, receiverPriority );

        dispatcher.setHandler( sess );

        Event m1 = new ClientNewOrderSingleImpl();
        Event m2 = new MarketNewOrderSingleImpl();
        Event m3 = new MarketNewOrderAckImpl();
        Event m4 = new ClientNewOrderAckImpl();

        // dispatcher is not started and session is not connected so no background thread running, ie can manipulate queue directly
        int warmupCount = WarmupRegistry.instance().getWarmupCount();
        for ( int i = 0; i < warmupCount; i++ ) {
            sess.handle( m1 );
            queue.next();
            sess.handle( m2 );
            queue.next();
            sess.handle( m3 );
            sess.handle( m4 );
            queue.poll();
            queue.poll();
            queue.poll();
            queue.poll();
            sess.handle( m1 );
            sess.handle( m2 );
            sess.handle( m3 );
            sess.handle( m4 );
            queue.poll();
            queue.poll();
            queue.poll();
            queue.poll();
        }

        queue.clear();
    }

    T1Algo getAlgo() {
        return _t1;
    }

    private void commonInit() {
        if ( _params.getClientDataFile() == null ) {
            _params.setClientDataFile( DEFAULT_TEMPLATES );
        }

        _templateDataFile = _params.getClientDataFile();
    }
}
