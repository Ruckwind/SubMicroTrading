/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common.noop.int64;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.common.LongFieldValWrapper;
import com.rr.core.codec.binary.fastfix.common.noop.NoOpFieldReader;

public final class ULongOptReaderNoOp extends NoOpFieldReader implements FixFieldReader<LongFieldValWrapper> {

    public ULongOptReaderNoOp( String name, int id, String init ) {
        this( name, id );
    }

    public ULongOptReaderNoOp( String name, int id ) {
        super( name, id, true );
        reset();
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, LongFieldValWrapper dest ) {
        dest.setVal( read( decoder ) );
    }

    @Override
    public void reset() {
        // nothing
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     */
    public long read( final FastFixDecodeBuilder decoder ) {

        return decoder.decodeOptionalULong();
    }
}
