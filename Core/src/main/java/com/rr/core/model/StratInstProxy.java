package com.rr.core.model;

import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.thread.RunState;

/**
 * placeholder pending creation of an expected strategy
 * <p>
 * Used to allow forward referencing of strategies in the strategy definition file
 */
public class StratInstProxy implements Instrument, StrategyInstrument {

    private final String  _stratId;
    private final ZString _zid;

    public StratInstProxy( final String stratIdStr ) {
        _stratId = stratIdStr;
        _zid     = new ViewString( stratIdStr );
    }

    @Override public void doWorkUnit()                                                         { /* nothing */ }

    @Override public void registerListener( final StatusChanged callback )                     { /* nothing */ }

    @Override public void stop()                                                               { /* nothing */ }

    @Override public void threadedInit()                                                       { /* nothing */ }

    @Override public void dump( final ReusableString out )                                     { out.append( "StrategyInstrumentProxy id=" ).append( _stratId ); }

    @Override public Currency getCurrency()                                                    { return null; }

    @Override public ZString getExchangeSymbol()                                               { return zid(); }

    @Override public ZString getSecurityDesc()                                                 { return zid(); }

    @Override public ExchangeCode getSecurityExchange()                                        { return null; }

    @Override public ZString getSecurityGroup()                                                { return null; }

    @Override public SecurityType getSecurityType()                                            { return SecurityType.Strategy; }

    @Override public ZString getSymbol()                                                       { return zid(); }

    @Override public TradeRestriction getTradeRestriction()                                    { return null; }

    @Override public boolean isDead()                                                          { return false; }

    @Override public String getComponentId()                                                   { return id(); }

    @Override public long getEventTimestamp()                                                  { return Constants.UNSET_LONG; }

    @Override public RunState getRunState()                                                    { return null; }

    @Override public void init( final SMTStartContext ctx, final CreationPhase creationPhase ) { /* nothing */ }

    @Override public RunState setRunState( final RunState newState )                           { return null; }

    @Override public boolean hasOutstandingWork()                                              { return false; }

    @Override public String id()                                                               { return _stratId; }

    @Override public ZString zid()                                                             { return _zid; }

    @Override public long getUniqueInstId()                                                    { return Constants.UNSET_LONG; }
}
