/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.loaders;

import com.rr.core.codec.FixDecoder;
import com.rr.core.component.SMTComponent;
import com.rr.core.component.SMTSingleComponentLoader;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.model.Exchange;
import com.rr.core.model.ExchangeCode;
import com.rr.core.model.SecDefSpecialType;
import com.rr.core.model.TickManager;
import com.rr.core.utils.SMTException;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.inst.*;
import com.rr.model.generated.fix.codec.MD44Decoder;
import com.rr.om.dummy.warmup.DummyInstrumentLocator;
import com.rr.om.exchange.ExchangeManager;

public class FixInstrumentFileStoreLoader implements SMTSingleComponentLoader {

    private static final Logger _console = ConsoleFactory.console( FixInstrumentFileStoreLoader.class, Level.info );

    private String                    _type                        = "multiExchange";
    private String                    _file                        = null;
    private int                       _preSize                     = 1024;
    private boolean                   _threadsafe                  = true;
    private ExchangeCode              _defaultMIC                  = ExchangeCode.UNKNOWN;   // if using singleExchange this property should be set
    private TickManager               _tickManager                 = new TickManager( "DefaultTickMgr" );
    private FixDecoder                _decoder                     = new MD44Decoder();
    private boolean                   _disableDecodeChecksum       = false;
    private boolean                   _throwErrorOnZeroInstruments = false;
    private boolean                   _useUniversalTickScales      = false;
    private SecDefSpecialType         _overrrideSecDefType         = null;
    private InstrumentFileChainLoader _next;

    @Override public SMTComponent create( String id ) throws SMTException {

        if ( _disableDecodeChecksum ) _decoder.setValidateChecksum( false );

        boolean _useThreadsafeWrapper = _threadsafe;

        InstrumentStore instrumentStore = null;

        if ( (_file != null || _next != null) && !_type.equalsIgnoreCase( "dummy" ) ) {

            if ( _type.equalsIgnoreCase( "concurrent" ) ) {
                instrumentStore = new ConcurrentInstrumentSecDefStore( id, _preSize );
                ((ConcurrentInstrumentSecDefStore) instrumentStore).setTickManager( _tickManager );

                _console.info( "Loading ConcurrentInstrumentSecDefStore with instrument file " + _file );

                _useThreadsafeWrapper = false;

            } else if ( _type.equalsIgnoreCase( "multiExchange" ) ) {
                instrumentStore = new MultiExchangeInstrumentStore( id, _preSize );
                ((MultiExchangeInstrumentStore) instrumentStore).setTickManager( _tickManager );

                _console.info( "Loading MultiExchangeInstrumentStore with instrument file " + _file );
            } else if ( _type.equalsIgnoreCase( "singleExchange" ) ) {
                Exchange e = ExchangeManager.instance().getByCode( _defaultMIC );

                if ( e == null ) {
                    throw new SMTRuntimeException( "Instrument store REC not in exchange manager : [" + _defaultMIC + "]" );
                }

                instrumentStore = new SingleExchangeInstrumentStore( id, e, _preSize );
                ((SingleExchangeInstrumentStore) instrumentStore).setTickManager( _tickManager );

                _console.info( "Loading SingleExchangeInstrumentStore with instrument file " + _file + ", srcRec=" + _defaultMIC );
            } else if ( _type.equalsIgnoreCase( "historic" ) || _type.length() == 0 ) {
                HistExchInstSecDefStore histStore = new HistExchInstSecDefStore( id, _preSize );

                histStore.setTickManager( _tickManager );

                instrumentStore = new ThreadsafeHistoricInstrumentStore( histStore );

                _useThreadsafeWrapper = false;

                _console.info( "Loading ThreadSafe HistoricalInstrumentStore with instrument file " + _file );
            } else {
                throw new SMTRuntimeException( "Unsupported inst.type of " + _type );
            }

            // use threadsafe inst store when intraday updates required

            if ( _useThreadsafeWrapper ) {
                _console.info( "Wrapping instrument store with ThreadsafeInstrumentStore with instrument file " );

                instrumentStore = new ThreadsafeInstrumentStore( instrumentStore );
            }

            instrumentStore.setUseUniversalTickScales( _useUniversalTickScales );

            FixInstrumentLoader loader = new FixInstrumentLoader( instrumentStore, _decoder );

            loader.setOverrideSecDefSpecialType( _overrrideSecDefType );

            if ( _file != null ) {
                loader.loadFromFile( _file, _throwErrorOnZeroInstruments );
            }

            if ( _next != null ) {
                _next.load( instrumentStore );
            }

        } else {
            _console.info( "Using DUMMY instrument store" );
            instrumentStore = new DummyInstrumentLocator( id );
        }

        return instrumentStore;
    }

    public FixDecoder getDecoder()                                                    { return _decoder; }

    public void setDecoder( final FixDecoder decoder )                                { _decoder = decoder; }

    public ExchangeCode getDefaultMIC() {
        return _defaultMIC;
    }

    public void setDefaultMIC( final ExchangeCode defaultMIC ) {
        _defaultMIC = defaultMIC;
    }

    public SecDefSpecialType getOverrrideSecDefType()                                 { return _overrrideSecDefType; }

    public void setOverrrideSecDefType( final SecDefSpecialType overrrideSecDefType ) { _overrrideSecDefType = overrrideSecDefType; }

    public TickManager getTickManager()                                               { return _tickManager; }

    public void setTickManager( final TickManager tickManager )                       { _tickManager = tickManager; }
}
