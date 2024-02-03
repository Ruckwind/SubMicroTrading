/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.msgdict.custom.noopexp.noopmant;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.ComponentFactory;
import com.rr.core.codec.binary.fastfix.common.DoubleFieldValWrapper;
import com.rr.core.codec.binary.fastfix.common.FastFixDecimal;
import com.rr.core.codec.binary.fastfix.common.noop.int32.IntMandReaderNoOp;
import com.rr.core.codec.binary.fastfix.common.noop.int64.LongMandReaderNoOp;
import com.rr.core.codec.binary.fastfix.msgdict.custom.CustomDecimalFieldReader;
import com.rr.core.lang.Constants;

/**
 * CME decimal are all default exponent of 2 with delta mantissa
 *
 * @author Richard Rose
 */
public class MandNoOpExpNoOpMantDecimalReader extends CustomDecimalFieldReader {

    private final FastFixDecimal _prevDecimal = new FastFixDecimal();

    private final IntMandReaderNoOp  _exp;
    private final LongMandReaderNoOp _mant;

    public MandNoOpExpNoOpMantDecimalReader( ComponentFactory cf, String name, int id, String initExp, String initMant ) {
        this( cf, name, id );
    }

    @SuppressWarnings( "boxing" )
    public MandNoOpExpNoOpMantDecimalReader( ComponentFactory cf, String name, int id ) {
        super( name, id, false );

        _exp  = cf.getReader( IntMandReaderNoOp.class, name + "Exp", id );
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
        final int  exp  = _exp.read( encoder );
        final long mant = _mant.read( encoder );

        return _prevDecimal.set( exp, mant );
    }
}
