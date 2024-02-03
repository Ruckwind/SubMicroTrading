/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.fulldict.delta.int64;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.common.LongFieldValWrapper;
import com.rr.core.codec.binary.fastfix.fulldict.delta.DeltaFieldReader;
import com.rr.core.codec.binary.fastfix.fulldict.entry.LongFieldDictEntry;
import com.rr.core.lang.Constants;

public final class LongOptReaderDelta extends DeltaFieldReader implements FixFieldReader<LongFieldValWrapper> {

    private final LongFieldDictEntry _previous;

    public LongOptReaderDelta( String name, int id, LongFieldDictEntry previous ) {
        super( name, id, true );
        _previous = previous;
        reset();
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, LongFieldValWrapper dest ) {
        dest.setVal( read( decoder ) );
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
    public long read( final FastFixDecodeBuilder decoder ) {

        final long previous = _previous.getVal();

        if ( previous != Constants.UNSET_LONG ) {
            final long delta = decoder.decodeOptionalLongOverflow();

            if ( delta != Constants.UNSET_LONG ) {
                final long value = previous + delta;

                _previous.setVal( value );

                return value;
            }
        } else {
            throwMissingPreviousException();
        }

        return Constants.UNSET_LONG;
    }
}
