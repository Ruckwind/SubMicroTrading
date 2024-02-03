/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.msgdict.increment.int64;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.common.LongFieldValWrapper;
import com.rr.core.codec.binary.fastfix.msgdict.increment.IncrementFieldReader;
import com.rr.core.lang.Constants;

public final class ULongOptReaderIncrement extends IncrementFieldReader implements FixFieldReader<LongFieldValWrapper> {

    private final long _init;
    private       long _previous;

    public ULongOptReaderIncrement( String name, int id, String init ) {
        this( name, id, FieldUtils.parseLong( init ) );
    }

    public ULongOptReaderIncrement( String name, int id ) {
        this( name, id, Constants.UNSET_LONG );
    }

    public ULongOptReaderIncrement( String name, int id, long init ) {
        super( name, id, true );
        _init = (init == Constants.UNSET_LONG) ? Constants.UNSET_LONG : init - 1;
        reset();
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, LongFieldValWrapper dest ) {
        dest.setVal( read( decoder, mapReader ) );
    }

    @Override
    public void reset() {
        _previous = _init;
    }

    public long getInitValue() {
        return _init;
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     */
    public long read( final FastFixDecodeBuilder decoder, final PresenceMapReader mapReader ) {

        if ( mapReader.isNextFieldPresent() ) {
            final long val = decoder.decodeOptionalULong();
            _previous = val;
            return val;
        }

        if ( _previous != Constants.UNSET_LONG ) { // no presence bit, but have prev field so just inc
            final long nextSeq = _previous + 1;
            _previous = nextSeq;
            return nextSeq;
        }

        return Constants.UNSET_LONG; // keep compiler happy as it cant see throw methods runtime exception
    }
}
