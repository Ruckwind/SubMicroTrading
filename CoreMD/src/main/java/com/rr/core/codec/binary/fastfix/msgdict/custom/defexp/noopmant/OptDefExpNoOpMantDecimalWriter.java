/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.msgdict.custom.defexp.noopmant;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapWriter;
import com.rr.core.codec.binary.fastfix.common.ComponentFactory;
import com.rr.core.codec.binary.fastfix.common.FastFixDecimal;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.common.def.int32.IntOptWriterDefault;
import com.rr.core.codec.binary.fastfix.common.noop.int64.LongMandWriterNoOp;
import com.rr.core.codec.binary.fastfix.msgdict.custom.CustomDecimalFieldWriter;
import com.rr.core.lang.Constants;
import com.rr.core.utils.Utils;

/**
 * CME decimal are all default exponent of 2 with delta mantissa
 *
 * @author Richard Rose
 */
public class OptDefExpNoOpMantDecimalWriter extends CustomDecimalFieldWriter {

    private final int _initExp;

    private final FastFixDecimal _prevDecimal = new FastFixDecimal();

    private final IntOptWriterDefault _exp;
    private final LongMandWriterNoOp  _mant;

    public OptDefExpNoOpMantDecimalWriter( ComponentFactory cf, String name, int id, String initExp, String initMant ) {
        this( cf, name, id, FieldUtils.parseInt( initExp ) );
    }

    @SuppressWarnings( "boxing" )
    public OptDefExpNoOpMantDecimalWriter( ComponentFactory cf, String name, int id, int initExp ) {
        super( name, id, true );

        _exp  = cf.getWriter( IntOptWriterDefault.class, name + "Exp", id, initExp );
        _mant = cf.getWriter( LongMandWriterNoOp.class, name + "Mant", id );

        _initExp = initExp;

        reset();
    }

    @Override
    public boolean requiresPMap() {
        return _exp.requiresPMap();
    }

    public double getInitValue( FastFixDecimal t ) {
        return t.set( _exp.getInitValue(), Constants.UNSET_LONG );
    }

    public double getPreviousValue() {
        return _prevDecimal.get();
    }

    public void reset() {
        _prevDecimal.set( _initExp, 0 );
        _exp.reset();
        _mant.reset();
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     *
     * @param encoder
     * @param mapWriter
     * @param value
     */
    public void write( final FastFixBuilder encoder, final PresenceMapWriter mapWriter, final double value ) {
        if ( Utils.isNull( value ) ) {
            mapWriter.setCurrentField();
            encoder.encodeNull();
        } else {
            _prevDecimal.set( value );
            _exp.write( encoder, mapWriter, _prevDecimal.getExponent() );
            _mant.write( encoder, _prevDecimal.getMantissa() );
        }
    }
}
