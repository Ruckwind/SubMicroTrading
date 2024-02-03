/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.model.Identifiable;
import com.rr.core.utils.DoubleStringUtils;
import com.rr.core.utils.NumberFormatUtils;
import com.rr.core.utils.Utils;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;

/**
 * mutateable string : combination of java String and StringBuilder but
 * without tempobj creation
 * <p>
 * this specialisation does OWN its buffer and can grow it as needed
 * <p>
 * offset is always zero to avoid extra addition on each ensureCapacity call
 *
 * @author Richard Rose
 */
public final class ReusableString extends ViewString implements Reusable<ReusableString> {

    public static final boolean DEFAULT_DBL_FAST_MODE = true;

    private static final String NULL_STR = "null";

    private static final int DEFAULT_LEN = Constants.DEFAULT_STRING_LENGTH;

    private static final ReusableType _reusableType = CoreReusableType.ReusableString;

    private static boolean _fastMode = DEFAULT_DBL_FAST_MODE;
    private ReusableString _next = null;

    public static boolean isFastMode()                        { return _fastMode; }

    public static void setFastMode( final boolean _fastMode ) { ReusableString._fastMode = _fastMode; }

    public ReusableString() {
        this( DEFAULT_LEN );
    }

    public ReusableString( final ViewString from ) {
        this( from._bytes, from._offset, from._len );
    }

    public ReusableString( final byte[] bytes, final int from, final int len ) {
        super( new byte[ len ], 0, len );

        if ( len < SizeConstants.MIN_MEMCPY_LENGTH ) {
            int srcIdx  = from;
            int destIdx = 0;
            while( destIdx < len ) {
                _bytes[ destIdx++ ] = bytes[ srcIdx++ ];
            }
        } else {
            System.arraycopy( bytes, from, _bytes, 0, _len );
        }
    }

    public ReusableString( final int initialCapacity ) {
        super( new byte[ initialCapacity ], 0, 0 );
    }

    public ReusableString( final String s ) {
        super( (s == null) ? new byte[ 0 ] : s.getBytes(), 0, (s == null) ? 0 : s.length() );
    }

    public ReusableString( final byte[] val ) {
        this( val, 0, val.length );
    }

    public ReusableString( ZString s ) {
        this( s.getBytes(), s.getOffset(), s.length() );
    }

    @Override
    public final ReusableString getNext() {
        return _next;
    }

    @Override
    public final void setNext( final ReusableString nxt ) {
        _next = nxt;
    }

    @Override
    public final ReusableType getReusableType() {
        return _reusableType;
    }

    @Override
    public final void setValue( final byte[] buf ) {
        setValue( buf, 0, buf.length );
    }

    @Override
    public final void setValue( final byte[] bytes, final int from, final int len ) {
        ensureCapacity( len );

        if ( len < SizeConstants.MIN_MEMCPY_LENGTH ) {
            int srcIdx  = from;
            int destIdx = 0;
            while( destIdx < len ) {
                _bytes[ destIdx++ ] = bytes[ srcIdx++ ];
            }
        } else {
            System.arraycopy( bytes, from, _bytes, 0, len );
        }

        _len = len;
    }

    @Override
    public final void setValue( final ZString from ) {
        reset();
        if ( from == null )
            return;
        final int fromLen = from.length();
        ensureCapacity( fromLen );
        from.getBytes( _bytes, 0 );
        _len = fromLen;
    }

    @Override
    public final int getOffset() {
        return 0;
    }

    @Override
    public final void ensureCapacity( final int minimumCapacity ) {
        _hash = 0;
        final int curLen = _bytes.length;
        if ( minimumCapacity > curLen ) {
            int newCapacity = curLen + (curLen >> 1);
            if ( newCapacity < 0 || minimumCapacity > newCapacity ) {
                newCapacity = minimumCapacity;
            }
            final byte newValue[] = new byte[ newCapacity ];
            final int  len        = _len;
            if ( len > 0 ) {
                if ( curLen < SizeConstants.MIN_MEMCPY_LENGTH ) {
                    int srcIdx  = 0;
                    int destIdx = 0;
                    while( destIdx < len ) {
                        newValue[ destIdx++ ] = _bytes[ srcIdx++ ];
                    }
                } else {
                    System.arraycopy( _bytes, 0, newValue, 0, len );
                }
            }
            _bytes = newValue;
        }
    }

