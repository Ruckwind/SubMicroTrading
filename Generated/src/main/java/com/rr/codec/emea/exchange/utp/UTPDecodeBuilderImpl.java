/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.codec.emea.exchange.utp;

import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.codec.binary.BinaryBigEndianDecoderUtils;
import com.rr.core.lang.CommonTimeUtils;
import com.rr.core.lang.Constants;
import com.rr.core.time.TimeTables;

import java.util.TimeZone;

/**
 * @author Richard Rose
 * @NOTE dont check for max idx before each put the calling code must ensure buffer is big enough
 * (typically 8K)
 * <p>
 * each message has header
 * <p>
 * HEADER          Message type Alpha                 1 A Logon
 * ProtocolVersion CCG Binary protocol version Alpha  1 2 Extended ClientOrderID protocol type P78
 * MsgLen          Message Length Num                 2 0->65535 P72
 */
public final class UTPDecodeBuilderImpl extends BinaryBigEndianDecoderUtils {

    private static final int    MAX_PRICE_DP = 6;
    private static final double DP_FACTOR    = 1000000D;

    public UTPDecodeBuilderImpl() {
        super();
        _tzCalc.setLocalTimezone( TimeZone.getTimeZone( "CET" ) );
    }

    @Override
    public double decodePrice() {

        final int value = decodeInt();

        double price;

        if ( value >= 0 ) {
            if ( value == Integer.MAX_VALUE ) {
                price = Double.MAX_VALUE;
            } else {
                price = value / DP_FACTOR;
            }
        } else {
            if ( value == Integer.MIN_VALUE ) { // NULL
                price = Constants.UNSET_DOUBLE;
            } else if ( value == Constants.MIN_PRICE_AS_INT ) {
                price = -Double.MAX_VALUE;
            } else {
                price = value / DP_FACTOR;
            }
        }

        return price;
    }

    @Override
    public long decodeTimeLocal() {
        final long msLocal = getMSFromStartDay();
        return _tzCalc.localMSFromMidnightToInternalTimeToday( msLocal );
    }

    @Override
    public long decodeTimeUTC() {
        final long msUTC = getMSFromStartDay();
        return msUTC;
    }

    @Override
    public long decodeTimestampLocal() {
        long unixTimeLocalSecs = decodeInt();
        return _tzCalc.localMSFromMidnightToInternalTimeToday( unixTimeLocalSecs * 1000 );
    }

    @Override
    public long decodeTimestampUTC() {
        long unixTimeSecs = decodeInt();
        return CommonTimeUtils.unixTimeToInternalTime( unixTimeSecs * 1000 );
    }

    @Override
    public void end() {
        final byte etx = decodeByte();

        if ( etx != 0x0A ) {
            throw new RuntimeDecodingException( "UTPMessage unexpected ETX byte at idx=" + _idx + ", val=" + etx );
        }

        super.end();
    }

    private long getMSFromStartDay() {

        if ( _maxIdx < (_idx + 6) ) {
            throw new RuntimeDecodingException( "Missing part of time field, idx=" + _idx + ", maxIdx is " + _maxIdx );
        }

        int hTen = _buffer[ _idx++ ] - '0';
        int hDig = _buffer[ _idx++ ] - '0';
        int mTen = _buffer[ _idx++ ] - '0';
        int mDig = _buffer[ _idx++ ] - '0';
        int sTen = _buffer[ _idx++ ] - '0';
        int sDig = _buffer[ _idx++ ] - '0';

        int hour = ((hTen) * 10) + (hDig);
        int min  = ((mTen) * 10) + (mDig);
        int sec  = ((sTen) * 10) + (sDig);

        if ( hour < 0 || hour > 23 ) {
            throw new RuntimeDecodingException( "Invalid hour '" + hour + "' in time format" );
        }

        if ( min < 0 || min > 59 ) {
            throw new RuntimeDecodingException( "Invalid min '" + min + "' in time format" );
        }

        if ( sec < 0 || sec > 59 ) {
            throw new RuntimeDecodingException( "Invalid sec '" + sec + "' in time format" );
        }

        // TODO bench the multiply vs table look up on target hw & os

        int ms = TimeTables._hourToMS[ hour ] + TimeTables._minToMS[ min ] + TimeTables._secToMS[ sec ] + _tzCalc.getOffset();
        // int ms = ((hour * 3600) + (min*60) + sec) * 1000 + _tzCalculator.getOffset();

        return ms;
    }
}
