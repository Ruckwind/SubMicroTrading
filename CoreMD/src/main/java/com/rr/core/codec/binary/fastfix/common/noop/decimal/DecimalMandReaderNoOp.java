/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common.noop.decimal;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.DoubleFieldValWrapper;
import com.rr.core.codec.binary.fastfix.common.FastFixDecimal;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.common.noop.NoOpFieldReader;
import com.rr.core.lang.Constants;

public final class DecimalMandReaderNoOp extends NoOpFieldReader implements FixFieldReader<DoubleFieldValWrapper> {

    private final FastFixDecimal _prevDecimal = new FastFixDecimal();

    public DecimalMandReaderNoOp( String name, int id, String init ) {
        this( name, id );
    }

    public DecimalMandReaderNoOp( String name, int id ) {
        super( name, id, false );
        reset();
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, DoubleFieldValWrapper dest ) {
        dest.setVal( read( decoder ) );
    }

    @Override
    public void reset() {
        // nothing
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     */
    public double read( final FastFixDecodeBuilder decoder ) {

        final int exp = decoder.decodeMandInt();

        if ( exp != Constants.UNSET_INT ) {
            final long mant = decoder.decodeMandLong();

            return _prevDecimal.set( exp, mant );    // store new values ready for next call
        }

        throwMissingValueException();

        return Constants.UNSET_DOUBLE; // keep compiler happy as it cant see throw methods runtime exception
    }
}
