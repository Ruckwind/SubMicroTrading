/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.codec.emea.exchange.utp;

import com.rr.core.codec.binary.BinaryBigEndianEncoderUtils;
import com.rr.core.lang.ZString;
import com.rr.core.utils.NumberUtils;

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
public final class UTPEncodeBuilderImpl extends BinaryBigEndianEncoderUtils {

    private final byte _protocolVersion;

    public UTPEncodeBuilderImpl( byte[] buffer, int offset, ZString protocolVersion ) {
        super( buffer, offset );

        _protocolVersion = protocolVersion.getByte( 0 );
        _tzCalc.setLocalTimezone( TimeZone.getTimeZone( "CET" ) );
    }

    @Override
    public void start( final int msgType ) {
        _idx              = _startOffset;
        _buffer[ _idx++ ] = (byte) msgType;
        _buffer[ _idx++ ] = _protocolVersion;
        _idx += 2;                  // spacer for MsgLen
    }

    @Override
    public int end() {
        _buffer[ _idx++ ] = 0x0A;          // END record marker
        _msgLen           = _idx - _startOffset;
        _idx              = _startOffset + 2;        // correct position for len
        encodeShort( _msgLen );
        return _msgLen;
    }

    @Override
    public final void encodePrice( final double price ) {

        int value = NumberUtils.priceToExternalInt6DP( price );

        encodeInt( value );
    }

    @Override
    public void encodeTimeLocal( long internalTime ) {
        _idx = _tzCalc.internalTimeToLocalHHMMSS( _buffer, _idx, internalTime );
    }

    @Override
    public void encodeTimeUTC( long internalTime ) {
        _idx = _tzCalc.internalTimeToHHMMSS( _buffer, _idx, internalTime );
    }

    @Override
    public void encodeTimestampUTC( long internalTime ) {
        final long msUTC    = _tzCalc.internalTimeToUnixTime( internalTime );
        final int  unixTime = (int) (msUTC / 1000);
        encodeInt( unixTime );
    }

    @Override
    public void encodeTimestampLocal( long internalTime ) {
        final long msUTC    = _tzCalc.internalTimeToLocalMSFromStartDay( internalTime );
        final int  unixTime = (int) (msUTC / 1000);
        encodeInt( unixTime );
    }
}
