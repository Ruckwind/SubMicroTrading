/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.warmup;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.Encoder;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.*;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.model.EventHandler;
import com.rr.core.model.Exchange;
import com.rr.core.persister.Persister;
import com.rr.core.persister.PersisterException;
import com.rr.core.session.*;
import com.rr.core.utils.FileException;
import com.rr.core.utils.FileUtils;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.Utils;
import com.rr.core.warmup.JITWarmup;
import com.rr.model.generated.codec.MilleniumLSEDecoder;
import com.rr.model.generated.codec.MilleniumLSEEncoder;
import com.rr.model.generated.fix.codec.CodecId;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;
import com.rr.om.dummy.warmup.DummyInstrumentLocator;
import com.rr.om.emea.exchange.millenium.MilleniumSocketConfig;
import com.rr.om.emea.exchange.millenium.MilleniumSocketSession;
import com.rr.om.emea.exchange.millenium.SequentialPersister;
import com.rr.om.emea.exchange.millenium.recovery.MilleniumRecoveryController;
import com.rr.om.exchange.ExchangeManager;
import com.rr.om.warmup.sim.FixSimParams;
import com.rr.om.warmup.sim.WarmupExchangeSimAdapter;
import com.rr.om.warmup.sim.WarmupUtils;

import java.util.TimeZone;

public class WarmupMilleniumSocketSession extends AbstractWarmupSession implements JITWarmup {

    public static WarmupMilleniumSocketSession create( String appName, int portOffset, boolean spinLocks, int count ) {

        FixSimParams params = createParams( appName, portOffset, spinLocks, count );

        WarmupMilleniumSocketSession wfss = new WarmupMilleniumSocketSession( params );

        return wfss;
    }

    public WarmupMilleniumSocketSession( String appName, String args[] ) {
        super( getProcessedParams( appName, args ), CodecId.Standard44 );
    }

    public WarmupMilleniumSocketSession( FixSimParams params ) {
        super( params, CodecId.Standard44 ); // client will be Standard44
    }

    @Override
    protected RecoverableSession createExchangeSession( WarmupExchangeSimAdapter exSimAdapter ) throws FileException, SessionException, PersisterException {
        MilleniumSocketSession sess    = getAnExSession( exSimAdapter, "WARM_MilleniumSim", _params.getDownPort(), false );
        MilleniumSocketSession recSess = getAnExSession( exSimAdapter, "WARM_MilleniumSimRec", _params.getDownPort() + 1, true );

        sess.getController().setRecoveryController( (MilleniumRecoveryController) recSess.getController() );

        return sess;
    }

    @Override
    protected RecoverableSession createOmToExchangeSession( EventHandler proc, RecoverableSession hub ) throws SessionException, FileException, PersisterException {

        MilleniumSocketSession sess    = getAnOMExSession( proc, hub, "WARM_OMMillenium", _params.getDownPort(), false );
        MilleniumSocketSession recSess = getAnOMExSession( proc, hub, "WARM_OMMilleniumRec", _params.getDownPort() + 1, true );

        sess.getController().setRecoveryController( (MilleniumRecoveryController) recSess.getController() );

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
        return "MilleniumSocketSession";
    }

    private Persister createMilleniumPersister( String id, String direction, ThreadPriority priority ) throws FileException {
        ReusableString fileName = new ReusableString( _persistFileNameBase );
        fileName.append( '/' ).append( id.toLowerCase() ).append( "/" ).append( direction ).append( "/" ).append( id ).append( ".dat" );
        if ( _params.isRemovePersistence() ) FileUtils.rm( fileName.toString() );
        SequentialPersister persister = new SequentialPersister( new ViewString( id ),
                                                                 fileName,
                                                                 _params.getPersistDatPreSize(),
                                                                 _params.getPersistDatPageSize(),
                                                                 priority );
        return persister;
    }

