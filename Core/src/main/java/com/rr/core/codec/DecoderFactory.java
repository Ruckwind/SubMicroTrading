/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

import com.rr.core.component.SMTComponent;
import com.rr.core.component.SMTStartContext;

public interface DecoderFactory extends SMTComponent {

    /**
     * get new decoder instance identified by baseName
     *
     * @param ctx
     * @param baseName
     * @return
     */
    Decoder getDecoder( SMTStartContext ctx, String baseName );

    Decoder getDefault();

}
