/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.mds.client;

import com.rr.core.codec.Encoder;
import com.rr.core.dispatch.DirectDispatcherNonThreadSafe;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.*;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.*;
import com.rr.core.persister.DummyPersister;
import com.rr.core.persister.PersisterException;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.recycler.EventRecycler;
import com.rr.core.session.ConnectionListener;
import com.rr.core.session.EventRouter;
import com.rr.core.session.PassThruRouter;
import com.rr.core.session.RecoverableSession;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.session.socket.SocketSession;
import com.rr.core.utils.ThreadPriority;
import com.rr.mds.common.*;
import com.rr.mds.common.events.Subscribe;
import com.rr.mds.common.events.TradingRangeUpdate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * MDS consumer/client layer
 *
 * @NOTE for market data critical events get the decoder to invoke the service directly
 */
public class MDSConsumer {

    public static final  int       DEFAULT_MDS_PORT    = 14200;
    public static final  ZString   DEFAULT_PRESUB_FILE = new ViewString( "./data/mdssubs.txt" );
    private static final Logger    _log                = LoggerFactory.create( MDSConsumer.class );
    private static final ErrorCode CANT_READ_FILE      = new ErrorCode( "MDC100", "Unable to read the presub file" );
    final EventRecycler _inboundRecycler = new MDSEventRecycler();
    private final Set<ReusableString> _rics = new HashSet<>( 8192 );
    InstrumentLocator _instLocator;
    PoolFactory<Subscribe> _subscribePool;
    private       SocketSession       _sess;

    public void addRIC( ZString ric ) {
        if ( !_rics.contains( ric ) ) {
            _rics.add( TLC.safeCopy( ric ) );
        }
    }

    public void init( InstrumentLocator il, int mdsPort ) {
        init( il, FXToUSDFromFile.DEFAULT_FX_TO_USD_FILE, mdsPort, DEFAULT_PRESUB_FILE );
    }

    public void init( InstrumentLocator il, ZString fxUSDFile, int mdsPort, ZString presubFile ) {

        _log.info( "MDSConsumer : listening to connection from MDSServer on port " + mdsPort );

        SuperpoolManager sp = SuperpoolManager.instance();
        _subscribePool = sp.getPoolFactory( Subscribe.class );

        _instLocator = il;

        FXToUSDFromFile.load( fxUSDFile );

        loadRICS( presubFile );

        runServer( mdsPort );
    }

    public void init( InstrumentLocator il ) {
        init( il, FXToUSDFromFile.DEFAULT_FX_TO_USD_FILE, DEFAULT_MDS_PORT, DEFAULT_PRESUB_FILE );
    }

    public synchronized void subscribeTradingRangeUpdates( ReusableString ricChain ) {
        Subscribe sub = _subscribePool.get();

        sub.setType( MDSReusableType.TradingBandUpdate );
        sub.addRICChain( ricChain );

        _sess.handle( sub );
    }

    void presub() {

        int count = 0;

        synchronized( _rics ) {

            ReusableString ricChain = null;
            ReusableString tmpRIC;

            for ( ReusableString ric : _rics ) {

                tmpRIC = TLC.instance().getString();
                tmpRIC.append( ric );

                tmpRIC.setNext( ricChain );
                ricChain = tmpRIC;

                ++count;

                if ( count % Subscribe.MAX_RIC_IN_SUB == 0 ) {
                    subscribe( ricChain );
                    TLC.instance().recycleChain( ricChain );
                    ricChain = null;
                }
            }

            if ( ricChain != null ) {
                subscribe( ricChain );
                TLC.instance().recycleChain( ricChain );
            }
        }

        _log.info( "Presubscribed count=" + count );
    }

