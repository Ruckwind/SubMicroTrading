/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.msgdict.custom.defexp.noopmant;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.ComponentFactory;
import com.rr.core.codec.binary.fastfix.common.DoubleFieldValWrapper;
import com.rr.core.codec.binary.fastfix.common.FastFixDecimal;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.common.def.int32.IntMandReaderDefault;
import com.rr.core.codec.binary.fastfix.common.noop.int64.LongMandReaderNoOp;
import com.rr.core.codec.binary.fastfix.msgdict.custom.CustomDecimalFieldReader;
import com.rr.core.lang.Constants;

/**
 * CME decimal are all default exponent of 2 with delta mantissa
 *
 * @author Richard Rose
 */
public class MandDefExpNoOpMantDecimalReader extends CustomDecimalFieldReader {

    private final int _initExp;

    private final FastFixDecimal _prevDecimal = new FastFixDecimal();

    private final IntMandReaderDefault _exp;
    private final LongMandReaderNoOp   _mant;

    public MandDefExpNoOpMantDecimalReader( ComponentFactory cf, String name, int id, String initExp, String initMant ) {
        this( cf, name, id, FieldUtils.parseInt( initExp ) );
    }

    @SuppressWarnings( "boxing" )
    public MandDefExpNoOpMantDecimalReader( ComponentFactory cf, String name, int id, int initExp ) {
        super( name, id, false );

        _exp  = cf.getReader( IntMandReaderDefault.class, name + "Exp", id, initExp );
        _mant = cf.getReader( LongMandReaderNoOp.class, name + "Mant", id );

        _initExp = initExp;

        reset();
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, DoubleFieldValWrapper dest ) {
        dest.setVal( read( decoder, mapReader ) );
    }

    @Override
    public boolean requiresPMap() {
        return _exp.requiresPMap() || _mant.requiresPMap();
    }

    @Override
    public void reset() {
        _prevDecimal.set( _initExp, 0 );
        _exp.reset();
        _mant.reset();
    }

    public double getInitValue( FastFixDecimal t ) {
        return t.set( _exp.getInitValue(), Constants.UNSET_LONG );
    }

    public double getPreviousValue() {
        return _prevDecimal.get();
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     *
     * @param encoder
     */
    public double read( final FastFixDecodeBuilder encoder, final PresenceMapReader mapReader ) {
        final int  exp  = _exp.read( encoder, mapReader );
        final long mant = _mant.read( encoder );

        return _prevDecimal.set( exp, mant );
    }
}
