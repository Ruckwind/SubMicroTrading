/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.codec.emea.exchange.millenium;

import com.rr.core.codec.binary.BinaryLittleEndianDecoderUtils;
import com.rr.core.lang.CommonTimeUtils;
import com.rr.core.lang.Constants;

import java.util.TimeZone;

/**
 * @author Richard Rose
 * @NOTE dont check for max idx before each put the calling code must ensure buffer is big enough
 * (typically 8K)
 * <p>
 * each message has header
 * <p>
 * ProtocolVersion message start                      1 2 message start, hard coded value of '2'
 * MsgLen          Message Length Num                 2 0->65535
 * MsgType         Message type Alpha                 1 A Logon
 */
public final class MilleniumDecodeBuilderImpl extends BinaryLittleEndianDecoderUtils {

    public static final double KEEP_DECIMAL_PLACE_FACTOR = 100000000D;

    public MilleniumDecodeBuilderImpl() {
        super();
        _tzCalc.setLocalTimezone( TimeZone.getTimeZone( "GMT" ) );
    }

    @Override
    public double decodePrice() {

        final long value = decodeLong();

        double price;

        if ( value >= 0 ) {
            if ( value == Long.MAX_VALUE ) {
                price = Double.MAX_VALUE;
            } else {
                price = value / KEEP_DECIMAL_PLACE_FACTOR;
            }
        } else {
            if ( value == Long.MIN_VALUE ) { // NULL
                price = Constants.UNSET_DOUBLE;
            } else if ( value == Constants.MIN_PRICE_AS_LONG ) {
                price = -Double.MAX_VALUE;
            } else {
                price = value / KEEP_DECIMAL_PLACE_FACTOR;
            }
        }

        return price;
    }

    @Override
    public long decodeTimeLocal() {
        final long msLocal = decodeInt() * 1000;
        return _tzCalc.localMSFromMidnightToInternalTimeToday( msLocal );
    }

    @Override
    public long decodeTimeUTC() {
        final long msUTC = decodeInt() * 1000;
        return CommonTimeUtils.unixTimeToInternalTime( msUTC );
    }

    @Override
    public long decodeTimestampLocal() {
        long unixTimeLocalSecs = decodeInt();
        long milliseconds      = decodeInt() / 10;
        return _tzCalc.localMSFromMidnightToInternalTimeToday( unixTimeLocalSecs * 1000 + milliseconds );
    }

    @Override
    public long decodeTimestampUTC() {
        long unixTimeSecs = decodeInt();
        long milliseconds = decodeInt() / 10;
        return CommonTimeUtils.unixTimeToInternalTime( unixTimeSecs * 1000 + milliseconds );
    }
}
