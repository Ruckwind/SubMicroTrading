package com.rr.inst;

import com.rr.core.collections.SMTMap;
import com.rr.core.factories.Factory;
import com.rr.core.hols.HolidayLoader;
import com.rr.core.lang.*;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Currency;
import com.rr.core.model.*;
import com.rr.core.pool.SuperPool;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.properties.AppProps;
import com.rr.core.utils.FileUtils;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.StringUtils;
import com.rr.core.utils.Utils;
import com.rr.model.generated.internal.events.factory.SecurityAltIDFactory;
import com.rr.model.generated.internal.events.impl.SecDefEventImpl;
import com.rr.model.generated.internal.events.impl.SecurityAltIDImpl;
import com.rr.model.generated.internal.events.impl.SecurityDefinitionImpl;
import com.rr.model.generated.internal.events.interfaces.SecurityDefinition;
import com.rr.model.generated.internal.events.interfaces.SecurityDefinitionWrite;
import com.rr.model.generated.internal.type.TradingStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

import static com.rr.core.lang.Constants.FUT_CONTRACT_MONTH_CODE;

public class InstUtils {

    private static final Logger _log = LoggerFactory.create( InstUtils.class );

    private final static SuperPool<SecurityAltIDImpl>      _secAltIdPool    = SuperpoolManager.instance().getSuperPool( SecurityAltIDImpl.class );
    private final static ThreadLocal<SecurityAltIDFactory> _secAltIdFactory = ThreadLocal.withInitial( () -> new SecurityAltIDFactory( _secAltIdPool ) );
    private static final EnumSet<FutureExchangeSymbol> _mainFuts       = EnumSet.noneOf( FutureExchangeSymbol.class );

    public static void sort( List<Instrument> insts ) {
        Collections.sort( insts, Comparator.comparing( Identifiable::id ) );
    }

    public static void sortListAndFillArray( final List<Instrument> instList, final Instrument[] instArray ) {

        sort( instList );

        Iterator<Instrument> it = instList.iterator();
        int                  i  = 0;
        while( it.hasNext() ) {

            Instrument inst = it.next();
            instArray[ i ] = inst;
            i++;
        }
    }

    public static boolean found( Identifiable inst, Identifiable[] instArray ) {

        String id = inst.id();
        for ( int i = 0; i < instArray.length; i++ )
            if ( id.equals( instArray[ i ].id() ) )
                return true;

        return false;
    }

    public static synchronized <T> void instLoadFromFile( InstrumentLocator store, String subscriptionFile, SMTMap<Instrument, T> outData, Factory<Instrument, T> f ) throws IOException {

        BufferedReader rdr = FileUtils.bufFileReader( subscriptionFile );

        _log.info( "InstrumentUtils.instLoadFromFile  file " + subscriptionFile );

        try {
            int fail = 0;

            ReusableString securityId = new ReusableString();
            ReusableString tmp        = new ReusableString();

            SecurityIDSource securityIDSource;

            int lineNum = 0;

            String line;
            while( (line = rdr.readLine()) != null ) {
                ++lineNum;
                line = line.trim();
                if ( line.length() > 0 && !line.startsWith( "#" ) ) {

                    try {
                        ExchangeCode securityExchange = ExchangeCode.UNKNOWN;
                        String[]     parts            = line.split( "," );

                        if ( parts.length == 3 ) {
                            securityId.copy( parts[ 0 ] );
                            securityIDSource = SecurityIDSource.valueOf( parts[ 1 ] );
                            securityExchange = ExchangeCode.getFromMktSegmentMIC( new ViewString( parts[ 2 ] ) );

                            ExchangeInstrument inst = store.getExchInst( securityId, securityIDSource, securityExchange );

                            if ( inst == null ) {
                                _log.warn( "[" + subscriptionFile + ":" + lineNum + "] No instrument found for symbol " + securityId + ", idSrc=" + securityIDSource + ", rec=" + securityExchange );
                                ++fail;
                            } else if ( outData != null ) {
                                if ( outData.containsKey( inst ) ) {
                                    _log.warn( "[" + subscriptionFile + ":" + lineNum + "] Duplicate subscription request using symbol " + securityId + ", idSrc=" + securityIDSource + ", rec=" + securityExchange );
                                    ++fail;
                                } else {
                                    T item = f.create( inst );

                                    outData.put( inst, item );
                                }
                            }
                        } else {
                            _log.warn( "Incorrect subscribtion in file " + subscriptionFile + ", line=" + lineNum + ", entry [ " + line + "] should be  secId,idSrc,REC" );
                            ++fail;
                        }
                    } catch( Exception e ) {
                        _log.warn( "Exception subscribing to file " + subscriptionFile + ", line=" + lineNum + ", entry [ " + line + "]" );
                        ++fail;
                    }
                }
            }

            _log.info( "Instrument.instLoadFromFile  " + subscriptionFile + " entries=" + ((outData != null) ? outData.size() : 0) + ", failed=" + fail );

        } finally {
            FileUtils.close( rdr );
        }
    }

    public static ZString getKey( final SecurityDefinition def, final SecurityIDSource idSrc ) {
        if ( def.getSecurityIDSource() == idSrc ) return def.getSecurityID();

        SecurityAltIDImpl idEntry = (SecurityAltIDImpl) def.getSecurityAltIDs();

        while( idEntry != null ) {
            if ( idEntry.getSecurityAltIDSource() == idSrc ) {
                return idEntry.getSecurityAltID();
            }

            idEntry = idEntry.getNext();
        }

        return null;
    }

    private static void find( final SecurityDefinition def, final SecurityIDSource typeWanted, final ReusableString out ) {

        switch( typeWanted ) {
        case UniqueInstId:
            out.copy( def.getUniqueInstId() );
            return;
        case ExchangeLongId:
            out.copy( def.getExchangeLongId() );
            return;
        case SecurityDesc:
            out.copy( def.getSecurityDesc() );
            return;
        case InternalString:
        case StrategyId:
        case ISIN:
        case FIGI:
        case ExchangeSymbol:
        case PrimaryBloombergCode:
        case BloombergCode:
        case BloombergTicker:
        case Symbol:
        case Unknown:
        case DEAD_1:
        case DEAD_2:
        case RIC:
            break;
        }

        if ( typeWanted == SecurityIDSource.ExchangeSymbol ) {
            out.copy( def.getSymbol() ); // default to symbol, 22=8, 48=val will override
        }

        if ( def.getSecurityIDSource() == typeWanted ) {
            out.copy( def.getSecurityID() );
            return;
        }

        SecurityAltIDImpl securityAltIDs = (SecurityAltIDImpl) def.getSecurityAltIDs();

        while( securityAltIDs != null ) {

            final ZString          id    = securityAltIDs.getSecurityAltID();
            final SecurityIDSource idSrc = securityAltIDs.getSecurityAltIDSource();

            if ( id.length() > 0 ) {
                if ( idSrc == typeWanted ) {
                    out.copy( id );
                    return;
                }
            }

            securityAltIDs = securityAltIDs.getNext();
        }
    }

