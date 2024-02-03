/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.msgdict.delta.int64;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.common.LongFieldValWrapper;
import com.rr.core.codec.binary.fastfix.msgdict.delta.DeltaFieldReader;
import com.rr.core.lang.Constants;

public final class LongMandReaderDelta extends DeltaFieldReader implements FixFieldReader<LongFieldValWrapper> {

    private final long _init;
    private       long _previous;

    public LongMandReaderDelta( String name, int id, String init ) {
        this( name, id, FieldUtils.parseLong( init ) );
    }

    public LongMandReaderDelta( String name, int id ) {
        this( name, id, 0 );
    }

    public LongMandReaderDelta( String name, int id, long init ) {
        super( name, id, false );
        if ( init == Constants.UNSET_LONG ) init = 0L;
        _init = init;
        reset();
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, LongFieldValWrapper dest ) {
        dest.setVal( read( decoder ) );
    }

    @Override
    public void reset() {
        _previous = _init;
    }

    public long getInitValue() {
        return _init;
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     */
    public long read( final FastFixDecodeBuilder decoder ) {

        if ( _previous != Constants.UNSET_LONG ) {
            final long delta = decoder.decodeMandLongOverflow();

            if ( delta != Constants.UNSET_LONG ) {
                final long value = _previous + delta;

                _previous = value;

                return value;
            }

            throwMissingValueException();
        } else {
            throwMissingPreviousException();
        }

        throwMissingValueException();

        return Constants.UNSET_LONG; // keep compiler happy as it cant see throw methods runtime exception
    }
}
