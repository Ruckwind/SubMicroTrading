/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.inst;

import com.rr.core.lang.*;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.*;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.StringUtils;
import com.rr.core.utils.Utils;
import com.rr.model.generated.internal.events.impl.SecDefEventImpl;
import com.rr.model.generated.internal.events.impl.SecurityAltIDImpl;
import com.rr.model.generated.internal.events.impl.SecurityDefinitionImpl;
import com.rr.model.generated.internal.events.impl.SecurityStatusImpl;
import com.rr.model.generated.internal.events.interfaces.SecDefLeg;
import com.rr.model.generated.internal.events.interfaces.SecurityDefinition;
import com.rr.model.generated.internal.events.interfaces.SecurityStatus;
import com.rr.model.generated.internal.type.SecurityTradingStatus;
import com.rr.model.generated.internal.type.SecurityUpdateAction;
import com.rr.om.dummy.warmup.TradingRangeImpl;

/**
 * base class for all future and equity and exchange traded instruments
 * <p>
 * persistent state all within SecurityDefinitionImpl
 */
public class InstrumentSecurityDefWrapperImpl implements InstrumentSecurityDefWrapper {

    public static final  TickType UNKNOWN_TICK_TYPE = new UnknownTickSize();
    private static final Logger   _log              = LoggerFactory.create( InstrumentSecurityDefWrapperImpl.class );
    private static final int      SEGMENT_UNKNOWN   = 0;
    private static       long     _nextTranId       = System.currentTimeMillis();

    private final CommonInstrument _commonInstrument;

    private SecurityDefinitionImpl _secDef;
    private SecurityStatusImpl     _lastStatus;
    private SecurityTradingStatus  _securityTradingStatus = SecurityTradingStatus.Unknown;
    private Exchange               _exchange;
    private ZString                _exSym;
    private ZString                _isin;
    private ZString                _figi;

    private String _id; // required for cases when no secdef present ... eg placeholder insts

    private TickType     _ts      = UNKNOWN_TICK_TYPE;
    private TradingRange _tr;
    private int          _segment = SEGMENT_UNKNOWN;

    private int              _secGrpId         = 0;
    private TradeRestriction _tradeRestriction = null;
    private boolean          _enabled          = true;

    private transient int              _hashCode;
    private           LoanAvailability _loanAvailability = null;

    public InstrumentSecurityDefWrapperImpl( Exchange exchange, SecurityDefinitionImpl secDef, CommonInstrument commonInstrument ) {
        if ( exchange == null ) {
            throw new SMTRuntimeException( "Cannot create InstrumentSecurityDefWrapper without exchange " + secDef );
        }

        _commonInstrument = commonInstrument;
        _exchange         = exchange;
        setSecurityDefinition( secDef );
        _tr = new TradingRangeImpl();
    }

    @Override public void dump( ReusableString out ) {
        out.append( "id=" ).append( id() );
        out.append( ", secDes=" ).append( _secDef.getSecurityDesc() );
        out.append( ", exSym=" ).append( _exSym ).append( ", " );
        out.append( ", sym=" ).append( _secDef.getSymbol() );
        out.append( ", MIC=" ).append( _secDef.getSecurityExchange() );
        if ( _figi != null && _figi.length() > 0 ) out.append( ", figi=" ).append( _figi ).append( ", " );
        _secDef.dump( out );
    }

    @Override public final Currency getCurrency()                                        { return _secDef.getCurrency(); }

    @Override public final ZString getExchangeSymbol()                                   { return _exSym; }

    @Override public LoanAvailability getLoanAvailability()                              { return _loanAvailability; }

    @Override public void setLoanAvailability( final LoanAvailability loanAvailability ) { _loanAvailability = loanAvailability; }

    @Override public final ExchangeCode getPrimaryExchangeCode() {
        ExchangeCode code = _secDef.getPrimarySecurityExchange();

        return code == null ? getExchange().getExchangeCode() : code;
    }

    @Override public ExchangeCode getSecurityExchange() {
        final ExchangeCode code = getSecDef().getSecurityExchange();

        if ( code != null ) {
            return code;
        }

        return _exchange.getExchangeCode();
    }

    @Override public final ZString getSecurityGroup()             { return _secDef.getSecurityGroup(); }