    public static OptionType getOptionType( final SecurityDefinitionImpl secDef ) {
        ZString cfiCode = secDef.getCFICode();

        if ( cfiCode.getByte( 0 ) == 'O' ) {
            byte callPut = cfiCode.getByte( 1 );

            return OptionType.getVal( callPut );
        }

        return null; // not an option
    }

    public static boolean keysChanged( final SecurityDefinition cur, final SecurityDefinition newDef ) {

        if ( cur.getExchangeLongId() != newDef.getExchangeLongId() ) return true;
        if ( cur.getUniqueInstId() != newDef.getUniqueInstId() ) return true;

        {
            SecurityAltIDImpl securityAltIDs = (SecurityAltIDImpl) cur.getSecurityAltIDs();

            while( securityAltIDs != null ) {

                final ZString          id    = securityAltIDs.getSecurityAltID();
                final SecurityIDSource idSrc = securityAltIDs.getSecurityAltIDSource();

                if ( unmatched( newDef, idSrc, id ) ) return true;

                securityAltIDs = securityAltIDs.getNext();
            }
        }

        {
            SecurityAltIDImpl securityAltIDs = (SecurityAltIDImpl) newDef.getSecurityAltIDs();

            while( securityAltIDs != null ) {

                final ZString          id    = securityAltIDs.getSecurityAltID();
                final SecurityIDSource idSrc = securityAltIDs.getSecurityAltIDSource();

                if ( unmatched( cur, idSrc, id ) ) return true;

                securityAltIDs = securityAltIDs.getNext();
            }
        }

        if ( cur.getSecurityIDSource() != newDef.getSecurityIDSource() ) {
            return unmatched( cur, newDef.getSecurityIDSource(), newDef.getSecurityID() );
        } else {
            return !cur.getSecurityID().equals( newDef.getSecurityID() );
        }
    }

    public static boolean unmatched( final SecurityDefinition def, final SecurityIDSource idSrc, final ZString id ) {

        if ( def.getSecurityIDSource() == idSrc ) {
            boolean unmatched = !def.getSecurityID().equals( id );
            if ( unmatched ) {
                _log.log( Level.info, "unmatched() " + id + " keyA  " + idSrc.name() + " unmatched now value " + id.toString() + " was " + def.getSecurityID().toString() );
            }
            return unmatched;
        }

        SecurityAltIDImpl securityAltIDs = (SecurityAltIDImpl) def.getSecurityAltIDs();

        while( securityAltIDs != null ) {

            final ZString          idB    = securityAltIDs.getSecurityAltID();
            final SecurityIDSource idSrcB = securityAltIDs.getSecurityAltIDSource();

            if ( idSrc == idSrcB ) {
                boolean unmatched = !idB.equals( id );
                if ( unmatched ) {
                    _log.log( Level.info, "unmatched() " + id + " keyB  " + idSrc.name() + " unmatched now value " + id.toString() + " was " + idB );
                }
                return unmatched;
            }

            securityAltIDs = securityAltIDs.getNext();
        }

        if ( idSrc == SecurityIDSource.Symbol ) {
            boolean unmatched = !def.getSymbol().equals( id );
            if ( unmatched ) {
                _log.log( Level.info, "unmatched() " + id + " keyC " + idSrc.name() + " unmatched now value " + id.toString() + " was " + def.getSymbol().toString() );
            }
            return unmatched;
        }

        _log.log( Level.info, "unmatched() " + id + " keyD " + idSrc.name() + " unmatched now value " + id.toString() + " unset before" );

        return true;
    }

    public static boolean matched( final SecurityDefinition def, final SecurityIDSource idSrc, final ZString id ) {

        if ( def.getSecurityIDSource() == idSrc ) return def.getSecurityID().equals( id );

        SecurityAltIDImpl securityAltIDs = (SecurityAltIDImpl) def.getSecurityAltIDs();

        while( securityAltIDs != null ) {

            final ZString          idB    = securityAltIDs.getSecurityAltID();
            final SecurityIDSource idSrcB = securityAltIDs.getSecurityAltIDSource();

            if ( idSrc == idSrcB ) {
                return idB.equals( id );
            }

            securityAltIDs = securityAltIDs.getNext();
        }

        return false;
    }

    /**
     * create a unique key for instrument combining securityId (eg ISIN), exchange code and CCY
     *
     * @param key
     * @param securityId
     * @param exchangeCode
     * @param currency
     * @return
     */
    public static ZString formCompoundInstUniqKey( final ReusableString key, final ZString securityId, final ExchangeCode exchangeCode, final Currency currency ) {
        key.copy( securityId ).append( '/' ).append( currency.toString() ).append( '/' ).append( exchangeCode.toString() );
        return key;
    }

    /**
     * create a unique key for instrument combining securityId (eg ISIN), exchange code and CCY
     *
     * @param key
     * @param exchangeCode
     * @param currency
     * @return
     */
    public static ZString formExchangeWithCcyKey( final ReusableString key, final ExchangeCode exchangeCode, final Currency currency ) {
        key.copy( currency.toString() ).append( '/' ).append( exchangeCode.toString() );
        return key;
    }

    public static ZString formFuturesSymbolKey( final ReusableString key, final ZString sym, int noLegs, final FutureExchangeSymbol grpSym, final int maturityMonthYear ) {
        if ( noLegs > 1 ) {
            key.copy( sym ).append( '/' ).append( maturityMonthYear );
        } else {
            key.copy( grpSym.getPhysicalSym() ).append( '/' ).append( maturityMonthYear );
        }
        return key;
    }

    public static ZString formOptionsSymbolKey( final ReusableString key, final ZString sym, final int maturityMonthYear, final double strikePrice, final OptionType type ) {
        char optType = (type == null) ? 'x' : (char) type.getVal();

        key.copy( sym ).append( '/' ).append( maturityMonthYear ).append( '/' ).append( optType ).append( '/' ).append( strikePrice );

        return key;
    }

    public static long getFirstNoticeDate( final DerivInstSecDefWrapper aw ) {

        if ( aw == null ) return Constants.UNSET_LONG;

        final SecurityDefinitionImpl sd = aw.getSecDef();

        for ( SecDefEventImpl c = (SecDefEventImpl) sd.getEvents(); c != null; c = c.getNext() ) {

            if ( c.getEventType() == SecDefEventType.FirstNoticeDate ) {
                return c.getEventDate();
            }
        }

        return Constants.UNSET_LONG;
    }

    public static void addDate( SecurityDefinitionWrite secDef, long yyyymmdd, SecDefEventType type ) {

        if ( Utils.isNullOrZero( yyyymmdd ) ) return;

        {
            SecDefEventImpl events = (SecDefEventImpl) secDef.getEvents();

            while( events != null ) {
                if ( events.getEventType() == type ) {
                    setDateTime( events, yyyymmdd );
                    return;
                }
                events = events.getNext();
            }
        }

        SecDefEventImpl events = new SecDefEventImpl();

        events.setEventType( type );

        setDateTime( events, yyyymmdd );

        events.setNext( (SecDefEventImpl) secDef.getEvents() );

        secDef.setEvents( events );

        secDef.setNoEvents( secDef.getNoEvents() + 1 );
    }

