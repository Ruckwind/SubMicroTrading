/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.msgdict.custom.noopexp.noopmant;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.common.ComponentFactory;
import com.rr.core.codec.binary.fastfix.common.FastFixDecimal;
import com.rr.core.codec.binary.fastfix.common.noop.int32.IntMandWriterNoOp;
import com.rr.core.codec.binary.fastfix.common.noop.int64.LongMandWriterNoOp;
import com.rr.core.codec.binary.fastfix.msgdict.custom.CustomDecimalFieldWriter;
import com.rr.core.lang.Constants;

/**
 * CME decimal are all default exponent of 2 with delta mantissa
 *
 * @author Richard Rose
 */
public class MandNoOpExpNoOpMantDecimalWriter extends CustomDecimalFieldWriter {

    private final FastFixDecimal _prevDecimal = new FastFixDecimal();

    private final IntMandWriterNoOp  _exp;
    private final LongMandWriterNoOp _mant;

    public MandNoOpExpNoOpMantDecimalWriter( ComponentFactory cf, String name, int id, String initExp, String initMant ) {
        this( cf, name, id );
    }

    @SuppressWarnings( "boxing" )
    public MandNoOpExpNoOpMantDecimalWriter( ComponentFactory cf, String name, int id ) {
        super( name, id, false );

        _exp  = cf.getWriter( IntMandWriterNoOp.class, name + "Exp", id );
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
        _prevDecimal.set( value );
        _exp.write( encoder, _prevDecimal.getExponent() );
        _mant.write( encoder, _prevDecimal.getMantissa() );
    }
}
