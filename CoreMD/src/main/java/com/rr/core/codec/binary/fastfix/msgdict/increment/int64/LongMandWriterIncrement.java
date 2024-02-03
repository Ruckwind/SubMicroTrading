/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.msgdict.increment.int64;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapWriter;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.msgdict.increment.IncrementFieldWriter;
import com.rr.core.lang.Constants;

public final class LongMandWriterIncrement extends IncrementFieldWriter {

    private final long _init;
    private       long _previous;

    public LongMandWriterIncrement( String name, int id, String init ) {
        this( name, id, FieldUtils.parseLong( init ) );
    }

    public LongMandWriterIncrement( String name, int id ) {
        this( name, id, Constants.UNSET_LONG );
    }

    public LongMandWriterIncrement( String name, int id, long init ) {
        super( name, id, false );
        _init = (init == Constants.UNSET_LONG) ? Constants.UNSET_LONG : init - 1;
        reset();
    }

    public long getInitValue() {
        return _init;
    }

    public long getPreviousValue() {
        return _previous;
    }

    public void reset() {
        _previous = _init;
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     *
     * @param encoder
     * @param mapWriter
     * @param value
     */
    public void write( final FastFixBuilder encoder, final PresenceMapWriter mapWriter, final long value ) {
        if ( value != Constants.UNSET_LONG ) {
            if ( _previous != Constants.UNSET_LONG ) {
                final long nextSeq = _previous + 1;

                if ( nextSeq == value ) {           // expected seq num value nothing to encode
                    mapWriter.clearCurrentField();
                    _previous = nextSeq;
                    return;
                }
            }

            _previous = value;                  // override expected seqnum and encode
            mapWriter.setCurrentField();
            encoder.encodeMandLong( value );
        } else {
            throwMissingValueException();
        }
    }
}
