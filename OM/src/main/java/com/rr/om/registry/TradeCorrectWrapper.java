/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.registry;

import com.rr.core.lang.ZString;

public interface TradeCorrectWrapper extends TradeWrapper {

    ZString getExecRefId();
}
