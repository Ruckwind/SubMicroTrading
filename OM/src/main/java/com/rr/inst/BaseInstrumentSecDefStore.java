/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.inst;

import com.rr.core.collections.IntMap;
import com.rr.core.collections.LongMap;
import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.*;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Currency;
import com.rr.core.model.*;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.thread.RunState;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.StringUtils;
import com.rr.core.utils.Utils;
import com.rr.model.generated.internal.events.impl.SecDefLegImpl;
import com.rr.model.generated.internal.events.impl.SecurityAltIDImpl;
import com.rr.model.generated.internal.events.impl.SecurityDefinitionImpl;
import com.rr.model.generated.internal.events.impl.SecurityStatusImpl;
import com.rr.model.generated.internal.events.recycle.SecurityDefinitionRecycler;
import com.rr.model.generated.internal.events.recycle.SecurityStatusRecycler;
import com.rr.model.generated.internal.type.SecurityTradingStatus;
import com.rr.model.generated.internal.type.SecurityUpdateAction;
import com.rr.om.dummy.warmup.DummyInstrument;
import com.rr.om.newmain.SMTContext;

import java.util.*;

import static com.rr.inst.InstUtils.*;
import static org.junit.Assert.assertEquals;

/**
 * instrument store
 * <p>
 * the store of choice for ULL (well the concurrent one is)
 *
 * @author Richard Rose
 */
public abstract class BaseInstrumentSecDefStore implements InstrumentStore {

    private final static Logger _log = LoggerFactory.create( BaseInstrumentSecDefStore.class );

    private final     Map<ZString, Instrument>                   _internalStrKeyMap;
    private final     Map<ZString, InstrumentSecurityDefWrapper> _bloombergCodeMap;
    private final     Map<ZString, InstrumentSecurityDefWrapper> _isinToInstMap;
    private final     Map<ZString, InstrumentSecurityDefWrapper> _figiMap;
    private final     Map<ZString, StrategyInstrument>           _strategyIdMap;
    private final     LongMap<InstrumentSecurityDefWrapper>      _uniqueLongInstId;
    private final     LongMap<Map<Currency, CommonInstrument>>   _commonInstMap;
    private final     LongMap<ParentCompany>                     _parentCompanyMap;
    private final     Map<ZString, Set<ExchangeInstrument>>      _secGrpMap              = new LinkedHashMap<>();
    private final     int                                        _preSize;
    private final     String                                     _id;
    private final     ReusableString                             _tmpISINKey             = new ReusableString( 16 );
    private final     ReusableString                             _tmpFutureKey           = new ReusableString( 16 );
    private final     ReusableString                             _tmpAddKey              = new ReusableString( 16 );
    private           TickManager                                _tickManager;
    private           boolean  _useUniversalTickScales = false;
    private final     boolean  _restrictISIN           = true; // with QH ISIN are non unique in Futures / Options
    private transient RunState _runState               = RunState.Unknown;
    private final     SecurityDefinitionRecycler _securityDefinitionRecycler;
    private final     SecurityStatusRecycler     _securityStatusRecycler;

    public BaseInstrumentSecDefStore( String id, int preSize ) {
        _id      = id;
        _preSize = preSize;

        _isinToInstMap     = createMap( preSize );
        _bloombergCodeMap  = createMap( preSize );
        _figiMap           = createMap( preSize );
        _internalStrKeyMap = createMap( preSize );

        _strategyIdMap    = createMap( 64 ); // dont expect alot of strat
        _uniqueLongInstId = createLongMap( preSize );
        _commonInstMap    = createLongMap( preSize );
        _parentCompanyMap = createLongMap( 256 );

        SuperpoolManager sp = SuperpoolManager.instance();
        _securityDefinitionRecycler = sp.getRecycler( SecurityDefinitionRecycler.class, SecurityDefinitionImpl.class );
        _securityStatusRecycler     = sp.getRecycler( SecurityStatusRecycler.class, SecurityStatusImpl.class );
    }

    @Override public synchronized boolean add( SecurityDefinitionImpl def ) {

        Exchange ex = getExchange( def.getSecurityExchange() );

        Indexes indexes = getExchangeMap( ex, _preSize );

        InstUtils.secDefOverrides( def );

        InstrumentSecurityDefWrapper inst = getInstrumentBySecurityDefWrapper( def, indexes );

        if ( inst == null ) {
            int legCnt = def.getNoLegs();

            if ( legCnt == Constants.UNSET_INT ) legCnt = 0;

            inst = createWrapper( ex, def, legCnt );

            // check for all duplicates before start mutating indexes
            checkForDuplicateKeys( inst, indexes, legCnt );

            if ( ex.isExchangeSymbolLongId() ) {
                storeExchangeLongId( indexes, inst );
            }

            if ( legCnt > 0 ) {
                addInstLegs( def, ex, indexes, (DerivInstSecDefWrapperImpl) inst );
            }

            storeInternalStrKey( inst );

        } else {

            SecurityDefinitionImpl oldDef = inst.getSecDef();
            if ( oldDef != null ) {
                if ( !oldDef.getSymbol().equals( def.getSymbol() ) && Utils.isNull( oldDef.getEndTimestamp() ) && Utils.isNull( def.getEndTimestamp() ) ) {
                    _log.info( "Instrument symbol has changed : secDef old= " + oldDef + ", new=" + def );
                }

                removeInstFromSupplementaryIndexes( inst, indexes ); // BEFORE KEYS RECYCLED

                _securityDefinitionRecycler.recycle( oldDef );
            }

            String origId = inst.id();

            inst.setSecurityDefinition( def );

            String newId = inst.id();

            if ( !origId.equals( newId ) ) {
                _internalStrKeyMap.remove( origId );
                storeInternalStrKey( inst );
            }
        }

        addInstToSupplementaryIndexes( inst, indexes );
        setTickType( inst );

        return true;
    }