    @Override
    public final void reset() {
        _len  = 0;
        _hash = 0;
    }

    @Override
    /**
     * length = dataLen and EXCLUDES offset
     *
     * can be used for truncation
     */
    public final void setLength( final int len ) {
        ensureCapacity( len );
        _len = len;
    }

    /**
     * append a ViewString, DONT append null if from is null
     *
     * @param from
     * @return
     */
    public final ReusableString append( final ViewString from ) {
        if ( from != null ) {
            return append( from._bytes, from._offset, from._len );
        }
        return this;
    }

    public final ReusableString append( final ZString from ) {
        if ( from != null ) {
            return append( from.getBytes(), from.getOffset(), from.length() );
        }
        return this;
    }

    public final ReusableString append( final String s ) {
        if ( s != null ) {
            final int sLen        = s.length();
            final int newCapacity = _len + sLen;
            ensureCapacity( newCapacity );

            int srcIdx = 0;
            while( srcIdx < sLen ) {
                _bytes[ _len++ ] = (byte) s.charAt( srcIdx++ );
            }
        }
        return this;
    }

    public final ReusableString append( final byte b ) {
        final int newCapacity = _len + 1;
        ensureCapacity( newCapacity );
        _bytes[ _len ] = b;
                         _len = newCapacity;
        return this;
    }

    public final ReusableString append( final byte[] bytes ) {
        final int len         = bytes.length;
        final int newCapacity = _len + len;
        ensureCapacity( newCapacity );

        if ( len < SizeConstants.MIN_MEMCPY_LENGTH ) {
            int srcIdx  = 0;
            int destIdx = _len;
            while( destIdx < newCapacity ) {
                _bytes[ destIdx++ ] = bytes[ srcIdx++ ];
            }
        } else {
            System.arraycopy( bytes, 0, _bytes, _len, len );
        }

        _len = newCapacity;

        return this;
    }

    public final ReusableString append( final byte[] bytes, final int from, final int len ) {
        final int newCapacity = _len + len;
        ensureCapacity( newCapacity );

        if ( len < SizeConstants.MIN_MEMCPY_LENGTH ) {
            int srcIdx  = from;
            int destIdx = _len;
            while( destIdx < newCapacity ) {
                _bytes[ destIdx++ ] = bytes[ srcIdx++ ];
            }
        } else {
            System.arraycopy( bytes, from, _bytes, _len, len );
        }

        _len = newCapacity;

        return this;
    }

    public final ReusableString append( final char c ) {
        final int newCapacity = _len + 1;
        ensureCapacity( newCapacity );
        _bytes[ _len ] = (byte) c;
        _len           = newCapacity;
        return this;
    }

    public final ReusableString append( double doubleValue ) { return appendZ( doubleValue, false ); }

    public final ReusableString append( double doubleValue, int fixedDP ) { return appendZ( doubleValue, fixedDP, false ); }

    public final ReusableString append( double doubleValue, boolean cashFmt ) { return appendZ( doubleValue, cashFmt, false ); }

    public final ReusableString append( double doubleValue, DecimalFormat format ) {
        String val = format.format( doubleValue );
        return append( val );
    }

    public final ReusableString append( final int num ) { return appendZ( num, false ); }

    /**
     * append a positive integer as fixed width
     *
     * @param num
     * @param fixedSize
     * @return
     * @NOTE strictly speaking should NOT assume number is +ve ... although that is guarenteed under  current usage
     */
    public final ReusableString append( final int num, final int fixedSize ) {
        final int newCapacity = _len + fixedSize;
        ensureCapacity( newCapacity );

        NumberFormatUtils.addPositiveIntFixedLength( _bytes, _len, num, fixedSize );

        _len = newCapacity;

        return this;
    }

    /**
     * append a positive long as fixed width
     *
     * @param num
     * @param fixedSize
     * @return
     * @NOTE strictly speaking should NOT assume number is +ve ... although that is guarenteed under  current usage
     */
    public final ReusableString append( final long num, final int fixedSize ) {
        final int newCapacity = _len + fixedSize;
        ensureCapacity( newCapacity );

        NumberFormatUtils.addPositiveLongFixedLength( _bytes, _len, num, fixedSize );

        _len = newCapacity;

        return this;
    }

