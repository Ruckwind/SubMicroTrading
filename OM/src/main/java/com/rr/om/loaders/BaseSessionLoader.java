/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.loaders;

import com.rr.core.codec.*;
import com.rr.core.component.SMTSingleComponentLoader;
import com.rr.core.lang.*;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.model.ClientProfile;
import com.rr.core.model.Exchange;
import com.rr.core.persister.DummyIndexPersister;
import com.rr.core.persister.IndexPersister;
import com.rr.core.persister.Persister;
import com.rr.core.persister.memmap.IndexMMPersister;
import com.rr.core.persister.memmap.MemMapPersister;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.session.RecoverableSession;
import com.rr.core.session.SessionDirection;
import com.rr.core.session.socket.SeqNumSession;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.tasks.BasicSchedulerCallback;
import com.rr.core.tasks.CoreScheduledEvent;
import com.rr.core.tasks.Scheduler;
import com.rr.core.tasks.SchedulerFactory;
import com.rr.core.utils.*;
import com.rr.inst.InstrumentStore;
import com.rr.model.generated.fix.codec.CMEEncoder;
import com.rr.model.generated.fix.codec.CodecId;
import com.rr.om.emea.exchange.millenium.SequentialPersister;
import com.rr.om.exchange.ExchangeManager;
import com.rr.om.session.SessionManager;

import java.util.TimeZone;

/**
 * remember properties HAVE to have the SAME name as config entries to be autowired !
 */
public abstract class BaseSessionLoader implements SMTSingleComponentLoader {

    private static final Logger     _console = ConsoleFactory.console( BaseSessionLoader.class, Level.info );
    private final byte[] _dateStrBytes;
    private final String _dateStr;
    protected boolean _logEvents     = true;
    protected boolean _logPojoEvents = true;
    protected boolean _logStats      = true;
    protected boolean _trace         = false;
    protected boolean _disableNanoStats = false;
    protected CodecId          _codecId          = CodecId.Standard44;
    protected ExchangeManager  _exchangeManager;
    protected SessionManager   _sessionManager;
    protected SessionDirection _sessionDirection = SessionDirection.Downstream;
    protected String           _multicastGroups;
    protected String  _mic; // exchange MIC
    protected boolean _dummyPersister = false;
    private              ViewString _persistFileNameBase;
    private int            _persistDatPageSize;
    private long           _persistIdxPreSize;
    private long           _persistDatPreSize;
    private int            _persistFlushPeriodMS   = 0; // no forced flush
    private int            _expectedOrders         = 1024 * 1024;
    private boolean        _forceRemovePersistence = false;
    private ThreadPriority _persistThreadPriority  = ThreadPriority.MemMapAllocator;
    private TimeZone _timeZone;
    private Exchange _ex;

    /**
     * mem vars to be set via reflection .. should be auto set
     */
    private CodecFactory    _codecFactory;
    private InstrumentStore _instrumentStore;
    private int             _codecBufSize = SizeConstants.DEFAULT_MAX_SESSION_BUFFER;

    public BaseSessionLoader() {
        // @TODO move TimeZone stuff into new component

        _dateStrBytes = TimeUtilsFactory.safeTimeUtils().getDateLocal();
        _dateStr      = new String( _dateStrBytes );

        AppProps props   = AppProps.instance();
        String   appName = props.getProperty( CoreProps.APP_NAME );
        String   baseDir = props.getProperty( CoreProps.PERSIST_DIR, false, "./persist" );
        _persistFileNameBase = new ViewString( baseDir + "/daily/" + appName );
    }

    protected byte[] createCodecBuffer( final int codecBufferSize ) {
        return new byte[ codecBufferSize ];
    }