    @Override public void add( final Instrument inst ) {
        _internalStrKeyMap.put( TLC.safeCopy( inst.id() ), inst );

        if ( inst instanceof FXInstrument ) {
            FXInstrument def = (FXInstrument) inst;

            Exchange ex = def.getExchange();

            Indexes indexes = getExchangeMap( ex, _preSize );

            if ( indexes == null ) return;

            indexes._ccyFXMap.putIfAbsent( def.getFXPair(), def );
        } else if ( inst instanceof StrategyInstrument ) {
            StrategyInstrument si = (StrategyInstrument) inst;

            ZString key = TLC.safeCopy( si.getComponentId() );

            final Instrument existing = _strategyIdMap.putIfAbsent( key, si );

            if ( existing != null && existing != inst ) {
                throw new SMTRuntimeException( getComponentId() + " attempt to register duplicate stratId " + key + " for different strat instances" );
            }
        }
    }


    @Override public boolean allowIntradayAddition() {
        return true;
    }

    @Override public synchronized void remove( SecurityDefinitionImpl def ) {

        Exchange                     ex      = getExchange( def.getSecurityExchange() );
        Indexes                      indexes = getExchangeMap( ex, _preSize );
        InstrumentSecurityDefWrapper inst    = getInstrumentBySecurityDefWrapper( def, indexes );

        if ( inst != null ) {
            _uniqueLongInstId.remove( inst.getUniqueInstId() );

            removeInstFromSupplementaryIndexes( inst, indexes ); // BEFORE KEYS RECYCLED
            SecurityDefinitionImpl oldDef = inst.getSecDef();
            oldDef.setSecurityUpdateAction( SecurityUpdateAction.Delete );
            _securityDefinitionRecycler.recycle( def );
        }
    }

    @Override public boolean updateStatus( SecurityStatusImpl status ) {

        boolean changed = false;

        InstrumentSecurityDefWrapper inst = getWrapper( status.getSecurityIDSource(), status.getSecurityID(), status.getSecurityExchange() );

        if ( inst != null ) {
            SecurityTradingStatus curSt = inst.getSecurityTradingStatus();
            SecurityTradingStatus newSt = status.getSecurityTradingStatus();

            if ( curSt != newSt ) {
                inst.setSecurityTradingStatus( newSt );
                changed = true;
            }

            SecurityStatusImpl oldStatus = inst.getLastStatus();
            if ( oldStatus != status ) {
                _securityStatusRecycler.recycle( oldStatus );
                changed = true;
            }

            inst.setLastStatus( status );
        } else {
            _log.warn( getComponentId() + " UNABLE TO FIND INSTRUMENT TO PROCESS SecurityStatus " + status );

            _securityStatusRecycler.recycle( status );
        }

        return changed;
    }

    @Override public void getAllCommonInsts( final Set<CommonInstrument> instruments ) {
        _commonInstMap.forEach( ( m ) -> m.values().forEach( instruments::add ) );
    }

    @Override public void getAllExchInsts( Set<ExchangeInstrument> instruments ) {
        _uniqueLongInstId.forEach( ( e ) -> instruments.add( e ) );
    }

    @Override public void getAllFXInsts( final Set<FXInstrument> instruments ) {
        getExchangeMaps().values().forEach( ( i ) -> i._ccyFXMap.values().forEach( ( inst ) -> instruments.add( inst ) ) );
    }

    @Override public void getAllStratInsts( final Set<StrategyInstrument> instruments ) {
        _strategyIdMap.values().forEach( ( e ) -> instruments.add( e ) );
    }

    @Override public Instrument getByInstId( final ZString id ) {
        return _internalStrKeyMap.get( id );
    }

    @Override public CommonInstrument getCommonInstrument( final long commonInstrumentId, final Currency ccy ) {
        Map<Currency, CommonInstrument> m = _commonInstMap.get( commonInstrumentId );
        if ( m == null ) return null;
        return m.get( ccy );
    }

    @Override public void getCommonInstruments( final long commonInstrumentId, final List<CommonInstrument> dest ) {
        Map<Currency, CommonInstrument> m = _commonInstMap.get( commonInstrumentId );
        if ( m == null ) return;
        dest.addAll( m.values() );
    }

    @Override public ExchangeInstrument getDummyExchInst() {
        return DummyInstrument.DUMMY;
    }

    @Override public ExchangeInstrument getExchInst( ZString securityId, SecurityIDSource securityIDSource, ExchangeCode mic ) {

        if ( securityId == ExchangeInstrument.DUMMY_INSTRUMENT_ID ) { // YES THIS IS CORRECT, DOES SECURITY_ID POINT to the DUMMY instance
            return DummyInstrument.DUMMY;
        }

        if ( securityIDSource.isUniversallyUnique() ) {
            return findByUniqueIndex( securityId, securityIDSource );
        }

        if ( securityIDSource == SecurityIDSource.ISIN ) return null;

        Exchange ex = getExchange( mic );

        if ( ex == null ) return null;

        Indexes indexes = getExchangeMap( ex, _preSize );

        if ( indexes == null ) return null;

        Map<ZString, InstrumentSecurityDefWrapper> idToInstMap = indexes.getIndex( securityIDSource );

        if ( idToInstMap == null ) return null;

        InstrumentSecurityDefWrapper inst = idToInstMap.get( securityId );

        if ( inst != null ) return inst;

        FXPair fxPair = FXPair.get( securityId );

        if ( fxPair != null ) {
            return doGetFxInstrument( fxPair, indexes );
        }

        return null;
    }

    @Override public ExchangeInstrument getExchInstByExchangeLong( ExchangeCode code, long instrumentId ) {

        if ( instrumentId == ExchangeInstrument.DUMMY_INSTRUMENT_LONG_ID ) return DummyInstrument.DUMMY;

        Exchange ex = getExchange( code );

        Indexes indexes = getExchangeMap( ex, _preSize );

        if ( indexes != null ) {
            LongMap<InstrumentSecurityDefWrapper> idToInstMap = indexes.getExchangeIdToMap();

            return idToInstMap.get( instrumentId );
        }

        return null;
    }