    public final ReusableString append( final long num ) { return appendZ( num, false ); }

    public final ReusableString append( final Enum<?> val ) {

        if ( val != null ) {
            append( val.toString() );
        }

        return this;
    }

    public final ReusableString append( final Identifiable val ) {

        if ( val != null ) {
            append( val.id() );
        }

        return this;
    }

    public final ReusableString append( final boolean val ) {

        append( (val) ? 'Y' : 'N' );

        return this;
    }

    public final ReusableString append( ByteBuffer buf ) {
        final int limit  = buf.limit();
        final int offset = buf.position();

        if ( offset >= limit ) return this;

        final int sLen        = limit - offset;
        final int newCapacity = _len + sLen;
        ensureCapacity( newCapacity );

        buf.get( _bytes, _len, sLen );

        _len = newCapacity;

        return this;
    }

    public ReusableString append( final ReusableString from, final int substringIdx ) {
        final int    len         = from.length() - substringIdx;
        final int    newCapacity = _len + len;
        final byte[] srcBytes    = from.getBytes();

        ensureCapacity( newCapacity );

        if ( len < SizeConstants.MIN_MEMCPY_LENGTH ) {
            int srcIdx  = substringIdx;
            int destIdx = _len;
            while( destIdx < newCapacity ) {
                _bytes[ destIdx++ ] = srcBytes[ srcIdx++ ];
            }
        } else {
            System.arraycopy( srcBytes, substringIdx, _bytes, _len, len );
        }

        _len = newCapacity;

        return this;
    }

    public ReusableString append( final ReusableString src, int stIdx, int len ) {
        final int    newCapacity = _len + len;
        final byte[] srcBytes    = src.getBytes();

        ensureCapacity( newCapacity );

        if ( len < SizeConstants.MIN_MEMCPY_LENGTH ) {
            int srcIdx  = stIdx;
            int destIdx = _len;
            while( destIdx < newCapacity ) {
                _bytes[ destIdx++ ] = srcBytes[ srcIdx++ ];
            }
        } else {
            System.arraycopy( srcBytes, stIdx, _bytes, _len, len );
        }

        _len = newCapacity;

        return this;
    }

    public final ReusableString append( final Object[] arr ) {
        append( "[ " );

        for ( int i = 0; i < arr.length; i++ ) {
            Object o = arr[ i ];

            if ( i > 0 ) {
                append( ", " );
            }

            if ( o == null ) {
                append( "null" );
            } else {
                append( o.toString() );
            }
        }
        append( " ]" );

        return this;
    }

    public final ReusableString appendHEX( final ZString from ) {
        if ( from != null ) {
            appendReadableHEX( from.getBytes(), from.getOffset(), from.length() );
        }

        return this;
    }

    public final ReusableString appendHEX( final byte[] srcBytes, final int offset, final int len ) {
        final int newCapacity = _len + (len * 3);
        ensureCapacity( newCapacity );

        int  srcIdx  = offset;
        int  destIdx = _len;
        byte b;
        byte major;
        byte minor;

        while( destIdx < newCapacity ) {
            b = srcBytes[ srcIdx++ ];

            _bytes[ destIdx++ ] = ' ';

            major = (byte) ((0xFF & b) >> 4); // upper nybble
            minor = (byte) ((0x0F & b));      // lower nybble

            if ( major <= 9 ) major = (byte) (major + '0');
            else major = (byte) ((major + 'A') - 10);

            if ( minor <= 9 ) minor = (byte) (minor + '0');
            else minor = (byte) ((minor + 'A') - 10);

            _bytes[ destIdx++ ] = major;
            _bytes[ destIdx++ ] = minor;
        }

        _len = destIdx;

        return this;
    }

    public final ReusableString appendReadableHEX( final byte[] srcBytes, final int offset, final int len ) {
        final int newCapacity = _len + (len * 3);
        ensureCapacity( newCapacity );

        int  srcIdx  = offset;
        int  destIdx = _len;
        byte b;
        byte major;
        byte minor;

        while( destIdx < newCapacity ) {
            b = srcBytes[ srcIdx++ ];

            _bytes[ destIdx++ ] = ' ';

            if ( Character.isISOControl( b ) ) {
                major = (byte) ((0xFF & b) >> 4); // upper nybble
                minor = (byte) ((0x0F & b));      // lower nybble

                if ( major <= 9 ) major = (byte) (major + '0');
                else major = (byte) ((major + 'A') - 10);

                if ( minor <= 9 ) minor = (byte) (minor + '0');
                else minor = (byte) ((minor + 'A') - 10);

                _bytes[ destIdx++ ] = major;
                _bytes[ destIdx++ ] = minor;
            } else {
                _bytes[ destIdx++ ] = ' ';
                _bytes[ destIdx++ ] = b;
            }
        }

        _len = destIdx;

        return this;
    }

