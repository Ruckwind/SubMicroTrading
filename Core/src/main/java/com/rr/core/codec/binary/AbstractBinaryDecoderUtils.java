/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary;

import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TimeUtils;
import com.rr.core.lang.TimeUtilsFactory;
import com.rr.core.lang.stats.SizeConstants;

/**
 * helper for binary decoder protocols
 * <p>
 * can throw RuntimeEncodingException
 */
public abstract class AbstractBinaryDecoderUtils implements BinaryDecodeBuilder {

    protected byte[]    _buffer;
    protected int       _startOffset;
    protected int       _idx;
    protected int       _msgLen;
    protected int       _maxIdx;
    protected TimeUtils _tzCalc = TimeUtilsFactory.createTimeUtils();

    public AbstractBinaryDecoderUtils() {
        // nothing
    }

    @Override public final void clear() {
        _idx = 0;
    }

    @Override public long decodeBase36Number( int len ) {
        if ( len == 0 ) {
            return Constants.UNSET_LONG;
        }

        rangeCheck( len );

        final long val = Base36Codec.decode( _buffer, _idx, len );

        _idx += len;

        return val;
    }

    @Override public boolean decodeBool() {
        return _buffer[ _idx++ ] == '1';
    }

    @Override public byte decodeByte() {
        return _buffer[ _idx++ ];
    }

    @Override public byte decodeChar() {
        return decodeByte();
    }

    /**
     * decodes a fixed width string, string must be null padded and null terminated
     */
    @Override public final void decodeData( final ReusableString dest, final int len ) {

        if ( len == 0 ) {
            dest.reset();
            return;
        }

        rangeCheck( len );

        dest.ensureCapacity( len );

        copy( dest.getBytes(), 0, len ); // increases _idx

        dest.setLength( len );
    }

    @Override public int decodeDate() {
        throw new RuntimeDecodingException( "AbstractBinaryDecoder.decodeDate() not implemented for this decoder" );
    }

    @Override public double decodeDecimal() {
        return decodePrice();
    }

    @Override public abstract int decodeInt();

    @Override public final void decodeIntToString( final ReusableString dest ) {
        int val = decodeInt();
        dest.append( val );
    }

    @Override public abstract long decodeLong();

    @Override
    public final void decodeLongToString( final ReusableString dest ) {
        long val = decodeLong();
        dest.append( val );
    }

    @Override public double decodePrice() {

        final long value = decodeLong();

        double price;

        if ( value >= 0 ) {
            if ( value == Long.MAX_VALUE ) {
                price = Double.MAX_VALUE;
            } else {
                price = value / 1000000D; // 6dp fixed
            }
        } else {
            if ( value == Long.MIN_VALUE ) { // NULL
                price = Constants.UNSET_DOUBLE;
            } else if ( value == Constants.MIN_PRICE_AS_LONG ) {
                price = -Double.MAX_VALUE;
            } else {
                price = value / 1000000D; // 6dp fixed
            }
        }

        return price;
    }

    @Override public double decodePrice( int wholeDigits, int decimalDigits ) { throw new RuntimeDecodingException( "AbstractBinaryDecoder.decodePrice() not implemented for this decoder" ); }

    @Override public int decodeQty() {
        return decodeInt();
    }

    @Override public int decodeQty( int len )                                 { throw new RuntimeDecodingException( "AbstractBinaryDecoder.decodeQty() not implemented for this decoder" ); }

    @Override public abstract short decodeShort();

    @Override public void decodeString( final ReusableString dest ) {

        final int len = readVarFieldLen();

        rangeCheck( len );

        if ( len > 0 ) {
            dest.ensureCapacity( len );

            copy( dest.getBytes(), 0, len );

            dest.setLength( len );
        } else {
            dest.reset();
        }
    }

    /**
     * encodes a fixed width string, string must be null padded and null terminated
     */
    @Override public final void decodeStringFixedWidth( final ReusableString dest, final int len ) {

        if ( len == 0 ) {
            dest.reset();
            return;
        }

        rangeCheck( len );

        int bufIdx  = _idx + len;
        int dataLen = len;

        do {
            final byte b = _buffer[ --bufIdx ];

            if ( b != 0x00 ) {
                break; // EXCLUDE padded nulls
            }

        } while( --dataLen > 0 );

        dest.ensureCapacity( dataLen );

        copy( dest.getBytes(), 0, dataLen ); // increases _idx

        dest.setLength( dataLen );

        _idx += (len - dataLen);
    }

