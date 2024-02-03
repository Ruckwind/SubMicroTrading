package com.rr.inst;

import com.rr.core.collections.*;
import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.*;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Currency;
import com.rr.core.model.*;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.recovery.json.JSONUtils;
import com.rr.core.thread.RunState;
import com.rr.core.utils.NumberUtils;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.StringUtils;
import com.rr.core.utils.Utils;
import com.rr.model.generated.internal.events.impl.SecurityAltIDImpl;
import com.rr.model.generated.internal.events.impl.SecurityDefinitionImpl;
import com.rr.model.generated.internal.events.impl.SecurityStatusImpl;
import com.rr.model.generated.internal.events.recycle.SecurityStatusRecycler;
import com.rr.model.generated.internal.type.SecurityTradingStatus;
import com.rr.model.generated.internal.type.SecurityUpdateAction;
import com.rr.om.dummy.warmup.DummyInstrument;
import com.rr.om.exchange.ExchangeManager;
import com.rr.om.newmain.SMTContext;

import java.util.*;

import static com.rr.core.utils.StringUtils.UTC;
import static com.rr.inst.HistoricMutatingInstMap.DEAD_NODE;
import static com.rr.inst.HistoricMutatingInstMap.MapEntry;
import static com.rr.inst.InstUtils.*;

/**
 * HistExchInstSecDefStore
 * <p>
 * Provide history of instrument key field changes (eg ISIN, not fields like trading status)
 * <p>
 * Changes to non key fields just updates the latest version
 * <p>
 * An array of maps is used, indexed by the ordinal of the idSrc
 * <p>
 * the nodes are an instance of MapEntry ... initially it will use UnambigousEntry where no exchange code or ccy is required to lookup the instrument series
 * <p>
 * when another series needs to be added to the same key, the node will be upgraded initially to MicSubscopeEntry and if any dups detected on same exchange then
 * <p>
 * will be upgraded to level requiring exchange + ccy for scoping (basically only will be used for small subset of ISIN)
 * <p>
 * each TimeSeries in node is not timeseries for a single instrument ... its the timeSeries for instrument used by the code at that time
 * <p>
 * for more an exchange instrument to reuse a key, first the previous instrument has to be deactivated or the key removed from it with an inst modify
 * <p>
 * IF duplicates detected for a key then the node will be marked as DEAD and all entries removed so no ambiguity can occur on lookups
 *
 * @NOTE tradingItemId will not change nor will the uniqueInstId or the QuanthouseUnique int code
 * @WARNING performs no recycling of insts / secdefs
 */
public class HistExchInstSecDefStore implements HistInstStore {

    private static int                                                          DEFAULT_LEAVE_NODE_ENTRIES            = 4;
    private static int                                                          DEFAULT_CCY_ENTRIES                   = 1;
    private static long                                                         _nextTempSecDefId                     = ClockFactory.get().currentTimeMillis();
    private static boolean                                                      _disableHistoricalFutureSymsThatClash = !Env.isProdOrDevOrUAT();
    private final  Map<ZString, StrategyInstrument>                             _strategyIdMap;
    private        Logger                                                       _log                                  = LoggerFactory.create( HistExchInstSecDefStore.class );
    private        HistoricMutatingInstMap[]                                    _idMaps;
    private        Map<FXPair, Map<ExchangeCode, FXInstrument>>                 _fxMap                                = new HashMap<>( DEFAULT_LEAVE_NODE_ENTRIES, 128 );
    /**
     * non historised maps for non changing ids
     */
    private        LongMap<HistoricalExchangeInstrumentImpl>                    _uniqueLongInstId;
    private        Map<ExchangeCode, LongMap<HistoricalExchangeInstrumentImpl>> _exchangeLongMap;
    private        LongMap<Map<Currency, HistoricalCommonInstrument>>           _commonInstMap;
    private        LongMap<HistoricalParentCompany>                             _parentCompanyMap;
    private        String                                                       _id;
    private        int                                                          _preSize;
    private        List<TimeSeries<ExchInstSecDefWrapperTSEntry>>               _tmpNodeList                          = new ArrayList<>();
    private        TickManager                                                  _tickManager;
    private        boolean                                                      _useUniversalTickScales               = false;
    private        RunState                                                     _runState;
    private        ReusableString                                               _tmpFutureKey                         = new ReusableString( 16 );
    private        SecurityStatusRecycler                                       _securityStatusRecycler;
    private        boolean                                                      _restrictDerivISIN                    = true; // with QH ISIN are non unique in Futures / Options
    private        Map<ZString, Set<HistoricalExchangeInstrumentImpl>>          _secGrpMap                            = new LinkedHashMap<>();

    public static void setDisableFutureBBTicker( final boolean disableFutureBBTicker ) { HistExchInstSecDefStore._disableHistoricalFutureSymsThatClash = disableFutureBBTicker; }

    private static synchronized long nextSecDefId() {
        return ++_nextTempSecDefId;
    }

    public HistExchInstSecDefStore( String id, int preSize ) {
        _id      = id;
        _preSize = preSize;

        _uniqueLongInstId     = createLongMap( preSize );
        _exchangeLongMap      = createMap( preSize );
        _strategyIdMap        = createMap( 64 );

        _commonInstMap    = createLongMap( preSize );
        _parentCompanyMap = createLongMap( 256 );

        SuperpoolManager sp = SuperpoolManager.instance();
        _securityStatusRecycler = sp.getRecycler( SecurityStatusRecycler.class, SecurityStatusImpl.class );

        _idMaps = new HistoricMutatingInstMap[ SecurityIDSource.values().length ];

        for ( SecurityIDSource src : SecurityIDSource.values() ) {
            switch( src ) {
            case UniqueInstId:
            case StrategyId:
            case ExchangeLongId:
                break;
            case Unknown:
                break;
            default:
                _idMaps[ src.ordinal() ] = createHistMap( preSize, src );
                break;
            }
        }
    }

