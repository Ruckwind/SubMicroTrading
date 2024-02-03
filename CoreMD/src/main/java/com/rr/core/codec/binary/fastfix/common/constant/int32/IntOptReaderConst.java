/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common.constant.int32;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.common.IntFieldValWrapper;
import com.rr.core.codec.binary.fastfix.common.constant.ConstantFieldReader;
import com.rr.core.lang.Constants;

public final class IntOptReaderConst extends ConstantFieldReader implements FixFieldReader<IntFieldValWrapper> {

    private final int _constVal;

    public IntOptReaderConst( String name, int id, String init ) {
        this( name, id, FieldUtils.parseInt( init ) );
    }

    public IntOptReaderConst( String name, int id, int init ) {
        super( name, id, true );
        _constVal = init;
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, IntFieldValWrapper dest ) {
        dest.setVal( read( decoder, mapReader ) );
    }

    public int getInitValue() {
        return _constVal;
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     */
    public int read( final FastFixDecodeBuilder decoder, final PresenceMapReader mapReader ) {
        final boolean present = mapReader.isNextFieldPresent();

        return (present) ? _constVal : Constants.UNSET_INT;
    }
}