    @Override public ExchangeInstrument getExchInstByIsin( ZString isin, ExchangeCode securityExchange, Currency currency ) {

        synchronized( _isinToInstMap ) {
            formCompoundInstUniqKey( _tmpISINKey, isin, securityExchange, currency );

            return _isinToInstMap.get( _tmpISINKey );
        }
    }

    @Override public ExchangeInstrument getExchInstByUniqueCode( SecurityIDSource src, long instrumentId ) {

        if ( src == SecurityIDSource.UniqueInstId ) {
            return _uniqueLongInstId.get( instrumentId );
        }

        return null;
    }

    @Override public ExchangeInstrument getExchInstByUniqueInstId( long uniqueInstId ) {
        return _uniqueLongInstId.get( uniqueInstId );
    }

    @Override public void getExchInsts( Set<ExchangeInstrument> insts, ZString keyVal, SecurityIDSource keySrc ) {
        throw new SMTRuntimeException( "Not Supported in legacy instrument store" );
    }

    @Override public void getExchInsts( Set<ExchangeInstrument> instruments, Exchange ex ) {

        Indexes indexes = getExchangeMap( ex, _preSize );

        if ( indexes == null ) return;

        Collection<InstrumentSecurityDefWrapper> insts = indexes.getSymbolIndex().values();

        instruments.addAll( insts );
    }

    @Override public void getExchInstsBySecurityGrp( final ZString securityGrp, ExchangeCode mic, final List<ExchangeInstrument> dest ) {
        if ( securityGrp != null && securityGrp.length() > 0 ) {
            final Set<ExchangeInstrument> grp = _secGrpMap.get( securityGrp );

            if ( grp != null ) {
                if ( mic == null || mic == ExchangeCode.UNKNOWN ) {
                    dest.addAll( grp );
                } else {
                    if ( grp != null ) {
                        for ( ExchangeInstrument ei : grp ) {
                            if ( ei.getExchange().getExchangeCode() == mic ) {
                                dest.add( ei );
                            }
                        }
                    }
                }
            }
        }
    }

    @Override public FXInstrument getFXInstrument( final FXPair fxCode, ExchangeCode code ) {
        Exchange ex = getExchange( code );

        Indexes indexes = getExchangeMap( ex, _preSize );

        if ( indexes == null ) return null;

        FXInstrument fxInst = doGetFxInstrument( fxCode, indexes );

        return fxInst;
    }

    @Override public ExchDerivInstrument getFutureInstrumentBySym( final FutureExchangeSymbol symbol, final int maturityDateYYYYMM, final ExchangeCode securityExchange ) {
        Exchange ex = getExchange( securityExchange );

        Indexes indexes = getExchangeMap( ex, _preSize );

        if ( indexes != null ) {

            synchronized( _tmpFutureKey ) {
                formFuturesSymbolKey( _tmpFutureKey, null, 0, symbol, maturityDateYYYYMM );

                InstrumentSecurityDefWrapper inst = indexes.getSymbolIndex().get( _tmpFutureKey );

                return (ExchDerivInstrument) inst;
            }
        }

        return null;
    }

    @Override public void getFuturesBySecurityGrp( final FutureExchangeSymbol symbol, final List<ExchangeInstrument> dest ) {
        if ( symbol != null ) {
            final Set<ExchangeInstrument> grp = _secGrpMap.get( symbol.getPhysicalSym() );

            if ( grp != null ) {
                for ( ExchangeInstrument ei : grp ) {
                    if ( symbol.isValidMIC( ei.getExchange().getExchangeCode() ) ) {
                        dest.add( ei );
                    }
                }
            }
        }
    }

    @Override public Instrument getInst( ZString securityId, SecurityIDSource securityIDSource, ExchangeCode exchangeCode ) {
        if ( securityIDSource == SecurityIDSource.StrategyId ) return _strategyIdMap.get( securityId );

        return getExchInst( securityId, securityIDSource, exchangeCode );
    }

    @Override public ExchDerivInstrument getOptionInstrumentBySym( final ZString symbol, final int maturityDateYYYYMM, final double strikePrice, final OptionType type, final ExchangeCode securityExchange ) {
        Exchange ex = getExchange( securityExchange );

        Indexes indexes = getExchangeMap( ex, _preSize );

        if ( indexes != null ) {

            synchronized( _tmpFutureKey ) {
                formOptionsSymbolKey( _tmpFutureKey, symbol, maturityDateYYYYMM, strikePrice, type );

                InstrumentSecurityDefWrapper inst = indexes.getSymbolIndex().get( _tmpFutureKey );

                return (ExchDerivInstrument) inst;
            }
        }

        return null;
    }

    @Override public ParentCompany getParentCompany( final long parentCompanyId ) { return _parentCompanyMap.get( parentCompanyId ); }

    @Override public void setUseUniversalTickScales( boolean useUniversalTickScales ) {
        _useUniversalTickScales = useUniversalTickScales;
    }

    @Override public String getComponentId() {
        return _id;
    }

    @Override public RunState getRunState() { return _runState; }

    @Override public void init( final SMTStartContext c, CreationPhase phase ) {
        if ( !(c instanceof SMTContext) ) {
            throw new SMTRuntimeException( "BaseInstrumentSecDefStore requires SMTContext" );
        }
        SMTContext ctx = (SMTContext) c;
        _tickManager = ctx.getTickManager();
    }

    @Override public void prepare() {
        /* nothing */
    }

    @Override public RunState setRunState( final RunState newState ) { return _runState = newState; }

