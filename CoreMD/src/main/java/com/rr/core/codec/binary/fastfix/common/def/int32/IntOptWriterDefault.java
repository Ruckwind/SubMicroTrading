/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common.def.int32;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapWriter;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.common.def.DefaultFieldWriter;
import com.rr.core.lang.Constants;

public final class IntOptWriterDefault extends DefaultFieldWriter {

    private final int _init;

    public IntOptWriterDefault( String name, int id, String init ) {
        this( name, id, FieldUtils.parseInt( init ) );
    }

    public IntOptWriterDefault( String name, int id ) {
        this( name, id, Constants.UNSET_INT );
    }

    public IntOptWriterDefault( String name, int id, int init ) {
        super( name, id, true );
        _init = init;
        reset();
    }

    public int getInitValue() {
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
    public void write( final FastFixBuilder encoder, final PresenceMapWriter mapWriter, final int value ) {
        if ( value != Constants.UNSET_INT ) {
            if ( value != _init ) {
                mapWriter.setCurrentField();
                encoder.encodeOptionalInt( value );
            } else {                                    // value unchanged dont need encode (it will be copied on decode)
                mapWriter.clearCurrentField();
            }
        } else {
            if ( _init != Constants.UNSET_INT ) {  // current value is null, need null previous value and encode null byte
                mapWriter.setCurrentField();
                encoder.encodeNull();
            } else {
                mapWriter.clearCurrentField();
            }
        }
    }
}