    /**
     * appends a double truncated at 7dp to the string
     * <p>
     * truncates decimal places for numbers bigger than 99,999,999,999
     *
     * @param doubleValue
     * @return
     */
    public final ReusableString appendZ( double doubleValue, boolean showNull ) {

        long value = (long) doubleValue;

        if ( Utils.isNull( doubleValue ) ) return appendNull( showNull );
        if ( value == Constants.UNSET_LONG ) return appendNull( showNull );

        if ( _fastMode ) {

            if ( (Math.abs( value ) & Constants.PRICE_DP_THRESHOLD_MASK_NODP) != 0 ) {
                return appendZ( value, showNull );
            }

            if ( doubleValue < 0 ) doubleValue -= Constants.WEIGHT;
            else doubleValue += Constants.WEIGHT;

            final int estLen      = NumberFormatUtils.getPriceLen( doubleValue );
            final int newCapacity = _len + estLen;
            ensureCapacity( newCapacity );

            int fmtLen = NumberFormatUtils.addPrice( _bytes, _len, doubleValue, estLen );

            _len += fmtLen;

        } else {

            if ( Utils.isNull( value ) || value == Long.MAX_VALUE ) {
                return append( value );
            }

            append( doubleValue, DoubleStringUtils.getVarFmt( doubleValue ) );
        }

        return this;
    }

    /**
     * encode a double with fixed number of decimal places ... round up only a trailing fractional 9 after the DP requested
     *
     * @param doubleValue
     * @param fixedDP
     * @return
     * @NOTE YOU GET A MAXIMUM OF 17 digits including fraction
     * @WARNING YOU WILL GET FRACTIONAL ERRORS AS THIS ROUTINE IS FOR SPEED AND USES double calcs
     */
    public final ReusableString appendZ( double doubleValue, int fixedDP, boolean showNull ) {

        if ( Utils.isNull( doubleValue ) ) return appendNull( showNull );

        long value = (long) doubleValue;

        if ( value == Constants.UNSET_LONG ) return appendNull( showNull );

        if ( _fastMode ) {

            final int newCapacity = _len + NumberFormatUtils.MAX_DOUBLE_DIGITS + fixedDP;
            ensureCapacity( newCapacity );

            int fmtLen = NumberFormatUtils.addPriceFixedDP( _bytes, _len, doubleValue, fixedDP );

            _len += fmtLen;
        } else {

            if ( Utils.isNull( value ) || value == Long.MAX_VALUE ) {
                return append( value );
            }

            append( doubleValue, DoubleStringUtils.get( fixedDP ) );
        }

        return this;
    }

    public final ReusableString appendZ( double doubleValue, boolean cashFmt, boolean showNull ) {

        if ( !cashFmt ) return append( doubleValue );

        if ( Utils.isNull( doubleValue ) ) return appendNull( showNull );

        long value = (long) doubleValue;

        if ( value == Constants.UNSET_LONG ) return appendNull( showNull );

        if ( Utils.isNull( value ) || value == Long.MAX_VALUE ) {
            return append( value );
        }

        append( doubleValue, DoubleStringUtils.getCashFmt() );

        return this;
    }

    public final ReusableString appendZ( final int num, boolean showNull ) {

        if ( num == Constants.UNSET_INT ) return appendNull( showNull );

        final int size        = (num < 0) ? NumberFormatUtils.getNegIntLen( num ) : NumberFormatUtils.getPosIntLen( num );
        final int newCapacity = _len + size;
        ensureCapacity( newCapacity );

        NumberFormatUtils.addInt( _bytes, _len, num, size );

        _len = newCapacity;

        return this;
    }

