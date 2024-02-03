/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common.def.int64;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapWriter;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.common.def.DefaultFieldWriter;
import com.rr.core.lang.Constants;

public final class ULongMandWriterDefault extends DefaultFieldWriter {

    private final long _init;

    public ULongMandWriterDefault( String name, int id, String init ) {
        this( name, id, FieldUtils.parseLong( init ) );
    }

    public ULongMandWriterDefault( String name, int id ) {
        this( name, id, Constants.UNSET_LONG );
    }

    public ULongMandWriterDefault( String name, int id, long init ) {
        super( name, id, false );
        _init = init;
        reset();
    }

    public long getInitValue() {
        return _init;
    }

    public void reset() {
        // nothing
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     *
     * @param encoder
     * @param mapWriter
     * @param value
     */
    public void write( final FastFixBuilder encoder, final PresenceMapWriter mapWriter, final long value ) {
        if ( value != Constants.UNSET_LONG ) {
            if ( value != _init ) {
                mapWriter.setCurrentField();
                encoder.encodeMandULong( value );
            } else {                                    // value unchanged dont need encode (it will be copied on decode)
                mapWriter.clearCurrentField();
            }
        } else {
            throwMissingValueException();
        }
    }
}