    @Override public final SecurityType getSecurityType()         { return _secDef.getSecurityType(); }

    @Override public final ZString getSymbol()                    { return _secDef.getSymbol(); }

    @Override public final TradeRestriction getTradeRestriction() { return _tradeRestriction; }

    @Override public boolean isDead() {
        boolean isDead = false;

        final SecurityDefinitionImpl sd = getSecDef();

        if ( sd != null ) {

            isDead = sd.isFlagSet( MsgFlag.DeprecatedData );

            if ( !isDead ) {
                long deadMS = sd.getDeadTimestamp();

                isDead = (deadMS > 0) && (deadMS < ClockFactory.get().currentTimeMillis());
            }

            if ( !isDead ) {
                TradeRestriction tradeRestriction = getTradeRestriction();

                if ( tradeRestriction != null ) {
                    isDead = tradeRestriction.isTradeFlagSet( TradeRestrictionFlag.Deprecated );
                }
            }
        }

        return isDead;
    }

    @Override public void setTradeRestriction( final TradeRestriction restricted ) { _tradeRestriction = restricted; }

    @Override public final long getEventTimestamp()                                { return _secDef.getEventTimestamp(); }

    @Override public final ZString getISIN()                                       { return _isin; }

    @Override public final ZString getFIGI()                                       { return _figi; }

    @Override public final SecurityStatusImpl getLastStatus() {
        return _lastStatus;
    }

    @Override public final void setLastStatus( final SecurityStatusImpl lastStatus ) {
        _lastStatus = lastStatus;
    }

    @Override public final SecurityDefinitionImpl getSecDef() {
        return _secDef;
    }

    @Override public final SecurityTradingStatus getSecurityTradingStatus() {
        return _securityTradingStatus;
    }

    @Override public final void setSecurityTradingStatus( SecurityTradingStatus securityTradingStatus ) {
        _securityTradingStatus = securityTradingStatus;
    }

    @Override public final void setPlaceHolderDefinition( final SecDefLeg def ) {
        _exSym = _isin = null;

        _id = "";

        if ( def != null ) {
            _id = def.getLegSecurityID().toString();

            if ( def.getLegSecurityIDSource() == SecurityIDSource.ExchangeSymbol ) setExchangeLongId( def.getLegSymbol(), def.getLegSecurityID() );

            ReusableString legId = new ReusableString( _id );

            setIdBySrc( legId, def.getLegSecurityIDSource() );

            setHash();
        }
    }

    @Override public final void setSecurityDefinition( final SecurityDefinitionImpl def ) {

        InstUtils.secDefOverrides( def );

        _secDef               = def;
        _figi                 = null;
        _exSym                = _isin = null;

        if ( def == null ) return;

        if ( def.getSecurityTradingStatus() != null ) {
            _securityTradingStatus = def.getSecurityTradingStatus();
        }

        ZString bloombergCode   = null;
        ZString bloombergTicker = null;

        _exSym = def.getSymbol(); // defauylt excgabfe symbol to symbol

        SecurityAltIDImpl securityAltIDs = (SecurityAltIDImpl) def.getSecurityAltIDs();

        while( securityAltIDs != null ) {

            final ZString          id    = securityAltIDs.getSecurityAltID();
            final SecurityIDSource idSrc = securityAltIDs.getSecurityAltIDSource();

            setIdBySrc( id, idSrc );

            switch( idSrc ) {
            case BloombergTicker:
                bloombergTicker = id;
                break;
            case BloombergCode:
                bloombergCode = id;
                break;
            }

            securityAltIDs = securityAltIDs.getNext();
        }

        if ( def.getSymbol().length() == 0 && bloombergTicker != null ) {
            ReusableString symbol = def.getSymbolForUpdate();

            StringUtils.copyMinusChar( bloombergTicker, ' ', symbol );
        }

        setIdBySrc( def.getSecurityID(), def.getSecurityIDSource() );

        if ( def != null && def.getSecurityDesc().length() == 0 ) {

            ZString secDes;

            secDes = (bloombergCode != null) ? bloombergCode : _secDef.getSecurityID();

            def.getSecurityDescForUpdate().copy( secDes );
        }

        setExchangeLongId( def.getSymbol(), _exSym );

        checkCFICode( def );

        _secGrpId = StringUtils.parseInt( def.getSecurityGroup(), 0 );

        _segment = StringUtils.parseInt( def.getApplID(), SEGMENT_UNKNOWN );

        checkSymbolOverride();

        determineTickScale();

        otherOverrides();

        setHash();
    }