    /**
     * @TODO remove this method and change interface to relay back stats which can be used in unit test
     */
    public synchronized void assertIndicesSameSize() {

        if ( Env.TEST != AppProps.instance().getProperty( CoreProps.RUN_ENV, Env.class ) ) {
            return;
        }

        int size = _uniqueLongInstId.size();

        if ( _isinToInstMap.size() > 0 ) assertEquals( size, _isinToInstMap.size() );
        if ( _isinToInstMap.size() > 0 ) assertEquals( size, _isinToInstMap.size() );
        if ( _figiMap.size() > 0 ) assertEquals( size, _figiMap.size() );

        getExchangeMaps().forEach( ( key, value ) -> {
            Indexes i = value;

            int exMapSize = i._symToInstMap.size();

            if ( i._secDescToInstMap.size() > 0 ) assertEquals( exMapSize, i._secDescToInstMap.size() );
            if ( i._ccyFXMap.size() > 0 ) assertEquals( exMapSize, i._ccyFXMap.size() );
            // if ( i._exchangeLongIdMap.size() > 0 ) assertEquals( exMapSize, i._exchangeLongIdMap.size() );
            // if ( i._exchangeSymToInstMap.size() > 0 ) assertEquals( exMapSize, i._exchangeSymToInstMap.size() );
        } );
    }

    public TickManager getTickManager()                         { return _tickManager; }

    public void setTickManager( final TickManager tickManager ) { _tickManager = tickManager; }

    public void logStats() {
        _log.info( getComponentId() + " _isinToInstMap.size=" + _isinToInstMap.size() );
        _log.info( getComponentId() + " _uniqueLongInstId.size=" + _uniqueLongInstId.size() );
        _log.info( getComponentId() + " _isinToInstMap.size=" + _isinToInstMap.size() );
        _log.info( getComponentId() + " _figiMap=" + _figiMap.size() );
        _log.info( getComponentId() + " _stratIdMap=" + _strategyIdMap.size() );

        getExchangeMaps().forEach( ( key, value ) -> {
            Indexes i = value;

            _log.info( getComponentId() + " " + key.getExchangeCode() + " _exchangeLongIdMap=" + i._exchangeLongIdMap.size() );
            _log.info( getComponentId() + " " + key.getExchangeCode() + " _secDescToInstMap=" + i._secDescToInstMap.size() );
            _log.info( getComponentId() + " " + key.getExchangeCode() + " _symToInstMap=" + i._symToInstMap.size() );
            _log.info( getComponentId() + " " + key.getExchangeCode() + " _exchangeSymToInstMap=" + i._exchangeSymToInstMap.size() );
            _log.info( getComponentId() + " " + key.getExchangeCode() + " _ccyFXMap=" + i._ccyFXMap.size() );
        } );
    }

    protected abstract Map<FXPair, FXInstrument> createFXMap();

    protected abstract IntMap<InstrumentSecurityDefWrapper> createIntMap( final int preSize );

    protected abstract <T> LongMap<T> createLongMap( final int preSize );

    protected abstract <K, V> Map<K, V> createMap( final int preSize );

    protected abstract Exchange getExchange( ExchangeCode mic );

    protected abstract Indexes getExchangeMap( Exchange ex, int preSize );

    protected abstract Map<Exchange, Indexes> getExchangeMaps();

    protected InstrumentSecurityDefWrapper getInstrumentBySecurityDefWrapper( final SecurityDefinitionImpl def, final Indexes indexes ) {
        InstrumentSecurityDefWrapper inst = null;

        Map<ZString, InstrumentSecurityDefWrapper> idToInstMap = null;
        idToInstMap = indexes.getIndex( def.getSecurityIDSource() );
        if ( idToInstMap != null ) {
            if ( def.getSecurityIDSource() == SecurityIDSource.ISIN ) {
                inst = (InstrumentSecurityDefWrapper) getExchInstByIsin( def.getSecurityID(), def.getSecurityExchange(), def.getCurrency() );
            } else if ( def.getSecurityIDSource() == SecurityIDSource.Symbol ) {
                if ( def.getSecurityType() == SecurityType.Future ) {
                    FutureExchangeSymbol sym = FutureExchangeSymbol.getVal( def.getSecurityGroup() );
                    inst = (InstrumentSecurityDefWrapper) getFutureInstrumentBySym( sym, def.getMaturityMonthYear(), def.getSecurityExchange() );
                } else if ( def.getSecurityType() == SecurityType.Option ) {
                    OptionType type = InstUtils.getOptionType( inst.getSecDef() );
                    inst = (InstrumentSecurityDefWrapper) getOptionInstrumentBySym( def.getSecurityID(), def.getMaturityMonthYear(), def.getStrikePrice(), type, def.getSecurityExchange() );
                } else {
                    inst = idToInstMap.get( def.getSecurityID() );
                }
            } else {
                inst = idToInstMap.get( def.getSecurityID() );
            }
        }

        if ( inst == null ) {
            inst = findByAltId( def, indexes ); // look for instrument already added using different IDSource
        }
        return inst;
    }

    private void addInstKey( final InstrumentSecurityDefWrapper inst, final Indexes indexes, final SecurityIDSource idSource, ZString id ) {
        if ( id == null || id.length() == 0 ) return;

        Map<ZString, InstrumentSecurityDefWrapper> indexMap = indexes.getIndex( idSource );

        if ( indexMap != null ) {
            if ( idSource == SecurityIDSource.ISIN ) {
                if ( _restrictISIN && (inst.getSecurityType() == SecurityType.Future || inst.getSecurityType() == SecurityType.Option) ) {
                    return; // IGNORE ISIN AS NON UNIQUE
                }
                id = formCompoundInstUniqKey( TLC.instance().getString(), id, inst.getExchange().getExchangeCode(), inst.getCurrency() );
            } else if ( idSource == SecurityIDSource.Symbol ) {
                if ( inst.getSecurityType() == SecurityType.Future ) {
                    FutureExchangeSymbol sym = FutureExchangeSymbol.getVal( inst.getSecurityGroup() );
                    id = formFuturesSymbolKey( TLC.instance().getString(), inst.getSymbol(), inst.getSecDef().getNoLegs(), sym, inst.getSecDef().getMaturityMonthYear() );
                } else if ( inst.getSecurityType() == SecurityType.Option ) {
                    OptionType type = InstUtils.getOptionType( inst.getSecDef() );
                    id = formOptionsSymbolKey( TLC.instance().getString(), id, inst.getSecDef().getMaturityMonthYear(), inst.getSecDef().getStrikePrice(), type );
                }
            }

            if ( id.length() > 0 ) {
                indexMap.put( id, inst );
            }
        }
    }

