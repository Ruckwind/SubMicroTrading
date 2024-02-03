/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.codec.emea.exchange.soupbin;

import com.rr.core.codec.binary.BinaryBigEndianEncoderUtils;
import com.rr.core.lang.ZString;

import java.util.TimeZone;

/**
 * @author Richard Rose
 * @NOTE dont check for max idx before each put the calling code must ensure buffer is big enough
 * (typically 8K)
 */
public final class SoupBin3EncodeBuilderImpl extends BinaryBigEndianEncoderUtils {

    public static final double KEEP_DECIMAL_PLACE_FACTOR = 100000000D;

    public SoupBin3EncodeBuilderImpl( byte[] buffer, int offset, ZString protocolVersion ) {
        super( buffer, offset );

        _tzCalc.setLocalTimezone( TimeZone.getDefault() );
    }
}
