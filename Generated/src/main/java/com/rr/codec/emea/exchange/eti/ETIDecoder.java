/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.codec.emea.exchange.eti;

import com.rr.core.codec.BinaryDecoder;

public interface ETIDecoder extends BinaryDecoder {

    ETIDecodeContext getLastContext( ETIDecodeContext context );

    void setExchangeEmulationOn();
}