    private void addInstLegs( final SecurityDefinitionImpl def, final Exchange ex, final Indexes indexes, final DerivInstSecDefWrapperImpl inst ) {
        SecDefLegImpl curLeg = (SecDefLegImpl) def.getLegs();

        int legIdx = 0;

        while( curLeg != null ) {
            Map<ZString, InstrumentSecurityDefWrapper> idToInstMap = indexes.getIndex( def.getSecurityIDSource() );
            InstrumentSecurityDefWrapper               legInst     = idToInstMap.get( curLeg.getLegSecurityID() );

            if ( legInst == null ) { // create placeholder instrument wrapper, assume secDef will come later

                final SecurityDefinitionImpl sdLeg = new SecurityDefinitionImpl();

                sdLeg.setSecurityExchange( def.getSecurityExchange() );

                legInst = new DerivInstSecDefWrapperImpl( ex, sdLeg, null, 0 );

                legInst.setPlaceHolderDefinition( curLeg );

                idToInstMap.put( curLeg.getLegSecurityID(), legInst );

                if ( ex.isExchangeSymbolLongId() ) {
                    long exchangeLongId = StringUtils.parseLongNoException( curLeg.getLegSecurityID() );

                    sdLeg.setExchangeLongId( exchangeLongId );

                    storeExchangeLongId( indexes, legInst );
                }
            }

            curLeg.setInstrument( legInst );

            inst.setLeg( legIdx++, curLeg );

            SecDefLegImpl prevLeg = curLeg;
            curLeg = curLeg.getNext();
            prevLeg.setNext( null );
        }

        def.setLegs( null ); // POTENTIAL GC ON MASS INST UPDATE
    }

    private void addInstToSupplementaryIndexes( InstrumentSecurityDefWrapper inst, Indexes indexes ) {

        SecurityDefinitionImpl def = inst.getSecDef();

        SecurityAltIDImpl securityAltIDs = (SecurityAltIDImpl) def.getSecurityAltIDs();

        while( securityAltIDs != null ) {

            SecurityIDSource idSource = securityAltIDs.getSecurityAltIDSource();
            ZString          id       = securityAltIDs.getSecurityAltID();

            addInstKey( inst, indexes, idSource, id );

            securityAltIDs = securityAltIDs.getNext();
        }

        addInstKey( inst, indexes, def.getSecurityIDSource(), def.getSecurityID() );

        // ExchangeSymbol
        if ( inst.getExchangeSymbol().length() > 0 ) indexes.getIndex( SecurityIDSource.ExchangeSymbol ).put( inst.getExchangeSymbol(), inst );

        ZString symbolKey = inst.getSymbol();
        if ( symbolKey.length() > 0 ) {
            if ( inst.getSecurityType() == SecurityType.Future ) {
                FutureExchangeSymbol sym = FutureExchangeSymbol.getVal( inst.getSecurityGroup() );
                symbolKey = formFuturesSymbolKey( TLC.instance().getString(), inst.getSymbol(), inst.getSecDef().getNoLegs(), sym, inst.getSecDef().getMaturityMonthYear() );
            } else if ( inst.getSecurityType() == SecurityType.Option ) {
                OptionType type = InstUtils.getOptionType( inst.getSecDef() );
                symbolKey = formOptionsSymbolKey( TLC.instance().getString(), symbolKey, inst.getSecDef().getMaturityMonthYear(), inst.getSecDef().getStrikePrice(), type );
            }

            indexes.getSymbolIndex().put( symbolKey, inst );
        }

        if ( inst.getSecDef().getSecurityDesc().length() > 0 ) indexes.getSecurityDescIndex().put( inst.getSecDef().getSecurityDesc(), inst );

        updateSecurityGroups( inst );

        final long uniqueInstId = inst.getUniqueInstId();
        if ( !Utils.isNull( uniqueInstId ) ) _uniqueLongInstId.put( uniqueInstId, inst );
    }

    private void checkForDuplicateKeys( final InstrumentSecurityDefWrapper inst, final Indexes indexes, final int legCnt ) {
        final Exchange ex = inst.getExchange();

        if ( ex.isExchangeSymbolLongId() ) {
            final long id = inst.getExchangeLongId();
            if ( id != Constants.UNSET_LONG ) {
                LongMap<InstrumentSecurityDefWrapper> idToInstMap = indexes.getExchangeIdToMap();
                ExchangeInstrument                    existing    = idToInstMap.get( id );
                if ( existing != null ) {
                    throw new SMTRuntimeException( "Reject Duplicate instrument key, exchangeLongId= " + id + ", oldInst=" + existing + ", newInst=" + inst );
                }
            }
        }

        final long uniqueInstId = inst.getUniqueInstId();
        if ( !Utils.isNull( uniqueInstId ) ) {
            ExchangeInstrument existing = _uniqueLongInstId.get( uniqueInstId );
            if ( existing != null ) {
                throw new SMTRuntimeException( "Reject Duplicate instrument key, uniqueInstId= " + uniqueInstId + ", oldInst=" + existing + ", newInst=" + inst );
            }
        }

        SecurityDefinitionImpl def = inst.getSecDef();

        SecurityAltIDImpl securityAltIDs = (SecurityAltIDImpl) def.getSecurityAltIDs();

        while( securityAltIDs != null ) {

            SecurityIDSource idSource = securityAltIDs.getSecurityAltIDSource();
            ZString          id       = securityAltIDs.getSecurityAltID();

            checkInstKeyNotDuplicate( inst, indexes, idSource, id );

            securityAltIDs = securityAltIDs.getNext();
        }

        checkInstKeyNotDuplicate( inst, indexes, def.getSecurityIDSource(), def.getSecurityID() );

        // ExchangeSymbol
        if ( inst.getExchangeSymbol().length() > 0 ) {
            ExchangeInstrument existing = indexes.getIndex( SecurityIDSource.ExchangeSymbol ).get( inst.getExchangeSymbol() );
            if ( existing != null ) {
                throw new SMTRuntimeException( "Reject Duplicate instrument key, exchangeSymbol= " + inst.getExchangeSymbol() + ", oldInst=" + existing + ", newInst=" + inst );
            }
        }

        ZString symbolKey = inst.getSymbol();
        if ( inst.getSecurityType() == SecurityType.Future ) {
            FutureExchangeSymbol sym = FutureExchangeSymbol.getVal( inst.getSecurityGroup() );
            symbolKey = formFuturesSymbolKey( TLC.instance().getString(), inst.getSymbol(), inst.getSecDef().getNoLegs(), sym, inst.getSecDef().getMaturityMonthYear() );
        } else if ( inst.getSecurityType() == SecurityType.Option ) {
            OptionType type = InstUtils.getOptionType( inst.getSecDef() );
            symbolKey = formOptionsSymbolKey( TLC.instance().getString(), symbolKey, inst.getSecDef().getMaturityMonthYear(), inst.getSecDef().getStrikePrice(), type );
        }

        if ( symbolKey.length() > 0 && inst.getExchange().getExchangeCode() == inst.getPrimaryExchangeCode() ) {
            ExchangeInstrument existing = indexes.getSymbolIndex().get( symbolKey );
            if ( existing != null ) {
                throw new SMTRuntimeException( "Reject Duplicate instrument key, symbol= " + symbolKey + ", oldInst=" + existing + ", newInst=" + inst );
            }
        }

        if ( inst.getSecDef().getSecurityDesc().length() > 0 ) {
            ExchangeInstrument existing = indexes.getSecurityDescIndex().get( inst.getSecDef().getSecurityDesc() );
            if ( existing != null ) {
                throw new SMTRuntimeException( "Reject Duplicate instrument key, secDesc= " + inst.getSecDef().getSecurityDesc() + ", oldInst=" + existing + ", newInst=" + inst );
            }
        }
    }

