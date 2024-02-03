/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

import com.rr.core.lang.Constants;
import com.rr.core.lang.TimeUtils;
import com.rr.core.lang.TimeUtilsFactory;
import com.rr.core.lang.ZString;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Logger;
import com.rr.core.model.MultiByteLookup;
import com.rr.core.utils.NumberFormatUtils;
import com.rr.core.utils.Utils;

/**
 * helper for the FIX decode process
 * <p>
 * doesnt check for buffer overrun, so ensure buffer big enough !
 * <p>
 * can throw RuntimeEncodingException
 *
 * @author Richard Rose
 */
public abstract class BaseFixEncodeBuilderImpl implements FixEncodeBuilder {

    private static final int CHECKSUM_TAG_LEN = 7;
    protected final byte[] _buffer;
    protected final int    _startOffset;
    private final int _maxLen;
    protected int _idx;
    protected int _msgOffset;
    protected int _msgLen;
    private TimeUtils _tzCalculator = TimeUtilsFactory.createTimeUtils();
    private   int _maxIdx;

    public BaseFixEncodeBuilderImpl( byte[] buffer, int offset ) {
        _buffer      = buffer;
        _maxLen      = buffer.length;
        _idx         = offset;
        _startOffset = offset;
        _maxIdx      = (buffer.length > 1024) ? buffer.length - 100 : buffer.length;
    }

    @Override
    public final int getNextFreeIdx() {
        return _idx;
    }

    @Override
    public final int getOffset() {
        return _msgOffset;
    }

    @Override
    public final int getLength() {
        return _msgLen;
    }

    @Override
    public final void encodeString( final int tag, final ZString str ) {
        if ( str == null ) return;

        int len = str.length();

        if ( len == 0 ) return;

        byte[] value = str.getBytes();

        writeTag( tag );

        copy( value, str.getOffset(), len );

        writeFixDelimiter();
    }

    @Override
    public final void encodeString( final int tag, final byte[] buf, final int offset, final int len ) {
        if ( buf == null || len == 0 ) return;

        writeTag( tag );

        copy( buf, offset, len );

        writeFixDelimiter();
    }

    @Override
    public final void encodePrice( final int tag, final double price ) {

        if ( Utils.isNull( price ) ) { // NULL
            return;
        }

        long asLong = (long) price;

        if ( asLong == price ) {
            encodeLong( tag, asLong );

            return;
        }

        long absVal = Math.abs( asLong );

        boolean useLongForm = true;

        int  len;
        long value;

        if ( (absVal & Constants.PRICE_DP_THRESHOLD_MASK_8DP) != 0 ) { // encode in short form ie 2dp

            useLongForm = false;

            if ( price >= 0 ) {
                value = (long) ((price + Constants.WEIGHT) * Constants.PRICE_DP_S_LFACTOR);
                len   = NumberFormatUtils.getPosLongLen( value );

                if ( len < Constants.PRICE_DP_S + 1 ) {
                    len = Constants.PRICE_DP_S + 1;
                }
            } else {
                value = (long) ((price - Constants.WEIGHT) * Constants.PRICE_DP_S_LFACTOR);
                len   = NumberFormatUtils.getNegLongLen( value ) - 1;

                if ( len < Constants.PRICE_DP_S + 1 ) {
                    len = Constants.PRICE_DP_S + 1;
                }
                len++; // -ve
            }

        } else { // encode in long form upto 8dp

            if ( price >= 0 ) {
                value = (long) ((price + Constants.WEIGHT) * Constants.PRICE_DP_L_LFACTOR);
                len   = NumberFormatUtils.getPosLongLen( value );

                if ( len < Constants.PRICE_DP_L + 1 ) {
                    len = Constants.PRICE_DP_L + 1;
                }
            } else {
                value = (long) ((price - Constants.WEIGHT) * Constants.PRICE_DP_L_LFACTOR);
                len   = NumberFormatUtils.getNegLongLen( value ) - 1;

                if ( len < Constants.PRICE_DP_L + 1 ) {
                    len = Constants.PRICE_DP_L + 1;
                }
                len++; // -ve
            }
        }

        len++; // '.'
        writeTag( tag );
        if ( useLongForm ) {
            writePriceLong( value, len, Constants.PRICE_DP_L );
        } else {
            writePriceLong( value, len, Constants.PRICE_DP_S );
        }
        writeFixDelimiter();
    }

