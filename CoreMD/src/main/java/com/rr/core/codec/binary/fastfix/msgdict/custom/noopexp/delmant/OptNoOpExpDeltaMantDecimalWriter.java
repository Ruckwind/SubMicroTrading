/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.msgdict.custom.noopexp.delmant;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapWriter;
import com.rr.core.codec.binary.fastfix.common.ComponentFactory;
import com.rr.core.codec.binary.fastfix.common.FastFixDecimal;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.common.noop.int32.IntOptWriterNoOp;
import com.rr.core.codec.binary.fastfix.msgdict.custom.CustomDecimalFieldWriter;
import com.rr.core.codec.binary.fastfix.msgdict.delta.int64.LongMandWriterDelta;
import com.rr.core.utils.Utils;

/**
 * CME decimal are all default exponent of 2 with delta mantissa
 *
 * @author Richard Rose
 */
public class OptNoOpExpDeltaMantDecimalWriter extends CustomDecimalFieldWriter {

    private final int  _initExp;
    private final long _initMant;

    private final FastFixDecimal _prevDecimal = new FastFixDecimal();

    private final IntOptWriterNoOp    _exp;
    private final LongMandWriterDelta _mant;

    public OptNoOpExpDeltaMantDecimalWriter( ComponentFactory cf, String name, int id, String initExp, String initMant ) {
        this( cf, name, id, FieldUtils.parseInt( initExp ), FieldUtils.parseLong( initMant ) );
    }

    @SuppressWarnings( "boxing" )
    public OptNoOpExpDeltaMantDecimalWriter( ComponentFactory cf, String name, int id, int initExp, long initMant ) {
        super( name, id, true );

        _exp  = cf.getWriter( IntOptWriterNoOp.class, name + "Exp", id, initExp );
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
        return t.set( 0, _mant.getInitValue() );
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
        if ( Utils.isNull( value ) ) {
            mapWriter.setCurrentField();
            encoder.encodeNull();
        } else {
            _prevDecimal.set( value );
            _exp.write( encoder, _prevDecimal.getExponent() );
            _mant.write( encoder, _prevDecimal.getMantissa() );
        }
    }
}
