/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.inst;

import com.rr.core.model.ExchDerivInstrument;
import com.rr.core.model.LegInstrument;

public interface DerivInstSecDefWrapper extends InstrumentSecurityDefWrapper, ExchDerivInstrument {

    /**
     * @return time after which the contract can be considered dead (possibly after expiry)
     */
    long getDeadTimestamp();

    void setLeg( int idx, LegInstrument legInst );
}
