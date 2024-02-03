/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common.def.string;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapWriter;
import com.rr.core.codec.binary.fastfix.common.def.DefaultFieldWriter;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;

public final class StringWriterDefault extends DefaultFieldWriter {

    private final ReusableString _init = new ReusableString();

    public StringWriterDefault( String name, int id, ZString init ) {
        super( name, id, false );
        _init.copy( init );
        reset();
    }

    public StringWriterDefault( String name, int id, String init ) {
        super( name, id, false );
        _init.copy( init );
        reset();
    }

    public ZString getInitValue() {
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
    public void write( final FastFixBuilder encoder, final PresenceMapWriter mapWriter, final ZString value ) {
        if ( value != null && !value.equals( _init ) ) {
            mapWriter.setCurrentField();
            encoder.encodeString( value );
        } else {                                    // value unchanged dont need encode (it will be copied on decode)
            mapWriter.clearCurrentField();
        }
    }
}
