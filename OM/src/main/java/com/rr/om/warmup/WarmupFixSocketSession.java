/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.warmup;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.Encoder;
import com.rr.core.codec.FixDecoder;
import com.rr.core.codec.FixEncoder;
import com.rr.core.collections.ConcLinkedEventQueueSingle;
import com.rr.core.collections.EventQueue;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;
import com.rr.core.persister.PersisterException;
import com.rr.core.session.*;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.session.socket.SocketSession;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.warmup.JITWarmup;
import com.rr.core.warmup.WarmupRegistry;
import com.rr.model.generated.fix.codec.CodecId;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;
import com.rr.model.generated.internal.events.impl.ClientNewOrderAckImpl;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.model.generated.internal.events.impl.MarketNewOrderAckImpl;
import com.rr.model.generated.internal.events.impl.MarketNewOrderSingleImpl;
import com.rr.om.session.fixsocket.FixSocketConfig;
import com.rr.om.session.fixsocket.FixSocketSession;
import com.rr.om.warmup.sim.FixSimParams;
import com.rr.om.warmup.sim.WarmupExchangeSimAdapter;
import com.rr.om.warmup.sim.WarmupUtils;

public class WarmupFixSocketSession extends AbstractWarmupSession implements JITWarmup {

    public static WarmupFixSocketSession create( String appName, String[] args ) {

        FixSimParams params = getProcessedParams( appName, args );

        WarmupFixSocketSession wfss = new WarmupFixSocketSession( params );

        return wfss;
    }

    public static WarmupFixSocketSession create( String appName, int portOffset, boolean spinLocks, int count, CodecId codecId ) {

        FixSimParams params = AbstractWarmupSession.createParams( appName, portOffset, spinLocks, count );

        WarmupFixSocketSession wfss = new WarmupFixSocketSession( params, codecId );

        return wfss;
    }

    public WarmupFixSocketSession( String appName, String args[] ) {
        super( getProcessedParams( appName, args ), CodecId.Standard44 );
    }

    public WarmupFixSocketSession( FixSimParams params, CodecId codecId ) {
        super( params, codecId );
    }

    public WarmupFixSocketSession( FixSimParams params ) {
        super( params, CodecId.Standard44 );
    }

    @Override
    public String getName() {
        return "FixSocketSession" + _codecId;
    }

    @Override
    public void warmup() throws Exception {
        warmSession();
        super.warmup();
    }

    @Override
    protected RecoverableSession createOmToExchangeSession( EventHandler proc, RecoverableSession hub ) throws SessionException, PersisterException {
        String      name          = "WARM_OM_2_SRV";
        EventRouter inboundRouter = new PassThruRouter( "passThruRouter", proc );
        int         logHdrOut     = AbstractSession.getDataOffset( name, false );
        byte[]      outBuf        = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        FixEncoder  encoder       = (FixEncoder) WarmupUtils.getEncoder( _codecId, outBuf, logHdrOut );
        FixDecoder  decoder       = (FixDecoder) WarmupUtils.getDecoder( _codecId );
        Decoder     fullDecoder   = WarmupUtils.getFullDecoder( _codecId );

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

    @Override
    protected RecoverableSession createExchangeSession( WarmupExchangeSimAdapter exSimAdapter ) throws SessionException, PersisterException {
        String         name             = "WARM_EXSIM";
        EventRouter    inboundRouter    = new PassThruRouter( "passThruRouter", exSimAdapter );
        int            logHdrOut        = AbstractSession.getDataOffset( name, false );
        byte[]         outBuf           = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        FixEncoder     encoder          = (FixEncoder) WarmupUtils.getEncoder( _codecId, outBuf, logHdrOut );
        FixDecoder     decoder          = (FixDecoder) WarmupUtils.getDecoder( _codecId );
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

    private void warmSession() {
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
}
