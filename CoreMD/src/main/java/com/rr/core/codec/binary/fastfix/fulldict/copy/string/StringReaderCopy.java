/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.fulldict.copy.string;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.common.StringFieldValWrapper;
import com.rr.core.codec.binary.fastfix.fulldict.copy.CopyFieldReader;
import com.rr.core.codec.binary.fastfix.fulldict.entry.StringFieldDictEntry;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;

public final class StringReaderCopy extends CopyFieldReader implements FixFieldReader<StringFieldValWrapper> {

    private final StringFieldDictEntry _previous;

    public StringReaderCopy( String name, int id, StringFieldDictEntry previous ) {
        super( name, id, false );
        _previous = previous;
        reset();
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, StringFieldValWrapper dest ) {
        read( decoder, mapReader, dest.getVal() );
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
    public void read( final FastFixDecodeBuilder decoder, final PresenceMapReader mapReader, ReusableString dest ) {

        final boolean present = mapReader.isNextFieldPresent();

        if ( present ) {
            decoder.decodeString( dest );

            _previous.setVal( dest );

        } else {

            // cannot differentiate null from empty string

            if ( dest != null ) dest.copy( _previous.getVal() );
        }
    }
}