    public final ReusableString appendZ( final long num, boolean showNull ) {

        if ( num == Constants.UNSET_LONG ) return appendNull( showNull );

        final int len         = NumberFormatUtils.getLongLen( num );
        final int newCapacity = _len + len;
        ensureCapacity( newCapacity );

        NumberFormatUtils.addLong( _bytes, _len, num, len );

        _len = newCapacity;

        return this;
    }

    public ReusableString chain( Procedure p ) {
        p.invoke();
        return this;
    }

    public final ReusableString copy( final ZString from ) {
        reset();
        if ( from == null )
            return this;
        final int fromLen = from.length();
        ensureCapacity( fromLen );
        from.getBytes( _bytes, 0 );
        _len = fromLen;
        return this;
    }

    @SuppressWarnings( "deprecation" )
    public final ReusableString copy( final String s ) {
        if ( s != null ) {
            final int len = s.length();
            ensureCapacity( len );
            s.getBytes( 0, len, _bytes, 0 );
            _len = len;
        } else {
            _len  = 0;
            _hash = 0;
        }
        return this;
    }

    public final ReusableString copy( final String buf, final int start, final int len ) {
        setValue( buf.getBytes(), start, len );
        return this;
    }

    public final ReusableString copy( final byte[] buf, final int start, final int len ) {
        setValue( buf, start, len );
        return this;
    }

    public final ReusableString copy( final ZString from, final int start, final int len ) {
        reset();
        if ( from == null )
            return this;
        ensureCapacity( len );
        from.getBytes( _bytes, start, 0, len );
        _len = len;
        return this;
    }

    public final ReusableString copy( final byte val ) {
        setLength( 0 );
        append( val );
        return this;
    }

    public final ReusableString copy( final char val ) {
        setLength( 0 );
        append( val );
        return this;
    }

    public final ReusableString copy( final int val ) {
        setLength( 0 );
        append( val );
        return this;
    }

    public final ReusableString copy( final long val ) {
        setLength( 0 );
        append( val );
        return this;
    }

    public final ReusableString copy( final double val ) {
        setLength( 0 );
        append( val );
        return this;
    }

    public final ReusableString delete( final byte byteToRemove ) {

        int newLen = _len;

        for ( int readIdx = 0, writeIdx = 0; readIdx < _len; ++readIdx ) {
            byte aByte = _bytes[ readIdx ];
            if ( aByte != byteToRemove ) {
                _bytes[ writeIdx++ ] = aByte;
            } else {
                --newLen;
            }
        }

        _len = newLen;

        return this;
    }

    public final ReusableString replace( final byte oldByte, final byte newByte ) {

        for ( int idx = 0; idx < _len; ++idx ) {
            if ( _bytes[ idx ] == oldByte ) _bytes[ idx ] = newByte;
        }

        return this;
    }

    public void reverse() {
        int end   = _len - 1;
        int start = 0;

        while( end > start ) {
            byte tmp = _bytes[ start ];
            _bytes[ start ] = _bytes[ end ];
            _bytes[ end ]   = tmp;

            end--;
            start++;
        }
    }

    public final ReusableString rtrim() {
        final int oldLen = _len;
        int       len    = oldLen - 1;

        while( len >= 0 && _bytes[ len ] == ' ' ) {
            len--;
        }

        len++;

        if ( len != oldLen ) {
            _hash = 0;
            _len  = len;
        }

        return this;
    }

    /**
     * @param buf
     * @param len
     * @NOTE IMPORTANT ensure buffer is big enough as IF ensureCapacity has to grow the array the offset will NOT be considered
     */
    public final void setBuffer( byte[] buf, int len ) {
        _bytes  = buf;
        _len    = len;
        _offset = 0;
    }

    public final void setValue( final int val ) {
        setLength( 0 );
        append( val );
    }

    public final void setValue( final long val ) {
        setLength( 0 );
        append( val );
    }

    public final void setValue( final char ch ) {
        ensureCapacity( 1 );
        _bytes[ 0 ] = (byte) ch;
        _len        = 1;
    }

    @SuppressWarnings( "deprecation" )
    public final void setValue( final String s ) {
        if ( s != null ) {
            final int len = s.length();
            ensureCapacity( len );
            s.getBytes( 0, len, _bytes, 0 );
            _len = len;
        } else {
            _len  = 0;
            _hash = 0;
        }
    }

    private ReusableString appendNull( boolean showNull ) {
        if ( showNull ) append( NULL_STR );
        return this;
    }
}