    private void checkInstKeyNotDuplicate( final InstrumentSecurityDefWrapper inst, final Indexes indexes, final SecurityIDSource idSource, ZString id ) {
        Map<ZString, InstrumentSecurityDefWrapper> indexMap = indexes.getIndex( idSource );

        if ( id == null || id.length() == 0 ) return;

        if ( indexMap != null ) {
            if ( idSource == SecurityIDSource.ISIN ) {
                id = formCompoundInstUniqKey( TLC.instance().getString(), id, inst.getExchange().getExchangeCode(), inst.getCurrency() );
            } else if ( idSource == SecurityIDSource.Symbol ) {
                if ( inst.getSecurityType() == SecurityType.Future ) {
                    FutureExchangeSymbol sym = FutureExchangeSymbol.getVal( inst.getSecurityGroup() );
                    id = formFuturesSymbolKey( TLC.instance().getString(), inst.getSymbol(), inst.getSecDef().getNoLegs(), sym, inst.getSecDef().getMaturityMonthYear() );
                } else if ( inst.getSecurityType() == SecurityType.Option ) {
                    OptionType type = InstUtils.getOptionType( inst.getSecDef() );
                    id = formOptionsSymbolKey( TLC.instance().getString(), id, inst.getSecDef().getMaturityMonthYear(), inst.getSecDef().getStrikePrice(), type );
                }
            }

            if ( id.length() > 0 ) {
                ExchangeInstrument existing = indexMap.get( id );
                if ( existing != null ) {
                    if ( existing != null ) {
                        throw new SMTRuntimeException( "Reject Duplicate instrument key, " + idSource + ", id= " + id + ", oldInst=" + existing + ", newInst=" + inst );
                    }
                }
            }
        }
    }

    private InstrumentSecurityDefWrapperImpl createWrapper( final Exchange ex, final SecurityDefinitionImpl def, final int legCnt ) {
        final CommonInstrument commonInst = getCommonInst( ex, def );

        InstrumentSecurityDefWrapperImpl iw = null;

        if ( def == null || def.getSecurityType() == null || legCnt > 0 ) {

            // if no definition assume its a deriv

            iw = new DerivInstSecDefWrapperImpl( ex, def, commonInst, legCnt );
        } else {

            switch( def.getSecurityType() ) {
            case Cash:
            case CommonStock:
            case Equity:
            case ExchangeTradedCommodity:
            case MunicipalFund:
            case GovTreasuries:
            case PreferredStock:
            case USTreasuryBill:
            case None:
            case Unknown:
            case Index:
                iw = new InstrumentSecurityDefWrapperImpl( ex, def, commonInst );
                break;
            case FX:
            case Warrant:
            case ExchangeTradedFund:
            case ConvBond:
            case CorpBond:
            case ExchangeTradedNote:
            case EuroCertDeposit:
            case ForeignExchangeContract:
            case Future:
            case Option:
            case MultiLeg:
            case MunicipalBond:
                iw = new DerivInstSecDefWrapperImpl( ex, def, commonInst, legCnt );
                break;
            case Strategy:
            default:
                throw new SMTRuntimeException( "Cannot create instrument " + def + " as unsupported by SMT" );
            }
        }

        if ( commonInst != null ) commonInst.attach( iw );

        return iw;
    }

    private FXInstrument doGetFxInstrument( final FXPair fxCode, final Indexes indexes ) {
        FXInstrument fxInst = indexes._ccyFXMap.get( fxCode );
        return fxInst;
    }

