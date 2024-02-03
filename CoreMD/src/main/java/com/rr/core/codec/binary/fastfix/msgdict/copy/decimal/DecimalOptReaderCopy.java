/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.msgdict.copy.decimal;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.DoubleFieldValWrapper;
import com.rr.core.codec.binary.fastfix.common.FastFixDecimal;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.msgdict.copy.CopyFieldReader;
import com.rr.core.lang.Constants;

public final class DecimalOptReaderCopy extends CopyFieldReader implements FixFieldReader<DoubleFieldValWrapper> {

    private final double         _init;
    private       FastFixDecimal _prevDecimal = new FastFixDecimal();
    private       double         _previous;

    public DecimalOptReaderCopy( String name, int id, String init ) {
        this( name, id, FieldUtils.parseDouble( init ) );
    }

    public DecimalOptReaderCopy( String name, int id ) {
        this( name, id, Constants.UNSET_DOUBLE );
    }

    public DecimalOptReaderCopy( String name, int id, double init ) {
        super( name, id, true );
        _init = init;
        reset();
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, DoubleFieldValWrapper dest ) {
        dest.setVal( read( decoder, mapReader ) );
    }

    @Override
    public void reset() {
        _prevDecimal.set( _init );
        _previous = _init;
    }

    public double getInitValue() {
        return _init;
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     */
    public double read( final FastFixDecodeBuilder decoder, final PresenceMapReader mapReader ) {

        final boolean present = mapReader.isNextFieldPresent();

        if ( present ) {
            final int exponent = decoder.decodeOptionalInt();

            if ( exponent != Constants.UNSET_INT ) {
                final long value = decoder.decodeMandLong();

                _previous = _prevDecimal.set( exponent, value );
            }
        }

        return _previous;                           // value not present, but have previous value to copy
    }
}