    public static boolean hasDate( SecurityDefinitionWrite secDef, SecDefEventType type ) {
        SecDefEventImpl events = (SecDefEventImpl) secDef.getEvents();

        while( events != null ) {
            if ( events.getEventType() == type ) {
                return true;
            }
            events = events.getNext();
        }

        return false;
    }

    public static long getActivationDate( final DerivInstSecDefWrapper aw ) {
        final SecurityDefinitionImpl sd = aw.getSecDef();

        return getActivationDate( sd );
    }

    public static long getActivationDate( final SecurityDefinition sd ) {
        for ( SecDefEventImpl c = (SecDefEventImpl) sd.getEvents(); c != null; c = c.getNext() ) {

            if ( c.getEventType() == SecDefEventType.Activation ) {
                return c.getEventDate();
            }
        }

        return Constants.UNSET_LONG;
    }

    public static long getLastTradeableDate( final SecurityDefinitionImpl sd ) {

        for ( SecDefEventImpl c = (SecDefEventImpl) sd.getEvents(); c != null; c = c.getNext() ) {

            if ( c.getEventType() == SecDefEventType.LastTradeableDate ) {
                return c.getEventDate();
            }
        }

        return Constants.UNSET_LONG;
    }

    public static boolean filterOut( final SecurityDefinition sd ) {
        long activation = InstUtils.getActivationDate( sd );

        if ( Utils.hasNonZeroVal( sd.getStartTimestamp() ) && sd.getStartTimestamp() < 0 ) {
            return true; // cannot handle negative times in fix decoder
        }

        if ( Utils.hasNonZeroVal( activation ) ) {
            if ( activation < 19700101 ) {
                return true; // cannot handle negative times in fix decoder
            }
        }

        if ( sd.getSecurityType() == SecurityType.Future ) {
            return sd.getMaturityMonthYear() < 197001; // cannot handle negative times in fix decoder
        }

        return false;
    }

    public static <T> void mapReplaceStratInstProxy( InstrumentLocator instLoc, final Map<Instrument, T> map ) {
        if ( map != null ) {
            final Set<StratInstProxy> proxies = new HashSet<>();

            map.keySet().forEach( ( i ) -> { if ( i instanceof StratInstProxy ) proxies.add( (StratInstProxy) i ); } );

            for ( StratInstProxy proxy : proxies ) {

                Instrument stratInst = instLoc.getInst( proxy.zid(), SecurityIDSource.StrategyId, null );

                if ( stratInst == null ) {
                    throw new SMTRuntimeException( "InstrumentUtils.mapReplaceStratInstProxy() unable to find strat instrument to replace proxy " + proxy.zid() );
                }

                T val = map.remove( proxy );

                map.put( stratInst, val );
            }
        }
    }

    public static void secDefOverrides( final SecurityDefinitionImpl sd ) {

        if ( sd == null ) return;

        SecurityType securityType = sd.getSecurityType();

        if ( securityType == null ) {
            ZString cfiCode = sd.getCFICode();

            if ( cfiCode != null ) {
                char type = (char) cfiCode.getByte( 0 );

                if ( type == 'F' ) {
                    securityType = SecurityType.Future;
                    sd.setSecurityType( securityType );
                } else if ( type == 'E' ) {
                    securityType = SecurityType.Equity;
                    sd.setSecurityType( securityType );
                }
            }
        }

        if ( securityType == SecurityType.Future ) {

            if ( Utils.isNull( sd.getFutPointValue() ) ) {
                try {
                    sd.setFutPointValue( getFutPointValue( sd ) );
                } catch( Exception e ) {
                    // unable to default PV dont generate warning
                }
            }

            ZString bbKey = getKey( sd, SecurityIDSource.BloombergTicker );
            if ( bbKey == null ) { // generate if none available
                setBloombergFutureTicker( sd );

                bbKey = getKey( sd, SecurityIDSource.BloombergTicker );
            }

            if ( sd.getSecDefSpecialType() == SecDefSpecialType.CMEFuture ) {

                sd.getSymbolForUpdate().copy( sd.getSecurityDesc() );

            } else if ( bbKey != null ) {
                sd.getSymbolForUpdate().copy( bbKey );
            }

            timestampOverrides( sd, false );

        } else if ( securityType != null && securityType.getProdType() == ProductType.Equity ) {

            if ( Utils.hasNonZeroVal( sd.getTickRule() ) ) {
                sd.setMinPriceIncrement( Constants.UNSET_DOUBLE );
            }

            setEqSym( sd );
        }

        if ( getKey( sd, SecurityIDSource.InternalString ) == null || getKey( sd, SecurityIDSource.InternalString ).length() == 0 ) { // generate if none available
            String id = getUniqInstId( sd );

            addKey( sd, SecurityIDSource.InternalString, id );
        }
    }

    public static void setEqSym( final SecurityDefinitionWrite sd ) {
        if ( sd.getSecurityType() == SecurityType.Equity ) {
            ZString exSym = getKey( sd, SecurityIDSource.ExchangeSymbol );

            ReusableString symbol = sd.getSymbolForUpdate();

            ZString bbKey = getKey( sd, SecurityIDSource.BloombergTicker );

            if ( bbKey != null && bbKey.length() > 0 ) {

                ReusableString smtSym = new ReusableString( bbKey );

                symbol.copy( smtSym );
            }
        }
    }

    public static ExchangeCode getExchangeCode( final ExchangeInstrument inst, final SecurityIDSource idSrc ) {
        ExchangeCode securityExchange;
        switch( idSrc ) {
        case PrimaryBloombergCode:
        case PrimaryMarketSymbol:
        case ISIN:
        case Symbol:
            securityExchange = inst.getPrimaryExchangeCode();
            break;
        case ExchangeSymbol:
        case FIGI:
        case BloombergCode:
        case BloombergTicker:
        case UniqueInstId:
        case ExchangeLongId:
        case SecurityDesc:
        case InternalString:
        case StrategyId:
        case DEAD_1:
        case DEAD_2:
        case RIC:
        case Unknown:
        default:
            securityExchange = inst.getSecurityExchange();
            break;
        }
        return securityExchange;
    }

