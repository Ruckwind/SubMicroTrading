/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

public interface BinaryEncoder extends Encoder {

    boolean isDebug();

    /**
     * optional support for detailed debugging, could impact performance only to be used for problem resolution
     *
     * @param debug
     */
    void setDebug( boolean debug );
}
