/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common.noop.string;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.common.noop.NoOpFieldWriter;
import com.rr.core.lang.ReusableString;

public final class StringWriterNoOp extends NoOpFieldWriter {

    public StringWriterNoOp( String name, int id, String init ) {
        this( name, id );
    }

    public StringWriterNoOp( String name, int id ) {
        super( name, id, false );
        reset();
    }

    public void reset() {
        // nothing
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     * <p>
     * compares the new value with previous, checking for best matching at start or end of string, then
     *
     * @param encoder
     * @param value
     */
    public void write( final FastFixBuilder encoder, final ReusableString value ) {

        encoder.encodeStringBytes( value.getBytes(), 0, value.length() );
    }
}