    public static void timestampOverrides( final SecurityDefinitionImpl sd, boolean overrideStartEndTS ) {
        final int maturity = sd.getMaturityMonthYear();

        if ( Utils.hasNonZeroVal( maturity ) ) {
            long yyyymmdd = InstUtils.getLastTradeableDate( sd );
            long tempMat  = maturity * 100L + 31;

            Calendar c = CommonTimeUtils.getCalendarUTC(); // timezone doesnt matter here

            if ( Utils.isNull( yyyymmdd ) || yyyymmdd < tempMat ) {

                int yyyymm = maturity;

                CommonTimeUtils.yyyymmToCalEndMonths( c, yyyymm );
            } else {

                CommonTimeUtils.yyyymmddToCalendar( c, yyyymmdd );
            }

            long deadMS = c.getTimeInMillis() / 1000 * 1000 - 1000; // round MS and subtract 1sec to help avoid overlap

            sd.setDeadTimestamp( deadMS );

            if ( Utils.isNullOrZero( sd.getEndTimestamp() ) && !Utils.isNull( deadMS ) ) {
                sd.setEndTimestamp( deadMS );
            }

            long activeYYMMDD = InstUtils.getActivationDate( sd );
            CommonTimeUtils.yyyymmddToCalendar( c, activeYYMMDD );
            long startMS = c.getTimeInMillis();

            if ( Utils.isNullOrZero( sd.getStartTimestamp() ) && !Utils.isNull( startMS ) ) {
                sd.setStartTimestamp( startMS );
            }

            if ( overrideStartEndTS ) {

                if ( Utils.hasNonZeroVal( sd.getStartTimestamp() ) ) {

                    if ( !Utils.isNull( activeYYMMDD ) ) {

                        sd.setStartTimestamp( startMS );
                    }
                }
            }
        }
    }

    public static <T extends SecurityDefinitionWrite> void setBloombergFutureTicker( final T secDef ) {
        int maturityYYYYMM = secDef.getMaturityMonthYear();

        ReusableString bbCode = new ReusableString( 6 );

        FutureExchangeSymbol futExSym = FutureExchangeSymbol.getFromPhysicalSymbol( secDef.getSecurityGroup(), secDef.getSecurityExchange() );

        if ( futExSym != FutureExchangeSymbol.UNKNOWN && Utils.isNullOrZero( secDef.getNoLegs() ) ) {
            bbCode.copy( futExSym.getBbRootSym().toString() );

            if ( Utils.hasNonZeroVal( maturityYYYYMM ) ) {
                int monthIdx = maturityYYYYMM % 100 - 1;

                int yyyy = maturityYYYYMM / 100;

                if ( futExSym == FutureExchangeSymbol.XNYM_NG ) {

                    bbCode.append( FUT_CONTRACT_MONTH_CODE.charAt( monthIdx ) ).append( yyyy % 100 );

                } else {

                    bbCode.append( FUT_CONTRACT_MONTH_CODE.charAt( monthIdx ) ).append( yyyy % 10 );
                }

                addKey( secDef, SecurityIDSource.BloombergTicker, bbCode.toString() );
            }
        }
    }

    public static String getFutureTicker( final int maturityYYYYMM, FutureExchangeSymbol fes ) {
        ReusableString ticker = new ReusableString( 6 );

        final ZString      symbol = fes.getPhysicalSym();
        final ExchangeCode mic    = fes.getValidMICs().iterator().next();

        ticker.copy( symbol );

        if ( Utils.hasNonZeroVal( maturityYYYYMM ) ) {
            int monthIdx = maturityYYYYMM % 100 - 1;

            int yyyy = maturityYYYYMM / 100;

            ticker.append( FUT_CONTRACT_MONTH_CODE.charAt( monthIdx ) ).append( yyyy % 10 );

//            if ( fes == FutureExchangeSymbol.XNYM_NG ) {
//
//                ticker.append( Constants.FUT_CONTRACT_MONTH_CODE.charAt( monthIdx ) ).append( yyyy % 100 );
//
//            } else {
//
//                ticker.append( Constants.FUT_CONTRACT_MONTH_CODE.charAt( monthIdx ) ).append( yyyy % 10 );
//            }

            return ticker.toString();
        }

        return null;
    }

    public static boolean addKey( final SecurityDefinitionWrite secDef, final SecurityIDSource idSrc, final String id ) {

        if ( secDef == null || id == null || id.length() == 0 ) return false;

        if ( secDef.getSecurityID().length() == 0 && idSrc == SecurityIDSource.InternalString && id.length() > 0 ) {
            secDef.getSecurityIDForUpdate().copy( id );
            secDef.setSecurityIDSource( idSrc );
            return false;
        }

        {
            SecurityAltIDImpl altIds = (SecurityAltIDImpl) secDef.getSecurityAltIDs();

            while( altIds != null ) {
                if ( altIds.getSecurityAltIDSource() == idSrc ) {
                    altIds.getSecurityAltIDForUpdate().copy( id );      // UPDATE KEY .. LAST ONE WINS
                    return false;
                }
                altIds = altIds.getNext();
            }
        }

        SecurityAltIDImpl altIds = _secAltIdFactory.get().get();

        altIds.getSecurityAltIDForUpdate().copy( id );
        altIds.setSecurityAltIDSource( idSrc );

        altIds.setNext( (SecurityAltIDImpl) secDef.getSecurityAltIDs() );

        secDef.setSecurityAltIDs( altIds );

        final int curKeys = secDef.getNoSecurityAltID();
        if ( Utils.isNull( curKeys ) || curKeys < 0 ) {
            secDef.setNoSecurityAltID( 1 );
        } else {
            secDef.setNoSecurityAltID( curKeys + 1 );
        }

        if ( idSrc == SecurityIDSource.Symbol && secDef.getSymbol().length() == 0 ) {
            secDef.getSymbolForUpdate().copy( id );
        }

        return true;
    }

    public static boolean removeKey( final SecurityDefinitionWrite secDef, final SecurityIDSource idSrc ) {

        if ( secDef == null ) return false;

        if ( idSrc == secDef.getSecurityIDSource() ) {
            secDef.getSecurityIDForUpdate().reset();
            // dont return check its not also in list of keys
        }

        SecurityAltIDImpl altIds = (SecurityAltIDImpl) secDef.getSecurityAltIDs();

        SecurityAltIDImpl prev = null;

        while( altIds != null ) {
            if ( altIds.getSecurityAltIDSource() == idSrc ) {
                if ( prev == null ) {
                    secDef.setSecurityAltIDs( altIds.getNext() );
                } else {
                    prev.setNext( altIds.getNext() );
                }

                final int curKeys = secDef.getNoSecurityAltID();
                if ( curKeys > 0 ) {
                    secDef.setNoSecurityAltID( curKeys - 1 );
                }

                return true; // dont bother recycling the altId instance
            }
            prev   = altIds;
            altIds = altIds.getNext();
        }

        return false;
    }

    public static boolean isContinuousFuturesStrategy( Instrument inst ) {
        return inst.getSecurityType() == SecurityType.Strategy && ((StrategyInstrument) inst).getStratClassification() == StrategyInstrument.StratClassification.GFUT;
    }

    public static boolean isFactorStrategy( Instrument inst ) {
        return inst != null && inst.getSecurityType() == SecurityType.Strategy && ((StrategyInstrument) inst).getStratClassification() == StrategyInstrument.StratClassification.Factor;
    }

    private static void setDateTime( final SecDefEventImpl event, final long yyyymmdd ) {
        event.setEventDate( yyyymmdd );
    }

