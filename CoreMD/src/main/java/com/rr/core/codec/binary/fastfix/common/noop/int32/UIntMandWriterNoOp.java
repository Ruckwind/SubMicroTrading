/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common.noop.int32;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.common.noop.NoOpFieldWriter;
import com.rr.core.lang.Constants;

public final class UIntMandWriterNoOp extends NoOpFieldWriter {

    public UIntMandWriterNoOp( String name, int id, String init ) {
        this( name, id );
    }

    public UIntMandWriterNoOp( String name, int id ) {
        super( name, id, false );
        reset();
    }

    public void reset() {
        // nothing
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     *
     * @param encoder
     * @param value
     */
    public void write( final FastFixBuilder encoder, final int value ) {
        if ( value != Constants.UNSET_INT ) {
            encoder.encodeMandUInt( value );
        } else {
            throwMissingValueException();
        }
    }
}
