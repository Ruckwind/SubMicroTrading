/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common.def.int64;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.common.LongFieldValWrapper;
import com.rr.core.codec.binary.fastfix.common.def.DefaultFieldReader;
import com.rr.core.lang.Constants;

public final class ULongOptReaderDefault extends DefaultFieldReader implements FixFieldReader<LongFieldValWrapper> {

    private final long _init;

    public ULongOptReaderDefault( String name, int id, String init ) {
        this( name, id, FieldUtils.parseLong( init ) );
    }

    public ULongOptReaderDefault( String name, int id ) {
        this( name, id, Constants.UNSET_LONG );
    }

    public ULongOptReaderDefault( String name, int id, long init ) {
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
        // nothing
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
            final long value = decoder.decodeOptionalULong();

            return value;
        }

        if ( _init != Constants.UNSET_LONG ) {
            return _init;                       // value not present, but have previous value to copy
        }

        return Constants.UNSET_LONG;            // not present and no previous value
    }
}