    private MilleniumSocketSession getAnExSession( WarmupExchangeSimAdapter exSimAdapter, String sesName, int port, boolean isRecoverySess ) throws FileException, SessionException, PersisterException {
        String         name             = sesName;
        EventRouter    inboundRouter    = new PassThruRouter( "passThru", exSimAdapter );
        int            logHdrOut        = AbstractSession.getDataOffset( name, false );
        byte[]         outBuf           = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        Encoder        encoder          = getEncoder( outBuf, logHdrOut );
        Decoder        decoder          = getDecoder();
        Decoder        fullDecoder      = getDecoder();
        ThreadPriority receiverPriority = ThreadPriority.Other;

        ZString userName = new ViewString( "testUser" );
        ZString pwd      = new ViewString( "testPwd" );
        MilleniumSocketConfig socketConfig = new MilleniumSocketConfig( AllEventRecycler.class, true, new ViewString( _params.getDownHost() ),
                                                                        null, port, userName, pwd, null, isRecoverySess );

        socketConfig.setDisconnectOnMissedHB( false );

        EventDispatcher dispatcher = getSessionDispatcher( sesName + "DISPATCHER", ThreadPriority.Other );

        socketConfig.setInboundPersister( createMilleniumPersister( name + "_EXMilleniumSIM_IN", "in", ThreadPriority.Other ) );
        socketConfig.setOutboundPersister( createMilleniumPersister( name + "_EXMilleniumSIM_OUT", "out", ThreadPriority.Other ) );

        setSocketPerfOptions( socketConfig );

        MilleniumSocketSession sess = new MilleniumSocketSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder,
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

        return sess;
    }

    private MilleniumSocketSession getAnOMExSession( EventHandler proc, RecoverableSession hub, String sesName, int port, boolean isRecoverySess ) throws FileException, SessionException, PersisterException {
        String      name          = sesName;
        EventRouter inboundRouter = new PassThruRouter( "passThru", proc );
        int         logHdrOut     = AbstractSession.getDataOffset( name, false );
        byte[]      outBuf        = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
        Encoder     encoder       = getEncoder( outBuf, logHdrOut );
        Decoder     decoder       = getDecoder();
        Decoder     fullDecoder   = getDecoder();

        ThreadPriority receiverPriority = ThreadPriority.Other;

        ZString userName = new ViewString( "testUser" );
        ZString pwd      = new ViewString( "testPwd" );
        MilleniumSocketConfig socketConfig = new MilleniumSocketConfig( AllEventRecycler.class, false, new ViewString( _params.getDownHost() ),
                                                                        null, port, userName, pwd, null, isRecoverySess );

        socketConfig.setDisconnectOnMissedHB( false );
        socketConfig.setRecoverySession( isRecoverySess );
        setSocketPerfOptions( socketConfig );

        socketConfig.setInboundPersister( createMilleniumPersister( name + "_EXU1_IN", "in", ThreadPriority.Other ) );
        socketConfig.setOutboundPersister( createMilleniumPersister( name + "_EXU1_OUT", "out", ThreadPriority.Other ) );

        EventDispatcher dispatcher = getSessionDispatcher( name + "Dispatcher", ThreadPriority.Other );

        MilleniumSocketSession sess = new MilleniumSocketSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder,
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

        return sess;
    }

    private Decoder getDecoder() {
        MilleniumLSEDecoder decoder = new MilleniumLSEDecoder();
        decoder.setClientProfile( WarmupUtils.getWarmupClient() );
        decoder.setInstrumentLocator( new DummyInstrumentLocator() );
        TimeUtils calc = TimeUtilsFactory.createTimeUtils();
        calc.setLocalTimezone( TimeZone.getTimeZone( "GMT" ) );
        decoder.setTimeUtils( calc );
        decoder.setReceived( Utils.nanoTime() );
        return decoder;
    }

    private Encoder getEncoder( byte[] outBuf, int offset ) {
        return new MilleniumLSEEncoder( outBuf, offset );
    }
}
