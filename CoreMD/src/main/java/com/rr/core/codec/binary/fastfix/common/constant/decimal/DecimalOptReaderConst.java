/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common.constant.decimal;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.DoubleFieldValWrapper;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.common.constant.ConstantFieldReader;
import com.rr.core.lang.Constants;

public final class DecimalOptReaderConst extends ConstantFieldReader implements FixFieldReader<DoubleFieldValWrapper> {

    private final double _constVal;

    public DecimalOptReaderConst( String name, int id, String init ) {
        this( name, id, FieldUtils.parseDouble( init ) );
    }

    public DecimalOptReaderConst( String name, int id, double init ) {
        super( name, id, true );
        _constVal = init;
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, DoubleFieldValWrapper dest ) {
        dest.setVal( read( decoder, mapReader ) );
    }

    public double getInitValue() {
        return _constVal;
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     */
    public double read( final FastFixDecodeBuilder decoder, final PresenceMapReader mapReader ) {

        final boolean present = mapReader.isNextFieldPresent();

        return (present) ? _constVal : Constants.UNSET_DOUBLE;
    }
}

