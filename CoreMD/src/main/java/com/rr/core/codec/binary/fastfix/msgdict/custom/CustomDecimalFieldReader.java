/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.msgdict.custom;

import com.rr.core.codec.binary.fastfix.common.DoubleFieldValWrapper;
import com.rr.core.codec.binary.fastfix.common.FieldReader;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;

public abstract class CustomDecimalFieldReader extends FieldReader implements FixFieldReader<DoubleFieldValWrapper> {

    public CustomDecimalFieldReader( String name, int id, boolean isOptional ) {
        super( name, id, isOptional );
    }
}
