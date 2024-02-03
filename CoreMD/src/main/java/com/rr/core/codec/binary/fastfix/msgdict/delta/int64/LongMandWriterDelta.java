/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.msgdict.delta.int64;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.msgdict.delta.DeltaFieldWriter;
import com.rr.core.lang.Constants;

public final class LongMandWriterDelta extends DeltaFieldWriter {

    private final long _init;
    private       long _previous;

    public LongMandWriterDelta( String name, int id, String init ) {
        this( name, id, FieldUtils.parseLong( init ) );
    }

    public LongMandWriterDelta( String name, int id ) {
        this( name, id, 0 );
    }

    public LongMandWriterDelta( String name, int id, long init ) {
        super( name, id, false );
        _init = init;
        reset();
    }

    public long getInitValue() {
        return _init;
    }

    public long getPreviousValue() {
        return _previous;
    }

    public void reset() {
        _previous = _init;
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     *
     * @param encoder
     * @param value
     */
    public void write( final FastFixBuilder encoder, final long value ) {
        if ( value != Constants.UNSET_LONG ) {
            if ( _previous != Constants.UNSET_LONG ) {
                final long delta = value - _previous;

                boolean overflow = false;

                if ( value > -1 && _previous <= (Long.MIN_VALUE + value) ) {
                    overflow = true;
                } else if ( value < -1 && _previous > (value - Long.MIN_VALUE) ) {
                    overflow = true;
                }

                encoder.encodeMandLongOverflow( delta, overflow );
            } else {
                throwMissingPreviousException();
            }
        } else {
            throwMissingValueException();
        }

        _previous = value;
    }
}
