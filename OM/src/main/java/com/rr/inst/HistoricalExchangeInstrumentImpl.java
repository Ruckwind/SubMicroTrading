package com.rr.inst;

import com.rr.core.collections.TimeSeries;
import com.rr.core.collections.TimeSeriesFactory;
import com.rr.core.lang.*;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.*;
import com.rr.core.utils.Utils;
import com.rr.model.generated.internal.events.impl.SecurityDefinitionImpl;
import com.rr.model.generated.internal.events.impl.SecurityStatusImpl;
import com.rr.model.generated.internal.events.interfaces.SecDefLeg;
import com.rr.model.generated.internal.events.interfaces.SecurityDefinition;
import com.rr.model.generated.internal.type.SecurityTradingStatus;

import java.util.Iterator;

/**
 * acts as a proxy to the latest version in the time series as well as allowing point in time version retrieval
 */
public class HistoricalExchangeInstrumentImpl implements HistExchInstSecDefWrapperTS {

    private static final Logger _log = LoggerFactory.create( HistoricalExchangeInstrumentImpl.class );

    private static final int DEFAULT_ENTRIES = 4;

    /**
     * array of InstrumentSecurityDefVersionWrapperImpl from newest to oldest in time order
     */
    private TimeSeries<ExchInstSecDefWrapperTSEntry> _versions = TimeSeriesFactory.createUnboundedSmallSeries( DEFAULT_ENTRIES );

    /**
     * have latest as seperate variable so can ensure latest version changes after fully added to version
     * required for the thread safe wrapper
     */
    private ExchInstSecDefWrapperTSEntry _latest = null;

    @Override public boolean add( ExchInstSecDefWrapperTSEntry instVersion ) {
        boolean added = _versions.add( instVersion );

        _latest = _versions.latest();

        if ( _latest == null ) {
            _log.warn( "HistoricalExchangeInstrumentImpl.add instrument added but version is future only : " + instVersion.toString() );
        }

        return added;
    }

    @Override public final ExchInstSecDefWrapperTSEntry getAt( final long atInternalTimeMS ) { return _versions.getAt( atInternalTimeMS ); }

    @Override public final ExchInstSecDefWrapperTSEntry latest()                             { return _versions.latest(); }

    @Override public final Iterator<ExchInstSecDefWrapperTSEntry> oldestToNewestIterator() { return _versions.oldestToNewestIterator(); }

    @Override public final int size()                                                        { return _versions.size(); }

    @Override public final void dump( final ReusableString out )                         { getLatest().dump( out ); }

    @Override public final Currency getCurrency()                                        { return getLatest().getCurrency(); }

    @Override public final ZString getExchangeSymbol()                                   { return getLatest().getExchangeSymbol(); }

    @Override public final LoanAvailability getLoanAvailability()                        { return getLatest().getLoanAvailability(); }

    @Override public final ExchangeCode getPrimaryExchangeCode()                         { return getLatest().getPrimaryExchangeCode(); }

    @Override public ExchangeCode getSecurityExchange()                                  { return getLatest().getSecurityExchange(); }

    @Override public final ZString getSecurityGroup()                                    { return getLatest().getSecurityGroup(); }

    @Override public final SecurityType getSecurityType()                                { return getLatest().getSecurityType(); }

    @Override public final ZString getSymbol()                                           { return getLatest().getSymbol(); }

    @Override public final TradeRestriction getTradeRestriction()                        { return getLatest().getTradeRestriction(); }

    @Override public boolean isDead()                                                    { return latest().isDead(); }

    @Override public void setTradeRestriction( final TradeRestriction tradeRestriction ) { getLatest().setTradeRestriction( tradeRestriction ); }

    @Override public void setLoanAvailability( final LoanAvailability loanAvailability ) { getLatest().setLoanAvailability( loanAvailability ); }

    @Override public final long getEventTimestamp()                                      { return getLatest().getEventTimestamp(); }

    @Override public final ZString getISIN() { return getLatest().getISIN(); }

    @Override public final ZString getFIGI() { return getLatest().getFIGI(); }

    @Override public final SecurityStatusImpl getLastStatus() { return getLatest().getLastStatus(); }

    @Override public final void setLastStatus( final SecurityStatusImpl lastStatus )                         { getLatest().setLastStatus( lastStatus ); }


    @Override public final SecurityDefinitionImpl getSecDef()                                                 { return getLatest().getSecDef(); }

    @Override public final SecurityTradingStatus getSecurityTradingStatus() { return getLatest().getSecurityTradingStatus(); }

    @Override public final void setSecurityTradingStatus( final SecurityTradingStatus securityTradingStatus ) { getLatest().setSecurityTradingStatus( securityTradingStatus ); }

    @Override public final void setPlaceHolderDefinition( final SecDefLeg def )                               { getLatest().setPlaceHolderDefinition( def ); }

    /**
     * should check if major change and create new version BEFORE invoking setSecurityDefinition
     *
     * @param def
     */
    @Override public final void setSecurityDefinition( final SecurityDefinitionImpl def ) { getLatest().setSecurityDefinition( def ); }

    @Override public final void getKey( final SecurityIDSource securityIDSource, final long atInternalTimeMS, final ReusableString dest ) {
        if ( Utils.isNull( atInternalTimeMS ) ) {
            getKey( securityIDSource, dest );
        } else {
            final ExchInstSecDefWrapperTSEntry version = _versions.getAt( atInternalTimeMS );

            version.getKey( securityIDSource, dest );
        }
    }

    @Override public final ZString getSecurityDesc()                                                         { return getLatest().getSecurityDesc(); }

    @Override public final int getBookLevels()                                                               { return getLatest().getBookLevels(); }

