/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.msgdict.custom.noopexp.noopmant;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.common.ComponentFactory;
import com.rr.core.codec.binary.fastfix.common.FastFixDecimal;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.common.noop.int32.IntOptWriterNoOp;
import com.rr.core.codec.binary.fastfix.common.noop.int64.LongMandWriterNoOp;
import com.rr.core.codec.binary.fastfix.msgdict.custom.CustomDecimalFieldWriter;
import com.rr.core.lang.Constants;
import com.rr.core.utils.Utils;

/**
 * CME decimal are all default exponent of 2 with delta mantissa
 *
 * @author Richard Rose
 */
public class OptNoOpExpNoOpMantDecimalWriter extends CustomDecimalFieldWriter {

    private final FastFixDecimal _prevDecimal = new FastFixDecimal();

    private final IntOptWriterNoOp   _exp;
    private final LongMandWriterNoOp _mant;

    public OptNoOpExpNoOpMantDecimalWriter( ComponentFactory cf, String name, int id, String initExp, String initMant ) {
        this( cf, name, id, FieldUtils.parseInt( initExp ) );
    }

    @SuppressWarnings( "boxing" )
    public OptNoOpExpNoOpMantDecimalWriter( ComponentFactory cf, String name, int id, int initExp ) {
        super( name, id, true );

        _exp  = cf.getWriter( IntOptWriterNoOp.class, name + "Exp", id, initExp );
        _mant = cf.getWriter( LongMandWriterNoOp.class, name + "Mant", id );

        reset();
    }

    @Override
    public boolean requiresPMap() {
        return _exp.requiresPMap() || _mant.requiresPMap();
    }

    public double getInitValue() {
        return Constants.UNSET_DOUBLE;
    }

    public double getPreviousValue() {
        return _prevDecimal.get();
    }

    public void reset() {
        // nothing
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     *
     * @param encoder
     * @param value
     */
    public void write( final FastFixBuilder encoder, final double value ) {
        if ( Utils.isNull( value ) ) {
            encoder.encodeNull();
        } else {
            _prevDecimal.set( value );
            _exp.write( encoder, _prevDecimal.getExponent() );
            _mant.write( encoder, _prevDecimal.getMantissa() );
        }
    }
}
