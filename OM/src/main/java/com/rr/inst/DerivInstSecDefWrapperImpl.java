/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.inst;

import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.model.generated.internal.events.impl.SecurityDefinitionImpl;

public class DerivInstSecDefWrapperImpl extends InstrumentSecurityDefWrapperImpl implements DerivInstSecDefWrapper {

    private final LegInstrument[]      _legs;
    private       FutureExchangeSymbol _futExSym;

    public DerivInstSecDefWrapperImpl( final Exchange exchange, final SecurityDefinitionImpl secDef, final CommonInstrument inst, final int legCnt ) {
        super( exchange, secDef, inst );
        _legs = (legCnt == 0) ? null : new LegInstrument[ legCnt ];
    }

    @Override public long getDeadTimestamp()          { return getSecDef().getDeadTimestamp(); }

    @Override public final void setLeg( int idx, LegInstrument legInst ) {
        _legs[ idx ] = legInst;
    }

    @Override public int getNumLegs()               { return (_legs == null) ? 0 : _legs.length; }

    @Override public final ZString getLegSecurityDesc( final int legIdx, final ReusableString secDef ) {
        final LegInstrument leg = _legs[ legIdx ];

        final ZString legSecDef = leg.getLegSecurityDesc();

        if ( legSecDef.length() != 0 ) {
            secDef.copy( legSecDef );
        } else {
            secDef.copy( leg.getInstrument().getSecurityDesc() );
        }

        return secDef;
    }

    @Override public final LegInstrument getLeg( final int idx ) throws SMTRuntimeException {
        int numEvents = getNumLegs();
        if ( idx < 0 || idx > getNumLegs() ) return null;
        return _legs[ idx ];
    }

    @Override public double getContractMultiplier() { return getSecDef().getContractMultiplier(); }

    @Override public final int getMaturityMonthYear() { return getSecDef().getMaturityMonthYear(); }

    @Override public double getStrikePrice()          { return getSecDef().getStrikePrice(); }

    @Override public OptionType getOptionType()       { return InstUtils.getOptionType( getSecDef() ); }

    @Override public FutureExchangeSymbol getFutExSym() {
        if ( _futExSym == null ) {
            _futExSym = FutureExchangeSymbol.getFromPhysicalSymbol( getSecDef().getSecurityGroup(), getSecDef().getSecurityExchange() );
        }
        return _futExSym;
    }

    @Override protected void otherOverrides() {
        InstUtils.timestampOverrides( getSecDef(), false );
    }
}
