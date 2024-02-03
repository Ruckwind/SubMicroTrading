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

public final class LongMandReaderConst extends ConstantFieldReader implements FixFieldReader<LongFieldValWrapper> {

    private final long _constVal;

    public LongMandReaderConst( String name, int id, String init ) {
        this( name, id, FieldUtils.parseLong( init ) );
    }

    public LongMandReaderConst( String name, int id, long init ) {
        super( name, id, false );
        _constVal = init;
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, LongFieldValWrapper dest ) {
        dest.setVal( read() );
    }

    public long getInitValue() {
        return _constVal;
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     */
    public long read() {
        return _constVal;
    }
}
