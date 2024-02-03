package com.rr.core.model;

import com.rr.core.collections.TimeSeries;
import com.rr.core.collections.TimeSeriesFactory;
import com.rr.core.lang.*;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;

import java.util.Iterator;
import java.util.List;

public class HistoricalCommonInstrumentImpl implements HistoricalCommonInstrument {

    private static final Logger _log = LoggerFactory.create( HistoricalCommonInstrumentImpl.class );

    private static final int DEFAULT_ENTRIES = 4;

    /**
     * array of CommonInstrument from newest to oldest in time order
     */
    private TimeSeries<CommonInstrument> _versions = TimeSeriesFactory.createUnboundedSmallSeries( DEFAULT_ENTRIES );

    /**
     * have latest as seperate variable so can ensure latest version changes after fully added to version
     * required for the thread safe wrapper
     */
    private CommonInstrument _latest = null;

    @Override public void attach( final ExchangeInstrument inst )             { getLatest().attach( inst ); }

    @Override public void detach( final ExchangeInstrument inst )             { getLatest().detach( inst ); }

    @Override public Currency getCcy()                                        { return getLatest().getCcy(); }

    @Override public long getCommonInstrumentId()                             { return getLatest().getCommonInstrumentId(); }

    @Override public ExchangeInstrument getListing( final ExchangeCode code ) { return getLatest().getListing( code ); }

    @Override public void getListings( final List<ExchangeInstrument> dest ) {
        final CommonInstrument latest = getLatest();

        if ( latest != null ) {
            latest.getListings( dest );
        } else {
            _log.log( Level.trace, " getListings but latest is null" );
        }
    }

    @Override public ParentCompany getParentCompany()                         { return getLatest().getParentCompany(); }

    @Override public ExchangeInstrument getPrimaryListing()                   { return getLatest().getPrimaryListing(); }

    @Override public long getUniqueInstId()                                            { return getLatest().getUniqueInstId(); }

    @Override public Currency getCurrency()                                          { return getLatest().getCurrency(); }

    @Override public ZString getSymbol()                                             { return getLatest().getSymbol(); }

    @Override public ZString getExchangeSymbol()                                     { return getLatest().getExchangeSymbol(); }

    @Override public SecurityType getSecurityType()                                  { return getLatest().getSecurityType(); }

    @Override public ZString getSecurityDesc()                                       { return getLatest().getSecurityDesc(); }

    @Override public LoanAvailability getLoanAvailability()                          { return getLatest().getLoanAvailability(); }

    @Override public TradeRestriction getTradeRestriction()                          { return getLatest().getTradeRestriction(); }

    @Override public ZString getSecurityGroup()                                      { return getLatest().getSecurityGroup(); }

    @Override public ExchangeCode getSecurityExchange()                       { return getLatest().getSecurityExchange(); }

    @Override public ExchangeCode getPrimaryExchangeCode()                           { return getLatest().getPrimaryExchangeCode(); }

    @Override public boolean isDead()                                         { return getLatest().isDead(); }

    @Override public void dump( final ReusableString out )                           { getLatest().dump( out ); }

    @Override public void setTradeRestriction( final TradeRestriction restriction )  { getLatest().setTradeRestriction( restriction ); }

    @Override public void setLoanAvailability( final LoanAvailability availability ) { getLatest().setLoanAvailability( availability ); }

    @Override public long getEventTimestamp()                                        { return getLatest() != null ? getLatest().getEventTimestamp() : Constants.UNSET_LONG; }

    @Override public String id()                                                     { return getLatest().id(); }

    @Override public Iterator<CommonInstrument> iterator()                    { return _versions.iterator(); }

    @Override public CommonInstrument latest()                                { return getLatest(); }

    @Override public CommonInstrument getAt( final long timeMS )              { return _versions.getAt( timeMS ); }

    @Override public int size()                                               { return _versions.size(); }

    @Override public boolean add( final CommonInstrument entry ) {
        boolean added = _versions.add( entry );

        _latest = _versions.latest();

        return added;
    }

    @Override public Iterator<CommonInstrument> oldestToNewestIterator()      { return _versions.oldestToNewestIterator(); }

    public CommonInstrument getLatest() {
        CommonInstrument ci = _latest;

        if ( Env.isBacktest() ) {
            CommonInstrument ti = getAt( ClockFactory.get().currentTimeMillis() );

            if ( ti != null ) {
                ci = ti;
            }
        }

        return ci;
    }
}

