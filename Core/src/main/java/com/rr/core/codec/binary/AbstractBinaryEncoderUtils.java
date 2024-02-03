/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary;

import com.rr.core.codec.RuntimeEncodingException;
import com.rr.core.lang.Constants;
import com.rr.core.lang.TimeUtils;
import com.rr.core.lang.TimeUtilsFactory;
import com.rr.core.lang.ZString;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.utils.NumberUtils;
import com.rr.core.utils.Utils;

/**
 * helper for binary protocols
 *
 * @NOTE DOESNT CHECK FOR BUFFER OVERRUN SO ENSURE BUF IS BIG ENOUGH
 * <p>
 * can throw RuntimeEncodingException
 */
public abstract class AbstractBinaryEncoderUtils implements BinaryEncodeBuilder {

    private static final int MAX_STRING = 1024;

    protected final byte[] _buffer;
    protected final int    _startOffset;
    protected       int    _idx;
    protected       int    _msgLen;

    protected TimeUtils _tzCalc = TimeUtilsFactory.createTimeUtils();

    public AbstractBinaryEncoderUtils( byte[] buffer, int offset ) {
        _buffer      = buffer;
        _idx         = offset;
        _startOffset = offset;
    }

    @Override
    public final void start() {
        _idx = _startOffset;
    }

    @Override
    public void start( final int msgType ) {
        _idx              = _startOffset;
        _buffer[ _idx++ ] = (byte) msgType;
    }

    @Override
    public final int getNextFreeIdx() {
        return _idx;
    }

    @Override
    public final int getOffset() {
        return _startOffset;
    }

    @Override
    public final int getLength() {
        return _msgLen;
    }

    @Override
    public int getCurLength() {
        return _idx - _startOffset;
    }

    @Override
    public int end() {
        _msgLen = _idx - _startOffset;
        return _msgLen;
    }

    @Override
    public void encodeChar( final byte code ) {
        if ( code == Constants.UNSET_CHAR ) {
            _buffer[ _idx++ ] = 0x00;
        } else {
            _buffer[ _idx++ ] = code;
        }
    }

    @Override
    public final void encodeString( final ZString str ) {

        if ( str == null ) {
            writeVarFieldLen( 0 );
            return;
        }

        int len = str.length();

        if ( len > MAX_STRING ) len = MAX_STRING;           // truncate

        writeVarFieldLen( len );

        if ( len == 0 ) return;

        byte[] value = str.getBytes();

        copy( value, str.getOffset(), len );
    }

    @Override
    public final void encodeString( final ZString str, int maxLen ) {

        if ( str == null ) {
            writeVarFieldLen( 0 );
            return;
        }

        int len = str.length();

        if ( len > maxLen && maxLen > 0 ) len = maxLen;               // truncate
        if ( len > MAX_STRING ) len = MAX_STRING;           // truncate

        writeVarFieldLen( len );

        if ( len == 0 ) return;

        byte[] value = str.getBytes();

        copy( value, str.getOffset(), len );
    }

    @Override
    public final void encodeString( final byte[] buf, final int offset, int len ) {
        if ( len > MAX_STRING ) len = MAX_STRING;           // truncate

        if ( buf == null || len == 0 ) {
            writeVarFieldLen( len );
            return;
        }

        writeVarFieldLen( len );
        copy( buf, offset, len );
    }

    @Override
    public final void encodeBytes( final byte[] buf ) {
        if ( buf == null ) {
            writeVarFieldLen( 0 );
            return;
        }

        writeVarFieldLen( buf.length );
        copy( buf );
    }

    @Override
    public void encodeDecimal( final double price ) {
        encodePrice( price );
    }

    @Override
    public void encodePrice( final double price ) { // FIXED at 6DP

        long value;

        if ( price >= 0 ) {
            if ( price == Double.MAX_VALUE ) {
                value = Long.MAX_VALUE;
            } else {
                value = (long) ((price + Constants.WEIGHT) * 1000000L);
            }
        } else {
            if ( Utils.isNull( price ) ) { // NULL
                value = 0;
            } else if ( price == -Double.MAX_VALUE ) {
                value = Constants.MIN_PRICE_AS_LONG;
            } else {
                value = (long) ((price - Constants.WEIGHT) * 1000000L);
            }
        }

        encodeLong( value );
    }

    @Override
    public void encodeQty( final int qty ) {
        encodeInt( qty );
    }

    @Override
    public void encodeByte( final byte code ) {
        if ( code == Constants.UNSET_BYTE ) {
            _buffer[ _idx++ ] = 0x00;
        } else {
            _buffer[ _idx++ ] = code;
        }
    }

    /**
     * encodes a fixed width string, string must be null padded and null terminated
     *
     * @param fixedDataSize
     */
    @Override
    public final void encodeZStringFixedWidth( final ZString value, final int fixedDataSize ) {

        int idx = fixedDataSize;
        if ( value != null ) {
            int len = value.length();

            if ( len >= fixedDataSize ) len = fixedDataSize - 1;           // truncate with space for null terminator

            if ( len > 0 ) {
                copy( value.getBytes(), value.getOffset(), len );
                idx -= len;
            }
        }

        // @NOTE
        while( idx-- > 0 ) {
            _buffer[ _idx++ ] = 0x00;
        }
    }

