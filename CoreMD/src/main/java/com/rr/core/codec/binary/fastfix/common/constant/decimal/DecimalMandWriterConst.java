/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common.constant.decimal;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapWriter;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.common.constant.ConstantFieldWriter;

public final class DecimalMandWriterConst extends ConstantFieldWriter {

    private final double _constVal;

    public DecimalMandWriterConst( String name, int id, String init ) {
        this( name, id, FieldUtils.parseDouble( init ) );
    }

    public DecimalMandWriterConst( String name, int id, double val ) {
        super( name, id, false );

        _constVal = val;
    }

    public double getInitValue() {
        return _constVal;
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     *
     * @param encoder
     * @param mapWriter
     */
    public void write( final FastFixBuilder encoder, final PresenceMapWriter mapWriter ) {
        mapWriter.setCurrentField();
    }
}
