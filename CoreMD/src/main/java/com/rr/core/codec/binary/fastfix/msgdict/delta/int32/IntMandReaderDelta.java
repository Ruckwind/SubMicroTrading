/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.msgdict.delta.int32;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.common.IntFieldValWrapper;
import com.rr.core.codec.binary.fastfix.msgdict.delta.DeltaFieldReader;
import com.rr.core.lang.Constants;

public final class IntMandReaderDelta extends DeltaFieldReader implements FixFieldReader<IntFieldValWrapper> {

    private final int _init;
    private       int _previous;

    public IntMandReaderDelta( String name, int id, String init ) {
        this( name, id, FieldUtils.parseInt( init ) );
    }

    public IntMandReaderDelta( String name, int id ) {
        this( name, id, 0 );
    }

    public IntMandReaderDelta( String name, int id, int init ) {
        super( name, id, false );
        if ( init == Constants.UNSET_INT ) init = 0;
        _init = init;
        reset();
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, IntFieldValWrapper dest ) {
        dest.setVal( read( decoder ) );
    }

    @Override
    public void reset() {
        _previous = _init;
    }

    public int getInitValue() {
        return _init;
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     */
    public int read( final FastFixDecodeBuilder decoder ) {

        if ( _previous != Constants.UNSET_INT ) {
            final int delta = decoder.decodeMandIntOverflow();

            if ( delta != Constants.UNSET_INT ) {
                final int value = _previous + delta;

                _previous = value;

                return value;
            }

            throwMissingValueException();
        } else {
            throwMissingPreviousException();
        }

        throwMissingValueException();

        return Constants.UNSET_INT; // keep compiler happy as it cant see throw methods runtime exception
    }
}
