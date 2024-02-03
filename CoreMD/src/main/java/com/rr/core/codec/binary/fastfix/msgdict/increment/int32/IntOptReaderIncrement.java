/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.msgdict.increment.int32;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.common.IntFieldValWrapper;
import com.rr.core.codec.binary.fastfix.msgdict.increment.IncrementFieldReader;
import com.rr.core.lang.Constants;

public final class IntOptReaderIncrement extends IncrementFieldReader implements FixFieldReader<IntFieldValWrapper> {

    private final int _init;
    private       int _previous;

    public IntOptReaderIncrement( String name, int id, String init ) {
        this( name, id, FieldUtils.parseInt( init ) );
    }

    public IntOptReaderIncrement( String name, int id ) {
        this( name, id, Constants.UNSET_INT );
    }

    public IntOptReaderIncrement( String name, int id, int init ) {
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
        _previous = _init;
    }

    public int getInitValue() {
        return _init;
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     */
    public int read( final FastFixDecodeBuilder decoder, final PresenceMapReader mapReader ) {

        if ( mapReader.isNextFieldPresent() ) {
            final int val = decoder.decodeOptionalInt();
            _previous = val;
            return val;
        }

        if ( _previous != Constants.UNSET_INT ) { // no presence bit, but have prev field so just inc
            final int nextSeq = _previous + 1;
            _previous = nextSeq;
            return nextSeq;
        }

        return Constants.UNSET_INT; // keep compiler happy as it cant see throw methods runtime exception
    }
}
