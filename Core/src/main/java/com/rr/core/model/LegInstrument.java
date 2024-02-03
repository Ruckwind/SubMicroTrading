/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.ZString;

public interface LegInstrument {

    ExchangeInstrument getInstrument();

    ZString getLegSecurityDesc();

    BasicSide getLegSide();
}
