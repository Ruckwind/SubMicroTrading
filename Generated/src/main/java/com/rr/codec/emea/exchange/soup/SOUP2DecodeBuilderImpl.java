/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.codec.emea.exchange.soup;

import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.codec.binary.Base36Codec;
import com.rr.core.codec.binary.BinaryDecodeBuilder;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TimeUtils;
import com.rr.core.lang.TimeUtilsFactory;

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
public final class SOUP2DecodeBuilderImpl implements BinaryDecodeBuilder {

    public static final double KEEP_DECIMAL_PLACE_FACTOR = 100000000D;

    private static final int MAX_DP = 8;
    private static int[] _dpShift = makeDecimalPlacesShift();
    private byte[]    _buffer;
    private int       _startOffset;
    private int       _idx;
    private int       _msgLen;
    private int       _maxIdx;
    private TimeUtils _tzCalc = TimeUtilsFactory.createTimeUtils();

    private static int[] makeDecimalPlacesShift() {
        int[] shift = new int[ MAX_DP + 1 ];

        int shiftDP = 1;
        for ( int i = 0; i <= MAX_DP; i++ ) {
            shift[ i ] = shiftDP;
                         shiftDP *= 10;
        }

        return shift;
    }

    public SOUP2DecodeBuilderImpl() {
        super();
        _tzCalc.setLocalTimezone( TimeZone.getTimeZone( "GMT" ) );
    }

    @Override public void clear()                             { _idx = 0; }

    @Override public long decodeBase36Number( int len ) {
        if ( len == 0 ) {
            return Constants.UNSET_LONG;
        }

        rangeCheck( len );

        final long val = Base36Codec.decode( _buffer, _idx, len );

        _idx += len;

        return val;
    }

    @Override public boolean decodeBool()                                        { throw new RuntimeDecodingException( "TCPPitchDecodeBuilderImpl.decodeBool not supported" ); }

    @Override public byte decodeByte()                        { return _buffer[ _idx++ ]; }

    @Override public byte decodeChar()                        { return decodeByte(); }

    @Override public void decodeData( final ReusableString dest, final int len ) { throw new RuntimeDecodingException( "TCPPitchDecodeBuilderImpl.decodeData not supported" ); }

    @Override public int decodeDate()                                            { throw new RuntimeDecodingException( "TCPPitchDecodeBuilderImpl.decodeDate not supported" ); }

    @Override public double decodeDecimal()                                      { throw new RuntimeDecodingException( "TCPPitchDecodeBuilderImpl.decodeDecimal not supported" ); }

    @Override public int decodeInt()                                             { throw new RuntimeDecodingException( "TCPPitchDecodeBuilderImpl.decodeInt not supported" ); }

    @Override public void decodeIntToString( final ReusableString dest )         { throw new RuntimeDecodingException( "TCPPitchDecodeBuilderImpl.decodeIntToString not supported" ); }

    @Override public long decodeLong()                                           { throw new RuntimeDecodingException( "TCPPitchDecodeBuilderImpl.decodeLong not supported" ); }

    @Override public void decodeLongToString( final ReusableString dest )        { throw new RuntimeDecodingException( "TCPPitchDecodeBuilderImpl.decodeLongToString not supported" ); }

    @Override public double decodePrice()                                        { throw new RuntimeDecodingException( "TCPPitchDecodeBuilderImpl.decodePrice not supported" ); }

    @Override public double decodePrice( int wholeDigits, int decimalPlaces ) {
        final long p1 = decodeLong( wholeDigits );
        // no delimiter
        final double p2      = decodeQty( decimalPlaces );
        final int    dpShift = _dpShift[ decimalPlaces ];

        double v = p1 + (p2 / dpShift);

        return v;
    }

    @Override public int decodeQty()                                             { throw new RuntimeDecodingException( "TCPPitchDecodeBuilderImpl.decodeQty not supported" ); }

    @Override public int decodeQty( int len ) {
        rangeCheck( len );

        int num = 0;

        byte digit = 0;

        int maxIdx = _idx + len;

        while( _idx < maxIdx ) {
            digit = _buffer[ _idx++ ];

            if ( digit >= '0' && digit <= '9' ) {

                num *= 10;
                num += (digit - '0');
            } else {
                throwDecodeException( "Number has non numeric chars" );
            }

            if ( num < 0 ) throwDecodeException( "Number is too big" );
        }

        return num;
    }

