package com.rr.inst;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;
import com.rr.core.model.FutureExchangeSymbol;
import com.rr.core.model.LegInstrument;
import com.rr.core.model.OptionType;

/**
 * acts as a proxy to the latest version in the time series as well as allowing point in time version retrieval
 */
public class HistoricalExchangeDerivInstrumentImpl extends HistoricalExchangeInstrumentImpl implements DerivInstSecDefWrapper {

    private DerivInstSecDefVersionWrapper _derivLatest;

    public HistoricalExchangeDerivInstrumentImpl() {
        super();
    }

    @Override public boolean add( ExchInstSecDefWrapperTSEntry instVersion ) {
        final boolean added = super.add( instVersion );

        _derivLatest = DerivInstSecDefVersionWrapper.class.cast( getLatest() );

        return added;
    }

    @Override public double getContractMultiplier()                                              { return _derivLatest.getContractMultiplier(); }

    @Override public FutureExchangeSymbol getFutExSym()                                          { return _derivLatest.getFutExSym(); }

    @Override public LegInstrument getLeg( final int idx )                                       { return _derivLatest.getLeg( idx ); }

    @Override public ZString getLegSecurityDesc( final int legIdx, final ReusableString secDef ) { return _derivLatest.getLegSecurityDesc( legIdx, secDef ); }

    @Override public int getMaturityMonthYear()                                                  { return _derivLatest.getMaturityMonthYear(); }

    @Override public int getNumLegs()                                                            { return _derivLatest.getNumLegs(); }

    @Override public OptionType getOptionType()                                                  { return _derivLatest.getOptionType(); }

    @Override public double getStrikePrice()                                                     { return _derivLatest.getStrikePrice(); }

    @Override public long getDeadTimestamp()                                                     { return _derivLatest.getDeadTimestamp(); }

    @Override public void setLeg( final int idx, final LegInstrument legInst )                   { _derivLatest.setLeg( idx, legInst ); }
}
