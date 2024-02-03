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
import com.rr.core.utils.Utils;

public final class DecimalMandReaderCopy extends CopyFieldReader implements FixFieldReader<DoubleFieldValWrapper> {

    private final double         _init;
    private final FastFixDecimal _prevDecimal = new FastFixDecimal();
    private       double         _previous;

    public DecimalMandReaderCopy( String name, int id, String init ) {
        this( name, id, FieldUtils.parseDouble( init ) );
    }

    public DecimalMandReaderCopy( String name, int id ) {
        this( name, id, Constants.UNSET_DOUBLE );
    }

    public DecimalMandReaderCopy( String name, int id, double init ) {
        super( name, id, false );
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
            final int  exponent = decoder.decodeMandInt();
            final long value    = decoder.decodeMandLong();

            _previous = _prevDecimal.set( exponent, value );

            return _previous;
        }

        if ( Utils.hasVal( _previous ) ) {
            return _previous;                           // value not present, but have previous value to copy
        }

        throwMissingValueException();

        return Constants.UNSET_DOUBLE; // keep compiler happy as it cant see throw methods runtime exception
    }
}