    @Override
    public final void encodeLong( final int tag, final long value ) {

        if ( value == 0 ) {
            encodeZero( tag );
            return;
        }

        int valLen;

        if ( value < 0 ) {
            valLen = NumberFormatUtils.getNegLongLen( value );

            if ( value == Constants.UNSET_LONG ) {
                return;
            }
        } else {
            valLen = NumberFormatUtils.getPosLongLen( value );
        }

        writeTag( tag );

        writeLong( value, valLen );

        writeFixDelimiter();
    }

    @Override
    public final void encodeInt( final int tag, final int value ) {

        if ( value == 0 ) {
            encodeZero( tag );
            return;
        }

        int valLen;

        if ( value < 0 ) {
            valLen = NumberFormatUtils.getNegIntLen( value );

            if ( value == Constants.UNSET_INT ) {
                return;
            }
        } else {
            valLen = NumberFormatUtils.getPosIntLen( value );
        }

        writeTag( tag );

        writeInt( value, valLen );

        writeFixDelimiter();
    }

    @Override
    public final void encodeByte( final int tag, final byte code ) {
        writeTag( tag );

        _buffer[ _idx++ ] = code;

        writeFixDelimiter();
    }

    @Override
    public final void encodeTwoByte( final int tag, final byte[] code ) {

        writeTag( tag );

        _buffer[ _idx++ ] = code[ 0 ];

        if ( code.length == 2 )
            _buffer[ _idx++ ] = code[ 1 ];

        writeFixDelimiter();
    }

    @Override
    public final void encodeBytes( final int tag, final byte[] buf ) {
        if ( buf == null || buf.length == 0 ) return;

        writeTag( tag );

        copy( buf );

        writeFixDelimiter();
    }

    @Override
    public final void encodeBool( final int tag, final boolean isOn ) {

        writeTag( tag );

        _buffer[ _idx++ ] = (isOn) ? (byte) 'Y' : (byte) 'N';

        writeFixDelimiter();

    }

    @Override
    public final void encodeUTCTimestamp( final int tag, final long internalTime ) {
        if ( Utils.isNull( internalTime ) ) return;

        writeTag( tag );

        _idx = _tzCalculator.internalTimeToFixStrMillis( _buffer, _idx, internalTime );

        writeFixDelimiter();
    }

    @Override
    public final void encodeDate( int tag, int yyyymmdd ) {
        if ( yyyymmdd == 0 ) {
            return;
        }
        encodeInt( tag, yyyymmdd );
    }

    @Override
    public abstract void encodeEnvelope();

    @Override
    public final void encodeAckStats( final long orderIn, final long orderOut, final long ackIn, final long ackOut ) {
        // convert nanos to micros

        int tag = Constants.FIX_TAG_ACK_STATS;

        long nanosNOSToMKt    = (orderOut - orderIn) >>> 10;
        long nanosInMkt       = (ackIn - orderOut) >>> 10;
        long nanosAckToClient = (ackOut - ackIn) >>> 10;

        int nosLen = (nanosNOSToMKt < 0) ? NumberFormatUtils.getNegLongLen( nanosNOSToMKt )
                                         : NumberFormatUtils.getPosLongLen( nanosNOSToMKt );

        int mktLen = (nanosInMkt < 0) ? NumberFormatUtils.getNegLongLen( nanosInMkt )
                                      : NumberFormatUtils.getPosLongLen( nanosInMkt );

        int ackLen = (nanosAckToClient < 0) ? NumberFormatUtils.getNegLongLen( nanosAckToClient )
                                            : NumberFormatUtils.getPosLongLen( nanosAckToClient );

        writeTag( tag );

        writeLong( nanosNOSToMKt, nosLen );
        _buffer[ _idx++ ] = ',';

        writeLong( nanosInMkt, mktLen );
        _buffer[ _idx++ ] = ',';

        writeLong( nanosAckToClient, ackLen );

        writeFixDelimiter();
    }

    public final void clear() {
        _idx = 0;
    }

