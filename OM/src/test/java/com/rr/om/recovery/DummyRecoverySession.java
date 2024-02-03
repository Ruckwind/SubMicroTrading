/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.recovery;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.FixDecoder;
import com.rr.core.codec.FixEncoder;
import com.rr.core.dispatch.DirectDispatcher;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.model.EventHandler;
import com.rr.core.model.FixVersion;
import com.rr.core.persister.IndexPersister;
import com.rr.core.persister.Persister;
import com.rr.core.persister.PersisterException;
import com.rr.core.session.*;
import com.rr.core.utils.ThreadPriority;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;
import com.rr.om.session.fixsocket.FixSocketConfig;
import com.rr.om.session.fixsocket.FixSocketSession;
import com.rr.om.session.fixsocket.MemoryIndexedPersister;
import com.rr.om.warmup.AbstractWarmupSession;
import com.rr.om.warmup.sim.FixSimParams;
import com.rr.om.warmup.sim.WarmupUtils;

public class DummyRecoverySession extends FixSocketSession {

    private static FixSimParams _params = AbstractWarmupSession.createParams( "TestRecovery", 0, false, 1 );

    public static DummyRecoverySession create( String sname, boolean isUpstream, EventHandler proc ) throws SessionException, PersisterException {

        String      name          = sname;
        EventRouter inboundRouter = new PassThruRouter( "passThru", proc );
        int         logHdrOut     = AbstractSession.getDataOffset( name, false );
        byte[]      outBuf        = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        FixEncoder  encoder       = WarmupUtils.getEncoder( FixVersion.Fix4_4, outBuf, logHdrOut );
        FixDecoder  decoder       = WarmupUtils.getFixOMSDecoder( FixVersion.Fix4_4 );
        Decoder     fullDecoder   = WarmupUtils.getFullDecoder( FixVersion.Fix4_4 );

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

        socketConfig.setDirection( isUpstream ? SessionDirection.Upstream : SessionDirection.Downstream );

        socketConfig.setHeartBeatIntSecs( 30 );
        socketConfig.setDisconnectOnMissedHB( false );
        socketConfig.setRecoverFromLoginSeqNumTooLow( true );

        EventDispatcher dispatcher = new DirectDispatcher();

        socketConfig.setInboundPersister( createPersister( name + "_IN" ) );
        socketConfig.setOutboundPersister( createPersister( name + "_OUT" ) );

        DummyRecoverySession sess = new DummyRecoverySession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder,
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

    private static Persister createPersister( String id ) {
        return new MemoryIndexedPersister();
    }

    private DummyRecoverySession( String name,
                                  EventRouter inboundRouter,
                                  FixSocketConfig fixConfig,
                                  EventDispatcher dispatcher,
                                  FixEncoder encoder,
                                  FixDecoder decoder,
                                  Decoder fullDecoder,
                                  ThreadPriority receiverPriority ) throws SessionException, PersisterException {

        super( name, inboundRouter, fixConfig, dispatcher, encoder, decoder, fullDecoder, receiverPriority );
    }

    @Override
    public IndexPersister getOutboundPersister() {
        return (IndexPersister) _outPersister;
    }

    public IndexPersister getInboundPersister() {
        return (IndexPersister) _inPersister;
    }
}