    @Override public final boolean add( SecurityDefinitionImpl def ) {

        InstUtils.secDefOverrides( def );

        long uniqueInstId = def.getUniqueInstId();

        if ( Utils.isNull( uniqueInstId ) ) {
            if ( _log.isEnabledFor( Level.trace ) ) {
                _log.log( Level.trace, id() + " generating TEMPORARY uniqueInstId " + def.toString() );
            }
            uniqueInstId = nextSecDefId();
            def.setUniqueInstId( uniqueInstId );
        }

        if ( Utils.isNull( uniqueInstId ) ) {
            _log.warn( id() + " add cant add secdef missing uniqueInstId " + def.toString() );
            return false;
        }

        if ( Utils.isNull( def.getEventTimestamp() ) ) {
            def.setEventTimestamp( 0 );
        }

        if ( def.getCurrency() == null ) def.setCurrency( Currency.Unknown );

        Exchange ex = getExchange( def.getSecurityExchange() );

        HistoricalExchangeInstrumentImpl instTimeSeries = _uniqueLongInstId.get( uniqueInstId );

        boolean isDeleteSecDef = def.getSecurityUpdateAction() == SecurityUpdateAction.Delete;
        if ( instTimeSeries == null ) {

            if ( isDeleteSecDef ) return false;

            setInstVersionTimeToStartTimeIfAvail( def );

            instTimeSeries = createTS( def );

            if ( _uniqueLongInstId.putIfKeyAbsent( uniqueInstId, instTimeSeries ) ) {

                try {
                    ExchInstSecDefWrapperTSEntry newInst = createNewVersion( ex, def, instTimeSeries );

                    instTimeSeries.add( newInst );

                    updateIndices( instTimeSeries, null, newInst );

                    if ( _log.isEnabledFor( Level.xtrace ) ) _log.log( Level.xtrace, getComponentId() + " ADD new inst " + JSONUtils.objectToJSON( def ) );

                } catch( Exception e ) {

                    _uniqueLongInstId.remove( uniqueInstId );

                    throw e;
                }

            } else {
                throw new SMTRuntimeException( getComponentId() + " concurrency exception adding " + def );
            }
        } else {
            if ( instTimeSeries.isNewVersionRequired( def ) ) {

                ExchInstSecDefWrapperTSEntry prev = instTimeSeries.latest();

                setInstVersionTimeToStartTimeIfAvail( def );

                if ( prev.getStartTimestamp() > def.getStartTimestamp() ) {
                    _log.info( getComponentId() + " unable to add out of sequence instrument, time order must be from old to new" +
                               ", EXISTING : " + JSONUtils.objectToJSON( prev.getSecDef() ) +
                               ", UPDATE   : " + JSONUtils.objectToJSON( def ) );

                    return false;
                }

                if ( !Utils.isNull( def.getCommonSecurityId() ) && prev.getCommonInstrument().getCommonInstrumentId() != def.getCommonSecurityId() ) {
                    removeFromOldCommonInstrument( prev, def.getEventTimestamp() );
                }

                prev.setEndTimestamp( def.getEventTimestamp() );

                ExchInstSecDefWrapperTSEntry newInst = createNewVersion( ex, def, instTimeSeries );

                prev.setNext( newInst );
                newInst.setPrev( prev );

                instTimeSeries.add( newInst );
                updateIndices( instTimeSeries, prev, newInst );

                if ( _log.isEnabledFor( Level.trace ) ) {
                    _log.log( Level.trace, getComponentId() + " ADD new version " + JSONUtils.objectToJSON( def ) );
                }

            } else {
                ExchInstSecDefWrapperTSEntry latest = instTimeSeries.latest();

                long origTime = latest.getEventTimestamp();

                latest.setSecurityDefinition( def );

                latest.getSecDef().setStartTimestamp( latest.getStartTimestamp() ); // retain original creation time
                latest.getSecDef().setEventTimestamp( origTime ); // retain original creation time

                if ( _log.isEnabledFor( Level.trace ) ) {
                    _log.log( Level.trace, getComponentId() + " ADD updated " + JSONUtils.objectToJSON( def ) );
                }
            }
        }

        return true;
    }

    @Override public void add( final Instrument inst ) {
        if ( inst instanceof FXInstrument ) {
            FXInstrument def = (FXInstrument) inst;

            Map<ExchangeCode, FXInstrument> fxMap = _fxMap.computeIfAbsent( def.getFXPair(), ( k ) -> new HashMap<>() );

            fxMap.putIfAbsent( def.getExchange().getExchangeCode(), def );

        } else if ( inst instanceof StrategyInstrument ) {
            StrategyInstrument si = (StrategyInstrument) inst;

            ZString key = TLC.safeCopy( si.getComponentId() );

            final Instrument existing = _strategyIdMap.putIfAbsent( key, si );

            if ( existing != null && existing != inst ) {
                throw new SMTRuntimeException( getComponentId() + " attempt to register duplicate stratId " + key + " for different strat instances" );
            } else {
                _log.info( getComponentId() + " register strategy instrument with stratId of " + key );
            }
        } else {
            throw new SMTRuntimeException( getComponentId() + " unable to register instrument " + inst.toString() );
        }
    }

    @Override public final boolean allowIntradayAddition() {
        return true;
    }

    @Override public final void remove( SecurityDefinitionImpl def ) {
        def.setSecurityUpdateAction( SecurityUpdateAction.Delete );

        if ( Utils.isNull( def.getDeadTimestamp() ) ) {
            def.setDeadTimestamp( ClockFactory.get().currentTimeMillis() );
        }

        if ( add( def ) ) {
            _log.info( "deprecated instrument " + InstUtils.getUniqInstId( def ) );
        } else {
            _log.info( "failed to deprecate instrument " + InstUtils.getUniqInstId( def ) );
        }
    }

    /**
     * Update Status of instrument ... NOT historised !
     *
     * @param status
     * @return
     */
    @Override public final boolean updateStatus( SecurityStatusImpl status ) {

        boolean changed = false;

        HistExchInstSecDefWrapperTS inst = getExchInstTS( status.getSecurityID(), status.getSecurityIDSource(), status.getSecurityExchange() );

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
            _log.warn( getComponentId() + " UNABLE TO FIND INSTRUMENT TO PROCESS SecurityStatus " + status.toString() );

            _securityStatusRecycler.recycle( status );
        }

