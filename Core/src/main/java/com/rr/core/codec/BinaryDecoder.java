/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

public interface BinaryDecoder extends Decoder {

    boolean isDebug();

    /**
     * if enabled will install a binary debug builder and allow tracing of all events in great detail
     *
     * @param isDebugOn
     */
    void setDebug( boolean isDebugOn );
}
