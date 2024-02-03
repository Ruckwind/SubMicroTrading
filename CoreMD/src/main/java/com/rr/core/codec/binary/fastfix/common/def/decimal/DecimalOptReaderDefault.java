/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common.def.decimal;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.DoubleFieldValWrapper;
import com.rr.core.codec.binary.fastfix.common.FastFixDecimal;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.common.def.DefaultFieldReader;
import com.rr.core.lang.Constants;

public final class DecimalOptReaderDefault extends DefaultFieldReader implements FixFieldReader<DoubleFieldValWrapper> {

    private final double         _init;
    private final FastFixDecimal _prevDecimal = new FastFixDecimal();
    private final FastFixDecimal _tmpDecimal  = new FastFixDecimal();
    private       double         _previous;

    public DecimalOptReaderDefault( String name, int id, String init ) {
        this( name, id, FieldUtils.parseDouble( init ) );
    }

    public DecimalOptReaderDefault( String name, int id, double init ) {
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

                return _tmpDecimal.set( exponent, value );
            }
        }

        return _previous;                           // value not present, but have previous value to copy
    }
}