    /**
     * encodes a fixed width string, string must be null padded and null terminated
     *
     * @param fixedDataSize
     */
    @Override
    public final void encodeZStringFixedWidth( final byte[] value, final int offset, final int fixedDataSize ) {

        int idx = fixedDataSize;
        if ( value != null ) {
            int len = value.length;

            if ( len >= fixedDataSize ) len = fixedDataSize - 1;           // truncate with space for null terminator

            if ( len > 0 ) {
                copy( value, offset, len );
                idx -= len;
            }
        }

        // @NOTE
        while( idx-- > 0 ) {
            _buffer[ _idx++ ] = 0x00;
        }
    }

    /**
     * encodes a fixed width string, string must be null padded and null terminated
     *
     * @param str
     * @param fixedDataSize
     */
    @Override
    public final void encodeStringFixedWidth( final ZString str, final int fixedDataSize ) {

        int idx = fixedDataSize;

        if ( str != null ) {
            int len = str.length();

            if ( len > 0 ) {
                byte[] value = str.getBytes();

                copy( value, str.getOffset(), len );

                idx -= len;
            }
        }

        // @NOTE
        while( idx-- > 0 ) {
            _buffer[ _idx++ ] = 0x00;
        }
    }

    /**
     * encodes a fixed width string, string must be null padded but doesnt have to be null terminated
     *
     * @param fixedDataSize
     */
    @Override
    public final void encodeStringFixedWidth( final byte[] value, final int offset, final int fixedDataSize ) {

        int idx = fixedDataSize;
        if ( value != null ) {
            int len = value.length - offset;

            if ( len >= fixedDataSize ) len = fixedDataSize;

            if ( len > 0 ) {
                copy( value, offset, len );
                idx -= len;
            }
        }

        // @NOTE
        while( idx-- > 0 ) {
            _buffer[ _idx++ ] = 0x00;
        }
    }

    /**
     * encodes a fixed width string, string must be null padded and null terminated
     *
     * @param str
     * @param fixedDataSize
     */
    @Override
    public final void encodeData( final ZString str, final int fixedDataSize ) {

        int idx = fixedDataSize;

        if ( str != null ) {
            int len = str.length();

            if ( len > 0 ) {
                byte[] value = str.getBytes();

                copy( value, str.getOffset(), len );

                idx -= len;
            }
        }

        // @NOTE
        while( idx-- > 0 ) {
            _buffer[ _idx++ ] = 0x00;
        }
    }

    @Override public void encodeBase36Number( final long value, final int len ) {
        if ( len == 0 ) {
            return;
        }

        Base36Codec.encode( value, _buffer, _idx, len );

        _idx += len;
    }

    @Override
    public final void encodeStringAsLong( final ZString strLong ) {

        long val = NumberUtils.parseLong( strLong );

        encodeLong( val );
    }

    @Override
    public final void encodeStringAsInt( final ZString strInt ) {

        int val = 0;

        byte[] b = strInt.getBytes();

        int       idx    = strInt.getOffset();
        final int maxLen = idx + strInt.length();
        for ( ; idx < maxLen; ++idx ) {
            byte x = b[ idx ];

            if ( x >= '0' && x <= '9' ) {
                val = (val * 10) + (x - '0');
            } else if ( x == 0x00 || x == ' ' ) {
                break;
            } else {
                throw new RuntimeEncodingException( "Invalid number, idx=" + idx + ", byte=" + (int) x + ", numStr=" + strInt );
            }
        }

        encodeInt( val );
    }

    @Override
    public void encodeULong( final long code ) {
        encodeLong( code );
    }

    @Override
    public void encodeUInt( final int code ) {
        encodeInt( code );
    }

    @Override
    public void encodeUShort( final short code ) {
        encodeShort( code );
    }

    @Override
    public void encodeUByte( final byte code ) {
        encodeByte( code );
    }

    @Override public void encodeTimestampLocal( long internalTime ) {
        throw new RuntimeEncodingException( "AbstractBinaryEncoder.encodeTimestampLocal() not implemented for this encoder" );
    }

    @Override public void encodeDate( int yyyymmdd ) {
        throw new RuntimeEncodingException( "AbstractBinaryEncoder.encodeDate() not implemented for this encoder" );
    }

    @Override public void encodeTimestampUTC( long internalTime ) {
        throw new RuntimeEncodingException( "AbstractBinaryEncoder.encodeTimestampUTC() not implemented for this encoder" );
    }

    @Override public void encodeTimeUTC( long internalTime ) {
        throw new RuntimeEncodingException( "AbstractBinaryEncoder.encodeTimeUTC() not implemented for this encoder" );
    }

    @Override public void encodeTimeLocal( long internalTime ) { throw new RuntimeEncodingException( "AbstractBinaryEncoder.encodeTimeLocal() not implemented for this encoder" ); }

    @Override
    public void encodeBool( final boolean isOn ) {

        _buffer[ _idx++ ] = (isOn) ? (byte) '1' : (byte) '0';
    }

    @Override
    public final void clear() {
        _idx = 0;
    }

    @Override
    public final byte[] getBuffer() {
        return _buffer;
    }

    @Override
    public final int getCurrentIndex() {
        return _idx;
    }

    @Override
    public void setTimeUtils( TimeUtils calc ) {
        _tzCalc = calc;
    }

    @Override
    public final void encodeFiller( int size ) {
        while( size-- > 0 ) {
            _buffer[ _idx++ ] = 0x00;
        }
    }

    protected final void copy( final byte[] value, final int offset, final int len ) {

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

    // write two byte len
    private void writeVarFieldLen( final int len ) {
        _buffer[ _idx++ ] = (byte) (len >> 8);
        _buffer[ _idx++ ] = (byte) (len & 0xFF);
    }
}
