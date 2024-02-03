/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.warmup;

import com.rr.core.codec.Decoder;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.TimeUtils;
import com.rr.core.lang.TimeUtilsFactory;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.model.EventHandler;
import com.rr.core.model.Exchange;
import com.rr.core.persister.DummyIndexPersister;
import com.rr.core.persister.PersisterException;
import com.rr.core.recovery.dma.DMARecoveryController;
import com.rr.core.session.*;
import com.rr.core.utils.*;
import com.rr.core.warmup.JITWarmup;
import com.rr.model.generated.codec.ETIEurexHFTDecoder;
import com.rr.model.generated.codec.ETIEurexHFTEncoder;
import com.rr.model.generated.fix.codec.CodecId;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;
import com.rr.model.generated.internal.type.ETIEnv;
import com.rr.model.generated.internal.type.ETISessionMode;
import com.rr.om.dummy.warmup.DummyInstrumentLocator;
import com.rr.om.emea.exchange.eti.gateway.ETIGatewayController;
import com.rr.om.emea.exchange.eti.trading.ETISocketConfig;
import com.rr.om.emea.exchange.eti.trading.ETISocketSession;
import com.rr.om.exchange.ExchangeManager;
import com.rr.om.recovery.DummyRecoveryController;
import com.rr.om.warmup.sim.FixSimParams;
import com.rr.om.warmup.sim.WarmupExchangeSimAdapter;
import com.rr.om.warmup.sim.WarmupUtils;

import java.io.IOException;
import java.util.TimeZone;

public class WarmupETIEurexSocketSession extends AbstractWarmupSession implements JITWarmup {

    public static boolean TRACE = false;
    private ETISocketSession _connOMtoExchangeGwySess;
    private ETISocketSession _exchangeTradeSess;

    public static WarmupETIEurexSocketSession create( String appName, int portOffset, boolean spinLocks, int count ) {

        FixSimParams params = createParams( appName, portOffset, spinLocks, count );

        if ( TRACE ) {
            // ONLY FOR DEBUG

            params.setLogPojoEvents( true );
            params.setDisableEventLogging( false );
            params.setDebug( true );
        }

        WarmupETIEurexSocketSession wfss = new WarmupETIEurexSocketSession( params );

        return wfss;
    }

    public WarmupETIEurexSocketSession( String appName, String args[] ) {
        super( getProcessedParams( appName, args ), CodecId.Standard44 );
    }

    public WarmupETIEurexSocketSession( FixSimParams params ) {
        super( params, CodecId.Standard44 ); // client will use standard44
    }

    @Override
    public String getName() {
        return "ETIEurexSocketSessionWarmup";
    }

    @Override
    protected void init() throws SessionException, FileException, PersisterException, IOException {
        super.init();

        _connOMtoExchangeGwySess.init();
        _exchangeTradeSess.init();
    }

    @Override
    protected void close() {
        _exchangeTradeSess.stop();
        _connOMtoExchangeGwySess.stop();
        super.close();
    }

