/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.hub.bridge;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.Encoder;
import com.rr.core.collections.BlockingSyncQueue;
import com.rr.core.collections.EventQueue;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.dispatch.ThreadedDispatcher;
import com.rr.core.dummy.warmup.TestStats;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.lang.stats.StatsMgr;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.persister.PersisterException;
import com.rr.core.recovery.dma.DMARecoveryController;
import com.rr.core.session.DummyRouter;
import com.rr.core.session.EventRouter;
import com.rr.core.session.SessionException;
import com.rr.core.session.socket.PortOffset;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.session.socket.SocketSession;
import com.rr.core.utils.FileException;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.core.utils.Utils;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;
import com.rr.model.generated.internal.events.impl.*;
import com.rr.om.order.OrderImpl;
import com.rr.om.order.OrderVersion;
import com.rr.om.order.collections.HashEntry;
import com.rr.om.recovery.DummyRecoveryController;
import com.rr.om.warmup.FixSimConstants;
import com.rr.om.warmup.FixTestUtils;
import com.rr.om.warmup.sim.BaseFixSimProcess;
import com.rr.om.warmup.sim.FixSimParams;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TstHubBridge extends BaseFixSimProcess {

    static final         Logger    _log   = ConsoleFactory.console( TstHubBridge.class, Level.info );
    private static final ErrorCode FAILED = new ErrorCode( "THB100", "Exception in main" );

    private HubBridge     _hubBridge;
    private SocketSession _omSession;

    public static void main( String[] args ) {

        LoggerFactory.setForceConsole( true );
        StatsMgr.setStats( new TestStats() );

        FixSimParams params = getProcessedParams( args );

        ThreadUtilsFactory.get().init( params.getCpuMasksFile(), true );

        ThreadUtilsFactory.get().setPriority( Thread.currentThread(), ThreadPriority.Other );

        TstHubBridge tes = new TstHubBridge( params );

        try {
            tes.init();

            Utils.invokeGC();

            tes.run();
        } catch( Exception e ) {

            _log.error( FAILED, "", e );
        }
    }

    private static FixSimParams getProcessedParams( String[] args ) {
        FixSimParams params = new FixSimParams( "TstHubBridge", true, false, "H" );

        int offset = PortOffset.getNext();

        params.setUpHost( "localhost" );
        params.setUpSenderCompId( FixSimConstants.DEFAULT_HUB_BRIDGE_ID );
        params.setUpTargetCompId( FixSimConstants.DEFAULT_OM_HUB_ID );
        params.setUpPort( FixSimConstants.DEFAULT_HUB_PORT + offset );

        params.procArgs( args );

        _log.info( "CONNECT PARAMS : host=" + params.getUpHost() + ", port=" + params.getUpPort() );

        return params;
    }

    public TstHubBridge( FixSimParams params ) {
        super( params );
    }

    @Override
    protected void init() throws SessionException, FileException, PersisterException, IOException {

        super.init();

        presize();

        int expOrders = _params.getNumOrders();

        DateFormat utcFormat = new SimpleDateFormat( "yyyyMMdd" );

        String today = utcFormat.format( new Date() );
        FixTestUtils.setTodayStr( today );

        _hubBridge = new HubBridgeImpl( expOrders );
        _omSession = createHubSession( expOrders, _hubBridge );

        // start the sockets

        _omSession.init();
    }

    private SocketSession createHubSession( int expOrders, HubBridge hubBridge ) {
        String      name          = "THUBBRIDGE";
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
        EventQueue      queue = new BlockingSyncQueue();
        dispatcher = new ThreadedDispatcher( "HUB_DISPATCHER", queue, ThreadPriority.Other );

        SocketSession sess = new SocketSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder,
                                                fullDecoder, receiverPriority );

        dispatcher.setHandler( sess );

        return sess;
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

        _omSession.recover( rct );

        _omSession.waitForRecoveryToComplete();

        _omSession.connect();

        // check received fix message

        _log.info( "ENTERING MAIN LOOP - ctrl-C to stop program" );

        while( true ) {

            ThreadUtilsFactory.get().sleep( 200 );

        }
    }
}
