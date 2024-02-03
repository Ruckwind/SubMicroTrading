/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.dummy.warmup;

import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.model.*;
import com.rr.om.exchange.ExchangeManager;

public class DummyFutureInstrument extends DummyInstrument implements ExchDerivInstrument {

    public static final DummyFutureInstrument DUMMY = new DummyFutureInstrument( new ViewString( "DummyId" ), DummyExchange.DUMMY, Currency.Other, true, ExchangeInstrument.DUMMY_INSTRUMENT_ID );

    private static int _nextKey = 1000000;

    private int _maturityMonthYear = 0;

    public static DummyFutureInstrument newDummy( final ZString securityId, final SecurityIDSource idSrc, final ExchangeCode securityExchange ) {
        Exchange              ex = ExchangeManager.instance().getByCode( securityExchange );
        DummyFutureInstrument d  = new DummyFutureInstrument( securityId, ex, Currency.Other, true, securityId, idSrc );
        return d;
    }

    public DummyFutureInstrument( final ZString securityId, final Exchange exchange, final Currency currency ) {
        super( securityId, exchange, currency );
    }

    public DummyFutureInstrument( final ZString securityId, final Exchange exchange, final Currency currency, final boolean isTestSymbol, final ZString id ) {
        super( securityId, exchange, currency, isTestSymbol, id );
    }

    public DummyFutureInstrument( final ZString securityId, final Exchange ex, final Currency ccy, final boolean isTestSymbol, final ZString id, final SecurityIDSource idSrc ) {
        super( securityId, ex, ccy, isTestSymbol, id, idSrc );
    }

    @Override public double getContractMultiplier()                 { return 1; }

    @Override public FutureExchangeSymbol getFutExSym()             { return FutureExchangeSymbol.getFromSMTSymbol( getExchangeSymbol() ); }

    @Override public LegInstrument getLeg( int idx )                { return null; }

    @Override public ZString getLegSecurityDesc( int legIdx, ReusableString secDef ) {
        secDef.reset();
        return secDef;
    }

    @Override public int getMaturityMonthYear()                     { return _maturityMonthYear; }

    @Override public int getNumLegs()                               { return 0; }

    @Override public OptionType getOptionType()                     { return null; }

    @Override public double getStrikePrice()                        { return Constants.UNSET_DOUBLE; }

    public void setMaturityMonthYear( final int maturityMonthYear ) { _maturityMonthYear = maturityMonthYear; }

    @Override public SecurityType getSecurityType() {
        return SecurityType.Future;
    }

    @Override public void dump( final ReusableString out ) {
        out.append( "DummyDerivInstrument ric=" ).append( getExchangeSymbol() ).append( ", exSym=" ).append( getExchangeSymbol() );
    }

}
