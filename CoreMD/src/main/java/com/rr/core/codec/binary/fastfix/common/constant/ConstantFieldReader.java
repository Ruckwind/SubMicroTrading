/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common.constant;

import com.rr.core.codec.binary.fastfix.common.FieldReader;

public abstract class ConstantFieldReader extends FieldReader {

    public ConstantFieldReader( String name, int id, boolean isOptional ) {
        super( name, id, isOptional );
    }

    @Override
    public boolean requiresPMap() {
        return isOptional();
    }

    @Override
    public void reset() {
        // nothing to reset
    }
}