    public static TradingStatus getTradingStatus( String id, ReusableString logMsg, Logger logger, final SnapableMktData data, boolean logBlocked ) {

        Instrument inst = data.getInstrument();

        TradingStatus ts = TradingStatus.OK;

        if ( inst.getSecurityType() == SecurityType.Future ) {
            ExchDerivInstrument edi = (ExchDerivInstrument) inst;

            final int dateYYYYMMDD = CommonTimeUtils.epochMillisToYYYYMMDD( ClockFactory.get().currentTimeMillis() );

            if ( HolidayLoader.instance().isHoliday( dateYYYYMMDD, edi ) ) {
                ts = TradingStatus.BlockedAsExchangeClosed;

                if ( ts != TradingStatus.OK && logBlocked ) {
                    logMsg.copy( id )
                          .append( " attempt to create order blocked as strategy MIC is on holiday " )
                          .append( ts )
                          .append( " : " )
                          .chain( () -> data.dump( logMsg ) );

                    logger.log( Level.WARN, logMsg );
                }
            }

        } else if ( inst instanceof ExchangeInstrument ) {

            ExchangeInstrument exInst = (ExchangeInstrument) inst;

            // Instruments on IFEU have different holiday calendars
            // so this requires instruments to specify which holiday calendar to use
            // as we dont trade without valid bar we can ignore this for prod
            // for backtest its useful as the age bar check is disabled

            ExchangeState state = exInst.getExchange().getSession().getExchangeStateNow();

            switch( state ) {
            case PostClose:
            case Closed:
                ts = TradingStatus.BlockedAsExchangeClosed;

                if ( ts != TradingStatus.OK && logBlocked ) {
                    logMsg.copy( id )
                          .append( " attempt to create order blocked " )
                          .append( ts )
                          .append( ", sessState=" ).append( state )
                          .append( ", sess= " )
                          .chain( () -> exInst.getExchange().getSession().dump( logMsg ) )
                          .append( " : " )
                          .chain( () -> data.dump( logMsg ) );

                    logger.log( Level.WARN, logMsg );
                }
                break;
            case PreOpen:
            case OpeningAuction:
            case Continuous:
            case IntradayAuction:
            case ClosingAuction:
            case Unknown:
            default:
                ts = TradingStatus.OK;
                break;
            }
        }

        return ts;
    }


    public static int compareByFirstNoticeDate( ExchangeInstrument a, ExchangeInstrument b, ZString secGrp ) {
        if ( !(a instanceof DerivInstSecDefWrapper) ) throw new SMTRuntimeException( "expected deriv inst for " + secGrp + " not " + a.toString() );
        if ( !(b instanceof DerivInstSecDefWrapper) ) throw new SMTRuntimeException( "expected deriv inst for " + secGrp + " not " + a );

        DerivInstSecDefWrapper aw = (DerivInstSecDefWrapper) a;
        DerivInstSecDefWrapper bw = (DerivInstSecDefWrapper) b;

        long ats = InstUtils.getFirstNoticeDate( aw );
        long bts = InstUtils.getFirstNoticeDate( bw );

        return Long.compare( ats, bts );
    }

    public static FutureExchangeSymbol getFutSymFromInstId( final ReusableString id, final ExchangeCode exchangeCode ) {

        ReusableString code = TLC.strPop();

        for ( FutureExchangeSymbol fes : FutureExchangeSymbol.values() ) {

            int idx = id.indexOf( '.' );

            if ( idx != -1 ) {
                code.copy( id, 0, idx );

                final FutureExchangeSymbol sym = FutureExchangeSymbol.getFromPhysicalSymbol( code, exchangeCode );

                if ( sym != FutureExchangeSymbol.UNKNOWN ) {
                    return sym;
                }
            }
        }

        TLC.strPush( code );

        return FutureExchangeSymbol.UNKNOWN;
    }

    public static void setMaturityFromId( final ReusableString id, final SecurityDefinitionImpl def ) {
        int lastIdx = id.lastIndexOf( '.' );

        int maturity = Constants.UNSET_INT;

        if ( lastIdx != -1 ) {

            ReusableString yyyymm = TLC.strPop();

            if ( Character.isDigit( id.getByte( lastIdx + 1 ) ) ) {
                id.substring( yyyymm, lastIdx + 1 );

                try {
                    maturity = StringUtils.parseInt( yyyymm );
                } catch( Exception e ) {
                    // swallow
                }
            }

            if ( Utils.isNull( maturity ) ) {
                int nextLastIdx = id.lastIndexOf( '.', lastIdx - 1 );

                if ( nextLastIdx != -1 ) {
                    id.substring( yyyymm, nextLastIdx + 1, lastIdx );
                    maturity = StringUtils.parseInt( yyyymm );
                }
            }

            TLC.strPush( yyyymm );
        }

        def.setMaturityMonthYear( maturity );
    }

    public static void setSymbolFromId( final ReusableString id, final SecurityDefinitionImpl def ) {
        int sepIdx = id.indexOf( '.' );

        int maturity = Constants.UNSET_INT;

        if ( sepIdx != -1 ) {

            int startIdx = sepIdx + 1;

            sepIdx = id.indexOf( '.', startIdx );

            if ( sepIdx != -1 && sepIdx != startIdx ) {

                def.getSymbolForUpdate().copy( id.substring( startIdx, sepIdx ) );

                ZString exSym = getKey( def, SecurityIDSource.ExchangeSymbol );

                if ( exSym == null ) {

                    addKey( def, SecurityIDSource.ExchangeSymbol, def.getSymbol().toString() );
                }
            }
        }
    }

    public static int getMaturityFromId( final ReusableString id ) {
        int lastIdx = id.lastIndexOf( '.' );

        int maturity = Constants.UNSET_INT;

        if ( lastIdx != -1 ) {

            ReusableString yyyymm = TLC.strPop();

            if ( Character.isDigit( id.getByte( lastIdx + 1 ) ) ) {
                id.substring( yyyymm, lastIdx + 1 );

                try {
                    maturity = StringUtils.parseInt( yyyymm );
                } catch( Exception e ) {
                    // swallow
                }
            }

            if ( Utils.isNull( maturity ) ) {
                int nextLastIdx = id.lastIndexOf( '.', lastIdx - 1 );

                if ( nextLastIdx != -1 ) {
                    id.substring( yyyymm, nextLastIdx + 1, lastIdx );
                    maturity = StringUtils.parseInt( yyyymm );
                }
            }

            TLC.strPush( yyyymm );
        }

        return maturity;
    }