    @Override public final CommonInstrument getCommonInstrument()                                            { return getLatest().getCommonInstrument(); }

    @Override public final long getEndTimestamp()                                                            { return getLatest().getEndTimestamp(); }

    @Override public final Exchange getExchange()                                                            { return getLatest().getExchange(); }

    @Override public final long getExchangeLongId()                                                          { return getLatest().getExchangeLongId(); }

    @Override public final ExchangeSession getExchangeSession()                                              { return getLatest().getExchangeSession(); }

    @Override public final InstrumentEvent getInstrumentEvent( final int idx )                               { return getLatest().getInstrumentEvent( idx ); }

    @Override public final int getIntSegment()                                                               { return getLatest().getIntSegment(); }

    @Override public final void getKey( final SecurityIDSource securityIDSource, final ReusableString dest ) { getLatest().getKey( securityIDSource, dest ); }

    @Override public final String getKey( final SecurityIDSource securityIDSource )                          { return getLatest().getKey( securityIDSource ); }

    @Override public final int getMinQty()                                                                   { return getLatest().getMinQty(); }

    @Override public final int getNumEvents()                                                                { return getLatest().getNumEvents(); }

    @Override public final int getSecurityGroupId()                                                          { return getLatest().getSecurityGroupId(); }

    @Override public ViewString getSecurityID()                                                              { return getSecDef().getSecurityID(); }

    @Override public SecurityIDSource getSecurityIDSource()                                                  { return getSecDef().getSecurityIDSource(); }

    @Override public final long getStartTimestamp()                                                          { return getLatest().getStartTimestamp(); }

    @Override public final TickType getTickType()                                                            { return getLatest().getTickType(); }

    @Override public final void setTickType( final TickType ts )                                             { getLatest().setTickType( ts ); }

    @Override public final UnitOfMeasure getUnitOfMeasure()                                                  { return getLatest().getUnitOfMeasure(); }

    @Override public final double getUnitOfMeasureQuantity()                                                 { return getLatest().getUnitOfMeasureQuantity(); }

    @Override public final TradingRange getValidTradingRange()                                               { return getLatest().getValidTradingRange(); }

    @Override public final boolean hasChanged( final Object source )                                         { return getLatest().hasChanged( source ); }

    @Override public final boolean isDeleted()                                                               { return getLatest().isDeleted(); }

    @Override public final boolean isEnabled()                                                               { return getLatest().isEnabled(); }

    @Override public final void setEnabled( boolean isEnabled )                                              { getLatest().setEnabled( isEnabled ); }

    @Override public boolean isFlagSet( final MsgFlag flag )                                                 { return getLatest().isFlagSet( flag ); }

    @Override public final boolean isTestInstrument()                                                        { return getLatest().isTestInstrument(); }

    @Override public HistExchInstSecDefWrapperTS getSeries() {
        return this;
    }

    @Override public int hashCode() {
        return getLatest().hashCode();
    }

    @Override public boolean equals( final Object o ) {
        return getLatest().equals( o );
    }

    @Override public String toString() {
        ReusableString s = TLC.instance().pop();
        dump( s );
        String str = s.toString();
        TLC.instance().pushback( s );
        return str;
    }

    @Override public final String id()                        { return getLatest().id(); }

    @Override public boolean isSame( Identifiable that ) {
        return getLatest().isSame( that );
    }

    @Override public final boolean isNewVersionRequired( final SecurityDefinition newDef ) {

        if ( getLatest() == null ) return true;

        SecurityDefinition cur = getLatest().getSecDef();

        if ( newDef.getSecurityUpdateAction() != cur.getSecurityUpdateAction() ) return true;

        if ( cur.getParentCompanyId() != newDef.getParentCompanyId() ) return true;

        if ( cur.getCommonSecurityId() != newDef.getCommonSecurityId() ) return true;

        if ( cur.getGicsCode() != newDef.getGicsCode() ) return true;

        if ( cur.getCurrency() != newDef.getCurrency() ) return true;

        if ( cur.getMaturityMonthYear() != newDef.getMaturityMonthYear() ) return true;

        if ( cur.getNoLegs() != newDef.getNoLegs() ) return true;

        if ( cur.getSecurityType() != newDef.getSecurityType() ) return true;

        if ( cur.getSecurityExchange() != newDef.getSecurityExchange() ) return true;

        if ( cur.getPrimarySecurityExchange() != newDef.getPrimarySecurityExchange() ) return true;

        if ( !cur.getCompanyName().equals( newDef.getCompanyName() ) ) return true;

        if ( !cur.getSecurityDesc().equals( newDef.getSecurityDesc() ) ) return true;

        if ( !cur.getSymbol().equals( newDef.getSymbol() ) ) return true;

        if ( cur.getTickRule() != newDef.getTickRule() ) return true;

        if ( cur.getMinPriceIncrement() != newDef.getMinPriceIncrement() ) return true;

        if ( cur.getFutPointValue() != newDef.getFutPointValue() ) return true;

        if ( InstUtils.keysChanged( cur, newDef ) ) return true;

        return false;
    }

    @Override public final Iterator<ExchInstSecDefWrapperTSEntry> iterator()                 { return _versions.iterator(); }

    @Override public final long getUniqueInstId()                                        { return getLatest().getUniqueInstId(); }

    protected ExchInstSecDefWrapperTSEntry getLatest() {

        ExchInstSecDefWrapperTSEntry v;

        if ( Utils.useClockForLatest() ) {
            v = getAt( ClockFactory.get().currentTimeMillis() );

            if ( v == null ) {
                v = _latest; // HACK TO STOP NULL POINTER
            }

        } else {
            v = _latest;
        }

        return v;
    }

}
