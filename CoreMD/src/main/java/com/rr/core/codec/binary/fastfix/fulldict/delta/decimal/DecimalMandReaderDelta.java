/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.fulldict.delta.decimal;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.DoubleFieldValWrapper;
import com.rr.core.codec.binary.fastfix.common.FastFixDecimal;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.fulldict.delta.DeltaFieldReader;
import com.rr.core.codec.binary.fastfix.fulldict.entry.DoubleFieldDictEntry;
import com.rr.core.lang.Constants;

public final class DecimalMandReaderDelta extends DeltaFieldReader implements FixFieldReader<DoubleFieldValWrapper> {

    private final DoubleFieldDictEntry _previous;
    private final FastFixDecimal       _prevDecimal;

    public DecimalMandReaderDelta( String name, int id, DoubleFieldDictEntry previous ) {
        super( name, id, false );
        _previous    = previous;
        _prevDecimal = previous.getPrevDecimal();
        reset();
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, DoubleFieldValWrapper dest ) {
        dest.setVal( read( decoder ) );
    }

    @Override
    public void reset() {
        _previous.reset();
    }

    public double getInitValue() {
        return _previous.getInit();
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     */
    public double read( final FastFixDecodeBuilder decoder ) {

        if ( !_prevDecimal.isNull() ) {
            final int deltaExp = decoder.decodeMandInt();

            if ( deltaExp != Constants.UNSET_INT ) {
                final long deltaMant = decoder.decodeMandLongOverflow();

                final int  newExp  = _prevDecimal.getExponent() + deltaExp;
                final long newMant = _prevDecimal.getMantissa() + deltaMant;

                final double val = _prevDecimal.set( newExp, newMant );

                _previous.setVal( val );

                return val;
            }

            throwMissingValueException();
        } else {
            throwMissingPreviousException();
        }

        throwMissingValueException();

        return Constants.UNSET_DOUBLE; // keep compiler happy as it cant see throw methods runtime exception
    }
}
