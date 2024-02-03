/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.msgdict.copy.int64;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.common.LongFieldValWrapper;
import com.rr.core.codec.binary.fastfix.msgdict.copy.CopyFieldReader;
import com.rr.core.lang.Constants;

public final class LongOptReaderCopy extends CopyFieldReader implements FixFieldReader<LongFieldValWrapper> {

    private final long _init;
    private       long _previous;

    public LongOptReaderCopy( String name, int id, String init ) {
        this( name, id, FieldUtils.parseLong( init ) );
    }

    public LongOptReaderCopy( String name, int id ) {
        this( name, id, Constants.UNSET_LONG );
    }

    public LongOptReaderCopy( String name, int id, long init ) {
        super( name, id, true );
        _init = init;
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

        final boolean present = mapReader.isNextFieldPresent();

        if ( present ) {
            final long value = decoder.decodeOptionalLong();

            _previous = value;

            return value;
        }

        if ( _previous != Constants.UNSET_LONG ) {
            return _previous;                       // value not present, but have previous value to copy
        }

        return Constants.UNSET_LONG;            // not present and no previous value
    }
}