    @Override public short decodeShort()                                         { throw new RuntimeDecodingException( "TCPPitchDecodeBuilderImpl.decodeShort not supported" ); }

    @Override public void decodeString( final ReusableString dest )              { throw new RuntimeDecodingException( "TCPPitchDecodeBuilderImpl.decodeString not supported" ); }

    @Override public void decodeStringFixedWidth( final ReusableString dest, final int len ) {
        decodeZStringFixedWidth( dest, len );
    }

    @Override public long decodeTimeLocal()                   { return decodeQty( 8 ); }

    @Override public long decodeTimeUTC()                                        { throw new RuntimeDecodingException( "TCPPitchDecodeBuilderImpl.decodeTimeUTC not supported" ); }

    @Override public long decodeTimestampLocal()              { return decodeQty( 11 ); }

    @Override public long decodeTimestampUTC()                                   { throw new RuntimeDecodingException( "TCPPitchDecodeBuilderImpl.decodeTimestampUTC not supported" ); }

    @Override public byte decodeUByte()                       { return _buffer[ _idx++ ]; }

    @Override public int decodeUInt()                                            { throw new RuntimeDecodingException( "TCPPitchDecodeBuilderImpl.decodeUInt not supported" ); }

    @Override public long decodeULong()                                          { throw new RuntimeDecodingException( "TCPPitchDecodeBuilderImpl.decodeULongnot supported" ); }

    @Override public short decodeUShort()                                        { throw new RuntimeDecodingException( "TCPPitchDecodeBuilderImpl.decodeUShort not supported" ); }

    @Override public void decodeZStringFixedWidth( final ReusableString dest, final int len ) {
        rangeCheck( len );

        final int startIdx = _idx;
        int       lastIdx  = _idx + len - 1;

        // ignore trailing spaces
        while( lastIdx >= startIdx && _buffer[ lastIdx ] == ' ' ) {
            --lastIdx;
        }

        int bufIdx = 0;

        dest.ensureCapacity( len );

        byte[] destBytes  = dest.getBytes();
        int    maxDestIdx = destBytes.length;
        int    destIdx    = 0;

        for ( int idx = startIdx; idx <= lastIdx; idx++ ) {
            final byte b = _buffer[ idx ];
            destBytes[ destIdx++ ] = b;
        }

        dest.setLength( destIdx );

        _idx += len;
    }

    @Override public void end()                               { _msgLen = _idx - _startOffset; }

    @Override public byte[] getBuffer()                       { return _buffer; }

    @Override public int getCurrentIndex()                    { return _idx; }

    @Override public final int getLength()                    { return _msgLen; }

    @Override public final int getMaxIdx()                    { return _maxIdx; }

    @Override public final void setMaxIdx( final int maxIdx ) { _maxIdx = maxIdx; }

    @Override public final int getNextFreeIdx()               { return _idx; }

    @Override public final int getOffset()                    { return _startOffset; }

    @Override public void setTimeUtils( TimeUtils calc )      { _tzCalc = calc; }

    @Override public void skip( final int size )              { _idx += size; }

    @Override public void start( final byte[] msg, final int offset, final int maxIdx ) {
        _buffer      = msg;
        _startOffset = offset;
        _idx         = _startOffset;
        _maxIdx      = maxIdx;
    }

    private long decodeLong( int len ) {
        rangeCheck( len );

        long lNum = 0;

        byte digit = 0;

        int maxIdx = _idx + len;

        while( _idx < maxIdx ) {
            digit = _buffer[ _idx++ ];

            if ( digit >= '0' && digit <= '9' ) {

                lNum *= 10;
                lNum += (digit - '0');
            } else {
                throwDecodeException( "Number has non numeric chars" );
            }

            if ( lNum < 0 ) throwDecodeException( "Number is too big" );
        }

        return lNum;
    }

    private final void rangeCheck( int len ) {
        if ( len + _idx - 1 > _maxIdx ) {
            throw new RuntimeDecodingException( "TCPPitchDecodeBuilderImpl.rangeCheck cant read " + len + " bytes from idx " +
                                                (_idx - _startOffset) + ", as max is " + (_maxIdx - _startOffset) );
        }
    }

    private void throwDecodeException( final String msg )                        { throw new RuntimeDecodingException( "TCPPitchDecodeBuilderImpl.throwDecodeException " + msg + " at idx=" + _idx ); }
}
