/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common.noop.vector;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.common.StringFieldValWrapper;
import com.rr.core.codec.binary.fastfix.common.noop.NoOpFieldReader;
import com.rr.core.lang.ReusableString;

public final class ByteVectorOptionalReaderNoOp extends NoOpFieldReader implements FixFieldReader<StringFieldValWrapper> {

    public ByteVectorOptionalReaderNoOp( String name, int id, String init ) {
        this( name, id );
    }

    public ByteVectorOptionalReaderNoOp( String name, int id ) {
        super( name, id, false );
        reset();
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, StringFieldValWrapper dest ) {
        read( decoder, dest.getVal() );
    }

    @Override
    public void reset() {
        // nothing
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     */
    public void read( final FastFixDecodeBuilder decoder, ReusableString dest ) {
        dest.reset();
        decoder.decodeOptionalByteVector( dest );
    }
}
