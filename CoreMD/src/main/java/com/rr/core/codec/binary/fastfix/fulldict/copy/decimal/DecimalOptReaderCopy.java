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

public final class DecimalOptReaderCopy extends CopyFieldReader implements FixFieldReader<DoubleFieldValWrapper> {

    private final DoubleFieldDictEntry _previous;

    public DecimalOptReaderCopy( String name, int id, DoubleFieldDictEntry previous ) {
        super( name, id, true );
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
            final int exponent = decoder.decodeOptionalInt();

            if ( exponent != Constants.UNSET_INT ) {
                final long value = decoder.decodeMandLong();

                return _previous.setVal( exponent, value );
            }
        }

        return _previous.getVal();
    }
}