    /**
     * In the case of full-size S&P future SP, 969=10, 9787=0.01, 1146=25.
     * <p>
     * PointValue =  MinPriceIncrementAmount / (minPriceIncrement * displayFactor)
     * <p>
     * This gives 25 / (10 * 0.01) = 250, the point value. Note that contract multiplier (tag 1147 in my secdef file) is 250 as well for SP.
     *
     * @return
     */
    public static double getFutPointValue( final SecurityDefinition def ) {

        if ( Utils.hasVal( def.getFutPointValue() ) ) {
            return def.getFutPointValue();
        }

        byte[] cfi = def.getCFICode().getBytes();

        if ( cfi.length > 0 && cfi[ 0 ] == 'F' && cfi[ 1 ] == 'F' ) {
            if ( cfi[ 2 ] == 'D' || cfi[ 2 ] == 'W' || cfi[ 2 ] == 'N' ) {
                double minPriceIncrementAmt = def.getMinPriceIncrementAmount();
                double minPriceIncrement    = def.getMinPriceIncrement();
                double displayFactor        = def.getDisplayFactor();

                if ( Utils.isNull( minPriceIncrementAmt ) ) {
                    throw new SMTRuntimeException( "PointValueCalculator unable to get pointValue as minPriceIncrementAmt null in " + def );
                }
                if ( Utils.isNull( minPriceIncrement ) ) {
                    throw new SMTRuntimeException( "PointValueCalculator unable to get pointValue as minPriceIncrement null in " + def );
                }
                if ( Utils.isNull( displayFactor ) ) {
                    throw new SMTRuntimeException( "PointValueCalculator unable to get pointValue as displayFactor null in " + def );
                }

                return minPriceIncrementAmt / (minPriceIncrement * displayFactor);
            }
        }

        if ( Utils.hasVal( def.getContractMultiplier() ) ) {
            return def.getContractMultiplier();
        }

        return 1.0;
    }

    /**
     * set a unique identifier for instrument
     * <p>
     * DONT CHANGE .... used to generate defunct deriv insts check InstUtils.getMaturityFromId  etc
     */
    public static String getUniqInstId( final SecurityDefinition secDef ) {
        ZString symbol = secDef.getSymbol();

        return getUniqInstId( secDef, symbol );
    }

    public static String getUniqInstId( final SecurityDefinition secDef, ZString symbol ) {
        ReusableString tmpId = TLC.instance().pop();

        int dotIdx = -1;

        if ( secDef.getSecurityType() == SecurityType.Future ) {
            tmpId.copy( secDef.getSecurityGroup() ).append( '.' ).append( symbol );
        } else {
            int symBytes = symbol.length();

            if ( symBytes > 0 ) {
                if ( ExchangeCode.isCBOE( secDef.getSecurityExchange() ) && Character.isLowerCase( symbol.getBytes()[ symBytes - 1 ] ) ) {
                    --symBytes;
                }

                if ( symbol.endsWith( "." ) || symbol.endsWith( "/" ) ) {
                    --symBytes;
                }
            }

            tmpId.copy( symbol, symbol.getOffset(), symBytes );

            dotIdx = tmpId.indexOf( '.' );
        }

        if ( !Utils.isNull( secDef.getMaturityMonthYear() ) ) {
            tmpId.append( "." ).append( secDef.getMaturityMonthYear() );
        } else {
            tmpId.append( "." ).append( secDef.getCurrency() );
        }
        if ( dotIdx == -1 && secDef.getSecurityExchange() != ExchangeCode.UNKNOWN ) {
            if ( secDef.getPrimarySecurityExchange() != null && secDef.getPrimarySecurityExchange() != ExchangeCode.UNKNOWN ) {
                tmpId.append( "." ).append( secDef.getPrimarySecurityExchange().getMIC() );
            }

            if ( secDef.getPrimarySecurityExchange() == null || secDef.getPrimarySecurityExchange() != secDef.getSecurityExchange() ) {
                tmpId.append( "." ).append( secDef.getSecurityExchange().getMIC() );
            }
        }

        tmpId.replace( (byte) ':', (byte) '_' );

        String id = tmpId.toString();

        TLC.instance().pushback( tmpId );

        return id;
    }

    public static String getUniqEquityInstId( final ExchangeCode exchangeCode, final ExchangeCode primaryExchangeCode, final String primaryTicker, final Currency ccy ) {
        ReusableString tmpId = TLC.instance().pop();

        int dotIdx = -1;

        int symBytes = primaryTicker.length();

        if ( symBytes > 0 ) {
            if ( primaryTicker.endsWith( "." ) || primaryTicker.endsWith( "/" ) ) {
                --symBytes;
            }
        }

        tmpId.copy( primaryTicker, 0, symBytes );

        tmpId.append( "." ).append( ccy );

        if ( exchangeCode != ExchangeCode.UNKNOWN ) {
            if ( primaryExchangeCode != null && primaryExchangeCode != ExchangeCode.UNKNOWN ) {
                tmpId.append( "." ).append( primaryExchangeCode.getMIC() );
            }

            if ( primaryExchangeCode == null || primaryExchangeCode != exchangeCode ) {
                tmpId.append( "." ).append( exchangeCode.getMIC() );
            }
        }

        tmpId.replace( (byte) ':', (byte) '_' );

        String id = tmpId.toString();

        TLC.instance().pushback( tmpId );

        return id;
    }

    public static String cleanPrimaryEquityTicker( String ticker ) {
        // make uniq id func call this

        // remove all "." and "/" and "'"

        ReusableString s = TLC.strPop();

        for ( int i = 0; i < ticker.length(); i++ ) {
            char c = ticker.charAt( i );

            if ( c == '.' || c == '/' ) {
                // skip
            } else if ( c == ':' ) {

                s.append( '_' );

            } else {
                s.append( c );
            }
        }

        ticker = s.toString();

        TLC.strPush( s );

        return ticker;

    }

    public static long getUniqueInstId( final SecurityDefinition def ) {

        long uniqueInstId = def.getUniqueInstId();

        if ( Utils.isNull( uniqueInstId ) ) {
            ZString cabId = getKey( def, SecurityIDSource.UniqueInstId );
            if ( cabId != null ) {
                uniqueInstId = StringUtils.parseLong( cabId );
            }
        }

        return uniqueInstId;
    }

    public static Instrument getInstMapKey( final Instrument inst ) {
        if ( inst instanceof HistExchInst ) {
            return ((HistExchInst) inst).getSeries();
        }
        return inst;
    }

    /**
     * the timestamp used to denote last modified or valid from
     * <p>
     * note MD44Decoder puts last update time in eventTimestamp as thats used in backtest for sequencing !
     *
     * @param def
     * @return
     */
    public static long getSecDefStartTimeStamp( final SecurityDefinition def ) {
        if ( def.getStartTimestamp() >= 0 ) {
            return def.getStartTimestamp();
        }
        return def.getEventTimestamp(); // time sec def was generated
    }

    public static boolean mainGFUTFilter( final Instrument instrument ) {
        EnumSet<FutureExchangeSymbol> futs = getMainFutures();

        if ( futs.size() == 0 ) {
            return false; // NO FILTER ENABLED
        }

        if ( instrument.getSecurityType() == SecurityType.Future ) {
            ExchDerivInstrument d = (ExchDerivInstrument) instrument;

            return futs.contains( d.getFutExSym() );
        }

        return false;
    }