    @Override public final ZString getSecurityDesc()              { return _secDef.getSecurityDesc(); }

    @Override public final int getBookLevels()                    { return 10; }

    @Override public final CommonInstrument getCommonInstrument() { return _commonInstrument; }

    @Override public long getEndTimestamp()                       { return _secDef.getEndTimestamp(); }

    @Override public final Exchange getExchange()                 { return _exchange; }

    @Override public final long getExchangeLongId()               { return _secDef.getExchangeLongId(); }

    @Override public final ExchangeSession getExchangeSession()   { return _exchange.getSession(); }

    @Override final public InstrumentEvent getInstrumentEvent( int idx ) {
        int numEvents = getNumEvents();
        if ( idx < 0 || idx > numEvents ) return null;
        @SuppressWarnings( "unchecked" )
        SecDefEventImpl event = (SecDefEventImpl) _secDef.getEvents();
        while( idx > 0 ) {
            event = event.getNext();
            --idx;
        }
        return event;
    }

    @Override public final int getIntSegment() { return _segment; }

    @Override public final void getKey( final SecurityIDSource src, final ReusableString dest ) {
        switch( src ) {
        case Symbol:
            dest.copy( _secDef.getSymbol() );
            return;
        case ExchangeSymbol:
            dest.copy( _exSym );
            return;
        case ISIN:
            dest.copy( _isin );
            return;
        case UniqueInstId:
            dest.copy( _secDef.getUniqueInstId() );
            return;
        case FIGI:
            dest.copy( _figi );
            return;
        case SecurityDesc:
            dest.copy( _secDef.getSecurityDesc() );
            return;
        case BloombergTicker:
        case BloombergCode:
        case PrimaryMarketSymbol:
        case PrimaryBloombergCode:
        case DEAD_1:
        case DEAD_2:
        case RIC:
        case Unknown:
            break;
        }

        final ZString v = InstUtils.getKey( getSecDef(), src );

        dest.copy( v );
    }

    @Override public final String getKey( final SecurityIDSource src ) {
        switch( src ) {
        case Symbol:
            return _secDef.getSymbol().toString();
        case ExchangeSymbol:
            return _exSym.toString();
        case UniqueInstId:
            return "" + _secDef.getUniqueInstId();
        }

        final ZString v = InstUtils.getKey( getSecDef(), src );

        return (v == null) ? null : v.toString();
    }

    @Override public final int getMinQty()                  { return _secDef.getMinQty(); }

    @Override public final int getNumEvents()               { return _secDef.getNoEvents(); }

    @Override public final int getSecurityGroupId()         { return _secGrpId; }

    @Override public ViewString getSecurityID()             { return getSecDef().getSecurityID(); }

    @Override public SecurityIDSource getSecurityIDSource() { return getSecDef().getSecurityIDSource(); }

    @Override public long getStartTimestamp() {
        return Utils.hasVal( _secDef.getStartTimestamp() ) ? _secDef.getStartTimestamp() : _secDef.getEventTimestamp();
    }

    @Override public final TickType getTickType()              { return _ts; }

    @Override public final UnitOfMeasure getUnitOfMeasure()    { return _secDef.getUnitOfMeasure(); }

    @Override public final double getUnitOfMeasureQuantity()   { return _secDef.getUnitOfMeasureQty(); }

    @Override public final TradingRange getValidTradingRange() { return _tr; }

    @Override public final boolean hasChanged( final Object source ) {
        if ( source == this ) return false;

        if ( source instanceof SecurityDefinition ) {
            return _secDef == source;
        }

        if ( source instanceof SecurityStatus ) {
            return _lastStatus == source;
        }

        return false;
    }

    @Override public final boolean isDeleted() {
        return _secDef.getSecurityUpdateAction() == SecurityUpdateAction.Delete;
    }

    @Override public final boolean isEnabled()      { return _enabled; }

    @Override public void setEnabled( final boolean enabled ) { _enabled = enabled; }