    @Override public long decodeTimeLocal() {
        throw new RuntimeDecodingException( "AbstractBinaryDecoder.decodeTimeLocal() not implemented for this decoder" );
    }

    @Override public long decodeTimeUTC() {
        throw new RuntimeDecodingException( "AbstractBinaryDecoder.decodeTimeUTC() not implemented for this decoder" );
    }

    @Override public long decodeTimestampLocal() {
        throw new RuntimeDecodingException( "AbstractBinaryDecoder.decodeTimestampLocal() not implemented for this decoder" );
    }

    @Override public long decodeTimestampUTC() {
        throw new RuntimeDecodingException( "AbstractBinaryDecoder.decodeTimestampUTC() not implemented for this decoder" );
    }

    @Override public byte decodeUByte() {
        return decodeByte();
    }

    @Override public int decodeUInt() {
        return decodeInt();
    }

    @Override public long decodeULong() {
        return decodeLong();
    }

    @Override public short decodeUShort() {
        return decodeShort();
    }

    @Override public final void decodeZStringFixedWidth( final ReusableString dest, final int len ) {
        rangeCheck( len );

        if ( len > 20 ) {
            // larger field worth the scan to find field length

            int bufIdx  = _idx;
            int dataLen = 0;

            do {
                final byte b = _buffer[ bufIdx++ ];

                if ( b == 0x00 ) {
                    break; // EXCLUDE padded nulls
                }
            } while( ++dataLen < len );

            dest.ensureCapacity( dataLen );

            copy( dest.getBytes(), 0, dataLen ); // increases _idx

            dest.setLength( dataLen );

            _idx += (len - dataLen);

        } else {
            // this faster decode assumes events sized correctly .. if not unwanted growing will occur

            int    bufIdx     = _idx;
            int    idx        = 0;
            int    destIdx    = 0;
            byte[] destBytes  = dest.getBytes();
            int    maxDestIdx = destBytes.length;

            while( idx++ < len ) {
                final byte b = _buffer[ bufIdx++ ];

                if ( b == 0x00 ) {
                    break; // EXCLUDE padded nulls
                }

                if ( destIdx >= maxDestIdx ) {
                    dest.ensureCapacity( len );
                    destBytes  = dest.getBytes();
                    maxDestIdx = destBytes.length;
                }

                destBytes[ destIdx++ ] = b;
            }

            dest.setLength( destIdx );

            _idx += len;
        }
    }

    @Override public void end() {
        _msgLen = _idx - _startOffset;
    }

    @Override public final byte[] getBuffer() {
        return _buffer;
    }

    @Override public final int getCurrentIndex() {
        return _idx;
    }

    @Override public final int getLength() {
        return _msgLen;
    }

    @Override public final int getMaxIdx() {
        return _maxIdx;
    }

    @Override public final void setMaxIdx( final int maxIdx ) {
        _maxIdx = maxIdx;
    }

    @Override public final int getNextFreeIdx() {
        return _idx;
    }

    @Override public final int getOffset() {
        return _startOffset;
    }

    @Override public void setTimeUtils( TimeUtils calc ) {
        _tzCalc = calc;
    }

    @Override public final void skip( int size ) {
        _idx += size;
        if ( _idx > _maxIdx ) {
            throw new RuntimeDecodingException( "BinaryDecoderUtils.skip cant skip " + size + " bytes from idx " +
                                                (_idx - size) + ", as maxIdx is " + _maxIdx );
        }
    }

    @Override public void start( final byte[] msg, final int offset, final int maxIdx ) {
        _buffer      = msg;
        _startOffset = offset;
        _idx         = _startOffset;
        _maxIdx      = maxIdx;
    }

    protected final void copy( final byte[] dest, final int offset, final int len ) {

        if ( len < SizeConstants.MIN_MEMCPY_LENGTH ) {

            final int max = offset + len;
            int       i   = offset;

            while( i < max ) {
                dest[ i++ ] = _buffer[ _idx++ ];
            }
        } else {
            System.arraycopy( _buffer, _idx, dest, offset, len );
            _idx += len;
        }
    }

    protected final void rangeCheck( int len ) {
        if ( len + _idx - 1 > _maxIdx ) {
            throw new RuntimeDecodingException( "AbstractBinaryDecoderUtils.rangeCheck cant read " + len + " bytes from idx " +
                                                (_idx - _startOffset) + ", as max is " + (_maxIdx - _startOffset) );
        }
    }

    // write two byte len
    private int readVarFieldLen() {

        int len = 0xFF & _buffer[ _idx++ ];
        len = (len << 8) + (0xFF & _buffer[ _idx++ ]);

        return len;
    }
}