    protected IndexPersister createInboundPersister( String id ) throws FileException {

        calcSizes();

        if ( _dummyPersister ) {
            _console.info( "Using dummy inbound persister for " + id );
            return new DummyIndexPersister();
        }

        ReusableString fileName = new ReusableString( _persistFileNameBase );
        fileName.append( '/' ).append( id.toLowerCase() ).append( "/in/" ).append( id ).append( ".dat" );
        if ( _forceRemovePersistence ) FileUtils.rm( fileName.toString() );
        MemMapPersister persister = new MemMapPersister( new ViewString( id ),
                                                         fileName,
                                                         _persistDatPreSize,
                                                         _persistDatPageSize,
                                                         _persistThreadPriority );

        fileName = new ReusableString( _persistFileNameBase );
        fileName.append( '/' ).append( id.toLowerCase() ).append( "/in/" ).append( id ).append( ".idx" );
        if ( _forceRemovePersistence ) FileUtils.rm( fileName.toString() );
        IndexPersister indexPersister = new IndexMMPersister( persister,
                                                              new ViewString( "IDX_" + id ),
                                                              fileName,
                                                              _persistIdxPreSize,
                                                              _persistThreadPriority );

        setPersisterFlush( id, indexPersister );

        return indexPersister;
    }

    protected IndexPersister createOutboundPersister( String id ) throws FileException {

        calcSizes();

        if ( _dummyPersister ) {
            _console.info( "Using dummy outbound persister for " + id );
            return new DummyIndexPersister();
        }

        ReusableString fileName = new ReusableString( _persistFileNameBase );
        fileName.append( '/' ).append( id.toLowerCase() ).append( "/out/" ).append( id ).append( ".dat" );
        if ( _forceRemovePersistence ) FileUtils.rm( fileName.toString() );
        MemMapPersister persister = new MemMapPersister( new ViewString( id ),
                                                         fileName,
                                                         _persistDatPreSize,
                                                         _persistDatPageSize,
                                                         _persistThreadPriority );

        fileName = new ReusableString( _persistFileNameBase );
        fileName.append( '/' ).append( id.toLowerCase() ).append( "/out/" ).append( id ).append( ".idx" );
        if ( _forceRemovePersistence ) FileUtils.rm( fileName.toString() );
        IndexPersister indexPersister = new IndexMMPersister( persister,
                                                              new ViewString( "IDX_" + id ),
                                                              fileName,
                                                              _persistIdxPreSize,
                                                              _persistThreadPriority );

        setPersisterFlush( id, indexPersister );

        return indexPersister;
    }

    protected Persister createOutboundSequentialPersister( String id ) throws FileException {

        ReusableString fileName = new ReusableString( _persistFileNameBase );
        fileName.append( '/' ).append( id.toLowerCase() ).append( "/out/" ).append( id ).append( ".dat" );
        if ( _forceRemovePersistence ) FileUtils.rm( fileName.toString() );
        SequentialPersister persister = new SequentialPersister( new ViewString( id ),
                                                                 fileName,
                                                                 _persistDatPreSize,
                                                                 _persistDatPageSize,
                                                                 _persistThreadPriority );

        setPersisterFlush( id, persister );

        return persister;
    }

    protected Encoder getEncoder( CodecId id, byte[] buf, int offset, boolean debug ) {
        Encoder   encoder = _codecFactory.getEncoder( id, buf, offset );
        TimeUtils calc    = TimeUtilsFactory.createTimeUtils();
        calc.setLocalTimezone( _timeZone );
        calc.setTodayFromLocalStr( _dateStr );
        encoder.setTimeUtils( calc );
        if ( encoder instanceof BinaryEncoder ) {
            ((BinaryEncoder) encoder).setDebug( debug );
        }
        return encoder;
    }

    protected Decoder getFullDecoder( CodecId id, ClientProfile client, boolean debug ) {
        Decoder decoder = _codecFactory.getFullDecoder( id );
        decoder.setClientProfile( client );
        decoder.setInstrumentLocator( _instrumentStore );
        TimeUtils calc = TimeUtilsFactory.createTimeUtils();
        calc.setLocalTimezone( _timeZone );
        calc.setTodayFromLocalStr( _dateStr );
        decoder.setTimeUtils( calc );
        decoder.setReceived( Utils.nanoTime() );
        if ( decoder instanceof BinaryDecoder ) {
            ((BinaryDecoder) decoder).setDebug( debug );
        }
        return decoder;
    }

