/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.msgdict.delta.int32;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.msgdict.delta.DeltaFieldWriter;
import com.rr.core.lang.Constants;

public final class IntMandWriterDelta extends DeltaFieldWriter {

    private final int _init;
    private       int _previous;

    public IntMandWriterDelta( String name, int id, String init ) {
        this( name, id, FieldUtils.parseInt( init ) );
    }

    public IntMandWriterDelta( String name, int id ) {
        this( name, id, 0 );
    }

    public IntMandWriterDelta( String name, int id, int init ) {
        super( name, id, false );
        _init = init;
        reset();
    }

    public int getInitValue() {
        return _init;
    }

    public int getPreviousValue() {
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
    public void write( final FastFixBuilder encoder, final int value ) {
        if ( value != Constants.UNSET_INT ) {
            if ( _previous != Constants.UNSET_INT ) {
                final int delta = value - _previous;

                boolean overflow = false;

                if ( value > -1 && _previous <= (Integer.MIN_VALUE + value) ) {
                    overflow = true;
                } else if ( value < -1 && _previous > (value - Integer.MIN_VALUE) ) {
                    overflow = true;
                }

                encoder.encodeMandIntOverflow( delta, overflow );
            } else {
                throwMissingPreviousException();
            }
        } else {
            throwMissingValueException();
        }

        _previous = value;
    }
}
