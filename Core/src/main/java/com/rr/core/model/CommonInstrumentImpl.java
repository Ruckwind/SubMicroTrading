package com.rr.core.model;

import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TLC;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.Utils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * for represents an instrument that may be tradeable on multiple exchanges
 * <p>
 * pnl / positions for fungible assets in same ccy to be held at level of common instrument
 */
public class CommonInstrumentImpl implements CommonInstrument {

    private static final Logger                                              _log      = LoggerFactory.create( CommonInstrumentImpl.class );
    private final        ParentCompany                                       _parentCompany;
    private final        String                                              _id;
    private              ExchangeCode                                        _primaryExchangeCode;
    private              Currency                                            _ccy;
    private              long                                                _commonInstrumentId;
    private              ExchangeInstrument                                  _primaryListing;
    private              ConcurrentHashMap<ExchangeCode, ExchangeInstrument> _listings = new ConcurrentHashMap<>( 4 );
    private              long                                                _created;
    private              TradeRestriction                                    _tradeRestriction;
    private              LoanAvailability                                    _loanAvailability;

    public CommonInstrumentImpl( ParentCompany parentCompany, ExchangeCode primary, Currency ccy, long commonInstrumentId, long startTimestamp ) {
        _parentCompany       = parentCompany;
        _primaryExchangeCode = primary;
        _ccy                 = ccy;
        _commonInstrumentId  = commonInstrumentId;
        _id                  = "CMN_" + parentCompany.getCompanyName() + "_" + commonInstrumentId;
        _created             = startTimestamp;
    }

    @Override public void attach( ExchangeInstrument inst ) {

        if ( _primaryListing != null && inst.getUniqueInstId() == _primaryListing.getUniqueInstId() ) {

            if ( inst.getCurrency() != _ccy || inst.getPrimaryExchangeCode() != _primaryExchangeCode ) {

                _log.log( Level.high, "CommonInstrumentImpl commonInstId=" + _commonInstrumentId + " new primary inst with different exchange " + inst.id() +
                                      ", prev was " + _primaryListing.id() );
            }

            _ccy                 = inst.getCurrency();
            _primaryExchangeCode = inst.getPrimaryExchangeCode();
            _primaryListing      = inst;

        } else {

            if ( inst.getCurrency() != _ccy ) {
                _log.warn( "CommonInstrumentImpl commonInstId=" + _commonInstrumentId + " instrument ccy mismatch, expected ccy to be " + _ccy +
                           " : instrument=" + inst.toString() );

                if ( inst.getPrimaryExchangeCode() == inst.getExchange().getExchangeCode() ) {
                    _ccy = inst.getCurrency();
                }
            }

            ExchangeCode instPrimCode = inst.getPrimaryExchangeCode();

            if ( instPrimCode != _primaryExchangeCode ) {
                _log.warn( "CommonInstrumentImpl commonInstId=" + _commonInstrumentId + " instrument mismatch, expected primaryExchange to be " + _primaryExchangeCode +
                           " : instrument=" + inst.toString() );

                if ( inst.getPrimaryExchangeCode() == inst.getExchange().getExchangeCode() ) {
                    _primaryExchangeCode = instPrimCode;
                }
            }

            ExchangeInstrument old = _listings.put( inst.getExchange().getExchangeCode(), inst );

            if ( inst.getPrimaryExchangeCode() == inst.getExchange().getExchangeCode() ) {
                _primaryListing = inst;
            }
        }
    }

    @Override public void detach( final ExchangeInstrument inst ) {
        ExchangeInstrument ei = _listings.get( inst.getExchange().getExchangeCode() );
        if ( ei == inst ) {
            _listings.remove( inst.getExchange().getExchangeCode() );
        }
    }

    @Override public Currency getCcy()            { return _ccy; }

    @Override public long getCommonInstrumentId() { return _commonInstrumentId; }

    /**
     * @param code
     * @return the listing for specified exchange code
     */
    @Override public ExchangeInstrument getListing( ExchangeCode code ) { return _listings.get( code ); }

    /**
     * @param dest list to copy all of the listed ExchangeInstruments into
     */
    @Override public void getListings( List<ExchangeInstrument> dest ) {
        if ( _listings != null ) {
            dest.addAll( _listings.values() );
        } else {
            _log.log( Level.trace, id() + " getListings but listings is null" );
        }
    }

    /**
     * @return the ParentCompany or null if none (eg future)
     */
    @Override public ParentCompany getParentCompany() { return _parentCompany; }

    @Override public ExchangeInstrument getPrimaryListing() { return _primaryListing; }

    @Override public void dump( final ReusableString out )                               { out.append( id() ); }

    @Override public Currency getCurrency()                 { return _ccy; }

    @Override public ZString getExchangeSymbol()    { return getSymbol(); }

    @Override public LoanAvailability getLoanAvailability() { return _loanAvailability; }

    @Override public ExchangeCode getPrimaryExchangeCode() { return _primaryExchangeCode; }

    @Override public ZString getSecurityDesc() {
        if ( _primaryListing != null ) return _primaryListing.getSecurityDesc();
        return null;
    }

    @Override public ExchangeCode getSecurityExchange() {
        return _primaryExchangeCode;
    }

    @Override public SecurityType getSecurityType() { return SecurityType.None; }

    @Override public ZString getSymbol() {
        if ( _primaryListing != null ) return _primaryListing.getSymbol();
        return null;
    }

    @Override public TradeRestriction getTradeRestriction() { return _tradeRestriction; }

    @Override public boolean isDead() {
        return getTradeRestriction().isTradeFlagSet( TradeRestrictionFlag.Deprecated );
    }

    @Override public void setTradeRestriction( final TradeRestriction tradeRestriction ) { _tradeRestriction = tradeRestriction; }

    @Override public void setLoanAvailability( final LoanAvailability loanAvailability ) { _loanAvailability = loanAvailability; }

    @Override public long getEventTimestamp()                                            { return _created; }

    @Override public String id()                                                         { return _id; }

    @Override public String toString() {
        ReusableString s = TLC.instance().pop();
        dump( s );
        String str = s.toString();
        TLC.instance().pushback( s );
        return str;
    }

    @Override public long getUniqueInstId()                 { return Constants.UNSET_LONG; }

}