    private InstrumentSecurityDefWrapper findByAltId( final SecurityDefinitionImpl def, final Indexes indexes ) {
        if ( def != null ) {
            SecurityAltIDImpl securityAltIDs = (SecurityAltIDImpl) def.getSecurityAltIDs();

            while( securityAltIDs != null ) {

                final SecurityIDSource idSrc = securityAltIDs.getSecurityAltIDSource();

                ZString id = securityAltIDs.getSecurityAltID();

                if ( id.length() > 0 ) {

                    if ( idSrc == def.getSecurityIDSource() && !((ViewString) id).equals( def.getSecurityID() ) ) {
                        throw new SMTRuntimeException( "SecurityDefinition has inconsistent key for idSrc=" + idSrc + ", 22=" + def.getSecurityID() + ", 455=" + id );
                    }

                    InstrumentSecurityDefWrapper inst = null;

                    Map<ZString, InstrumentSecurityDefWrapper> indexMap = indexes.getIndex( idSrc );

                    if ( indexMap != null ) {
                        inst = indexMap.get( id );
                    }

                    if ( inst != null ) return inst;
                }

                securityAltIDs = securityAltIDs.getNext();
            }
        }
        return null;
    }

    private ExchangeInstrument findByUniqueIndex( final ZString securityId, final SecurityIDSource securityIDSource ) {
        switch( securityIDSource ) {
        case UniqueInstId:
            throw new SMTRuntimeException( "getIndex cannot be used for UniqueInstId" );
        case BloombergCode:
            return _bloombergCodeMap.get( securityId );
        case FIGI:
            return _figiMap.get( securityId );
        case InternalString:
            Instrument i = _internalStrKeyMap.get( securityId );
            if ( i != null ) {
                if ( i instanceof ExchangeInstrument ) {
                    return (ExchangeInstrument) i;
                }
                throw new SMTRuntimeException( getComponentId() + " findByUniqueIndex " + securityId.toString() + " is not an ExchangeInstrument" );
            }
            break;
        case StrategyId:
        case ISIN:
        case BloombergTicker:
        case ExchangeSymbol:
        case ExchangeLongId:
        case SecurityDesc:
        case Symbol:
        case DEAD_1:
        case DEAD_2:
        case RIC:
        case Unknown:
            break;
        }

        return null;
    }

    private CommonInstrument getCommonInst( final Exchange ex, final SecurityDefinitionImpl def ) {

        if ( def.getPrimarySecurityExchange() == null ) {
            return null;
        }

        Currency ccy = def.getCurrency();

        if ( ccy == null ) throw new SMTRuntimeException( "Cannot add instrument as missing currency " + def );

        Map<Currency, CommonInstrument> map = _commonInstMap.get( def.getCommonSecurityId() );

        if ( map == null ) {
            map = new HashMap<>( 4 );
            _commonInstMap.put( def.getCommonSecurityId(), map );
        }

        CommonInstrument commonInstrument = map.get( ccy );

        if ( commonInstrument == null ) {

            ParentCompany parentCompany = getParentCompany( def );

            commonInstrument = new CommonInstrumentImpl( parentCompany, def.getPrimarySecurityExchange(), ccy, def.getCommonSecurityId(), def.getEventTimestamp() );

            parentCompany.attach( commonInstrument );
        }

        return commonInstrument;
    }

    private ParentCompany getParentCompany( final SecurityDefinitionImpl def ) {
        final long    parentCompanyId = def.getParentCompanyId();
        ParentCompany pc              = _parentCompanyMap.get( parentCompanyId );

        if ( pc == null ) {
            pc = new ParentCompanyImpl( def.getCompanyName(), parentCompanyId, def.getEventTimestamp() );

            _parentCompanyMap.put( parentCompanyId, pc );
        }

        return pc;
    }

    private InstrumentSecurityDefWrapper getWrapper( SecurityIDSource idSrc, ZString secId, ExchangeCode exchangeCode ) {

        Exchange ex = getExchange( exchangeCode );

        if ( ex == null ) return null;

        Indexes indexes = getExchangeMap( ex, _preSize );

        Map<ZString, InstrumentSecurityDefWrapper> idToInstMap = indexes.getIndex( idSrc );

        if ( idToInstMap == null ) return null;

        InstrumentSecurityDefWrapper inst = idToInstMap.get( secId );

        return inst;
    }

    private void removeInstFromSupplementaryIndexes( InstrumentSecurityDefWrapper inst, Indexes indexes ) {

        SecurityDefinitionImpl def = inst.getSecDef();

        if ( def != null ) {
            SecurityAltIDImpl securityAltIDs = (SecurityAltIDImpl) def.getSecurityAltIDs();

            while( securityAltIDs != null ) {

                SecurityIDSource                           idSource = securityAltIDs.getSecurityAltIDSource();
                ZString                                    id       = securityAltIDs.getSecurityAltID();
                Map<ZString, InstrumentSecurityDefWrapper> indexMap = indexes.getIndex( idSource );

                if ( id.length() > 0 ) {

                    if ( indexMap != null ) {
                        if ( idSource == SecurityIDSource.ISIN ) {
                            id = formCompoundInstUniqKey( _tmpAddKey, id, inst.getExchange().getExchangeCode(), inst.getCurrency() );
                        } else if ( idSource == SecurityIDSource.Symbol ) {
                            if ( inst.getSecurityType() == SecurityType.Future ) {
                                FutureExchangeSymbol sym = FutureExchangeSymbol.getVal( def.getSecurityGroup() );
                                id = formFuturesSymbolKey( _tmpAddKey, def.getSymbol(), def.getNoLegs(), sym, def.getMaturityMonthYear() );
                            } else if ( inst.getSecurityType() == SecurityType.Option ) {
                                OptionType type = InstUtils.getOptionType( def );
                                id = formOptionsSymbolKey( _tmpAddKey, id, def.getMaturityMonthYear(), def.getStrikePrice(), type );
                            }
                        }

                        if ( id.length() > 0 ) {
                            indexMap.remove( id );
                        }
                    }
                }

                securityAltIDs = securityAltIDs.getNext();
            }
        }

        ZString symbolKey = inst.getExchangeSymbol();
        if ( inst.getSecurityType() == SecurityType.Future ) {
            FutureExchangeSymbol sym = FutureExchangeSymbol.getVal( inst.getSecurityGroup() );
            symbolKey = formFuturesSymbolKey( _tmpAddKey, inst.getSymbol(), inst.getSecDef().getNoLegs(), sym, inst.getSecDef().getMaturityMonthYear() );
        } else if ( inst.getSecurityType() == SecurityType.Option ) {
            OptionType type = InstUtils.getOptionType( def );
            symbolKey = formOptionsSymbolKey( _tmpAddKey, symbolKey, inst.getSecDef().getMaturityMonthYear(), inst.getSecDef().getStrikePrice(), type );
        }

        indexes.getSymbolIndex().remove( symbolKey );

        indexes.getIndex( SecurityIDSource.ExchangeSymbol ).remove( inst.getExchangeSymbol() );
    }

