/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common.noop.int32;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.common.IntFieldValWrapper;
import com.rr.core.codec.binary.fastfix.common.noop.NoOpFieldReader;
import com.rr.core.lang.Constants;

public final class IntMandReaderNoOp extends NoOpFieldReader implements FixFieldReader<IntFieldValWrapper> {

    public IntMandReaderNoOp( String name, int id, String init ) {
        this( name, id );
    }

    public IntMandReaderNoOp( String name, int id ) {
        super( name, id, false );
        reset();
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, IntFieldValWrapper dest ) {
        dest.setVal( read( decoder ) );
    }

    @Override
    public void reset() {
        // nothing
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     */
    public int read( final FastFixDecodeBuilder decoder ) {

        final int value = decoder.decodeMandInt();

        if ( value != Constants.UNSET_INT ) {
            return value;
        }

        throwMissingValueException();

        return Constants.UNSET_INT; // keep compiler happy as it cant see throw methods runtime exception
    }
}
