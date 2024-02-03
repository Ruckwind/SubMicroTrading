/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common.def.int32;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.common.IntFieldValWrapper;
import com.rr.core.codec.binary.fastfix.common.def.DefaultFieldReader;
import com.rr.core.lang.Constants;

public final class IntOptReaderDefault extends DefaultFieldReader implements FixFieldReader<IntFieldValWrapper> {

    private final int _init;

    public IntOptReaderDefault( String name, int id, String init ) {
        this( name, id, FieldUtils.parseInt( init ) );
    }

    public IntOptReaderDefault( String name, int id ) {
        this( name, id, Constants.UNSET_INT );
    }

    public IntOptReaderDefault( String name, int id, int init ) {
        super( name, id, true );
        _init = init;
        reset();
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, IntFieldValWrapper dest ) {
        dest.setVal( read( decoder, mapReader ) );
    }

    @Override
    public void reset() {
        // nothing
    }

    public int getInitValue() {
        return _init;
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     */
    public int read( final FastFixDecodeBuilder decoder, final PresenceMapReader mapReader ) {

        final boolean present = mapReader.isNextFieldPresent();

        if ( present ) {
            final int value = decoder.decodeOptionalInt();

            return value;
        }

        if ( _init != Constants.UNSET_INT ) {
            return _init;                       // value not present, but have previous value to copy
        }

        return Constants.UNSET_INT;            // not present and no previous value
    }
}