    @Override
    protected void connect() {
        _exchangeTradeSess.connect();
        _exSess.connect();
        _omToClientSess.connect();
        _connOMtoExchangeGwySess.connect();
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

    @Override
    protected RecoverableSession createExchangeSession( WarmupExchangeSimAdapter exSimAdapter ) throws SessionException, PersisterException {
        _exchangeTradeSess = getAnExSession( exSimAdapter, "WARM_ETIEurexSim", _params.getDownPort(), false );
        ETISocketSession gwySess = getAnExSession( exSimAdapter, "WARM_ETIEurexSimGwy", _params.getDownPort() + 1, true );

        gwySess.getStateConfig().setEmulationTestPort( _params.getDownPort() );

        ((ETIGatewayController) gwySess.getController()).setTradingSession( _exchangeTradeSess );

        return gwySess;
    }

    @Override
    protected RecoverableSession createOmToExchangeSession( EventHandler proc, RecoverableSession hub ) throws SessionException, PersisterException {

        ETISocketSession tradingSess = getAnOMExSession( proc, hub, "WARM_OMETIEurex", _params.getDownPort(), false );
        _connOMtoExchangeGwySess = getAnOMExSession( proc, hub, "WARM_OMETIEurexGwy", _params.getDownPort() + 1, true );

        ((ETIGatewayController) _connOMtoExchangeGwySess.getController()).setTradingSession( tradingSess );

        return tradingSess;
    }

    @Override
    protected Decoder getDecoder( CodecId codecId ) {
        Decoder  decoder = WarmupUtils.getDecoder( codecId );
        Exchange enp     = ExchangeManager.instance().getByMIC( new ViewString( "XPAR" ) );
        decoder.setInstrumentLocator( new DummyInstrumentLocator( enp ) );
        return decoder;
    }

    @Override
    protected void recover() {

        // EX
        DMARecoveryController exRct = new DummyRecoveryController();

        _connOMtoExchangeGwySess.recover( exRct );
        _connOMtoExchangeGwySess.waitForRecoveryToComplete();

        _exchangeTradeSess.recover( exRct );
        _exchangeTradeSess.waitForRecoveryToComplete();

        super.recover();
    }

    private ETISocketSession getAnExSession( WarmupExchangeSimAdapter exSimAdapter, String sesName, int port, boolean isConnGwySess ) throws SessionException, PersisterException {
        String             name             = sesName;
        EventRouter        inboundRouter    = new PassThruRouter( "passThru", exSimAdapter );
        int                logHdrOut        = AbstractSession.getDataOffset( name, false );
        byte[]             outBuf           = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        ETIEurexHFTEncoder encoder          = getEncoder( outBuf, logHdrOut );
        ETIEurexHFTDecoder decoder          = getDecoder();
        ETIEurexHFTDecoder fullDecoder      = getDecoder();
        ThreadPriority     receiverPriority = ThreadPriority.Other;

        int     userName = 1234;
        ZString pwd      = new ViewString( "testPwd" );
        ETISocketConfig socketConfig = new ETISocketConfig( AllEventRecycler.class, true, new ViewString( _params.getDownHost() ),
                                                            null, port, userName, pwd, isConnGwySess, ETIEnv.Simulation, ETISessionMode.HF );

        socketConfig.setDisconnectOnMissedHB( false );

        if ( isConnGwySess ) {
            socketConfig.setInboundPersister( new DummyIndexPersister() );
            socketConfig.setOutboundPersister( new DummyIndexPersister() );
        } else {
            socketConfig.setInboundPersister( createInboundPersister( name + "_EXETIEurexSIM_IN", ThreadPriority.Other ) );
            socketConfig.setOutboundPersister( createOutboundPersister( name + "_EXETIEurexSIM_OUT", ThreadPriority.Other ) );
        }

        setSocketPerfOptions( socketConfig );

        EventDispatcher dispatcher = getSessionDispatcher( sesName + "DISPATCHER", ThreadPriority.Other );

        ETISocketSession sess = new ETISocketSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder,
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

        sess.setLogPojos( _params.isLogPojoEvents() );

        if ( _params.isDebug() ) {
            encoder.setDebug( true );
            decoder.setDebug( true );

            // need reset these as debug will have created new builder instance
            encoder.setExchangeEmulationOn();
            decoder.setExchangeEmulationOn();
        }

        return sess;
    }

    private ETISocketSession getAnOMExSession( EventHandler proc, RecoverableSession hub, String sesName, int port, boolean isConnGwySess ) throws SessionException, PersisterException {
        String             name          = sesName;
        EventRouter        inboundRouter = new PassThruRouter( "passThru", proc );
        int                logHdrOut     = AbstractSession.getDataOffset( name, false );
        byte[]             outBuf        = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        ETIEurexHFTEncoder encoder       = getEncoder( outBuf, logHdrOut );
        ETIEurexHFTDecoder decoder       = getDecoder();
        ETIEurexHFTDecoder fullDecoder   = getDecoder();

        ThreadPriority receiverPriority = ThreadPriority.Other;

        int     userName = 1234;
        ZString pwd      = new ViewString( "testPwd" );

        ETISocketConfig socketConfig = new ETISocketConfig( AllEventRecycler.class, false, new ViewString( _params.getDownHost() ),
                                                            null, port, userName, pwd, isConnGwySess, ETIEnv.Simulation, ETISessionMode.HF );

        socketConfig.setDisconnectOnMissedHB( false );
        socketConfig.setGatewaySession( isConnGwySess );
        setSocketPerfOptions( socketConfig );

        if ( isConnGwySess ) {
            socketConfig.setInboundPersister( new DummyIndexPersister() );
            socketConfig.setOutboundPersister( new DummyIndexPersister() );
        } else {
            socketConfig.setInboundPersister( createInboundPersister( name + "_EXU1_IN", ThreadPriority.Other ) );
            socketConfig.setOutboundPersister( createOutboundPersister( name + "_EXU1_OUT", ThreadPriority.Other ) );
        }

        EventDispatcher dispatcher = getSessionDispatcher( name + "Dispatcher", ThreadPriority.Other );

        ETISocketSession sess = new ETISocketSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder,
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

        sess.setLogPojos( _params.isLogPojoEvents() );

        if ( _params.isDebug() ) {
            encoder.setDebug( true );
            decoder.setDebug( true );
        }

        return sess;
    }

    private ETIEurexHFTDecoder getDecoder() {
        ETIEurexHFTDecoder decoder = new ETIEurexHFTDecoder();
        decoder.setClientProfile( WarmupUtils.getWarmupClient() );
        decoder.setInstrumentLocator( new DummyInstrumentLocator() );
        TimeUtils calc = TimeUtilsFactory.createTimeUtils();
        calc.setLocalTimezone( TimeZone.getTimeZone( "GMT" ) );
        decoder.setTimeUtils( calc );
        decoder.setReceived( Utils.nanoTime() );
        return decoder;
    }

    private ETIEurexHFTEncoder getEncoder( byte[] outBuf, int offset ) {
        return new ETIEurexHFTEncoder( outBuf, offset );
    }
}
