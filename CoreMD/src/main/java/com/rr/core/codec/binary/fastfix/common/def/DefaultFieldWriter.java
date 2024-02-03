/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common.def;

import com.rr.core.codec.binary.fastfix.common.FieldWriter;

public abstract class DefaultFieldWriter extends FieldWriter {

    public DefaultFieldWriter( String name, int id, boolean isOptional ) {
        super( name, id, isOptional );
    }

    @Override
    public boolean requiresPMap() {
        return true;
    }
}