    protected Decoder getOMSDecoder( CodecId id, ClientProfile client, boolean debug ) {
        Decoder decoder = _codecFactory.getOMSDecoder( id );
        decoder.setClientProfile( client );
        decoder.setInstrumentLocator( _instrumentStore );
        TimeUtils calc = TimeUtilsFactory.createTimeUtils();
        calc.setLocalTimezone( _timeZone );
        calc.setTodayFromLocalStr( _dateStr );
        decoder.setTimeUtils( calc );
        decoder.setReceived( Utils.nanoTime() );
        if ( decoder instanceof BinaryDecoder ) {
            ((BinaryDecoder) decoder).setDebug( debug );
        }
        return decoder;
    }

    protected void postSessionCreate( Encoder encoder, RecoverableSession sess ) {

        if ( _codecId == CodecId.CME && encoder != null ) {
            ((CMEEncoder) encoder).setSession( (SeqNumSession) sess );
        }

        if ( _sessionDirection == SessionDirection.DropCopy ) {
            _sessionManager.setHub( sess );
        } else {
            boolean isDown = _sessionDirection == SessionDirection.Downstream;

            _sessionManager.add( sess, isDown );

            if ( isDown ) {
                registerExchangeSession( _sessionManager, sess );
            }
        }
    }

    protected void prep() {
        if ( _mic != null ) {
            _ex       = ExchangeManager.instance().getByMIC( new ViewString( _mic ) );
            _timeZone = _ex.getTimeZone();
        }
    }

    protected void registerExchangeSession( SessionManager sessMgr, RecoverableSession sess ) {
        if ( _mic != null ) {
            Exchange e = ExchangeManager.instance().getByMIC( new ViewString( _mic ) );

            if ( e == null ) {
                throw new SMTRuntimeException( "Session " + sess.getComponentId() + " exchangeMIC=" + _mic + " exchange not registered with manager" );
            }

            sessMgr.associateExchange( sess, e );
        }
    }

    protected void setMulticastGroups( SocketConfig socketCfg ) {
        if ( _multicastGroups != null ) {
            String[]  parts = _multicastGroups.split( "," );
            ZString[] grps  = new ZString[ parts.length ];
            for ( int i = 0; i < parts.length; i++ ) {
                grps[ i ] = new ViewString( parts[ i ].trim() );
            }
            socketCfg.setMulticastGroups( grps );
        }
    }

    private void calcSizes() {
        long estDataSize = 256L * _expectedOrders * 8;

        int  defPageSize = 10000000;
        long estPageSize = 4096L + Math.abs( estDataSize / 10 );

        // dont overwrite values that may have been in config

        if ( _persistDatPageSize == 0 ) _persistDatPageSize = (estPageSize < defPageSize) ? (int) estPageSize : defPageSize;
        if ( _persistIdxPreSize == 0 ) _persistIdxPreSize = 1024L * 1024L + (_expectedOrders * 8 * 16);
        if ( _persistDatPreSize == 0 ) _persistDatPreSize = 1024L * 1024L + estDataSize;
    }

    private void setPersisterFlush( final String id, final Persister persister ) {
        if ( _persistFlushPeriodMS > 0 ) {

            final Scheduler.Callback flusher = new BasicSchedulerCallback( "Persist" + id + "Flush", ( a ) -> persister.flush() );

            SchedulerFactory.get().registerIndividualRepeating( CoreScheduledEvent.Flush, flusher, _persistFlushPeriodMS, _persistFlushPeriodMS );

        } else if ( _persistFlushPeriodMS == -1 ) {

            persister.setFlushPerCall( true );
        }
    }
}
