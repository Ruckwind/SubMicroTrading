/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.fulldict.copy;

import com.rr.core.codec.binary.fastfix.common.FieldReader;

public abstract class CopyFieldReader extends FieldReader {

    public CopyFieldReader( String name, int id, boolean isOptional ) {
        super( name, id, isOptional );
    }

    @Override
    public boolean requiresPMap() {
        return true;
    }
}
