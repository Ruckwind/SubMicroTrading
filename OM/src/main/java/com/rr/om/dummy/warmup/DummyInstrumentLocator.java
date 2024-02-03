/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.dummy.warmup;

import com.rr.core.collections.LongHashMap;
import com.rr.core.collections.LongMap;
import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.*;
import com.rr.core.model.Currency;
import com.rr.core.model.*;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.thread.RunState;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.inst.InstrumentStore;
import com.rr.model.generated.internal.events.impl.SecurityDefinitionImpl;
import com.rr.model.generated.internal.events.impl.SecurityStatusImpl;
import com.rr.om.exchange.ExchangeManager;
import com.rr.om.model.instrument.InstrumentWrite;

import java.util.*;

// DO NOT USE IN PROD
public final class DummyInstrumentLocator implements InstrumentStore {

    private static final ZString EMPTY_STR = new ViewString( "" );

    private final     Exchange                         _overrideExchange;
    private final     LongMap<DummyInstrument>         _instrumentsById   = new LongHashMap<>( 1024, 0.75f );
    private final     Map<InstKey, FXInstrument>       _fxInstrumentsById = new HashMap<>( 1024, 0.75f );
    private final     Map<ZString, StrategyInstrument> _strategyIdMap     = new HashMap<>();
    private final     Map<InstKey, DummyInstrument>    _instruments       = new HashMap<>( 1024 );
    private final     String                           _id;
    private final     InstKey                          _tmpKey            = new InstKey();
    private transient RunState                         _runState          = RunState.Unknown;

    public DummyInstrumentLocator() {
        this( null, null );
    }

    public DummyInstrumentLocator( String id ) {
        this( id, null );
    }

    public DummyInstrumentLocator( Exchange ex ) {
        this( null, ex );
    }

    public DummyInstrumentLocator( String id, Exchange ex ) {
        _overrideExchange = ex;

        _id = (id == null) ? "DummyInstLoader" : id;

        Env env = AppProps.instance().getProperty( CoreProps.RUN_ENV, false, Env.class, Env.DEV );

        if ( env.isProdOrUAT() && AppProps.instance().getBooleanProperty( "DISABLE_DUMMY_INST_LOC", false, true ) ) {
            throw new RuntimeException( "Class DummyInstrumentLocator is banned from prod env" );
        }

        storeInstrument( DummyInstrument.DUMMY, SecurityIDSource.ExchangeSymbol, DummyInstrument.DUMMY_INSTRUMENT_ID );
    }

    @Override public boolean add( SecurityDefinitionImpl def ) { return true; }

    @Override public void add( final Instrument inst ) {
        if ( inst instanceof FXInstrument ) {
            FXInstrument fxi = (FXInstrument) inst;

            InstKey key = new InstKey();
            key.set( fxi.getExchange().getExchangeCode(), SecurityIDSource.ExchangeSymbol, fxi.getFXPair().getFXCode() );
            _fxInstrumentsById.put( key, fxi );

        } else if ( inst instanceof StrategyInstrument ) {
            StrategyInstrument si = (StrategyInstrument) inst;

            ZString key = TLC.safeCopy( si.getComponentId() );

            final Instrument existing = _strategyIdMap.putIfAbsent( key, si );

            if ( existing != inst ) {
                throw new SMTRuntimeException( getComponentId() + " attempt to register duplicate stratId " + key + " for different strat instances" );
            }
        }
    }

    @Override public boolean allowIntradayAddition() {
        return true;
    }

    @Override public void remove( SecurityDefinitionImpl def ) { /* nothing */ }

    @Override public boolean updateStatus( SecurityStatusImpl status ) {
        return false;
    }

    @Override public void getAllCommonInsts( final Set<CommonInstrument> instruments ) { /* nothing */ }

    @Override public void getAllExchInsts( final Set<ExchangeInstrument> instruments ) {
        instruments.addAll( _instruments.values() );
    }

    @Override public void getAllFXInsts( final Set<FXInstrument> instruments )                                     { /* nothing */ }

    @Override public void getAllStratInsts( final Set<StrategyInstrument> instruments )                            { /* nothing */ }

    @Override public Instrument getByInstId( final ZString id )                                                    { return null; }

    @Override public CommonInstrument getCommonInstrument( final long commonInstrumentId, final Currency ccy )     { return null; }

    @Override public void getCommonInstruments( final long commonInstrumentId, final List<CommonInstrument> dest ) { /* nothing */ }

    @Override public ExchangeInstrument getDummyExchInst() {
        return DummyInstrument.DUMMY;
    }