    private void setTickType( final InstrumentSecurityDefWrapper inst ) {
        TickType tt = inst.getTickType();

        int tickScaleId = inst.getSecDef().getTickRule();

        if ( Constants.UNSET_INT != tickScaleId && tickScaleId > 0 ) {
            if ( _useUniversalTickScales ) {

                tt = _tickManager.getUniversalTickType( tickScaleId );

            } else {
                ReusableString scaleId = TLC.instance().pop();
                scaleId.setValue( tickScaleId );
                tt = _tickManager.getTickType( inst.getSecDef().getSecurityExchange().getOperatingMIC(), scaleId );
                TLC.instance().pushback( scaleId );

                if ( tt == null ) {
                    tt = _tickManager.getUniversalTickType( tickScaleId );
                }
            }

            if ( tt == null ) {
                throw new SMTRuntimeException( "Unable to process instrument add/update with bad tickScaleId of " + tickScaleId + ", mic=" + inst.getSecDef().getSecurityExchange().getOperatingMIC() );
            }
        } else {
            double fixedTick     = inst.getSecDef().getMinPriceIncrement();
            double displayFactor = inst.getSecDef().getDisplayFactor();

            if ( Utils.hasVal( fixedTick ) ) {
                if ( Utils.hasNonZeroVal( displayFactor ) ) {
                    switch( inst.getSecurityType() ) {

                    case Cash:
                    case CommonStock:
                    case Equity:
                    case PreferredStock:
                    case None:
                    case Unknown:
                        break;
                    default:
                        fixedTick = fixedTick * displayFactor;
                    }
                }

                tt = _tickManager.addFixedTickSize( fixedTick );
            }
        }

        inst.setTickType( tt );
    }

    private void storeExchangeLongId( final Indexes indexes, final InstrumentSecurityDefWrapper inst ) {
        LongMap<InstrumentSecurityDefWrapper> idToInstMap = indexes.getExchangeIdToMap();
        final long                            id          = inst.getExchangeLongId();
        if ( id != Constants.UNSET_LONG ) {
            if ( !idToInstMap.putIfKeyAbsent( id, inst ) ) {
                throw new SMTRuntimeException( "Reject Duplicate instrument key, exchangeLongId= " + id + ", oldInst=" + idToInstMap.get( id ) + ", newInst=" + inst );
            }
        }
    }

    private void storeInternalStrKey( final InstrumentSecurityDefWrapper inst ) {
        _internalStrKeyMap.put( TLC.safeCopy( inst.id() ), inst );
    }

    private void updateSecurityGroups( final InstrumentSecurityDefWrapper inst ) {
        final ZString newSecurityGroup = inst.getSecurityGroup();

        Set<ExchangeInstrument> grp = _secGrpMap.get( newSecurityGroup );

        if ( grp == null ) {
            grp = new LinkedHashSet<>();
            _secGrpMap.put( TLC.safeCopy( newSecurityGroup ), grp );
        }

        grp.add( inst );
    }

    protected final class Indexes {

        // indexes which should be scoped winthin exchange (to avoid exchange clash
        private final LongMap<InstrumentSecurityDefWrapper>      _exchangeLongIdMap;
        private final Map<ZString, InstrumentSecurityDefWrapper> _secDescToInstMap;
        private final Map<ZString, InstrumentSecurityDefWrapper> _symToInstMap;
        private final Map<ZString, InstrumentSecurityDefWrapper> _exchangeSymToInstMap;
        private final Map<ZString, InstrumentSecurityDefWrapper> _bloombergTickerMap;

        private final Map<FXPair, FXInstrument> _ccyFXMap;

        public Indexes( int preSize ) {
            _exchangeLongIdMap    = createLongMap( preSize );
            _symToInstMap         = createMap( preSize );
            _exchangeSymToInstMap = createMap( preSize );
            _secDescToInstMap     = createMap( preSize );
            _bloombergTickerMap   = createMap( preSize );
            _ccyFXMap             = createFXMap();
        }

        public LongMap<InstrumentSecurityDefWrapper> getExchangeIdToMap() {
            return _exchangeLongIdMap;
        }

        // from tag 48 .. future scoped with maturityDate
        public Map<ZString, InstrumentSecurityDefWrapper> getExchangeSymbolIndex() {
            return _exchangeSymToInstMap;
        }

        public Map<ZString, InstrumentSecurityDefWrapper> getIndex( SecurityIDSource src ) {
            if ( src == null ) {
                return _symToInstMap;
            }

            switch( src ) {
            case BloombergTicker:
                return _bloombergTickerMap;
            case ExchangeSymbol:
                return _exchangeSymToInstMap;
            case UniqueInstId:
                throw new SMTRuntimeException( "getIndex cannot be used for UniqueInstId" );
            case ISIN:
                return _isinToInstMap;
            case FIGI:
                return _figiMap;
            case SecurityDesc:
                return _secDescToInstMap;
            case Symbol:
                return _symToInstMap;
            case BloombergCode:
                return _bloombergCodeMap;
            case Unknown:
            case ExchangeLongId:
            case StrategyId:
            case DEAD_1:
            case DEAD_2:
            case RIC:
            default:
                break;
            }

            return null;
        }

        public Map<ZString, InstrumentSecurityDefWrapper> getSecurityDescIndex() {
            return _secDescToInstMap;
        }

        /**
         * from tag 55 .. future scoped with maturityDate
         */
        public Map<ZString, InstrumentSecurityDefWrapper> getSymbolIndex() {
            return _symToInstMap;
        }
    }
}
