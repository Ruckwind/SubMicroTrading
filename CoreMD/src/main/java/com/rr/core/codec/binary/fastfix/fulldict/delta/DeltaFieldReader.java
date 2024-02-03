/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.fulldict.delta;

import com.rr.core.codec.binary.fastfix.common.FieldReader;

public abstract class DeltaFieldReader extends FieldReader {

    public DeltaFieldReader( String name, int id, boolean isOptional ) {
        super( name, id, isOptional );
    }

    @Override
    public boolean requiresPMap() {
        return false;
    }
}