    @Override
    public InstrumentWrite getExchInst( ZString securityId,
                                        SecurityIDSource securityIDSource,
                                        ExchangeCode code ) {

        if ( securityId.equals( ExchangeInstrument.DUMMY_INSTRUMENT_ID ) ) return DummyInstrument.DUMMY;

        ZString exDest = EMPTY_STR;

        if ( securityIDSource == SecurityIDSource.ExchangeSymbol ) {
            int recIdx = securityId.lastIndexOf( '.' );
            if ( recIdx > 0 ) {
                exDest = new ViewString( securityId.toString().substring( recIdx + 1 ) );
            }
        }

        if ( exDest.length() > 0 && code == null ) {
            try {
                code = ExchangeCode.getVal( exDest );
            } catch( Exception e ) {
            }
        }

        if ( code == null && securityIDSource == SecurityIDSource.ExchangeSymbol ) {
            int idx = securityId.indexOf( '.' );

            if ( idx > 0 && idx < securityId.length() ) {
                ReusableString rec = TLC.instance().pop();

                securityId.substring( rec, idx + 1 );

                code = ExchangeCode.getVal( rec );

                TLC.instance().pushback( rec );
            }
        }

        Exchange exchange = getExchange( code, exDest );

        code = exchange.getExchangeCode();

        _tmpKey.set( code, securityIDSource, securityId );

        DummyInstrument inst = _instruments.get( _tmpKey );

        if ( inst != null ) return inst;

        return addInst( exchange, securityIDSource, securityId );
    }

    @Override public ExchangeInstrument getExchInstByExchangeLong( final ExchangeCode exchangeCode, final long instrumentId ) {
        DummyInstrument inst = _instrumentsById.get( instrumentId );

        if ( inst == null ) {
            inst = addInst( exchangeCode, instrumentId );
        }

        return inst;
    }

    @Override public ExchangeInstrument getExchInstByIsin( final ZString isin, final ExchangeCode securityExchange, final Currency currency ) {
        return null;
    }

    @Override public ExchangeInstrument getExchInstByUniqueCode( final SecurityIDSource src, final long instrumentId ) {
        DummyInstrument inst = _instrumentsById.get( instrumentId );

        if ( inst == null ) {
            inst = addInst( DummyInstrument.DUMMY.getExchange().getExchangeCode(), instrumentId );
        }

        return inst;
    }

    @Override public void getExchInsts( Set<ExchangeInstrument> insts, ZString keyVal, SecurityIDSource keySrc ) {
        throw new SMTRuntimeException( "Not Supported in legacy instrument store" );
    }

    @Override public void getExchInsts( Set<ExchangeInstrument> instruments, Exchange ex ) {
        instruments.addAll( _instruments.values() );
    }

    @Override public void getExchInstsBySecurityGrp( final ZString securityGrp, ExchangeCode mic, final List<ExchangeInstrument> dest ) {  /* nothing */ }

    @Override public FXInstrument getFXInstrument( final FXPair fxPair, final ExchangeCode code ) {
        _tmpKey.set( code, SecurityIDSource.ExchangeSymbol, fxPair.getFXCode() );

        FXInstrument inst = _fxInstrumentsById.get( _tmpKey );

        return inst;
    }

    @Override public ExchDerivInstrument getFutureInstrumentBySym( final FutureExchangeSymbol symbol, final int maturityDateYYYYMMDD, final ExchangeCode securityExchange ) { return null; }

    @Override public void getFuturesBySecurityGrp( final FutureExchangeSymbol symbol, final List<ExchangeInstrument> dest )                                                 { /* nothing */ }

    @Override public Instrument getInst( ZString securityId, SecurityIDSource securityIDSource, ExchangeCode exchangeCode ) {
        if ( securityIDSource == SecurityIDSource.StrategyId ) return _strategyIdMap.get( securityId );

        return getExchInst( securityId, securityIDSource, exchangeCode );
    }

    @Override public ExchDerivInstrument getOptionInstrumentBySym( final ZString symbol, final int maturityDateYYYYMMDD, final double strikePrice, final OptionType type, final ExchangeCode securityExchange ) { return null; }

    @Override public ParentCompany getParentCompany( final long parentCompanyId )                                                                                                                               { return null; }

    @Override public void setUseUniversalTickScales( final boolean useUniversalTickScales )                                                                                                                     { /* nothing */ }

    @Override public String getComponentId() {
        return _id;
    }

    @Override public RunState getRunState()                                      { return _runState; }

    @Override public void init( final SMTStartContext ctx, CreationPhase phase ) { /* nothing */ }

    @Override public void prepare()                                              { /* nothing */ }

    @Override public RunState setRunState( final RunState newState )             { return _runState = newState; }

    @Override public ExchangeInstrument getExchInstByUniqueInstId( final long uniqueInstId ) { return _instrumentsById.get( uniqueInstId ); }

