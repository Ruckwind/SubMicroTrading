/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.msgdict.delta.decimal;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.DoubleFieldValWrapper;
import com.rr.core.codec.binary.fastfix.common.FastFixDecimal;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.msgdict.delta.DeltaFieldReader;
import com.rr.core.lang.Constants;

public final class DecimalOptReaderDelta extends DeltaFieldReader implements FixFieldReader<DoubleFieldValWrapper> {

    private final double         _init;
    private final FastFixDecimal _prevDecimal = new FastFixDecimal();
    private       double         _curVal;

    public DecimalOptReaderDelta( String name, int id, String init ) {
        this( name, id, FieldUtils.parseDouble( init ) );
    }

    public DecimalOptReaderDelta( String name, int id ) {
        this( name, id, 0 );
    }

    public DecimalOptReaderDelta( String name, int id, double init ) {
        super( name, id, true );
        _init = init;
        reset();
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, DoubleFieldValWrapper dest ) {
        dest.setVal( read( decoder ) );
    }

    @Override
    public void reset() {
        _prevDecimal.set( _init );
        _curVal = _init;
    }

    public double getInitValue() {
        return _init;
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     */
    public double read( final FastFixDecodeBuilder decoder ) {

        if ( !_prevDecimal.isNull() ) {
            final int deltaExp = decoder.decodeOptionalInt();

            if ( deltaExp != Constants.UNSET_INT ) {
                final long deltaMant = decoder.decodeMandLongOverflow();

                final int  newExp  = _prevDecimal.getExponent() + deltaExp;
                final long newMant = _prevDecimal.getMantissa() + deltaMant;

                _curVal = _prevDecimal.set( newExp, newMant );    // store new values ready for next call

                return _curVal;
            }
        } else {
            throwMissingPreviousException();
        }

        return Constants.UNSET_DOUBLE; // keep compiler happy as it cant see throw methods runtime exception
    }
}