    public final void encodeEmpty( final int tag ) {
        writeTag( tag );
        writeFixDelimiter();
    }

    public final void encodeMultiByte( final int tag, final MultiByteLookup code ) {
        if ( code == null ) return;

        byte[] value = code.getVal();

        writeTag( tag );

        copy( value );

        writeFixDelimiter();
    }

    public final void encodeString( final int tag, final String str ) {
        if ( str == null ) return;

        int len = str.length();

        if ( len == 0 ) return;

        byte[] value = str.getBytes();

        writeTag( tag );

        copy( value, 0, len );

        writeFixDelimiter();
    }

    public final void encodeZero( final int tag ) {
        writeTag( tag );
        _buffer[ _idx++ ] = '0';
        writeFixDelimiter();
    }

    public final byte[] getBuffer() {
        return _buffer;
    }

    public final int getCurrentIndex() {
        return _idx;
    }

    public int getStartOffset() { return _startOffset; }

    public final TimeUtils getTimeZoneCalculator() {
        return _tzCalculator;
    }

    public final void setTimeUtils( TimeUtils calc ) {
        _tzCalculator = calc;
    }

    protected final void encodeChecksum() {

        int val = 0;

        for ( int idx = _msgOffset; idx < _idx; ) {
            val += _buffer[ idx++ ];
        }

        val = val & 0xFF;

        final int len = CHECKSUM_TAG_LEN;
        if ( _idx + len > _maxLen )
            throw new RuntimeEncodingException( "Buffer overflow  max=" + _maxLen + ", idx=" + _idx );

        _buffer[ _idx++ ] = '1';
        _buffer[ _idx++ ] = '0';
        _buffer[ _idx++ ] = '=';

        int div10 = (val * 205) >> 11;

        byte leastSigDigit = (byte) ((val - (div10 * 10)) + '0');

        val = div10; // now at most 25

        final int t = NumberFormatUtils._dig100[ val ];

        _buffer[ _idx++ ] = (byte) (t >> 8);      // tens
        _buffer[ _idx++ ] = (byte) (t & 0xFF);    // units
        _buffer[ _idx++ ] = leastSigDigit;

        writeFixDelimiter();
    }

    protected final void writeFixDelimiter() {
        _buffer[ _idx++ ] = FixField.FIELD_DELIMITER;
    }

    protected final void writePosInt( int value, final int length ) {
        int q, r;
        int charPos = _idx + length;
        while( value > 65536 ) {
            q     = value / 100;
            r     = (value - (q * 100));
            value = q;
            final int t = NumberFormatUtils._dig100[ r ];

            _buffer[ --charPos ] = (byte) (t & 0xFF);   // units
            _buffer[ --charPos ] = (byte) (t >> 8);     // tens
        }

        do {
            q                    = value / 10;
            r                    = (value - (q * 10));
            _buffer[ --charPos ] = NumberFormatUtils._dig10[ r ];
            value                = q;
        } while( value != 0 );

        _idx += length;
    }

    private void copy( final byte[] value ) {

        final int len = value.length;

        if ( len < SizeConstants.MIN_MEMCPY_LENGTH ) {
            int i = 0;
            while( i < len ) {
                _buffer[ _idx++ ] = value[ i++ ];
            }
        } else {
            System.arraycopy( value, 0, _buffer, _idx, len );
            _idx += len;
        }
    }

    private void copy( final byte[] value, final int offset, final int len ) {

        if ( len < SizeConstants.MIN_MEMCPY_LENGTH ) {

            final int max = offset + len;
            int       i   = offset;

            while( i < max ) {
                _buffer[ _idx++ ] = value[ i++ ];
            }
        } else {
            System.arraycopy( value, offset, _buffer, _idx, len );
            _idx += len;
        }
    }

    private int getTagLength( final int tag ) {
        if ( tag < 0 ) throw new RuntimeEncodingException( "Negative tag=" + tag );

        int tagLen = NumberFormatUtils.getPosIntLen( tag );

        return tagLen;
    }

    /**
     * COPY OF NumberFormatUtils routines so dont need to pass buffer, offset and index to routines
     */

