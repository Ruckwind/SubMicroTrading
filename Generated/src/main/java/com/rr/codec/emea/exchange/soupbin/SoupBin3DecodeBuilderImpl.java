/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.codec.emea.exchange.soupbin;

import com.rr.core.codec.binary.BinaryBigEndianDecoderUtils;
import com.rr.core.lang.TimeUtils;
import com.rr.core.lang.TimeUtilsFactory;

import java.util.TimeZone;

/**
 * @author Richard Rose
 * @NOTE dont check for max idx before each put the calling code must ensure buffer is big enough
 * (typically 8K)
 */
public final class SoupBin3DecodeBuilderImpl extends BinaryBigEndianDecoderUtils {

    public static final double KEEP_DECIMAL_PLACE_FACTOR = 100000000D;

    private static final int MAX_DP = 8;

    private byte[]    _buffer;
    private int       _startOffset;
    private int       _idx;
    private int       _msgLen;
    private int       _maxIdx;
    private TimeUtils _tzCalc = TimeUtilsFactory.createTimeUtils();

    public SoupBin3DecodeBuilderImpl() {
        super();
        _tzCalc.setLocalTimezone( TimeZone.getTimeZone( "GMT" ) );
    }

    @Override public void setTimeUtils( TimeUtils calc ) { _tzCalc = calc; }

    @Override public void start( final byte[] msg, final int offset, final int maxIdx ) {
        _buffer      = msg;
        _startOffset = offset;
        _idx         = _startOffset;
        _maxIdx      = maxIdx;
    }
}
