/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.dummy.warmup;

import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.model.*;
import com.rr.core.utils.StringUtils;
import com.rr.om.exchange.ExchangeManager;
import com.rr.om.model.instrument.InstrumentWrite;

public class DummyInstrument implements InstrumentWrite {

    public static final DummyInstrument  DUMMY              = dummyInstFactory( "DummyId" );
    private static      int              _nextKey           = 1000000;
    private final       ReusableString   _exchangeSym;
    private final       ReusableString   _internalKey;
    private final       ZString          _country           = new ViewString( "GB" );
    private final       TradingRange     _dummyBand;
    private final       boolean          _testSymbol;
    private             SecurityIDSource _idSrc;
    private             ZString          _securityId;
    private             int              _intSegment;
    private             Currency         _ccy;
    private             Exchange         _exchange;
    private             TickType         _tickType          = new FixedTickSize( 0.000001 );
    private             TradeRestriction _tradeRestriction  = null;
    private             LoanAvailability _loanAvailability  = null;
    private             boolean          _enabled           = true;
    private             int              _bookLevels        = 0;
    private             ExchangeCode     _primaryExchangeCode;
    private             CommonInstrument _commonInstrument;
    private             long             _longId            = Constants.UNSET_LONG;
    private             int              _maturityMonthYear = 0;
    private             String           _id;

    public static DummyInstrument dummyInstFactory( String id ) {
        return new DummyInstrument( new ViewString( id ), DummyExchange.DUMMY, Currency.Other, true, ExchangeInstrument.DUMMY_INSTRUMENT_ID );
    }

    private static long nextKey() {
        return ++_nextKey;
    }

    public static DummyInstrument newDummy( final ZString securityId, final SecurityIDSource idSrc, final ExchangeCode securityExchange ) {
        Exchange        ex = ExchangeManager.instance().getByCode( securityExchange );
        DummyInstrument d  = new DummyInstrument( securityId, ex, Currency.Other, true, securityId, idSrc );
        return d;
    }

    public static int encodeSymbolToId( ZString securityId ) {

        int id = 0;

        try {
            id = Integer.parseInt( securityId.toString() );
        } catch( NumberFormatException e ) {
            byte[] idBytes = securityId.getBytes();

            int offset = securityId.getOffset();
            int max    = offset + 4;

            for ( int i = offset; i < max; i++ ) {
                byte b = idBytes[ i ];
                if ( b == '.' ) {
                    break;
                }
                id = (id << 8) + (0xFF & b);
            }
        }

        return id;
    }

    public DummyInstrument( ZString securityId, Exchange exchange, Currency currency ) {
        this( securityId, exchange, currency, false, securityId );
    }

    public DummyInstrument( ZString securityId, Exchange exchange, Currency currency, boolean isTestSymbol, ZString id ) {
        _exchangeSym = new ReusableString( securityId );
        _internalKey = new ReusableString( id );

        _ccy        = currency;
        _exchange   = exchange;
        _dummyBand  = new TradingRangeImpl();
        _testSymbol = isTestSymbol;

        if ( exchange.isExchangeSymbolLongId() ) {
            try {
                _longId = StringUtils.parseLong( id );
            } catch( Exception e ) {
                // dont care
            }
        }

        _primaryExchangeCode = exchange.getExchangeCode();

        _id = securityId.toString();

        if ( _longId == Constants.UNSET_LONG ) _longId = nextKey();
    }

    public DummyInstrument( ZString securityId, Exchange ex, Currency ccy, boolean isTestSymbol, ZString id, SecurityIDSource idSrc ) {
        this( securityId, ex, ccy, isTestSymbol, id );
        _idSrc      = idSrc;
        _securityId = securityId;
    }

    @Override public void dump( final ReusableString out ) {
        out.append( "DummyInstrument exSym=" ).append( _exchangeSym );
    }

    @Override public ViewString getExchangeSymbol() {
        return _exchangeSym;
    }

    @Override public LoanAvailability getLoanAvailability() { return _loanAvailability; }

    @Override public ExchangeCode getPrimaryExchangeCode() { return _primaryExchangeCode; }

    @Override public ExchangeCode getSecurityExchange()    { return _exchange.getExchangeCode(); }

    @Override public ZString getSecurityGroup() {
        return _exchangeSym;
    }

    @Override public SecurityType getSecurityType() {
        return SecurityType.Cash;
    }

    @Override public TradeRestriction getTradeRestriction() { return _tradeRestriction; }

    @Override public long getUniqueInstId() { return _longId; }

    @Override public boolean isDead()                      { return false; }

    @Override public void setTradeRestriction( final TradeRestriction tradeRestriction ) { _tradeRestriction = tradeRestriction; }

