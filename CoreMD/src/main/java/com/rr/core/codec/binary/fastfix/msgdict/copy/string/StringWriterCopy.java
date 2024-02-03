/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.msgdict.copy.string;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapWriter;
import com.rr.core.codec.binary.fastfix.msgdict.copy.CopyFieldWriter;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;

public final class StringWriterCopy extends CopyFieldWriter {

    private final ReusableString _init     = new ReusableString();
    private final ReusableString _previous = new ReusableString();

    public StringWriterCopy( String name, int id ) {
        this( name, id, (String) null );
    }

    public StringWriterCopy( String name, int id, ZString init ) {
        super( name, id, false );
        _init.copy( init );
        reset();
    }

    public StringWriterCopy( String name, int id, String init ) {
        super( name, id, false );
        _init.copy( init );
        reset();
    }

    public ZString getInitValue() {
        return _init;
    }

    public ReusableString getPreviousValue() {
        return _previous;
    }

    public void reset() {
        _previous.copy( _init );
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     *
     * @param encoder
     * @param mapWriter
     * @param value
     */
    public void write( final FastFixBuilder encoder, final PresenceMapWriter mapWriter, final ZString value ) {
        if ( value != null && !value.equals( _previous ) ) {
            _previous.setValue( value );
            mapWriter.setCurrentField();
            encoder.encodeString( value );
        } else {                                    // value unchanged dont need encode (it will be copied on decode)
            mapWriter.clearCurrentField();
        }
    }
}