    private DummyInstrument addInst( ExchangeCode exchangeCode, long instrumentId ) {
        DummyInstrument inst;
        Exchange        exchange = ExchangeManager.instance().getByCode( exchangeCode );

        if ( exchange == null ) {
            exchange = DummyExchangeManager.instance().get( exchangeCode );
        }

        ReusableString ric = new ReusableString( 8 );
        ric.append( instrumentId ).append( '.' ).append( exchangeCode );

        Currency ccy = Currency.GBP;

        //noinspection EqualsBetweenInconvertibleTypes
        if ( exchangeCode.equals( "XCME" ) ) {
            ccy = Currency.USD;
        }

        final ViewString zInstId = new ViewString( "" + instrumentId );

        inst = new DummyInstrument( ric, exchange, ccy, false, zInstId );

        storeInstrument( inst, SecurityIDSource.ExchangeSymbol, zInstId );

        return inst;
    }

    private DummyInstrument addInst( Exchange exchange, SecurityIDSource securityIDSource, ZString instrumentId ) {

        DummyInstrument inst;

        ReusableString code = new ReusableString( 8 );

        ZString mic = exchange.getExchangeCode().getMIC();

        if ( securityIDSource == SecurityIDSource.ExchangeSymbol || securityIDSource == SecurityIDSource.FIGI ) {
            code.copy( instrumentId );
        } else {
            code.append( instrumentId ).append( '.' ).append( mic );
        }

        Currency ccy = Currency.GBP;

        final byte lastInstByte = instrumentId.getByte( instrumentId.length() - 1 );

        //noinspection EqualsBetweenInconvertibleTypes
        if ( mic.equals( "XCME" ) || instrumentId.contains( "XCHI" ) ) {
            ccy = Currency.USD;
        }

        if ( mic.equals( "XPAR" ) || instrumentId.contains( "XPAR" ) || lastInstByte == 'p' ) {
            ccy = Currency.EUR;
        }

        inst = new DummyInstrument( code, exchange, ccy, false, instrumentId );

        storeInstrument( inst, securityIDSource, instrumentId );

        return inst;
    }

    private DummyInstrument addInst( ZString ric ) {
        DummyInstrument inst;
        String          ex = "L";

        int idx = ric.indexOf( '.' );

        if ( idx > -1 ) {
            ex = ric.toString().substring( idx + 1 );
        }

        Exchange exchange = DummyExchangeManager.instance().get( ExchangeCode.getVal( new ViewString( ex ) ) );

        inst = new DummyInstrument( ric, exchange, Currency.GBP );

        storeInstrument( inst, SecurityIDSource.ExchangeSymbol, ric );
        return inst;
    }

    private Exchange getExchange( final ExchangeCode securityExchange, final ZString ex ) {
        Exchange exchange = null;

        if ( _overrideExchange != null ) {
            exchange = _overrideExchange;
        } else {
            if ( securityExchange != null ) {
                try {
                    exchange = ExchangeManager.instance().getByCode( securityExchange );
                } catch( Exception e ) {
                    // ignore
                }
            }
        }

        if ( exchange == null ) {
            if ( exchange == null && ex.length() > 0 ) {
                try {
                    exchange = DummyExchangeManager.instance().get( ExchangeCode.getVal( ex ) );
                } catch( Exception e ) {
                    // ignore
                }
                if ( exchange == null && ex.length() > 0 ) {
                    try {
                        exchange = DummyExchangeManager.instance().get( ExchangeCode.getVal( ex ) ); // assume its MIC
                    } catch( Exception e ) {
                        // ignore
                    }
                }
            }

            if ( exchange == null ) {
                exchange = DummyExchangeManager.instance().get( ExchangeCode.UNKNOWN );
            }
        }
        return exchange;
    }

    private void storeInstrument( DummyInstrument inst, SecurityIDSource idSrc, ZString secId ) {
        _instruments.put( new InstKey( inst.getExchange().getExchangeCode(), idSrc, secId ), inst );

        _instruments.put( new InstKey( inst.getExchange().getExchangeCode(), SecurityIDSource.ExchangeSymbol, inst.getExchangeSymbol() ), inst );

        _instrumentsById.put( inst.getExchangeLongId(), inst );
    }

    private class InstKey {

        final ReusableString _securityId;
        ExchangeCode     _code;
        SecurityIDSource _idSource;

        public InstKey() {
            _code       = ExchangeCode.UNKNOWN;
            _securityId = TLC.instance().getString();
        }

        public InstKey( final ExchangeCode code, final SecurityIDSource idSource, final ZString securityId ) {
            _code       = code;
            _idSource   = idSource;
            _securityId = TLC.safeCopy( securityId );
        }

        @Override public int hashCode() {
            return _securityId.hashCode();
        }

        @Override public boolean equals( final Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;
            final InstKey instKey = (InstKey) o;
            return _code == instKey._code && _idSource == instKey._idSource && Objects.equals( _securityId, instKey._securityId );
        }

        public void set( final ExchangeCode code, final SecurityIDSource idSource, final ZString securityId ) {
            _code = code;
            _securityId.copy( securityId );
            _idSource = idSource;
        }
    }
}
