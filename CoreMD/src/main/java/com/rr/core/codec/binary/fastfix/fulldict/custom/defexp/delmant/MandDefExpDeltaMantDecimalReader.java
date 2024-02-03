/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.fulldict.custom.defexp.delmant;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.ComponentFactory;
import com.rr.core.codec.binary.fastfix.common.DoubleFieldValWrapper;
import com.rr.core.codec.binary.fastfix.common.FastFixDecimal;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.common.def.int32.IntMandReaderDefault;
import com.rr.core.codec.binary.fastfix.fulldict.custom.CustomDecimalFieldReader;
import com.rr.core.codec.binary.fastfix.fulldict.delta.int64.LongMandReaderDelta;
import com.rr.core.codec.binary.fastfix.fulldict.entry.DictEntry;

/**
 * CME decimal are all default exponent of 2 with delta mantissa
 *
 * @author Richard Rose
 */
public class MandDefExpDeltaMantDecimalReader extends CustomDecimalFieldReader {

    private final int  _initExp;
    private final long _initMant;

    private final FastFixDecimal _prevDecimal = new FastFixDecimal();

    private final IntMandReaderDefault _exp;
    private final LongMandReaderDelta  _mant;

    public MandDefExpDeltaMantDecimalReader( ComponentFactory cf, String name, int id, String initExp, String initMant ) {
        this( cf, name, id, FieldUtils.parseInt( initExp ), FieldUtils.parseLong( initMant ) );
    }

    @SuppressWarnings( "boxing" )
    public MandDefExpDeltaMantDecimalReader( ComponentFactory cf, String name, int id, int initExp, long initMant ) {
        super( name, id, false );

        String mantName = name + "Mant";

        DictEntry prevMant = cf.getPrevFieldValInt64Wrapper( mantName, initMant );

        _exp  = cf.getReader( IntMandReaderDefault.class, name + "Exp", id, initExp );
        _mant = cf.getReader( LongMandReaderDelta.class, mantName, id, prevMant );

        _initExp  = initExp;
        _initMant = initMant;

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
        _prevDecimal.set( _initExp, _initMant );
        _exp.reset();
        _mant.reset();
    }

    public double getInitValue( FastFixDecimal t ) {
        return t.set( _exp.getInitValue(), _mant.getInitValue() );
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