    private void writeInt( int value, final int length ) {
        int  q, r;
        int  charPos = _idx + length;
        byte sign    = 0;
        if ( value < 0 ) {
            sign  = '-';
            value = -value;
        }
        while( value > 65536 ) {
            q     = value / 100;
            r     = (value - (q * 100));
            value = q;
            final int t = NumberFormatUtils._dig100[ r ];

            _buffer[ --charPos ] = (byte) (t & 0xFF);   // units
            _buffer[ --charPos ] = (byte) (t >> 8);     // tens
        }

        do {
            q                    = value / 10;
            r                    = (value - (q * 10));
            _buffer[ --charPos ] = NumberFormatUtils._dig10[ r ];
            value                = q;
        } while( value != 0 );

        if ( sign != 0 ) {
            _buffer[ --charPos ] = sign;
        }
        _idx += length;
    }

    private void writeLong( long value, final int length ) {
        long q;
        int  r;
        int  charPos = _idx + length;
        byte sign    = 0;

        if ( value < 0 ) {
            sign  = '-';
            value = -value;
        }

        while( value > 65536 ) {
            q     = value / 100;
            r     = (int) (value - (q * 100));
            value = q;
            final int t = NumberFormatUtils._dig100[ r ];

            _buffer[ --charPos ] = (byte) (t & 0xFF);   // units
            _buffer[ --charPos ] = (byte) (t >> 8);     // tens
        }

        do {
            q                    = value / 10;
            r                    = (int) (value - (q * 10));
            _buffer[ --charPos ] = NumberFormatUtils._dig10[ r ];
            value                = q;
        } while( value != 0 );

        if ( sign != 0 ) {
            _buffer[ --charPos ] = sign;
        }
        _idx += length;
    }

    private void writePriceLong( long value, final int length, int numDP ) {
        long q;
        int  r;
        int  charPos  = _idx + length;
        int  startPos = 0;
        byte sign     = 0;

        if ( value < 0 ) {
            sign  = '-';
            value = -value;
        }

        // input value will be at least 6 digits long due to 1M multiplier
        boolean startedEncoding = false;

        int pairs = numDP >> 1;

        for ( int pairIdx = 0; pairIdx < pairs; pairIdx++ ) {

            q = value / 100;
            r = (int) (value - ((q * 100)));

            final int t     = NumberFormatUtils._dig100[ r ];
            final int units = (t & 0xFF);

            value = q;
            if ( startedEncoding ) {
                _buffer[ --charPos ] = (byte) units;   // units
                _buffer[ --charPos ] = (byte) (t >> 8);     // tens
            } else {
                if ( r == 0 ) {
                    charPos -= 2;  // avoid zero padding to RHS of least sig digit
                } else {
                    startedEncoding = true;
                    if ( units == 0x30 ) {
                        charPos--;
                        startPos = charPos;
                    } else {
                        startPos             = charPos;
                        _buffer[ --charPos ] = (byte) units;
                    }
                    _buffer[ --charPos ] = (byte) (t >> 8); // tens
                }
            }
        }

        if ( !startedEncoding ) {
            startPos           = charPos + 1;
            _buffer[ charPos ] = '0';
        }
        _buffer[ --charPos ] = '.';

        // Do the integer part now
        if ( value == 0 ) {
            _buffer[ --charPos ] = '0';
        } else {
            while( value > 65536 ) {
                q     = value / 100;
                r     = (int) (value - (q * 100));
                value = q;
                final int t = NumberFormatUtils._dig100[ r ];

                _buffer[ --charPos ] = (byte) (t & 0xFF);   // units
                _buffer[ --charPos ] = (byte) (t >> 8);     // tens
            }

            do {
                q                    = value / 10;
                r                    = (int) (value - (q * 10));
                _buffer[ --charPos ] = NumberFormatUtils._dig10[ r ];
                value                = q;
            } while( value != 0 );
        }
        if ( sign != 0 ) {
            _buffer[ --charPos ] = sign;
        }
        _idx = startPos;
    }

    private void writeTag( final int tag ) {
        if ( _idx > _maxIdx ) throw new RuntimeEncodingException( "Unable to encode message as buffer not big enough, index at " + _idx );
        final int tagLen = getTagLength( tag );
        writePosInt( tag, tagLen );
        _buffer[ _idx++ ] = '=';
    }
}