    public static boolean shouldIgnoreUpdate( String instStrId, final SecurityDefinition sdOlder, final SecurityDefinition sdNewer, boolean ignoreDesc ) {

        double pointValueA              = sdOlder.getFutPointValue();
        double minPriceIncrementA       = sdOlder.getMinPriceIncrement();
        double minPriceIncrementAmountA = sdOlder.getMinPriceIncrementAmount();
        double displayFactorA           = sdOlder.getDisplayFactor();
        double priceRatioA              = sdOlder.getPriceRatio();
        double pricePrecisionA          = sdOlder.getPricePrecision();
        double unitOfMeasureQtyA        = sdOlder.getUnitOfMeasureQty();
        double contractMultiplierA      = sdOlder.getContractMultiplier();
        double sharesOutstandingA       = sdOlder.getSharesOutstanding();

        int tickRuleA               = sdOlder.getTickRule();
        int maturityMonthYearA      = sdOlder.getMaturityMonthYear();
        int contractMultiplierTypeA = sdOlder.getContractMultiplierType();

        long commonSecurityIdA = sdOlder.getCommonSecurityId();
        long parentCompanyIdA  = sdOlder.getParentCompanyId();
        long gicsCodeA         = sdOlder.getGicsCode();

        ViewString securityGroupA    = sdOlder.getSecurityGroup();
        ViewString securityDescA     = sdOlder.getSecurityDesc();
        ViewString securityLongDescA = sdOlder.getSecurityLongDesc();
        ViewString cfiCodeA          = sdOlder.getCFICode();
        ViewString companyNameA      = sdOlder.getCompanyName();

        SecurityType securityTypeA            = sdOlder.getSecurityType();
        ExchangeCode primarySecurityExchangeA = sdOlder.getPrimarySecurityExchange();
        Currency     currencyA                = sdOlder.getCurrency();
        Currency     settlCurrencyA           = sdOlder.getSettlCurrency();

        double pointValueB              = sdNewer.getFutPointValue();
        double minPriceIncrementB       = sdNewer.getMinPriceIncrement();
        double minPriceIncrementAmountB = sdNewer.getMinPriceIncrementAmount();
        double displayFactorB           = sdNewer.getDisplayFactor();
        double priceRatioB              = sdNewer.getPriceRatio();
        double pricePrecisionB          = sdNewer.getPricePrecision();
        double unitOfMeasureQtyB        = sdNewer.getUnitOfMeasureQty();
        double contractMultiplierB      = sdNewer.getContractMultiplier();
        double sharesOutstandingB       = sdNewer.getSharesOutstanding();

        int tickRuleB               = sdNewer.getTickRule();
        int maturityMonthYearB      = sdNewer.getMaturityMonthYear();
        int contractMultiplierTypeB = sdNewer.getContractMultiplierType();

        long commonSecurityIdB = sdNewer.getCommonSecurityId();
        long parentCompanyIdB  = sdNewer.getParentCompanyId();
        long gicsCodeB         = sdNewer.getGicsCode();

        ViewString securityGroupB    = sdNewer.getSecurityGroup();
        ViewString securityDescB     = sdNewer.getSecurityDesc();
        ViewString securityLongDescB = sdNewer.getSecurityLongDesc();
        ViewString cfiCodeB          = sdNewer.getCFICode();
        ViewString companyNameB      = sdNewer.getCompanyName();

        /**
         * @NOTE we dont want to check securityDesc or securityLongDesc  as they can be slightly different on diff feeds
         */

        SecurityType securityTypeB            = sdNewer.getSecurityType();
        ExchangeCode primarySecurityExchangeB = sdNewer.getPrimarySecurityExchange();
        Currency     currencyB                = sdNewer.getCurrency();
        Currency     settlCurrencyB           = sdNewer.getSettlCurrency();

        if ( !ignoreDesc && !securityDescA.equals( securityDescB ) ) {
            _log.log( Level.info, "isSame() " + instStrId + " securityDesc changed from " + securityDescA + " to " + securityDescB );
            return false;
        }

        if ( Utils.hasVal( pointValueB ) && !Utils.isSamePx( pointValueA, pointValueB ) ) {
            _log.log( Level.info, "isSame() " + instStrId + " pointValue changed from " + pointValueA + " to " + pointValueB );
            return false;
        }
        if ( Utils.hasVal( minPriceIncrementB ) && !Utils.isSamePx( minPriceIncrementA, minPriceIncrementB ) ) {
            _log.log( Level.info, "isSame() " + instStrId + " minPriceIncrement changed from " + minPriceIncrementA + " to " + minPriceIncrementB );
            return false;
        }
        if ( Utils.hasVal( minPriceIncrementAmountB ) && !Utils.isSamePx( minPriceIncrementAmountA, minPriceIncrementAmountB ) ) {
            _log.log( Level.info, "isSame() " + instStrId + " minPriceIncrementAmount changed from " + minPriceIncrementAmountA + " to " + minPriceIncrementAmountB );
            return false;
        }
        if ( Utils.hasVal( displayFactorB ) && !Utils.isSamePx( displayFactorA, displayFactorB ) ) {
            _log.log( Level.info, "isSame() " + instStrId + " displayFactor changed from " + displayFactorA + " to " + displayFactorB );
            return false;
        }
        if ( Utils.hasVal( priceRatioB ) && !Utils.isSamePx( priceRatioA, priceRatioB ) ) {
            _log.log( Level.info, "isSame() " + instStrId + " priceRatio changed from " + priceRatioA + " to " + priceRatioB );
            return false;
        }
        if ( Utils.hasVal( pricePrecisionB ) && !Utils.isSamePx( pricePrecisionA, pricePrecisionB ) ) {
            _log.log( Level.info, "isSame() " + instStrId + " pricePrecision changed from " + pricePrecisionA + " to " + pricePrecisionB );
            return false;
        }
        if ( Utils.hasVal( unitOfMeasureQtyB ) && !Utils.isSamePx( unitOfMeasureQtyA, unitOfMeasureQtyB ) ) {
            _log.log( Level.info, "isSame() " + instStrId + " unitOfMeasureQty changed from " + unitOfMeasureQtyA + " to " + unitOfMeasureQtyB );
            return false;
        }
        if ( Utils.hasVal( contractMultiplierB ) && !Utils.isSamePx( contractMultiplierA, contractMultiplierB ) ) {
            _log.log( Level.info, "isSame() " + instStrId + " contractMultiplier changed from " + contractMultiplierA + " to " + contractMultiplierB );
            return false;
        }

        if ( Utils.hasNonZeroVal( sharesOutstandingB ) && !Utils.isSamePx( sharesOutstandingA, sharesOutstandingB ) ) {
            if ( Utils.hasNonZeroVal( sharesOutstandingA ) ) {
                _log.log( Level.vhigh, "isSame() " + instStrId + " sharesOutstanding changed from " + sharesOutstandingA + " to " + sharesOutstandingB + " check for corp action split !" );
            }
            return false;
        }

        if ( Utils.hasNonZeroVal( tickRuleB ) && tickRuleA != tickRuleB ) {
            _log.log( Level.info, "isSame() " + instStrId + " tickRule changed from " + tickRuleA + " to " + tickRuleB );
            return false;
        }
        if ( Utils.hasNonZeroVal( maturityMonthYearB ) && maturityMonthYearA != maturityMonthYearB ) {
            _log.log( Level.info, "isSame() " + instStrId + " maturityMonthYear changed from " + maturityMonthYearA + " to " + maturityMonthYearB );
            return false;
        }
        if ( Utils.hasNonZeroVal( contractMultiplierTypeB ) && contractMultiplierTypeA != contractMultiplierTypeB ) {
            _log.log( Level.info, "isSame() " + instStrId + " contractMultiplierType changed from " + contractMultiplierTypeA + " to " + contractMultiplierTypeB );
            return false;
        }

        if ( Utils.hasNonZeroVal( commonSecurityIdB ) && commonSecurityIdA != commonSecurityIdB ) {
            _log.log( Level.info, "isSame() " + instStrId + " commonSecurityId changed from " + commonSecurityIdA + " to " + commonSecurityIdB );
            return false;
        }
        if ( Utils.hasNonZeroVal( parentCompanyIdB ) && parentCompanyIdA != parentCompanyIdB ) {
            _log.log( Level.info, "isSame() " + instStrId + " parentCompanyId changed from " + parentCompanyIdA + " to " + parentCompanyIdB );
            return false;
        }
        if ( Utils.hasNonZeroVal( gicsCodeB ) && gicsCodeA != gicsCodeB ) {
            _log.log( Level.info, "isSame() " + instStrId + " gicsCode changed from " + gicsCodeA + " to " + gicsCodeB );
            return false;
        }

        if ( securityTypeB != null && securityTypeB != SecurityType.Unknown && securityTypeA != securityTypeB ) {
            _log.log( Level.WARN, "isSame() " + instStrId + " securityType changed from " + N( securityTypeA ) + " to " + N( securityTypeB ) );
            return false;
        }
        if ( primarySecurityExchangeB != null && primarySecurityExchangeB != ExchangeCode.UNKNOWN && primarySecurityExchangeA != primarySecurityExchangeB ) {
            _log.log( Level.WARN, "isSame() " + instStrId + " primarySecurityExchange changed from " + N( primarySecurityExchangeA ) + " to " + N( primarySecurityExchangeB ) );
            return false;
        }
        if ( currencyB != null && currencyB != Currency.Unknown && currencyA != currencyB ) {
            _log.log( Level.WARN, "isSame() " + instStrId + " currency changed from " + N( currencyA ) + " to " + N( currencyB ) );
            return false;
        }
        if ( settlCurrencyB != null && settlCurrencyB != Currency.Unknown && settlCurrencyA != settlCurrencyB ) {
            _log.log( Level.info, "isSame() " + instStrId + " settlCurrency changed from " + N( settlCurrencyA ) + " to " + N( settlCurrencyB ) );
            return false;
        }

        ZString newSId = sdNewer.getSecurityID();
        ZString oldSId = InstUtils.getKey( sdOlder, sdNewer.getSecurityIDSource() );

        if ( !newSId.equals( oldSId ) ) {
            _log.log( Level.info, "isSame() " + instStrId + " key  " + sdNewer.getSecurityIDSource().name() + " unmatched now value " + newSId + ", was " + ((oldSId == null) ? null : oldSId.toString()) );
            return false;
        }

        ZString newId = InstUtils.getKey( sdNewer, SecurityIDSource.InternalString );
        ZString oldId = InstUtils.getKey( sdOlder, SecurityIDSource.InternalString );

        if ( !newId.equals( oldId ) ) {
            _log.log( Level.info, "isSame() " + instStrId + " " + sdNewer.getSecurityIDSource().name() + " unmatched now value " + newId + ", was " + ((oldId == null) ? null : oldId.toString()) );
            return false;
        }

        SecurityAltIDImpl securityAltIDs = (SecurityAltIDImpl) sdNewer.getSecurityAltIDs();

        while( securityAltIDs != null ) {

            final ZString          id    = securityAltIDs.getSecurityAltID();
            final SecurityIDSource idSrc = securityAltIDs.getSecurityAltIDSource();

            if ( InstUtils.unmatched( sdOlder, idSrc, id ) ) {
                ZString oldKey = InstUtils.getKey( sdOlder, idSrc );
                _log.log( Level.info, "isSame() " + instStrId + " key  " + idSrc.name() + " unmatched now value " + id.toString() + ", was " + ((oldKey == null) ? null : oldKey.toString()) );
                return false;
            }

            securityAltIDs = securityAltIDs.getNext();
        }

        SecDefEventImpl newSDEvents = (SecDefEventImpl) sdNewer.getEvents();

        while( newSDEvents != null ) {

            long            newDate = newSDEvents.getEventDate();
            SecDefEventType newType = newSDEvents.getEventType();

            SecDefEventImpl sdOldEvents = (SecDefEventImpl) sdOlder.getEvents();

            while( sdOldEvents != null ) {

                SecDefEventType oldType = sdOldEvents.getEventType();

                if ( oldType == newType && newType != SecDefEventType.Activation ) { // dont care about activation date
                    long oldDate = sdOldEvents.getEventDate();

                    if ( oldDate != newDate ) {
                        _log.log( Level.info, "isSame() " + instStrId + " date  " + newType.name() + " unmatched now value " + newDate + " was " + oldDate );
                        return false;
                    } else {
                        break;
                    }
                }

                sdOldEvents = sdOldEvents.getNext();
            }

            newSDEvents = newSDEvents.getNext();
        }

        return true;
    }