        return changed;
    }

    @Override public void getAllCommonInsts( final Set<CommonInstrument> instruments ) {
        _commonInstMap.forEach( ( m ) -> m.values().forEach( instruments::add ) );
    }

    @Override public final void getAllExchInsts( Set<ExchangeInstrument> instruments ) {
        _uniqueLongInstId.forEach( instruments::add );
    }

    @Override public void getAllFXInsts( final Set<FXInstrument> instruments )          { _fxMap.values().forEach( ( map ) -> map.values().forEach( ( inst ) -> instruments.add( inst ) ) ); }

    @Override public void getAllStratInsts( final Set<StrategyInstrument> instruments ) { _strategyIdMap.values().forEach( instruments::add ); }

    @Override public Instrument getByInstId( final ZString id ) {
        Instrument inst = _strategyIdMap.get( id );

        if ( inst == null ) {
            inst = getInst( id, SecurityIDSource.InternalString, null );
        }

        return inst;
    }

    @Override public final CommonInstrument getCommonInstrument( long commonInstrumentId, Currency ccy ) {
        Map<Currency, HistoricalCommonInstrument> map = _commonInstMap.get( commonInstrumentId );
        return (map != null) ? map.get( ccy ) : null;
    }

    @Override public final void getCommonInstruments( long commonInstrumentId, List<CommonInstrument> dest ) {
        Map<Currency, HistoricalCommonInstrument> map = _commonInstMap.get( commonInstrumentId );

        for ( HistoricalCommonInstrument hi : map.values() ) {
            dest.add( hi.latest() );
        }
    }

    @Override public final ExchangeInstrument getDummyExchInst() {
        return DummyInstrument.DUMMY;
    }

    @Override public final ExchangeInstrument getExchInst( ZString securityId, SecurityIDSource securityIDSource, ExchangeCode exchangeCode ) {
        if ( exchangeCode == ExchangeCode.FCQG ) {
            FXPair pair = FXPair.get( securityId );
            if ( pair != null ) {
                return getFXInstrument( pair, exchangeCode );
            }
        }

        return getExchInstTS( securityId, securityIDSource, exchangeCode );
    }

    @Override public final ExchangeInstrument getExchInstByExchangeLong( ExchangeCode exchangeCode, long instrumentId ) {
        if ( instrumentId == ExchangeInstrument.DUMMY_INSTRUMENT_LONG_ID ) return DummyInstrument.DUMMY;

        return _exchangeLongMap.computeIfAbsent( exchangeCode, ( k ) -> new LongHashMap<>( 256, 0.75f ) ).get( instrumentId );
    }

    @Override public final ExchangeInstrument getExchInstByIsin( ZString isin, ExchangeCode securityExchange, Currency currency ) {
        return getExchInstTS( isin, SecurityIDSource.ISIN, securityExchange, currency );
    }

    @Override public final ExchangeInstrument getExchInstByUniqueCode( SecurityIDSource src, long instrumentId ) {

        if ( src == SecurityIDSource.UniqueInstId ) {
            return _uniqueLongInstId.get( instrumentId );
        }

        return null;
    }

    @Override public final ExchangeInstrument getExchInstByUniqueInstId( long uniqueInstId ) {
        return _uniqueLongInstId.get( uniqueInstId );
    }

    @Override public void getExchInsts( Set<ExchangeInstrument> insts, ZString securityId, SecurityIDSource securityIDSource ) {

        insts.clear();

        HistoricMutatingInstMap index = getMapForSearch( securityIDSource );

        MapEntry e = index.get( securityId );

        if ( e == DEAD_NODE )
            throw new AmbiguousKeyRuntimeException( "DEAD_NODE : (f) Attempt to search for instrument on key matching multiple instruments idSrc=" + securityIDSource + ", secId=" + securityId, securityIDSource, securityId.toString() );

        if ( e != null ) {
            e.forEach( ( i ) -> {
                ExchInstSecDefWrapperTSEntry v = i.getAt( ClockFactory.get().currentTimeMillis() );

                if ( v != null ) insts.add( v.getSeries() );
            } );
        }

        return;
    }

    @Override public final void getExchInsts( Set<ExchangeInstrument> instruments, Exchange ex ) {
        _uniqueLongInstId.forEach( ( i ) -> { if ( i.getExchange() == ex ) instruments.add( i ); } );
    }

    @Override public void getExchInstsBySecurityGrp( final ZString securityGrp, ExchangeCode mic, final List<ExchangeInstrument> dest ) {
        if ( securityGrp != null && securityGrp.length() > 0 ) {
            final Set<HistoricalExchangeInstrumentImpl> grp = _secGrpMap.get( securityGrp );

            if ( grp != null ) {
                for ( ExchangeInstrument ei : grp ) {
                    if ( ei.getExchange().getExchangeCode() == mic ) {
                        dest.add( ei );
                    }
                }
            }
        }
    }

    @Override public final FXInstrument getFXInstrument( FXPair fxPair, ExchangeCode code ) {

        if ( fxPair == null ) return null;

        Map<ExchangeCode, FXInstrument> fxMap = _fxMap.computeIfAbsent( fxPair, ( k ) -> new HashMap<>() );

        return fxMap.get( code );
    }

    @Override public final ExchDerivInstrument getFutureInstrumentBySym( FutureExchangeSymbol symbol, int maturityDateYYYYMM, ExchangeCode exchangeCode ) {
        Exchange ex = getExchange( exchangeCode );

        HistoricMutatingInstMap index = getMapForSearch( SecurityIDSource.Symbol );

        synchronized( _tmpFutureKey ) {
            formFuturesSymbolKey( _tmpFutureKey, null, 0, symbol, maturityDateYYYYMM );

            MapEntry e = index.get( _tmpFutureKey );

            if ( e == DEAD_NODE ) throw new AmbiguousKeyRuntimeException( "(m) Attempt to search for future instrument on ambigious key matching multiple instruments key=" + _tmpFutureKey, SecurityIDSource.Symbol, symbol.toString() );

            if ( e == null ) return null;

            ExchInstSecDefWrapperTSEntry latest = e.getAt( Constants.UNSET_LONG );

            if ( exchangeCode != null && exchangeCode != ExchangeCode.UNKNOWN && latest.getSecDef().getSecurityExchange() != exchangeCode && latest.getSecDef().getPrimarySecurityExchange() != exchangeCode ) {
                throw new AmbiguousKeyRuntimeException( getComponentId() + " (n) found instrument but " + latest.getSecDef().getSecurityExchange() + " didnt match requested exchangeCode " + exchangeCode + ", inst=" + latest,
                                                        SecurityIDSource.Symbol, symbol.toString() );
            }

            return (ExchDerivInstrument) latest.getSeries();
        }
    }

    @Override public void getFuturesBySecurityGrp( final FutureExchangeSymbol symbol, final List<ExchangeInstrument> dest ) {
        if ( symbol != null ) {
            final Set<HistoricalExchangeInstrumentImpl> grp = _secGrpMap.get( symbol.getPhysicalSym() );

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

    @Override public final ExchDerivInstrument getOptionInstrumentBySym( ZString symbol, int maturityDateYYYYMM, double strikePrice, OptionType type, ExchangeCode exchangeCode ) {
        Exchange ex = getExchange( exchangeCode );

        HistoricMutatingInstMap index = getMapForSearch( SecurityIDSource.Symbol );

        synchronized( _tmpFutureKey ) {
            formOptionsSymbolKey( _tmpFutureKey, symbol, maturityDateYYYYMM, strikePrice, type );

            MapEntry e = index.get( _tmpFutureKey );

            if ( e == DEAD_NODE ) throw new AmbiguousKeyRuntimeException( "(o) Attempt to search for option instrument on ambiguous key matching multiple instruments key=" + _tmpFutureKey, SecurityIDSource.Symbol, symbol.toString() );

            ExchInstSecDefWrapperTSEntry latest = e.getAt( Constants.UNSET_LONG );

            if ( exchangeCode != null && exchangeCode != ExchangeCode.UNKNOWN && latest.getSecDef().getSecurityExchange() != exchangeCode && latest.getSecDef().getPrimarySecurityExchange() != exchangeCode ) {
                throw new AmbiguousKeyRuntimeException( getComponentId() + " (p) found instrument but " + latest.getSecDef().getSecurityExchange() + " didnt match requested exchangeCode " + exchangeCode + ", inst=" + latest,
                                                        SecurityIDSource.Symbol, symbol.toString() );
            }

            return (ExchDerivInstrument) latest.getSeries();
        }
    }

    @Override public final ParentCompany getParentCompany( long parentCompanyId ) {
        return _parentCompanyMap.get( parentCompanyId );
    }

    @Override public final void setUseUniversalTickScales( boolean useUniversalTickScales ) {
        _useUniversalTickScales = useUniversalTickScales;
    }

    @Override public final void getAllInstruments( Set<ExchInstSecDefWrapperTSEntry> instruments, long atTimestamp ) {
        _uniqueLongInstId.forEach( ( i ) -> instruments.add( i.getAt( atTimestamp ) ) );
    }

    @Override public final CommonInstrument getCommonInstrument( long commonInstrumentId, Currency ccy, long atTimestamp ) {
        Map<Currency, HistoricalCommonInstrument> cimap = _commonInstMap.get( commonInstrumentId );

        if ( cimap == null ) return null;

        HistoricalCommonInstrument ccymap = cimap.get( ccy );

        return (ccymap != null) ? ccymap.getAt( atTimestamp ) : null;
    }

    @Override public final TimeSeries<CommonInstrument> getCommonInstrumentSeries( long commonInstrumentId, Currency ccy ) {
        Map<Currency, HistoricalCommonInstrument> map = _commonInstMap.get( commonInstrumentId );
        return (map != null) ? map.get( ccy ) : null;
    }

    @Override public final void getCommonInstruments( long commonInstrumentId, long atTimestamp, List<CommonInstrument> dest ) {

        Map<Currency, HistoricalCommonInstrument> map = _commonInstMap.get( commonInstrumentId );

        for ( HistoricalCommonInstrument hi : map.values() ) {
            dest.add( hi.getAt( atTimestamp ) );
        }
    }

    @Override public final ExchInstSecDefWrapperTSEntry getExchInstAt( ZString securityId, SecurityIDSource securityIDSource, long atTimestamp ) {
        HistoricMutatingInstMap index = getMapForSearch( securityIDSource );

        MapEntry e = index.get( securityId );

        if ( e == DEAD_NODE )
            throw new AmbiguousKeyRuntimeException( "(g) Attempt to search for instrument on key matching multiple instruments idSrc=" + securityIDSource + ", secId=" + securityId, securityIDSource, securityId.toString() );

        if ( e == null ) return null;

        return e.getAt( atTimestamp );
    }

    @Override public final ExchInstSecDefWrapperTSEntry getExchInstAt( ZString securityId, SecurityIDSource securityIDSource, ExchangeCode exchangeCode, long atTimestamp ) {
        HistoricMutatingInstMap index = getMapForSearch( securityIDSource );

        MapEntry e = index.get( securityId );

        if ( e == DEAD_NODE ) throw new AmbiguousKeyRuntimeException( "(h) Attempt to search for instrument on key matching multiple instruments idSrc=" + securityIDSource + ", secId=" + securityId +
                                                                      ", exchangeCode=" + exchangeCode, securityIDSource, securityId.toString() );

        if ( e == null ) return null;

        ExchInstSecDefWrapperTSEntry tse = e.getAt( exchangeCode, atTimestamp );

        if ( tse == null ) {
            throw new SMTRuntimeException( getComponentId() + " (i) no valid instrument for " + securityId.toString() + " at " + UTC( atTimestamp ) );
        }

        if ( exchangeCode != null && exchangeCode != ExchangeCode.UNKNOWN && tse.getSecDef().getSecurityExchange() != exchangeCode && tse.getSecDef().getPrimarySecurityExchange() != exchangeCode ) {
            throw new AmbiguousKeyRuntimeException( getComponentId() + " (i) found instrument but " + tse.getSecDef().getSecurityExchange() + " didnt match requested exchangeCode " + exchangeCode + ", inst=" + tse, securityIDSource,
                                                    securityId.toString() );
        }

        return tse;
    }

    @Override public final ExchInstSecDefWrapperTSEntry getExchInstAt( ZString securityId, SecurityIDSource securityIDSource, ExchangeCode exchangeCode, Currency currency, long atTimestamp ) {
        HistoricMutatingInstMap index = getMapForSearch( securityIDSource );

        MapEntry e = index.get( securityId );

        if ( e == DEAD_NODE ) throw new AmbiguousKeyRuntimeException( "(j) Attempt to search for instrument on key matching multiple instruments idSrc=" + securityIDSource + ", secId=" + securityId +
                                                                      ", exchangeCode=" + exchangeCode + ", ccy=" + currency, securityIDSource, securityId.toString() );

        if ( e == null ) return null;

        ExchInstSecDefWrapperTSEntry tse = e.getAt( exchangeCode, currency, atTimestamp );

        if ( tse == null ) {
            throw new SMTRuntimeException( getComponentId() + " (i) no valid instrument for " + securityId.toString() + " at " + UTC( atTimestamp ) );
        }

        if ( exchangeCode != null && exchangeCode != ExchangeCode.UNKNOWN && tse.getSecDef().getSecurityExchange() != exchangeCode && tse.getSecDef().getPrimarySecurityExchange() != exchangeCode ) {
            throw new AmbiguousKeyRuntimeException( getComponentId() + " (k) found instrument but " + tse.getSecDef().getSecurityExchange() + " didnt match requested exchangeCode " + exchangeCode + ", inst=" + tse, securityIDSource,
                                                    securityId.toString() );
        }

        if ( currency != null && currency != Currency.Unknown && tse.getSecDef().getCurrency() != currency ) {
            throw new AmbiguousKeyRuntimeException( getComponentId() + " (l) found instrument but " + tse.getSecDef().getCurrency() + " didnt match requested currency " + currency + ", inst=" + tse, securityIDSource,
                                                    securityId.toString() );
        }

        return tse;
    }

    @Override public final TimeSeries<ExchInstSecDefWrapperTSEntry> getExchInstSeriesByUniqueInstId( long uniqueInstId ) {
        return _uniqueLongInstId.get( uniqueInstId );
    }

    @Override public final HistExchInstSecDefWrapperTS getExchInstTS( long longKey, SecurityIDSource src ) {

        if ( src == SecurityIDSource.UniqueInstId ) {
            return _uniqueLongInstId.get( longKey );
        }

        return null;
    }

    @Override public final HistExchInstSecDefWrapperTS getExchInstTS( final long instrumentId, final ExchangeCode exchangeCode ) {
        return _exchangeLongMap.computeIfAbsent( exchangeCode, ( k ) -> new LongHashMap<>( 256, 0.75f ) ).get( instrumentId );
    }

    @Override public final HistExchInstSecDefWrapperTS getExchInstTS( ZString securityId, SecurityIDSource securityIDSource ) {
        HistoricMutatingInstMap index = getMapForSearch( securityIDSource );

        MapEntry e = index.get( securityId );

        if ( e == null ) return null;

        ExchInstSecDefWrapperTSEntry latest = e.getAt( Constants.UNSET_LONG );

        if ( latest == null ) {
            latest = e.getAt( ClockFactory.get().currentTimeMillis() );
        }

        return latest.getSeries();
    }

    @Override public final HistExchInstSecDefWrapperTS getExchInstTS( ZString securityId, SecurityIDSource idSrc, ExchangeCode exchangeCode ) {

        if ( idSrc == SecurityIDSource.UniqueInstId ) {
            return _uniqueLongInstId.get( NumberUtils.parseLong( securityId ) );
        }

        HistoricMutatingInstMap index = getMapForSearch( idSrc );

        HistoricMutatingInstMap.MapEntry e = index.get( securityId );

        if ( e == DEAD_NODE ) throw new AmbiguousKeyRuntimeException( "(a) Attempt to search for instrument on key matching multiple instruments idSrc=" + idSrc + ", secId=" + securityId +
                                                                      ", exchangeCode=" + exchangeCode, idSrc, securityId.toString() );

        if ( e == null ) return null;

        ExchInstSecDefWrapperTSEntry latest = e.getAt( exchangeCode, Constants.UNSET_LONG );

        if ( latest == null ) {
            latest = e.getAt( exchangeCode, ClockFactory.get().currentTimeMillis() );
        }

        if ( exchangeCode != null && exchangeCode != ExchangeCode.UNKNOWN && latest.getSecDef().getSecurityExchange() != exchangeCode && latest.getSecDef().getPrimarySecurityExchange() != exchangeCode ) {
            _log.log( Level.debug, getComponentId() + " (b) found instrument but " + latest.getSecDef().getSecurityExchange() + " didnt match requested exchangeCode " + exchangeCode + ", inst=" + latest.toString() +
                                   " " + idSrc.name() + " " + securityId.toString() );

            return null;
            // throw new AmbiguousKeyRuntimeException( getComponentId() + " (b) found instrument but " + latest.getSecDef().getSecurityExchange() + " didnt match requested exchangeCode " + exchangeCode + ", inst=" + latest, idSrc, securityId.toString() );
        }

        return latest.getSeries();
    }

    @Override public final HistExchInstSecDefWrapperTS getExchInstTS( ZString securityId, SecurityIDSource securityIDSource, ExchangeCode exchangeCode, Currency currency ) {
        HistoricMutatingInstMap index = getMapForSearch( securityIDSource );

        MapEntry e = index.get( securityId );

        if ( e == DEAD_NODE ) throw new AmbiguousKeyRuntimeException( "(c) Attempt to search for instrument on key matching multiple instruments idSrc=" + securityIDSource + ", secId=" + securityId +
                                                                      ", exchangeCode=" + exchangeCode + ", ccy=" + currency, securityIDSource, securityId.toString() );

        if ( e == null ) return null;

        ExchInstSecDefWrapperTSEntry latest = e.getAt( exchangeCode, currency, Constants.UNSET_LONG );

        if ( latest == null ) {
            latest = e.getAt( exchangeCode, currency, ClockFactory.get().currentTimeMillis() );
        }

        if ( exchangeCode != null && exchangeCode != ExchangeCode.UNKNOWN && latest.getSecDef().getSecurityExchange() != exchangeCode && latest.getSecDef().getPrimarySecurityExchange() != exchangeCode ) {
            _log.log( Level.debug, getComponentId() + " (d) found instrument but " + latest.getSecDef().getSecurityExchange() + " didnt match requested exchangeCode " + exchangeCode + ", inst=" + latest.toString() +
                                   " " + securityIDSource.name() + " " + securityId.toString() );
            return null;
            // throw new AmbiguousKeyRuntimeException( getComponentId() + " (d) found instrument but " + latest.getSecDef().getSecurityExchange() + " didnt match requested exchangeCode " + exchangeCode + ", inst=" + latest, securityIDSource, securityId.toString() );
        }

        if ( currency != null && currency != Currency.Unknown && latest.getSecDef().getCurrency() != currency ) {
            _log.log( Level.debug, getComponentId() + " (e) found instrument but " + latest.getSecDef().getCurrency() + " didnt match requested currency " + currency + ", inst=" + latest.toString() + " " +
                                   securityIDSource.name() + " " + securityId.toString() );
            return null;
            // throw new AmbiguousKeyRuntimeException( getComponentId() + " (e) found instrument but " + latest.getSecDef().getCurrency() + " didnt match requested currency " + currency + ", inst=" + latest, securityIDSource, securityId.toString() );
        }

        return latest.getSeries();
    }

    @Override public final void getInstruments( Set<ExchInstSecDefWrapperTSEntry> instruments, Exchange ex, long atTimestamp ) {
        _uniqueLongInstId.forEach( ( i ) -> { if ( i.getExchange() == ex ) instruments.add( i.getAt( atTimestamp ) ); } );
    }

    @Override public final ParentCompany getParentCompany( long parentCompanyId, long atTimestamp ) {
        HistoricalParentCompany pc = _parentCompanyMap.get( parentCompanyId );
        return (pc != null) ? pc.getAt( atTimestamp ) : null;
    }

    @Override public final TimeSeries<ParentCompany> getParentCompanySeries( long parentCompanyId ) {
        return _parentCompanyMap.get( parentCompanyId );
    }

    @Override public final String getComponentId() { return _id; }

    @Override public final RunState getRunState()  { return _runState; }

    @Override public void init( SMTStartContext c, CreationPhase phase ) {
        if ( !(c instanceof SMTContext) ) {
            throw new SMTRuntimeException( "BaseInstrumentSecDefStore requires SMTContext" );
        }
        SMTContext ctx = (SMTContext) c;
        _tickManager = ctx.getTickManager();
    }

    @Override public final RunState setRunState( RunState newState )  { return _runState = newState; }

    public Logger getLogger()                                         { return _log; }

    public final TickManager getTickManager()                         { return _tickManager; }

    public final void setTickManager( final TickManager tickManager ) { _tickManager = tickManager; }

    private void addKey( SecurityIDSource idSrc, ZString keyVal, HistoricalExchangeInstrumentImpl instTimeSeries, ExchInstSecDefWrapperTSEntry latest ) {

        switch( idSrc ) {
        case UniqueInstId:
            // added manually
            break;
        case ExchangeLongId:
            final long id = StringUtils.parseLong( keyVal );
            if ( id != Constants.UNSET_LONG ) {
                final LongMap<HistoricalExchangeInstrumentImpl> idToInstMap = _exchangeLongMap.computeIfAbsent( latest.getExchange().getExchangeCode(), ( k ) -> new LongHashMap<>( 256, 0.75f ) );
                if ( !idToInstMap.putIfKeyAbsent( id, instTimeSeries ) ) {
                    throw new SMTRuntimeException( "Reject Duplicate instrument key, exchangeLongId= " + id + ", oldInst=" + idToInstMap.get( id ) + ", newInst=" + latest );
                }
            }
            break;
        case ISIN: {
            HistoricMutatingInstMap map = _idMaps[ idSrc.ordinal() ];

            if ( map == null ) return; // unsupported index

            if ( _restrictDerivISIN && (latest.getSecurityType() == SecurityType.Future || latest.getSecurityType() == SecurityType.Option) ) {
                return; // IGNORE ISIN AS NON UNIQUE
            }
            map.addKey( idSrc, keyVal, instTimeSeries, latest );
            break;
        }
        case Symbol: {
            HistoricMutatingInstMap map = _idMaps[ idSrc.ordinal() ];

            if ( map == null ) return; // unsupported index

            final SecurityDefinitionImpl secDef = latest.getSecDef();
            if ( latest.getSecurityType() == SecurityType.Future ) {
                FutureExchangeSymbol sym = FutureExchangeSymbol.getVal( secDef.getSecurityGroup() );
                keyVal = formFuturesSymbolKey( TLC.instance().getString(), secDef.getSymbol(), secDef.getNoLegs(), sym, secDef.getMaturityMonthYear() );
                map.addKey( idSrc, keyVal, instTimeSeries, latest );
            } else if ( latest.getSecurityType() == SecurityType.Option ) {
                OptionType type = InstUtils.getOptionType( secDef );
                keyVal = formOptionsSymbolKey( TLC.instance().getString(), keyVal, secDef.getMaturityMonthYear(), secDef.getStrikePrice(), type );
                map.addKey( idSrc, keyVal, instTimeSeries, latest );
            } else {
                // NOT SUPPORTED AS NON UNIQUE eg JEN on CHIX (is XBRU and XETR different companies)
                // may add back and force exchange to always be primary in future
            }
            break;
        }
        case BloombergTicker: {
            if ( latest.getSecurityType() == SecurityType.Future && !Env.isProdOrUAT() &&
                 (_disableHistoricalFutureSymsThatClash && (
                         latest.getSecDef().isFlagSet( MsgFlag.Historical ) ||
                         latest.getSecDef().getSecDefSpecialType() == SecDefSpecialType.CMEFuture)) ) {

                // dont add bloomberg ticker for futures its very non unique ... blows up quickly with historic sec defs
                return;
            }
            HistoricMutatingInstMap map = _idMaps[ idSrc.ordinal() ];

            if ( map == null ) return; // unsupported index

            map.addKey( idSrc, keyVal, instTimeSeries, latest );
            break;
        }
        case InternalString:
        case SecurityDesc:
        case ExchangeSymbol:
        case BloombergCode:
        case PrimaryBloombergCode:
        case PrimaryMarketSymbol:
        case FIGI:
        case DEAD_1:
        case DEAD_2:
        case RIC:
        case Unknown:
        default:
            HistoricMutatingInstMap map = _idMaps[ idSrc.ordinal() ];

            if ( map == null ) return; // unsupported index

            map.addKey( idSrc, keyVal, instTimeSeries, latest );
            break;
        }
    }

    private void checkIdChange( ExchInstSecDefWrapperTSEntry latest, HistoricalExchangeInstrumentImpl instTimeSeries, ExchInstSecDefWrapperTSEntry prev ) {

        if ( !latest.id().equals( prev.id() ) ) {
            ReusableString key = TLC.strPop();

            key.copy( prev.id() );

            removeKey( SecurityIDSource.InternalString, key, instTimeSeries, prev );

            key.copy( latest.id() );

            addKey( SecurityIDSource.InternalString, key, instTimeSeries, latest );

            // dont recycle key as its now in map
        }
    }

    private void checkKeyChange( SecurityIDSource securityIDSource, ZString securityID, ExchInstSecDefWrapperTSEntry latest, HistoricalExchangeInstrumentImpl instTimeSeries, ExchInstSecDefWrapperTSEntry prev ) {
        ReusableString latestKeyValue = TLC.instance().pop();

        latest.getKey( securityIDSource, latestKeyValue );

        boolean matched = latestKeyValue.equals( securityID );

        if ( !matched ) {
            removeKey( securityIDSource, securityID, instTimeSeries, prev );
            addKey( securityIDSource, latestKeyValue, instTimeSeries, latest );

            // dont recycle key as its now in map
        } else {
            TLC.instance().pushback( latestKeyValue );
        }
    }

    private <T> HistoricMutatingInstMap createHistMap( int preSize, SecurityIDSource idSrc ) { return new HistoricMutatingInstMap( preSize, idSrc ); }

    private <T> IntMap<T> createIntMap( int preSize )                                        { return new IntHashMap<>( preSize, 0.75f ); }

    private <T> LongMap<T> createLongMap( int preSize )                                      { return new LongHashMap<>( preSize, 0.75f ); }

    private <K, V> Map<K, V> createMap( final int preSize )                                  { return new HashMap<>( preSize ); }

    private ExchInstSecDefWrapperTSEntry createNewVersion( Exchange ex, SecurityDefinitionImpl def, HistoricalExchangeInstrumentImpl instTimeSeries ) {

        boolean createDeriv = shouldCreateDeriv( def );

        CommonInstrument commonInst = newCommonInstrumentVersion( ex, def, def.getStartTimestamp() );

        ExchInstSecDefWrapperTSEntry iw = null;

        if ( createDeriv ) {
            int legCnt = def.getNoLegs();

            if ( legCnt == Constants.UNSET_INT ) legCnt = 0;

            iw = new DerivInstSecDefVersionWrapperImpl( ex, def, commonInst, legCnt, instTimeSeries );

        } else {

            iw = new InstrumentSecurityDefVersionWrapperImpl( ex, def, commonInst, instTimeSeries );

        }

        long startTimestamp = def.getStartTimestamp();

        if ( Utils.isNullOrZero( startTimestamp ) ) {
            startTimestamp = def.getEventTimestamp();
        }

        iw.setStartTimestamp( startTimestamp );

        iw.setEndTimestamp( def.getEndTimestamp() );

        if ( def.getSecurityUpdateAction() == SecurityUpdateAction.Delete ) {

            long endTimestamp = def.getStartTimestamp();
            iw.setEndTimestamp( endTimestamp );

            if ( Utils.isNullOrZero( def.getDeadTimestamp() ) ) {
                def.setDeadTimestamp( endTimestamp );
            }

        } else { // only attach to new common inst version IF not delete

            if ( commonInst != null ) {
                commonInst.attach( iw );
            }
        }

        setTickType( iw );

        return iw;
    }

    private HistoricalExchangeInstrumentImpl createTS( final SecurityDefinitionImpl def ) {
        boolean createDeriv = shouldCreateDeriv( def );
        // if unsure creates deriv holder
        return (createDeriv) ? new HistoricalExchangeDerivInstrumentImpl() : new HistoricalExchangeInstrumentImpl();
    }

    private Exchange getExchange( ExchangeCode exCode ) {
        if ( exCode == null ) return null;

        Exchange ex = ExchangeManager.instance().getByCode( exCode );

        if ( ex == null ) {
            throw new RuntimeException( "ExchangeManager doesnt have exCode=[" + exCode + "] loaded" );
        }

        return ex;
    }

    private HistoricMutatingInstMap getMapForSearch( SecurityIDSource securityIDSource ) {
        HistoricMutatingInstMap map = _idMaps[ securityIDSource.ordinal() ];

        if ( map == null ) throw new SMTRuntimeException( getComponentId() + " getInstrument() cannot lookup by " + securityIDSource + " as that is not supported for index lookups" );

        return map;
    }

    private CommonInstrument newCommonInstrumentVersion( Exchange ex, SecurityDefinitionImpl def, long startTimestamp ) {

        if ( def.getPrimarySecurityExchange() == null || Utils.isNull( def.getCommonSecurityId() ) ) {
            return null;
        }

        Currency ccy = def.getCurrency();

        if ( ccy == null ) throw new SMTRuntimeException( "Cannot add instrument as missing currency " + def.toString() );

        Map<Currency, HistoricalCommonInstrument> map = _commonInstMap.computeIfAbsent( def.getCommonSecurityId(), ( k ) -> new HashMap<>( DEFAULT_CCY_ENTRIES ) );

        ParentCompany parentCompany = newParentCompanyVersion( def, startTimestamp );

        CommonInstrument commonInstrument = parentCompany.getCommonInstrument( def.getCommonSecurityId() );

        if ( commonInstrument == null ) {
            HistoricalCommonInstrument commonInstrumentSeries = map.computeIfAbsent( ccy, ( k ) -> new HistoricalCommonInstrumentImpl() );

            commonInstrument = new CommonInstrumentImpl( parentCompany, def.getPrimarySecurityExchange(), ccy, def.getCommonSecurityId(), startTimestamp );

            parentCompany.attach( commonInstrument );

            commonInstrumentSeries.add( commonInstrument );
        }

        return commonInstrument;
    }

    private ParentCompany newParentCompanyVersion( SecurityDefinitionImpl def, long startTimestamp ) {
        long parentCompanyId = def.getParentCompanyId();

        HistoricalParentCompany pcSeries = _parentCompanyMap.computeIfAbsent( parentCompanyId, ( k ) -> new HistoricalParentCompanyImpl() );

        ParentCompany latest = pcSeries.latest();

        ParentCompany newVersion = null;

        newVersion = new ParentCompanyImpl( def.getCompanyName(), parentCompanyId, startTimestamp );

        if ( latest != null ) { // new time slice for parent company, must clone the common instruments below as they have refs to time series exchange instrument
            List<CommonInstrument> dest = new ArrayList<>( latest.getNumCommonInsts() );

            latest.getCommonInstruments( dest );

            for ( CommonInstrument ci : dest ) {
                CommonInstrument copyCI = new CommonInstrumentImpl( newVersion, def.getPrimarySecurityExchange(), def.getCurrency(), ci.getCommonInstrumentId(), startTimestamp );
                newVersion.attach( copyCI );
            }
        }

        pcSeries.add( newVersion );

        return newVersion;
    }

    private void removeFromOldCommonInstrument( ExchInstSecDefWrapperTSEntry prev, long eventTimestamp ) {
        CommonInstrument commonInst = newCommonInstrumentVersion( prev.getExchange(), prev.getSecDef(), eventTimestamp );

        commonInst.detach( prev );
    }

    private void removeKey( SecurityIDSource idSrc, ZString keyVal, HistoricalExchangeInstrumentImpl instTimeSeries, ExchInstSecDefWrapperTSEntry prev ) {
        HistoricMutatingInstMap map = _idMaps[ idSrc.ordinal() ];

        if ( map == null ) return; // unsupported index

        map.removeKey( keyVal, instTimeSeries, prev );
    }

    private void setInstVersionTimeToStartTimeIfAvail( final SecurityDefinitionImpl def ) {
        if ( Utils.hasVal( def.getStartTimestamp() ) ) {
            def.setEventTimestamp( def.getStartTimestamp() ); // force event timestamp to match start timestamp
        } else if ( Utils.hasVal( def.getEventTimestamp() ) ) {
            def.setStartTimestamp( def.getEventTimestamp() ); // force event timestamp to match start timestamp
        }
    }

    private void setTickType( final InstrumentSecurityDefWrapper inst ) {
        TickType tt = inst.getTickType();

        int tickScaleId = inst.getSecDef().getTickRule();

        if ( !Utils.isNull( tickScaleId ) && tickScaleId > 0 ) {
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

    private boolean shouldCreateDeriv( SecurityDefinitionImpl def ) {
        boolean isDeriv = false;

        int legCnt = def.getNoLegs();

        if ( legCnt == Constants.UNSET_INT ) legCnt = 0;

        if ( def == null || def.getSecurityType() == null || legCnt > 0 ) {

            isDeriv = true;

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
                isDeriv = false;
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
                isDeriv = true;
                break;
            case Strategy:
            default:
                throw new SMTRuntimeException( "Cannot create instrument " + def.toString() + " as securityType " + def.getSecurityType() + " unsupported by SMT" );
            }
        }

        return isDeriv;
    }

    private void updateIndices( HistoricalExchangeInstrumentImpl instTimeSeries, ExchInstSecDefWrapperTSEntry prev, ExchInstSecDefWrapperTSEntry newInst ) {

        SecurityDefinitionImpl latestSecDef = newInst.getSecDef();

        // ensure HistoricalExchangeInstrumentImpl registered in unique over time maps

        if ( prev != null ) {
            SecurityDefinitionImpl prevSecDef = prev.getSecDef();

            checkIdChange( newInst, instTimeSeries, prev );
            checkKeyChange( SecurityIDSource.Symbol, prevSecDef.getSymbol(), newInst, instTimeSeries, prev );

            if ( SecurityIDSource.InternalString != prevSecDef.getSecurityIDSource() && SecurityIDSource.Symbol != prevSecDef.getSecurityIDSource() ) {
                checkKeyChange( prevSecDef.getSecurityIDSource(), prevSecDef.getSecurityID(), newInst, instTimeSeries, prev );
            }

            SecurityAltIDImpl securityAltIDs = (SecurityAltIDImpl) prevSecDef.getSecurityAltIDs();

            while( securityAltIDs != null ) {

                ZString          idB    = securityAltIDs.getSecurityAltID();
                SecurityIDSource idSrcB = securityAltIDs.getSecurityAltIDSource();

                if ( idSrcB != prevSecDef.getSecurityIDSource() ) {
                    checkKeyChange( idSrcB, idB, newInst, instTimeSeries, prev );
                }

                securityAltIDs = securityAltIDs.getNext();
            }

            // check for any newly added keys

            securityAltIDs = (SecurityAltIDImpl) latestSecDef.getSecurityAltIDs();

            while( securityAltIDs != null ) {

                ZString          idB    = securityAltIDs.getSecurityAltID();
                SecurityIDSource idSrcB = securityAltIDs.getSecurityAltIDSource();

                if ( idSrcB != latestSecDef.getSecurityIDSource() && idSrcB != SecurityIDSource.Symbol ) {
                    if ( InstUtils.getKey( prevSecDef, idSrcB ) == null ) {
                        addKey( idSrcB, idB, instTimeSeries, newInst );
                    }
                }

                securityAltIDs = securityAltIDs.getNext();
            }

        } else {
            addKey( SecurityIDSource.InternalString, TLC.safeCopy( newInst.id() ), instTimeSeries, newInst );
            addKey( SecurityIDSource.Symbol, newInst.getSymbol(), instTimeSeries, newInst );

            if ( SecurityIDSource.InternalString != latestSecDef.getSecurityIDSource() && SecurityIDSource.Symbol != latestSecDef.getSecurityIDSource() ) {
                addKey( latestSecDef.getSecurityIDSource(), latestSecDef.getSecurityID(), instTimeSeries, newInst );
            }

            SecurityAltIDImpl securityAltIDs = (SecurityAltIDImpl) latestSecDef.getSecurityAltIDs();

            while( securityAltIDs != null ) {

                ZString          idB    = securityAltIDs.getSecurityAltID();
                SecurityIDSource idSrcB = securityAltIDs.getSecurityAltIDSource();

                if ( idSrcB != latestSecDef.getSecurityIDSource() && idSrcB != SecurityIDSource.Symbol && idSrcB != SecurityIDSource.InternalString ) {
                    addKey( idSrcB, idB, instTimeSeries, newInst );
                }

                securityAltIDs = securityAltIDs.getNext();
            }
        }

        updateSecurityGroups( instTimeSeries, prev, newInst );
    }

    private void updateSecurityGroups( final HistoricalExchangeInstrumentImpl instTimeSeries, final ExchInstSecDefWrapperTSEntry prev, final ExchInstSecDefWrapperTSEntry newInst ) {
        final ZString newSecurityGroup = newInst.getSecurityGroup();

        if ( prev != null ) {
            final ZString prevSecurityGroup = prev.getSecurityGroup();
            if ( prevSecurityGroup != null ) {
                if ( newSecurityGroup != null ) {
                    if ( newSecurityGroup.equals( prevSecurityGroup ) ) {
                        return; // nothing to do
                    } else {
                        // unexpected ... consider if security group itself needs to become timelined

                        _log.info( getComponentId() + " inst " + newInst.id() + " note had secGrp " + prevSecurityGroup + " now its " + newSecurityGroup );
                    }
                }
            }
        }

        Set<HistoricalExchangeInstrumentImpl> grp = _secGrpMap.get( newSecurityGroup );

        if ( grp == null ) {
            grp = new LinkedHashSet<>();
            _secGrpMap.put( TLC.safeCopy( newSecurityGroup ), grp );
        }

        grp.add( instTimeSeries );
    }
}
