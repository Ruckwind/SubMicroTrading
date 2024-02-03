/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.warmup;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.Encoder;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.TimeUtils;
import com.rr.core.lang.TimeUtilsFactory;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.model.EventHandler;
import com.rr.core.model.Exchange;
import com.rr.core.persister.PersisterException;
import com.rr.core.session.*;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.Utils;
import com.rr.core.warmup.JITWarmup;
import com.rr.model.generated.codec.UTPEuronextCashDecoder;
import com.rr.model.generated.codec.UTPEuronextCashEncoder;
import com.rr.model.generated.fix.codec.CodecId;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;
import com.rr.om.dummy.warmup.DummyInstrumentLocator;
import com.rr.om.emea.exchange.utp.UTPSocketConfig;
import com.rr.om.emea.exchange.utp.UTPSocketSession;
import com.rr.om.exchange.ExchangeManager;
import com.rr.om.warmup.sim.FixSimParams;
import com.rr.om.warmup.sim.WarmupExchangeSimAdapter;
import com.rr.om.warmup.sim.WarmupUtils;

import java.util.TimeZone;

public class WarmupUTPSocketSession extends AbstractWarmupSession implements JITWarmup {

    public static WarmupUTPSocketSession create( String appName, int portOffset, boolean spinLocks, int count ) {

        FixSimParams params = createParams( appName, portOffset, spinLocks, count );

        WarmupUTPSocketSession wfss = new WarmupUTPSocketSession( params );

        return wfss;
    }

    public WarmupUTPSocketSession( String appName, String args[] ) {
        super( getProcessedParams( appName, args ), CodecId.Standard44 );
    }

    public WarmupUTPSocketSession( FixSimParams params ) {
        super( params, CodecId.Standard44 );
    }

    @Override
    protected RecoverableSession createOmToExchangeSession( EventHandler proc, RecoverableSession hub ) throws SessionException, PersisterException {
        String      name          = "WARM_OMUTP";
        EventRouter inboundRouter = new PassThruRouter( "passThru", proc );
        int         logHdrOut     = AbstractSession.getDataOffset( name, false );
        byte[]      outBuf        = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        Encoder     encoder       = getEncoder( outBuf, logHdrOut );
        Decoder     decoder       = getDecoder();
        Decoder     fullDecoder   = getDecoder();

        ThreadPriority receiverPriority = ThreadPriority.Other;

        ZString userName = new ViewString( "" );
        UTPSocketConfig socketConfig = new UTPSocketConfig( AllEventRecycler.class,
                                                            false,
                                                            new ViewString( _params.getDownHost() ),
                                                            null,
                                                            _params.getDownPort(),
                                                            userName );

        socketConfig.setDisconnectOnMissedHB( false );
        socketConfig.setRecoverFromLoginSeqNumTooLow( true );
        setSocketPerfOptions( socketConfig );

        socketConfig.setInboundPersister( createInboundPersister( name + "_EXU1_IN", ThreadPriority.Other ) );
        socketConfig.setOutboundPersister( createOutboundPersister( name + "_EXU1_OUT", ThreadPriority.Other ) );

        EventDispatcher dispatcher = getSessionDispatcher( "WARM_OM2EX_UTPDISPATCHER", ThreadPriority.Other );

        UTPSocketSession sess = new UTPSocketSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder,
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
        String         name             = "WARM_UTPSIM";
        EventRouter    inboundRouter    = new PassThruRouter( "passThru", exSimAdapter );
        int            logHdrOut        = AbstractSession.getDataOffset( name, false );
        byte[]         outBuf           = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        Encoder        encoder          = getEncoder( outBuf, logHdrOut );
        Decoder        decoder          = getDecoder();
        Decoder        fullDecoder      = getDecoder();
        ThreadPriority receiverPriority = ThreadPriority.Other;
        ZString        userName         = new ViewString( "" );

        UTPSocketConfig socketConfig = new UTPSocketConfig( AllEventRecycler.class,
                                                            true,
                                                            new ViewString( _params.getDownHost() ),
                                                            null,
                                                            _params.getDownPort(),
                                                            userName );
        socketConfig.setDisconnectOnMissedHB( false );
        socketConfig.setRecoverFromLoginSeqNumTooLow( true );

        EventDispatcher dispatcher = getSessionDispatcher( "WARMUP_UTPSIM_DISPATCHER", ThreadPriority.Other );

        socketConfig.setInboundPersister( createInboundPersister( name + "_EXUTPSIM_IN", ThreadPriority.Other ) );
        socketConfig.setOutboundPersister( createOutboundPersister( name + "_EXUTPSIM_OUT", ThreadPriority.Other ) );

        setSocketPerfOptions( socketConfig );

        UTPSocketSession sess = new UTPSocketSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder,
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
    protected Decoder getDecoder( CodecId id ) {
        Decoder  decoder = WarmupUtils.getDecoder( id );
        Exchange enp     = ExchangeManager.instance().getByMIC( new ViewString( "XPAR" ) );
        decoder.setInstrumentLocator( new DummyInstrumentLocator( enp ) );
        return decoder;
    }

    @Override
    public String getName() {
        return "UTPSocketSession";
    }

    private Decoder getDecoder() {
        UTPEuronextCashDecoder decoder = new UTPEuronextCashDecoder();
        decoder.setClientProfile( WarmupUtils.getWarmupClient() );
        decoder.setInstrumentLocator( new DummyInstrumentLocator() );
        TimeUtils calc = TimeUtilsFactory.createTimeUtils();
        calc.setLocalTimezone( TimeZone.getTimeZone( "CET" ) );
        decoder.setTimeUtils( calc );
        decoder.setReceived( Utils.nanoTime() );
        return decoder;
    }

    private Encoder getEncoder( byte[] outBuf, int offset ) {
        return new UTPEuronextCashEncoder( outBuf, offset );
    }
}
