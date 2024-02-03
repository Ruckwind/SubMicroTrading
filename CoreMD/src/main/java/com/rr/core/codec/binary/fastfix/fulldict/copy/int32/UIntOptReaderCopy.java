/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.fulldict.copy.int32;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.common.IntFieldValWrapper;
import com.rr.core.codec.binary.fastfix.fulldict.copy.CopyFieldReader;
import com.rr.core.codec.binary.fastfix.fulldict.entry.IntFieldDictEntry;
import com.rr.core.lang.Constants;

public final class UIntOptReaderCopy extends CopyFieldReader implements FixFieldReader<IntFieldValWrapper> {

    private final IntFieldDictEntry _previous;

    public UIntOptReaderCopy( String name, int id, IntFieldDictEntry previous ) {
        super( name, id, true );
        _previous = previous;
        reset();
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, IntFieldValWrapper dest ) {
        dest.setVal( read( decoder, mapReader ) );
    }

    @Override
    public void reset() {
        _previous.reset();
    }

    public int getInitValue() {
        return _previous.getInit();
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     */
    public int read( final FastFixDecodeBuilder decoder, final PresenceMapReader mapReader ) {

        final boolean present = mapReader.isNextFieldPresent();

        if ( present ) {
            final int value = decoder.decodeOptionalUInt();

            _previous.setVal( value );

            return value;
        }

        final int previous = _previous.getVal();

        if ( previous != Constants.UNSET_INT ) {
            return previous; // value not present, but have previous value to copy
        }

        return Constants.UNSET_INT;            // not present and no previous value
    }
}
