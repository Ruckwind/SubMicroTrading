/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common.constant.string;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapWriter;
import com.rr.core.codec.binary.fastfix.common.constant.ConstantFieldWriter;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;

public final class StringOptWriterConst extends ConstantFieldWriter {

    private ReusableString _constVal = new ReusableString();

    public StringOptWriterConst( String name, int id, String init ) {
        this( name, id, new ViewString( init ) );
    }

    public StringOptWriterConst( String name, int id, ZString init ) {
        super( name, id, false );
        _constVal.copy( init );
    }

    public ZString getInitValue() {
        return _constVal;
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     *
     * @param encoder
     * @param mapWriter
     */
    public void write( final FastFixBuilder encoder, final PresenceMapWriter mapWriter, boolean present ) {
        if ( present ) {
            mapWriter.setCurrentField();
        } else {
            mapWriter.clearCurrentField();
        }
    }
}
