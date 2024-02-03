/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.msgdict.copy.decimal;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapWriter;
import com.rr.core.codec.binary.fastfix.common.FastFixDecimal;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.msgdict.copy.CopyFieldWriter;
import com.rr.core.lang.Constants;
import com.rr.core.utils.Utils;

public final class DecimalMandWriterCopy extends CopyFieldWriter {

    private final double         _init;
    private final FastFixDecimal _prevDecimal = new FastFixDecimal();
    private final FastFixDecimal _tmpValue    = new FastFixDecimal();

    public DecimalMandWriterCopy( String name, int id, String init ) {
        this( name, id, FieldUtils.parseDouble( init ) );
    }

    public DecimalMandWriterCopy( String name, int id ) {
        this( name, id, Constants.UNSET_DOUBLE );
    }

    public DecimalMandWriterCopy( String name, int id, double init ) {
        super( name, id, false );
        _init = init;
        reset();
    }

    public double getInitValue() {
        return _init;
    }

    public double getPreviousValue() {
        return _prevDecimal.get();
    }

    public void reset() {
        _prevDecimal.set( _init );
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     *
     * @param encoder
     * @param mapWriter
     * @param value
     */
    public void write( final FastFixBuilder encoder, final PresenceMapWriter mapWriter, final double value ) {
        if ( Utils.hasVal( value ) ) {
            _tmpValue.set( value );
            if ( !_tmpValue.equals( _prevDecimal ) ) {
                _prevDecimal.set( _tmpValue );                      // new value must be encoded
                mapWriter.setCurrentField();
                encoder.encodeMandInt( _tmpValue.getExponent() );
                encoder.encodeMandLong( _tmpValue.getMantissa() );
            } else {                                    // value unchanged dont need encode (it will be copied on decode)
                mapWriter.clearCurrentField();
            }
        } else {
            throwMissingValueException();
        }
    }
}
