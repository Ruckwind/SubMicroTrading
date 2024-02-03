/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.fulldict.copy.int64;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.common.LongFieldValWrapper;
import com.rr.core.codec.binary.fastfix.fulldict.copy.CopyFieldReader;
import com.rr.core.codec.binary.fastfix.fulldict.entry.LongFieldDictEntry;
import com.rr.core.lang.Constants;

public final class ULongMandReaderCopy extends CopyFieldReader implements FixFieldReader<LongFieldValWrapper> {

    private final LongFieldDictEntry _previous;

    public ULongMandReaderCopy( String name, int id, LongFieldDictEntry previous ) {
        super( name, id, false );
        _previous = previous;
        reset();
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, LongFieldValWrapper dest ) {
        dest.setVal( read( decoder, mapReader ) );
    }

    @Override
    public void reset() {
        _previous.reset();
    }

    public long getInitValue() {
        return _previous.getInit();
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     */
    public long read( final FastFixDecodeBuilder decoder, final PresenceMapReader mapReader ) {

        final boolean present = mapReader.isNextFieldPresent();

        if ( present ) {
            final long value = decoder.decodeMandULong();

            _previous.setVal( value );

            return value;
        }

        final long previous = _previous.getVal();

        if ( previous != Constants.UNSET_LONG ) {
            return previous; // value not present, but have previous value to copy
        }

        throwMissingValueException();

        return Constants.UNSET_LONG; // keep compiler happy as it cant see throw methods runtime exception
    }
}
