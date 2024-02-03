/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix;

import com.rr.core.codec.BinaryEncoder;

public interface FastFixEncoder extends BinaryEncoder {

    void logLastMsg();

    void logStats();

}
