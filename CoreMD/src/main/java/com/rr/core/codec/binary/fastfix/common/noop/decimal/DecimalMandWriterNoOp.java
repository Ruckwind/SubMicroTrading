/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common.noop.decimal;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.common.FastFixDecimal;
import com.rr.core.codec.binary.fastfix.common.noop.NoOpFieldWriter;
import com.rr.core.utils.Utils;

public final class DecimalMandWriterNoOp extends NoOpFieldWriter {

    private final FastFixDecimal _prevDecimal = new FastFixDecimal();

    public DecimalMandWriterNoOp( String name, int id, String init ) {
        this( name, id );
    }

    public DecimalMandWriterNoOp( String name, int id ) {
        super( name, id, false );
        reset();
    }

    public void reset() {
        // init
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     *
     * @param encoder
     * @param value
     */
    public void write( final FastFixBuilder encoder, final double value ) {
        if ( Utils.hasVal( value ) ) {

            _prevDecimal.set( value );  // we extracted prev value can now overwrite ready for next call

            final long mant = _prevDecimal.getMantissa();
            final int  exp  = _prevDecimal.getExponent();

            encoder.encodeMandInt( exp );
            encoder.encodeMandLong( mant );
        } else {
            throwMissingValueException();
        }
    }
}
