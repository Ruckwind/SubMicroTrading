/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix;

import com.rr.core.codec.BinaryDecoder;

public interface FastFixDecoder extends BinaryDecoder {

    void logLastMsg();

    void logStats();

    // test utility method
    void setNextDummy();

}
