/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common.constant.int64;

import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.common.constant.ConstantFieldWriter;

public final class LongMandWriterConst extends ConstantFieldWriter {

    private final long _constVal;

    public LongMandWriterConst( String name, int id, String init ) {
        this( name, id, FieldUtils.parseLong( init ) );
    }

    public LongMandWriterConst( String name, int id, long init ) {
        super( name, id, false );
        _constVal = init;
    }

    public long getInitValue() {
        return _constVal;
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     */
    public void write() {
        // nothing
    }
}
