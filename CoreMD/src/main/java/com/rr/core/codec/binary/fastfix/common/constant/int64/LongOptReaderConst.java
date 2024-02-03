/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common.constant.int64;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.common.LongFieldValWrapper;
import com.rr.core.codec.binary.fastfix.common.constant.ConstantFieldReader;
import com.rr.core.lang.Constants;

public final class LongOptReaderConst extends ConstantFieldReader implements FixFieldReader<LongFieldValWrapper> {

    private final long _constVal;

    public LongOptReaderConst( String name, int id, String init ) {
        this( name, id, FieldUtils.parseLong( init ) );
    }

    public LongOptReaderConst( String name, int id, long init ) {
        super( name, id, true );
        _constVal = init;
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, LongFieldValWrapper dest ) {
        dest.setVal( read( decoder, mapReader ) );
    }

    public long getInitValue() {
        return _constVal;
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     */
    public long read( final FastFixDecodeBuilder decoder, final PresenceMapReader mapReader ) {
        final boolean present = mapReader.isNextFieldPresent();

        return (present) ? _constVal : Constants.UNSET_LONG;
    }
}