    @Override public boolean isFlagSet( final MsgFlag flag ) {
        return getSecDef().isFlagSet( flag );
    }

    @Override public final boolean isTestInstrument()            { return false; }

    @Override public final void setTickType( final TickType ts ) { _ts = ts; }

    @Override public int hashCode() {
        long cabId = getUniqueInstId();

        if ( Utils.hasNonZeroVal( cabId ) ) {
            return Long.hashCode( cabId );
        }

        return _hashCode;
    }

    @Override public boolean equals( final Object o ) {
        if ( this == o ) return true;
        if ( o == null || !(o instanceof Instrument) ) return false;
        final Instrument that = (Instrument) o;

        long cabId = getUniqueInstId();

        if ( Utils.hasNonZeroVal( cabId ) ) {
            return cabId == that.getUniqueInstId();
        }

        String id = id();

        if ( id != null && id.length() > 0 ) {
            return id.equals( that.id() );
        }

        return super.equals( o );
    }

    @Override public final String toString() {
        ReusableString s = TLC.instance().pop();
        dump( s );
        String rs = s.toString();
        TLC.instance().pushback( s );
        return rs;
    }

    @Override public final String id() { return _id; }

    @Override public boolean isSame( Identifiable that ) {
        return equals( that );
    }

    @Override public final long getUniqueInstId()                                        { return _secDef.getUniqueInstId(); }

    public void setId( final String id ) {
        _id = id;
        if ( _secDef != null && id != null && id.length() > 0 ) {
            InstUtils.addKey( _secDef, SecurityIDSource.InternalString, id );
        }
        setHash();
    }

    protected void otherOverrides() { /* nothing */ }

    private void checkCFICode( SecurityDefinitionImpl def ) {
        SecurityType st = def.getSecurityType();

        if ( st == null ) {
            ZString cfi = def.getCFICode();

            if ( cfi != null ) {
                byte type = cfi.getByte( 0 );

                if ( type == 'F' ) {
                    def.setSecurityType( SecurityType.Future );
                } else if ( type == 'O' ) {
                    def.setSecurityType( SecurityType.Option );
                } else if ( type == 'T' && cfi.getByte( 1 ) == 'I' ) {
                    def.setSecurityType( SecurityType.Index );
                }
            }
        }
    }

    private void checkSymbolOverride() {
        if ( _exSym == _secDef.getSymbol() ) { // using default symbol
            if ( _secDef.getSecurityType() == SecurityType.Future ) {
                ReusableString copy = TLC.safeCopy( _exSym ).append( _secDef.getMaturityMonthYear() );

                // this is a GC leak

                _exSym = copy;
            }
        }
    }

    private void determineTickScale() {
        // TBD
    }

    private void setExchangeLongId( final ZString sym, final ZString exSym ) {
        if ( _exchange.isExchangeSymbolLongId() && _exSym != null && _secDef != null && Utils.isNull( _secDef.getExchangeLongId() ) ) {
            try {
                long exchangeLongId = StringUtils.parseLongNoException( _exSym );

                _secDef.setExchangeLongId( exchangeLongId );

            } catch( Exception e ) {
                // ignore
            }
        }
    }

    private void setHash() {
        long cabId = getUniqueInstId();

        int hash = 0;

        if ( Utils.hasNonZeroVal( cabId ) ) {
            hash = Long.hashCode( cabId );
        } else {
            String id = id();

            if ( id.length() > 0 ) {
                hash = id.hashCode();
            } else {
                hash = super.hashCode();
            }
        }

        _hashCode = hash;
    }

    private void setIdBySrc( final ZString id, final SecurityIDSource idSrc ) {
        if ( id.length() > 0 ) {
            switch( idSrc ) {
            case ExchangeSymbol:
                _exSym = id;
                break;
            case ISIN:
                _isin = id;
                break;
            case FIGI:
                _figi = id;
                break;
            case InternalString:
                _id = id.toString();
                setHash();
                break;
            case UniqueInstId:
            case BloombergTicker:
            case PrimaryMarketSymbol:
            case PrimaryBloombergCode:
            case BloombergCode:
            case DEAD_1:
            case DEAD_2:
            case RIC:
            case Unknown:
            default:
                break;
            }
        }
    }
}
