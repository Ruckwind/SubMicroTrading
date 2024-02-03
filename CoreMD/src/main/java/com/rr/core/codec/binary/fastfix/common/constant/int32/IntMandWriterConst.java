/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common.constant.int32;

import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.common.constant.ConstantFieldWriter;

public final class IntMandWriterConst extends ConstantFieldWriter {

    private final int _constVal;

    public IntMandWriterConst( String name, int id, String init ) {
        this( name, id, FieldUtils.parseInt( init ) );
    }

    public IntMandWriterConst( String name, int id, int init ) {
        super( name, id, false );
        _constVal = init;
    }

    public int getInitValue() {
        return _constVal;
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     */
    public void write() {
        // nothing
    }
}
