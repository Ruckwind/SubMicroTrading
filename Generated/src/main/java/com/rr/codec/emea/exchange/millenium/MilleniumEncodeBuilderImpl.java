/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.codec.emea.exchange.millenium;

import com.rr.core.codec.binary.BinaryLittleEndianEncoderUtils;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ZString;
import com.rr.core.utils.Utils;

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
public final class MilleniumEncodeBuilderImpl extends BinaryLittleEndianEncoderUtils {

    public static final  double KEEP_DECIMAL_PLACE_FACTOR = 100000000D;
    private static final int    HEADER_UPTO_TYPE          = 3;

    private final byte _protocolVersion;

    public static long priceToExternalLong( double price ) {

        if ( price >= 0 ) {
            if ( price == Double.MAX_VALUE ) {
                return Long.MAX_VALUE;
            }
            return (long) ((price + Constants.WEIGHT) * KEEP_DECIMAL_PLACE_FACTOR);
        }

        if ( Utils.isNull( price ) ) { // NULL
            return 0;
        } else if ( price == -Double.MAX_VALUE ) {
            return Long.MIN_VALUE;
        }

        return (long) ((price - Constants.WEIGHT) * KEEP_DECIMAL_PLACE_FACTOR);
    }

    public MilleniumEncodeBuilderImpl( byte[] buffer, int offset, ZString protocolVersion ) {
        super( buffer, offset );

        _protocolVersion = protocolVersion.getByte( 0 );
        _tzCalc.setLocalTimezone( TimeZone.getTimeZone( "GMT" ) );
    }

    @Override
    public void start( final int msgType ) {
        _idx              = _startOffset;
        _buffer[ _idx++ ] = _protocolVersion;
        _idx += 2;                  // spacer for MsgLen
        _buffer[ _idx++ ] = (byte) msgType;
    }

    @Override
    public int end() {
        _msgLen = _idx - _startOffset;
        _idx    = _startOffset + 1;        // correct position for len
        encodeShort( _msgLen - HEADER_UPTO_TYPE );
        return _msgLen;
    }

    @Override
    public final void encodePrice( final double price ) {

        long value = priceToExternalLong( price );

        encodeLong( value );
    }

    @Override
    public void encodeTimestampLocal( long internalTime ) {
        final long msUTC    = _tzCalc.internalTimeToLocalMSFromStartDay( internalTime );
        final int  unixTime = (int) (msUTC / 1000);
        final int  micros   = (int) ((msUTC - (unixTime * 1000)) * 10);
        encodeInt( unixTime );
        encodeInt( micros );
    }

    @Override
    public void encodeTimestampUTC( long internalTime ) {
        final long msUTC    = _tzCalc.internalTimeToUnixTime( internalTime );
        final int  unixTime = (int) (msUTC / 1000);
        final int  micros   = (int) ((msUTC - (unixTime * 1000)) * 10);
        encodeInt( unixTime );
        encodeInt( micros );
    }

    @Override
    public void encodeTimeUTC( long internalTime ) {
        _idx = _tzCalc.internalTimeToHHMMSS( _buffer, _idx, internalTime );
    }

    @Override
    public void encodeTimeLocal( long internalTime ) {
        final long msUTC    = _tzCalc.internalTimeToLocalMSFromStartDay( internalTime );
        final int  unixTime = (int) (msUTC / 1000);
        encodeInt( unixTime );
    }
}
