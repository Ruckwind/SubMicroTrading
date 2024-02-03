/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.fulldict.copy.decimal;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.DoubleFieldValWrapper;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.fulldict.copy.CopyFieldReader;
import com.rr.core.codec.binary.fastfix.fulldict.entry.DoubleFieldDictEntry;
import com.rr.core.lang.Constants;
import com.rr.core.utils.Utils;

public final class DecimalMandReaderCopy extends CopyFieldReader implements FixFieldReader<DoubleFieldValWrapper> {

    private final DoubleFieldDictEntry _previous;

    public DecimalMandReaderCopy( String name, int id, DoubleFieldDictEntry previous ) {
        super( name, id, false );
        _previous = previous;
        reset();
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, DoubleFieldValWrapper dest ) {
        dest.setVal( read( decoder, mapReader ) );
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
    public double read( final FastFixDecodeBuilder decoder, final PresenceMapReader mapReader ) {

        final boolean present = mapReader.isNextFieldPresent();

        if ( present ) {
            final int  exponent = decoder.decodeMandInt();
            final long value    = decoder.decodeMandLong();

            final double val = _previous.setVal( exponent, value );

            return val;
        }

        final double previous = _previous.getVal();

        if ( Utils.hasVal( previous ) ) {
            return previous; // value not present, but have previous value to copy
        }

        throwMissingValueException();

        return Constants.UNSET_DOUBLE; // keep compiler happy as it cant see throw methods runtime exception
    }
}
