/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.fulldict.increment.int32;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.common.IntFieldValWrapper;
import com.rr.core.codec.binary.fastfix.fulldict.entry.IntFieldDictEntry;
import com.rr.core.codec.binary.fastfix.fulldict.increment.IncrementFieldReader;
import com.rr.core.lang.Constants;

public final class IntMandReaderIncrement extends IncrementFieldReader implements FixFieldReader<IntFieldValWrapper> {

    private final IntFieldDictEntry _previous;

    public IntMandReaderIncrement( String name, int id, IntFieldDictEntry previous ) {
        super( name, id, false );
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

        if ( mapReader.isNextFieldPresent() ) {
            final int val = decoder.decodeMandInt();
            _previous.setVal( val );
            return val;
        }

        final int previous = _previous.getVal();

        if ( previous != Constants.UNSET_INT ) { // no presence bit, but have prev field so just inc
            final int nextSeq = previous + 1;
            _previous.setVal( nextSeq );
            return nextSeq;
        }

        throwMissingPreviousException();

        return Constants.UNSET_INT; // keep compiler happy as it cant see throw methods runtime exception
    }
}
