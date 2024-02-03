/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.msgdict.copy.int32;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapWriter;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.msgdict.copy.CopyFieldWriter;
import com.rr.core.lang.Constants;

public final class IntMandWriterCopy extends CopyFieldWriter {

    private final int _init;
    private       int _previous;

    public IntMandWriterCopy( String name, int id, String init ) {
        this( name, id, FieldUtils.parseInt( init ) );
    }

    public IntMandWriterCopy( String name, int id ) {
        this( name, id, Constants.UNSET_INT );
    }

    public IntMandWriterCopy( String name, int id, int init ) {
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
     * @param mapWriter
     * @param value
     */
    public void write( final FastFixBuilder encoder, final PresenceMapWriter mapWriter, final int value ) {
        if ( value != Constants.UNSET_INT ) {
            if ( value != _previous ) {
                _previous = value;                      // new value must be encoded
                mapWriter.setCurrentField();
                encoder.encodeMandInt( value );
            } else {                                    // value unchanged dont need encode (it will be copied on decode)
                mapWriter.clearCurrentField();
            }
        } else {
            throwMissingValueException();
        }
    }
}
