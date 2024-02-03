/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.fixsocket;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.FixDecoder;
import com.rr.core.codec.FixEncoder;
import com.rr.core.dispatch.DirectDispatcher;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.dummy.warmup.TestStats;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.lang.stats.StatsMgr;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.EventHandler;
import com.rr.core.persister.PersisterException;
import com.rr.core.recovery.dma.DMARecoveryController;
import com.rr.core.session.AbstractSession;
import com.rr.core.session.EventRouter;
import com.rr.core.session.PassThruRouter;
import com.rr.core.session.SessionException;
import com.rr.core.utils.FileException;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.core.utils.Utils;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;
import com.rr.model.generated.internal.events.impl.*;
import com.rr.om.dummy.warmup.ClientStatsManager;
import com.rr.om.order.collections.HashEntry;
import com.rr.om.recovery.DummyRecoveryController;
import com.rr.om.warmup.FixSimConstants;
import com.rr.om.warmup.FixTestUtils;
import com.rr.om.warmup.WarmupFixSocketSession;
import com.rr.om.warmup.sim.BaseFixSimProcess;
import com.rr.om.warmup.sim.ClientSimSender;
import com.rr.om.warmup.sim.FixSimParams;
import com.rr.om.warmup.sim.WarmClientReplyHandler;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TstFixClientSimulator extends BaseFixSimProcess {

    static final         Logger    _log     = ConsoleFactory.console( TstFixClientSimulator.class, Level.info );
    private static final ErrorCode FAILED   = new ErrorCode( "TFC100", "Exception in main" );
    private static final ErrorCode WARM_ERR = new ErrorCode( "TFC200", "Warmup Exception in main" );
    ClientStatsManager _statsMgr;
    private FixSocketSession _clientSession;
    private List<byte[]>     _templateRequests = new ArrayList<>();
    private WarmClientReplyHandler _clientReplyHandler;

    private ClientSimSender _clientSimSender;

    public static void main( String[] args ) {

        LoggerFactory.setForceConsole( true );
        StatsMgr.setStats( new TestStats() );
        FixSimParams params = getProcessedParams( args );

        ThreadUtilsFactory.get().init( params.getCpuMasksFile(), true );

        ThreadUtilsFactory.get().setPriority( Thread.currentThread(), ThreadPriority.ClientSimulatorOut );

        if ( params.getWarmupCount() > 0 ) {
            try {
                WarmupFixSocketSession sess = WarmupFixSocketSession.create( "TstFixClient", args );
                sess.warmup();
            } catch( Throwable t ) {
                _log.error( WARM_ERR, "Error in warmup", t );
            }
        }

        TstFixClientSimulator tcs = new TstFixClientSimulator( params );

        try {
            tcs.init();

            tcs.run();
        } catch( Exception e ) {

            _log.error( FAILED, "", e );
        }
    }

    private static FixSimParams getProcessedParams( String[] args ) {
        FixSimParams params = new FixSimParams( "TstFixClientSim", false, true, "C" );
        params.enableClientParams();

        params.setDownHost( "localhost" );
        params.setDownSenderCompId( FixSimConstants.DEFAULT_CLIENT_SIM_ID );
        params.setDownTargetCompId( FixSimConstants.DEFAULT_OM_UP_ID );
        params.setDownPort( FixSimConstants.DEFAULT_OM_CLIENT_PORT );
        params.setFileName( FixSimConstants.DEFAULT_CLIENT_DATA_FILE );

        params.procArgs( args );

        _log.info( "CONNECT PARAMS : OM host=" + params.getDownHost() + ", port=" + params.getDownPort() );

        return params;
    }

    public TstFixClientSimulator( FixSimParams params ) {
        super( params );
    }

    @Override
    protected void init() throws SessionException, FileException, PersisterException, IOException {

        super.init();

        presize();

        int expOrders = _params.getNumOrders();

        _statsMgr = new ClientStatsManager( expOrders );

        DateFormat utcFormat = new SimpleDateFormat( "yyyyMMdd" );
        String     today     = utcFormat.format( new Date() );
        FixTestUtils.setTodayStr( today );

        _clientReplyHandler = new WarmClientReplyHandler( _statsMgr );
        _clientSession      = createClientSession( expOrders, _clientReplyHandler );

        // start the sockets

        _clientSession.init();
    }

    private FixSocketSession createClientSession( int expOrders, EventHandler inHandler ) throws SessionException, PersisterException {
        String         name             = "TCLIENT";
        EventRouter    inboundRouter    = new PassThruRouter( "passThru", inHandler );
        int            logHdrOut        = AbstractSession.getDataOffset( name, false );
        byte[]         outBuf           = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        FixEncoder     encoder          = FixTestUtils.getEncoder44( outBuf, logHdrOut );
        FixDecoder     decoder          = FixTestUtils.getOMSDecoder44();
        Decoder        fullDecoder      = FixTestUtils.getFullDecoder44();
        ThreadPriority receiverPriority = ThreadPriority.ClientSimulatorIn;
        ZString        senderCompId     = new ViewString( _params.getDownSenderCompId() );
        ZString        senderSubId      = new ViewString( "" );
        ZString        targetCompId     = new ViewString( _params.getDownTargetCompId() );
        ZString        targetSubId      = new ViewString( "" );
        ZString        userName         = new ViewString( "" );
        ZString        password         = new ViewString( "" );

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

        EventDispatcher dispatcher = new DirectDispatcher(); // want a direct dispatcher for stats logging

        socketConfig.setLocalPort( _params.getDownLocalPort() );
        socketConfig.setInboundPersister( createInboundPersister( "CL_IN", ThreadPriority.Other ) );
        socketConfig.setOutboundPersister( createOutboundPersister( "CL_OUT", ThreadPriority.Other ) );

        setSocketPerfOptions( socketConfig );

        FixSocketSession sess = new FixSocketSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder,
                                                      fullDecoder, receiverPriority );

        dispatcher.setHandler( sess );

        if ( _params.isDisableEventLogging() ) {
            sess.setLogEvents( false );
        }

        return sess;
    }

    private void presize() {
        int orders      = _params.getNumOrders();
        int recycledMax = Math.min( _params.getNumOrders(), 20000 ); // allowing 20000 per second, assume in second get time to recycle

        int chainSize           = 1000;
        int orderChains         = orders / chainSize;
        int recycledEventChains = recycledMax / chainSize;
        int extraAlloc          = 50;

        presize( ClientNewOrderSingleImpl.class, recycledEventChains, chainSize, extraAlloc );
        presize( MarketNewOrderSingleImpl.class, recycledEventChains, chainSize, extraAlloc );
        presize( ClientNewOrderAckImpl.class, recycledEventChains, chainSize, extraAlloc );
        presize( MarketNewOrderAckImpl.class, recycledEventChains, chainSize, extraAlloc );
        presize( ClientCancelRequestImpl.class, recycledEventChains, chainSize, extraAlloc );
        presize( MarketCancelRequestImpl.class, recycledEventChains, chainSize, extraAlloc );
        presize( ClientCancelledImpl.class, recycledEventChains, chainSize, extraAlloc );
        presize( MarketCancelledImpl.class, recycledEventChains, chainSize, extraAlloc );

        presize( ReusableString.class, orderChains, chainSize, extraAlloc );
        presize( HashEntry.class, orderChains, chainSize, extraAlloc );
    }

    private void run() throws IOException {

        DMARecoveryController rct = new DummyRecoveryController();

        _clientSession.recover( rct );

        _clientSession.waitForRecoveryToComplete();

        Utils.invokeGC();

        _clientSession.connect();

        loadTradesFromFile( _params.getFileName(), _templateRequests );

        FixSocketSession[] client = { _clientSession };
        _clientSimSender = new ClientSimSender( _templateRequests, client, _statsMgr, _params.getIdPrefix() );

        ThreadUtilsFactory.get().sleep( 200 ); // wait for any inflight recovery messages

        _clientReplyHandler.reset();

        _clientSimSender.dispatchEvents( _params.getNumOrders(), _params.getBatchSize(), _params.getDelayMicros() );

        waitForReplies();

        _clientSession.stop();

        _statsMgr.logStats();
    }

    private void waitForReplies() {
        // check received fix message

        _log.info( "AWAITING REPLIES - ctrl-C to stop program, send=" + _clientSimSender.getSent() +
                   ", replies=" + _clientReplyHandler.getStatsBasedReplies() );

        int cnt = 0;
        while( _clientReplyHandler.getStatsBasedReplies() < _clientSimSender.getSent() ) {

            ThreadUtilsFactory.get().sleep( 1000 );

            if ( ++cnt % 10 == 0 ) {
                _log.info( "AWAITING REPLIES [" + cnt + "] - ctrl-C to stop program, send=" + _clientSimSender.getSent() +
                           ", replies=" + _clientReplyHandler.getStatsBasedReplies() );
            }
        }
    }
}
