/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;

/**
 * interface to represent a tradeable derivative instrument eg option/future or a synthetic instrument such as Generic Future
 * <p>
 * key is association with exchange and ccy and standard identifiers
 */
public interface ExchDerivInstrument extends ExchangeInstrument {

    /**
     * Derivs Members .... ideally would be split out into ftuure/option interfaces ... here for perf reasons
     */
    double getContractMultiplier();

    FutureExchangeSymbol getFutExSym();

    /**
     * @param idx leg index starting from 0
     * @return instrument representing the leg
     * @NOTE use getNumLegs first and dont invoke if numLegs == 0 as it will cause IndexOutOfBounds or NullPointerException
     */
    LegInstrument getLeg( int idx );

    /**
     * helper method to abstract logic of obtaining the security description from a leg
     * it can reside on the LegInstrument or on the LegInstruments underlying instrument letter
     *
     * @param secDef - destination, which will have the security description copied too
     * @return the supplied secDef buffer
     */
    ZString getLegSecurityDesc( int legIdx, ReusableString secDef );

    /**
     * @return YYYYMM  for maturity
     */
    int getMaturityMonthYear();

    /**
     * @return number of legs, or Constants.UNSET_INT if number of legs unknown ... ie could be multileg instrument but we didnt get the leg info
     */
    int getNumLegs();

    OptionType getOptionType();

    double getStrikePrice();
}
