/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.fulldict.delta.string;

import com.rr.core.codec.RuntimeEncodingException;
import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.common.StringFieldValWrapper;
import com.rr.core.codec.binary.fastfix.fulldict.delta.DeltaFieldReader;
import com.rr.core.codec.binary.fastfix.fulldict.entry.StringFieldDictEntry;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;

public final class StringReaderDelta extends DeltaFieldReader implements FixFieldReader<StringFieldValWrapper> {

    private final StringFieldDictEntry _previous;

    public StringReaderDelta( String name, int id, StringFieldDictEntry previous ) {
        super( name, id, false );
        _previous = previous;
        reset();
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, StringFieldValWrapper dest ) {
        read( decoder, dest.getVal() );
    }

    @Override
    public void reset() {
        _previous.reset();
    }

    public ZString getInitValue() {
        return _previous.getInit();
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     */
    public void read( final FastFixDecodeBuilder decoder, ReusableString dest ) {

        dest.reset();

        final int deltaIdx = decoder.decodeOptionalInt();

        if ( deltaIdx == Constants.UNSET_INT ) return;

        final ReusableString previous = _previous.getVal();

        if ( deltaIdx < 0 ) {
            final int substringIdx = -1 - deltaIdx;

            if ( substringIdx > previous.length() ) {
                throwBadDeltaValueException( substringIdx );
            }

            decoder.decodeString( dest );
            dest.append( previous, substringIdx );
        } else {
            if ( deltaIdx > previous.length() ) {
                throwBadDeltaValueException( deltaIdx );
            }

            dest.append( previous, 0, previous.length() - deltaIdx );

            decoder.decodeString( dest );
        }

        previous.copy( dest );
    }

    private void throwBadDeltaValueException( int idx ) {
        throw new RuntimeEncodingException( "Bad string delta idx of " + idx + ", prevVal=" + _previous.toString() + ", id=" + getName() );
    }
}
