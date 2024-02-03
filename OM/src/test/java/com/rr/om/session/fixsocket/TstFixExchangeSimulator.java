/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.fixsocket;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.FixDecoder;
import com.rr.core.codec.FixEncoder;
import com.rr.core.collections.EventQueue;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.dispatch.ThreadedDispatcher;
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
import com.rr.om.order.OrderImpl;
import com.rr.om.order.OrderVersion;
import com.rr.om.order.collections.HashEntry;
import com.rr.om.recovery.DummyRecoveryController;
import com.rr.om.warmup.FixSimConstants;
import com.rr.om.warmup.FixTestUtils;
import com.rr.om.warmup.WarmupFixSocketSession;
import com.rr.om.warmup.sim.BaseFixSimProcess;
import com.rr.om.warmup.sim.FixSimParams;
import com.rr.om.warmup.sim.WarmupExchangeSimAdapter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TstFixExchangeSimulator extends BaseFixSimProcess {

    static final         Logger    _log     = ConsoleFactory.console( TstFixExchangeSimulator.class, Level.info );
    private static final ErrorCode FAILED   = new ErrorCode( "TFE100", "Exception in main" );
    private static final ErrorCode WARM_ERR = new ErrorCode( "TFE200", "Warmup Exception in main" );

    private WarmupExchangeSimAdapter _exSimAdapter;
    private FixSocketSession         _exchangeSession;

    public static void main( String[] args ) {

        LoggerFactory.setForceConsole( true );
        StatsMgr.setStats( new TestStats() );
        FixSimParams params = getProcessedParams( args );

        ThreadUtilsFactory.get().init( params.getCpuMasksFile(), true );

        ThreadUtilsFactory.get().setPriority( Thread.currentThread(), ThreadPriority.Other );

        if ( params.getWarmupCount() > 0 ) {
            try {
                WarmupFixSocketSession sess = WarmupFixSocketSession.create( "TstExchangeSim", args );
                sess.warmup();
            } catch( Throwable t ) {
                _log.error( WARM_ERR, "Error in warmup", t );
            }
        }

        TstFixExchangeSimulator tes = new TstFixExchangeSimulator( params );

        try {
            tes.init();

            tes.run();
        } catch( Exception e ) {

            _log.error( FAILED, "", e );
        }
    }

    private static FixSimParams getProcessedParams( String[] args ) {
        FixSimParams params = new FixSimParams( "TstFixExchangeSim", true, false, "E" );

        params.setUpHost( "localhost" );
        params.setUpSenderCompId( FixSimConstants.DEFAULT_EXCHANGE_SIM_ID );
        params.setUpTargetCompId( FixSimConstants.DEFAULT_OM_DOWN_ID );
        params.setUpPort( FixSimConstants.DEFAULT_OM_EXCHANGE_PORT );

        params.procArgs( args );

        _log.info( "CONNECT PARAMS : host=" + params.getUpHost() + ", port=" + params.getUpPort() );

        return params;
    }

    public TstFixExchangeSimulator( FixSimParams params ) {
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

        EventQueue      queue      = getQueue( "WarmupExchangeSimProcessorQueue" );
        EventDispatcher dispatcher = new ThreadedDispatcher( "WarmupExchangeSimAdapter", queue, ThreadPriority.ExchangeSimProcessor );
        _exSimAdapter    = new WarmupExchangeSimAdapter( dispatcher, expOrders, queue );
        _exchangeSession = createExchangeSession( expOrders, _exSimAdapter );

        // start the sockets

        _exchangeSession.init();
    }

    private FixSocketSession createExchangeSession( int expOrders, WarmupExchangeSimAdapter exSimAdapter ) throws SessionException, PersisterException {
        String         name             = "TEXCHANGE";
        EventRouter    inboundRouter    = new PassThruRouter( "passThru", exSimAdapter );
        int            logHdrOut        = AbstractSession.getDataOffset( name, false );
        byte[]         outBuf           = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        FixEncoder     encoder          = FixTestUtils.getEncoder44( outBuf, logHdrOut );
        FixDecoder     decoder          = FixTestUtils.getOMSDecoder44();
        Decoder        fullDecoder      = FixTestUtils.getFullDecoder44();
        ThreadPriority receiverPriority = ThreadPriority.ExchangeSimulatorIn;
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
        EventDispatcher dispatcher = getSessionDispatcher( "EX_DISPATCHER", ThreadPriority.ExchangeSimulatorOut );

        socketConfig.setLocalPort( _params.getUpLocalPort() );
        socketConfig.setInboundPersister( createInboundPersister( "EX_IN", ThreadPriority.Other ) );
        socketConfig.setOutboundPersister( createOutboundPersister( "EX_OUT", ThreadPriority.Other ) );

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

        _exchangeSession.recover( rct );

        _exchangeSession.waitForRecoveryToComplete();

        Utils.invokeGC();

        _exchangeSession.connect();

        // check received fix message

        _log.info( "ENTERING MAIN LOOP - ctrl-C to stop program" );

        while( true ) {

            ThreadUtilsFactory.get().sleep( 200 );

        }
    }
}
