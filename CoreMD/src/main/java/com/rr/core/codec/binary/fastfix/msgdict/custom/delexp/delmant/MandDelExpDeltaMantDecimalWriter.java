/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.msgdict.custom.delexp.delmant;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapWriter;
import com.rr.core.codec.binary.fastfix.common.ComponentFactory;
import com.rr.core.codec.binary.fastfix.common.FastFixDecimal;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.msgdict.custom.CustomDecimalFieldWriter;
import com.rr.core.codec.binary.fastfix.msgdict.delta.int32.IntMandWriterDelta;
import com.rr.core.codec.binary.fastfix.msgdict.delta.int64.LongMandWriterDelta;

/**
 * CME decimal are all default exponent of 2 with delta mantissa
 *
 * @author Richard Rose
 */
public class MandDelExpDeltaMantDecimalWriter extends CustomDecimalFieldWriter {

    private final int  _initExp;
    private final long _initMant;

    private final FastFixDecimal _prevDecimal = new FastFixDecimal();

    private final IntMandWriterDelta  _exp;
    private final LongMandWriterDelta _mant;

    public MandDelExpDeltaMantDecimalWriter( ComponentFactory cf, String name, int id, String initExp, String initMant ) {
        this( cf, name, id, FieldUtils.parseInt( initExp ), FieldUtils.parseLong( initMant ) );
    }

    @SuppressWarnings( "boxing" )
    public MandDelExpDeltaMantDecimalWriter( ComponentFactory cf, String name, int id, int initExp, long initMant ) {
        super( name, id, false );

        _exp  = cf.getWriter( IntMandWriterDelta.class, name + "Exp", id, initExp );
        _mant = cf.getWriter( LongMandWriterDelta.class, name + "Mant", id, initMant );

        _initExp  = initExp;
        _initMant = initMant;

        reset();
    }

    @Override
    public boolean requiresPMap() {
        return _exp.requiresPMap() || _mant.requiresPMap();
    }

    public double getInitValue( FastFixDecimal t ) {
        return t.set( _exp.getInitValue(), _mant.getInitValue() );
    }

    public double getPreviousValue() {
        return _prevDecimal.get();
    }

    public void reset() {
        _prevDecimal.set( _initExp, _initMant );
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
        _prevDecimal.set( value );
        _exp.write( encoder, _prevDecimal.getExponent() );
        _mant.write( encoder, _prevDecimal.getMantissa() );
    }
}
