/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.ReusableString;

/**
 * A strategy that can act as an instrument
 */
public interface StrategyInstrument extends Strategy, Instrument {

    enum StratClassification {Factor, GFUT, Other}

    @Override default void dump( ReusableString out )    { out.append( toString() ); }

    @Override default SecurityType getSecurityType()     { return SecurityType.Strategy; }

    /**
     * value of trade = lastQty * lastPx * pointValue
     *
     * @return the point value ... could also be contract size or multiplier
     */
    default double getPointValue() { return 1.0; }

    default StratClassification getStratClassification() { return StratClassification.Other; }
}