    private static String N( final Enum<?> e ) {
        return e == null ? "<null>" : e.name();
    }

    private static EnumSet<FutureExchangeSymbol> getMainFutures() {
        if ( _mainFuts.size() == 0 ) {
            AppProps.instance().getPropertySet( "FILTER_KEEP_FUTS", FutureExchangeSymbol.class, _mainFuts );
        }

        return _mainFuts;
    }

    public static void addMissingKeys( final SecurityDefinitionImpl src, final SecurityDefinitionImpl dest ) {
        if ( getKey( dest, src.getSecurityIDSource() ) == null ) {
            addKey( dest, src.getSecurityIDSource(), src.getSecurityID().toString() );
        }

        SecurityAltIDImpl altExistIds = (SecurityAltIDImpl) src.getSecurityAltIDs();

        while( altExistIds != null ) {

            if ( getKey( dest, altExistIds.getSecurityAltIDSource() ) == null ) {
                addKey( dest, altExistIds.getSecurityAltIDSource(), altExistIds.getSecurityAltID().toString() );
            }

            altExistIds = altExistIds.getNext();
        }
    }

    public static double getTickSize( final Instrument i, double px ) {
        double ts = Constants.UNSET_DOUBLE;

        if ( i instanceof ExchangeInstrument ) {
            ExchangeInstrument ei = (ExchangeInstrument) i;

            if ( ei.getSecurityType() == SecurityType.Future ) {

                final SecurityDefinition secDef = ((DerivInstSecDefWrapper) ei).getSecDef();

                ts = secDef.getMinPriceIncrement() * secDef.getDisplayFactor();

            } else {
                final TickType tt = ei.getTickType();

                if ( tt != null ) {
                    ts = tt.tickSize( px ); // equities
                }
            }
        }

        return ts;
    }

    public static ExchangeCode getPrimary( final SecurityDefinition sd ) {
        ExchangeCode ex = sd.getPrimarySecurityExchange();

        if ( ex == null ) ex = sd.getSecurityExchange();

        return ex;
    }
}
