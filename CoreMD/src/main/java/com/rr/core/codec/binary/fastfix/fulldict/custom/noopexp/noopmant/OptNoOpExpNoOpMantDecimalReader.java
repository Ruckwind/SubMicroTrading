/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.fulldict.custom.noopexp.noopmant;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.ComponentFactory;
import com.rr.core.codec.binary.fastfix.common.DoubleFieldValWrapper;
import com.rr.core.codec.binary.fastfix.common.FastFixDecimal;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.common.noop.int32.IntOptReaderNoOp;
import com.rr.core.codec.binary.fastfix.common.noop.int64.LongMandReaderNoOp;
import com.rr.core.codec.binary.fastfix.fulldict.custom.CustomDecimalFieldReader;
import com.rr.core.lang.Constants;

/**
 * CME decimal are all default exponent of 2 with delta mantissa
 *
 * @author Richard Rose
 */
public class OptNoOpExpNoOpMantDecimalReader extends CustomDecimalFieldReader {

    private final FastFixDecimal _prevDecimal = new FastFixDecimal();

    private final IntOptReaderNoOp   _exp;
    private final LongMandReaderNoOp _mant;

    public OptNoOpExpNoOpMantDecimalReader( ComponentFactory cf, String name, int id, String initExp, String initMant ) {
        this( cf, name, id, FieldUtils.parseInt( initExp ) );
    }

    @SuppressWarnings( "boxing" )
    public OptNoOpExpNoOpMantDecimalReader( ComponentFactory cf, String name, int id, int initExp ) {
        super( name, id, true );

        _exp  = cf.getReader( IntOptReaderNoOp.class, name + "Exp", id );
        _mant = cf.getReader( LongMandReaderNoOp.class, name + "Mant", id );

        reset();
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, DoubleFieldValWrapper dest ) {
        dest.setVal( read( decoder ) );
    }

    @Override
    public boolean requiresPMap() {
        return _exp.requiresPMap() || _mant.requiresPMap();
    }

    @Override
    public void reset() {
        // nothing
    }

    public double getInitValue() {
        return Constants.UNSET_DOUBLE;
    }

    public double getPreviousValue() {
        return _prevDecimal.get();
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     *
     * @param encoder
     */
    public double read( final FastFixDecodeBuilder encoder ) {
        final int exp = _exp.read( encoder );

        if ( exp != Constants.UNSET_INT ) {
            final long mant = _mant.read( encoder );

            return _prevDecimal.set( exp, mant );
        }

        return Constants.UNSET_DOUBLE;
    }
}