    public void setPrimaryExchangeCode( final ExchangeCode primaryExchangeCode )         { _primaryExchangeCode = primaryExchangeCode; }

    @Override public void setLoanAvailability( final LoanAvailability loanAvailability ) { _loanAvailability = loanAvailability; }

    @Override public Currency getCurrency() {
        return _ccy;
    }

    @Override public ZString getSymbol() {
        return _exchangeSym;
    }

    @Override public Exchange getExchange() {
        return _exchange;
    }

    @Override public ZString getISIN() {
        return null;
    }

    @Override public TickType getTickType() {
        return _tickType;
    }

    @Override public ZString getCountry() {
        return _country;
    }

    @Override public ZString getCusip() {
        return null;
    }

    @Override public long getLotSize() {
        return 0;
    }

    @Override public ZString getMarket() {
        return null;
    }

    @Override public ZString getMarketSector() {
        return null;
    }

    @Override public ZString getMarketSegment() {
        return null;
    }

    @Override public PriceToleranceLimits getPriceToleranceLimits() {
        return null;
    }

    @Override public ZString getSecurityID( SecurityIDSource idsource ) {

        if ( SecurityIDSource.ExchangeSymbol == idsource ) {
            return _exchangeSym;
        }

        if ( _idSrc == idsource ) return _securityId;

        return null;
    }

    @Override public Currency getSettlementCurrency() {
        return _ccy;
    }

    @Override public boolean isPrimaryInstrument() {
        return false;
    }

    @Override public void setTickType( TickType t ) {
        _tickType = t;
    }

    @Override public void setExchange( Exchange ex ) {
        _exchange = ex;
    }

    @Override public void setCurrency( Currency ccy ) {
        _ccy = ccy;
    }

    @Override public long getEventTimestamp()                                            { return Constants.UNSET_LONG; }

    @Override public ZString getSecurityDesc() {
        return _exchangeSym;
    }

    @Override public int getBookLevels() {
        return _bookLevels;
    }

    @Override public void setBookLevels( int bookLevels ) {
        _bookLevels = bookLevels;
    }

    @Override public CommonInstrument getCommonInstrument()                              { return _commonInstrument; }

    @Override public long getEndTimestamp()                                              { return Constants.UNSET_LONG; }

    @Override public long getExchangeLongId()      { return _longId; }

    @Override public ExchangeSession getExchangeSession() {
        return _exchange.getExchangeSession( getMarketSegment() );
    }

    @Override public InstrumentEvent getInstrumentEvent( final int idx ) { return null; }

    @Override public int getIntSegment() {
        return _intSegment;
    }

    public void setIntSegment( int intSegment ) {
        _intSegment = intSegment;
    }

    @Override public void getKey( final SecurityIDSource src, final ReusableString dest ) {
        switch( src ) {
        case ISIN:
            break;
        case ExchangeSymbol:
            dest.copy( _exchangeSym );
            return;
        case ExchangeLongId:
            break;
        case SecurityDesc:
            break;
        case BloombergTicker:
        case PrimaryMarketSymbol:
        case PrimaryBloombergCode:
        case BloombergCode:
        case UniqueInstId:
        case FIGI:
        case DEAD_1:
        case DEAD_2:
        case RIC:
        case Unknown:
            break;
        case Symbol:
            break;
        case InternalString:
            break;
        case StrategyId:
            break;
        }
        return;
    }

    @Override public String getKey( final SecurityIDSource src ) {
        return null;
    }

    @Override public final int getMinQty() {
        return 1;
    }

    @Override public int getNumEvents()                                  { return 0; }

    @Override public int getSecurityGroupId() {
        return 0;
    }

    @Override public ViewString getSecurityID()                                          { return _exchangeSym; }

    @Override public SecurityIDSource getSecurityIDSource()                              { return SecurityIDSource.ExchangeSymbol; }

    @Override public long getStartTimestamp()                                            { return 0; }

    @Override public UnitOfMeasure getUnitOfMeasure()  { return null; }

    @Override public double getUnitOfMeasureQuantity() { return 0; }

    @Override public TradingRange getValidTradingRange() {
        return _dummyBand;
    }

    @Override public boolean hasChanged( final Object source ) {
        return true;
    }

    @Override public boolean isDeleted() { return false; }

    @Override public boolean isEnabled() {
        return _enabled;
    }

    @Override public boolean isFlagSet( final MsgFlag flag ) { return false; }

    @Override public boolean isTestInstrument() {
        return _testSymbol;
    }

    @Override public void setEnabled( boolean isEnabled ) {
        _enabled = isEnabled;
    }

    @Override public void setCommonInstrument( final CommonInstrument commonInstrument ) { _commonInstrument = commonInstrument; }

    @Override public String id()                             { return _id; }
}
