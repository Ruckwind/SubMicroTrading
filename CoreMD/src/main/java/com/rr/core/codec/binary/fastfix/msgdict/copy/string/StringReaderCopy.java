/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.msgdict.copy.string;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.common.StringFieldValWrapper;
import com.rr.core.codec.binary.fastfix.msgdict.copy.CopyFieldReader;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;

public final class StringReaderCopy extends CopyFieldReader implements FixFieldReader<StringFieldValWrapper> {

    private final ReusableString _init     = new ReusableString();
    private final ReusableString _previous = new ReusableString();

    public StringReaderCopy( String name, int id ) {
        this( name, id, (String) null );
    }

    public StringReaderCopy( String name, int id, ZString init ) {
        super( name, id, false );
        _init.copy( init );
        reset();
    }

    public StringReaderCopy( String name, int id, String init ) {
        super( name, id, false );
        _init.copy( init );
        reset();
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, StringFieldValWrapper dest ) {
        read( decoder, mapReader, dest.getVal() );
    }

    @Override
    public void reset() {
        _previous.copy( _init );
    }

    public ZString getInitValue() {
        return _init;
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     */
    public void read( final FastFixDecodeBuilder decoder, final PresenceMapReader mapReader, ReusableString dest ) {

        final boolean present = mapReader.isNextFieldPresent();

        if ( present ) {
            decoder.decodeString( dest );

            _previous.setValue( dest );

        } else {

            // cannot differentiate null from empty string

            if ( dest != null ) dest.copy( _previous );
        }
    }
}
