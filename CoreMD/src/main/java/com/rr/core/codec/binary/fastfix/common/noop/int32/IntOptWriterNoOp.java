/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common.noop.int32;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.common.noop.NoOpFieldWriter;

public final class IntOptWriterNoOp extends NoOpFieldWriter {

    public IntOptWriterNoOp( String name, int id, String init ) {
        this( name, id );
    }

    public IntOptWriterNoOp( String name, int id ) {
        super( name, id, true );
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
        encoder.encodeOptionalInt( value );
    }
}