    private void loadRICS( ZString fileName ) {
        if ( fileName == null ) fileName = DEFAULT_PRESUB_FILE;

        File file = new File( fileName.toString() );

        if ( !file.canRead() ) {
            _log.error( CANT_READ_FILE, fileName );

            throw new RuntimeException( "Cant read from presub file " + fileName );
        }

        try {

            try( BufferedReader input = new BufferedReader( new FileReader( file ) ) ) {
                int count      = 0;
                int badInst    = 0;
                int spaceLines = 0;

                String line;

                ReusableString tmpRIC;

                while( (line = input.readLine()) != null ) {
                    if ( line.charAt( 0 ) == '#' ) continue;

                    line = line.trim();

                    if ( line.indexOf( ' ' ) > 0 ) {
                        ++spaceLines;
                        continue;
                    }

                    tmpRIC = TLC.instance().getString();
                    tmpRIC.append( line );

                    ExchangeInstrument inst = _instLocator.getExchInst( tmpRIC, SecurityIDSource.ExchangeSymbol, null );

                    if ( inst != null ) {

                        _rics.add( tmpRIC );

                        ++count;

                    } else {
                        ++badInst;
                        TLC.instance().recycle( tmpRIC );
                    }
                }

                _log.info( "RICsub ric file has count=" + count + ", badInst=" + badInst + ", badSpaceLines=" + spaceLines );
            }
        } catch( IOException e ) {
            _log.warn( "Unable to read file " + fileName + " : " + e.getMessage() );
            throw new RuntimeException( e );
        }

    }

    private void runServer( int mdsPort ) {

        String         name             = "MDS_CLIENT";
        EventHandler   mdsRouter        = new MDSInboundServiceRouter();
        EventRouter    inboundRouter    = new PassThruRouter( "passThruRouter", mdsRouter );
        Encoder        encoder          = new MDSEncoder( new byte[ Constants.MAX_BUF_LEN ], 0 );
        MDSDecoder     decoder          = new MDSDecoder();
        ThreadPriority receiverPriority = ThreadPriority.PriceTolerance;
        SocketConfig socketConfig = new SocketConfig( MDSEventRecycler.class,
                                                      true,
                                                      new ViewString( "127.0.0.1" ),
                                                      null,
                                                      mdsPort );

        decoder.init( _instLocator );

        EventDispatcher dispatcher = new DirectDispatcherNonThreadSafe();

        socketConfig.setInboundPersister( new DummyPersister() );
        socketConfig.setOutboundPersister( new DummyPersister() );

        socketConfig.setSoDelayMS( 0 );

        // for OM the MD feed is not time critical so disable NIO and TCP_NO_DELAY
        socketConfig.setUseNIO( false );
        socketConfig.setTcpNoDelay( false );

        _sess = new SocketSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder, null, receiverPriority );

        dispatcher.setHandler( _sess );

        try {
            _sess.init();
        } catch( PersisterException e ) {
            _log.warn( "Session init error : " + e.getMessage() );  // should not be possible as using dummy persister
        }

        _sess.registerConnectionListener( new ConnectionListener() {

            @Override
            public void connected( RecoverableSession session ) {
                presub();
            }

            @Override
            public void disconnected( RecoverableSession session ) {
                // TODO Auto-generated method stub

            }
        } );

        _sess.connect();

    }

    private void subscribe( ReusableString ricChain ) {
        subscribeTradingRangeUpdates( ricChain );
    }

    class MDSInboundServiceRouter implements EventHandler {

        private final String _name = "MDSIn";

        @Override public boolean canHandle() { return true; }

        @Override
        public void handle( Event msg ) { // dispatcher will invoke handleNow
            handleNow( msg );
        }

        @Override
        public void handleNow( Event msg ) {
            switch( msg.getReusableType().getSubId() ) {
            case MDSReusableTypeConstants.SUB_ID_TRADING_BAND_UPDATE:
                TradingRangeUpdate upd = (TradingRangeUpdate) msg;

                ExchangeInstrument inst = _instLocator.getExchInst( upd.getExchangeSymbol(), SecurityIDSource.ExchangeSymbol, null );

                if ( inst != null ) {
                    upd.setTradingRange( inst.getValidTradingRange() );
                }

                _inboundRecycler.recycle( upd );
                break;
            case MDSReusableTypeConstants.SUB_ID_FX_SNAPSHOT:
            case MDSReusableTypeConstants.SUB_ID_MARKET_DATA_ACTIVE_BBO:
            case MDSReusableTypeConstants.SUB_ID_MARKET_DATA_ACTIVE_DEPTH:
            case MDSReusableTypeConstants.SUB_ID_MARKET_DATA_SNAPSHOT_BBO:
            case MDSReusableTypeConstants.SUB_ID_MARKET_DATA_SNAPSHOT_DEPTH:
            default:
                _inboundRecycler.recycle( msg );
                break;
            }
        }

        @Override
        public String getComponentId() {
            return _name;
        }

        @Override public void threadedInit() { /* nothing */ }
    }
}
