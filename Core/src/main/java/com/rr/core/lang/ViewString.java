/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

import com.rr.core.lang.stats.SizeConstants;

import java.nio.ByteBuffer;

/**
 * a View String which doesnt own and cant modify the underlying buffer
 * <p>
 * has an offset into the buffer
 * <p>
 * Main purpose is allow Strings to share the same byte array in similar way to String but
 * avoid all the temp obj creations with String
 * <p>
 * The underlying buffer can be changed as can the offset into the buffer
 * thus this is NOT an immutable object
 *
 * @author Richard Rose
 */

public class ViewString implements AssignableString {

    private static final byte[] NULL_BUF = new byte[ 10 ];

    protected byte[] _bytes;
    protected int    _hash;
    protected int    _len;
    protected int    _offset;

    static int indexOf( byte[] source, int sourceOffset, int sourceCount, byte[] target, int targetOffset, int targetCount, int fromIndex ) {
        if ( fromIndex >= sourceCount ) {
            return (targetCount == 0 ? sourceCount : -1);
        }
        if ( fromIndex < 0 ) {
            fromIndex = 0;
        }
        if ( targetCount == 0 ) {
            return fromIndex;
        }

        byte first = target[ targetOffset ];
        int  max   = sourceOffset + (sourceCount - targetCount);

        for ( int i = sourceOffset + fromIndex; i <= max; i++ ) {
            if ( source[ i ] != first ) {
                while( ++i <= max && source[ i ] != first ) ;
            }

            if ( i <= max ) {
                int j   = i + 1;
                int end = j + targetCount - 1;
                for ( int k = targetOffset + 1; j < end && source[ j ] == target[ k ]; j++, k++ )
                    ;

                if ( j == end ) {
                    /* Found whole string. */
                    return i - sourceOffset;
                }
            }
        }

        return -1;
    }

    public ViewString( final ViewString from ) {
        this( from._bytes, from._offset, from._len );
        _hash = from._hash;
    }

    public ViewString( final byte[] bytes, final int offset, final int len ) {
        _len    = len;
        _offset = offset;
        _bytes  = bytes;
    }

    public ViewString( final byte[] buf ) {
        this( buf, 0, buf.length );
    }

    public ViewString( final ReusableString buf ) {
        this( buf._bytes, 0, buf.length() );
    }

    public ViewString( final ZString buf ) {
        this( buf.getBytes(), 0, buf.length() );
    }

    public ViewString( final String s ) {
        this( (s == null) ? new byte[ 0 ] : s.getBytes(), 0, (s == null) ? 0 : s.length() );
    }

    public ViewString() {
        _len    = 0;
        _offset = 0;
        _bytes  = NULL_BUF;
    }

    @Override
    public int compareTo( final ZString o ) {
        final int  len2 = o.length();
        final byte v2[] = o.getBytes();
        int        idx2 = o.getOffset();
        int        idx  = _offset;

        if ( _len != len2 ) {
            int minLen = _len;
            int resp   = -1;
            if ( _len > len2 ) {
                minLen = len2;
                resp   = 1;
            }
            for ( int i = 0; i < minLen; i++ ) {
                byte b1 = _bytes[ idx++ ];
                byte b2 = v2[ idx2++ ];
                if ( b1 != b2 )
                    return b1 - b2;
            }
            return resp;

        }
        for ( int i = 0; i < _len; i++ ) {
            byte b1 = _bytes[ idx++ ];
            byte b2 = v2[ idx2++ ];
            if ( b1 != b2 )
                return b1 - b2;
        }
        return 0;
    }

    @Override public boolean contains( final String s ) {
        return indexOf( s ) > -1;
    }

    @Override public boolean contains( final ZString s ) {
        return indexOf( s ) > -1;
    }

    @Override public boolean endsWith( String suffix )    { return startsWith( suffix, _len + getOffset() - suffix.length() ); }

    @Override public boolean endsWith( ZString suffix )   { return startsWith( suffix, _len + getOffset() - suffix.length() ); }

    @Override public final boolean equals( final ZString other ) {
        if ( this == other )
            return true;

        if ( other == null ) return false;

        final int len2 = other.length();
        if ( _len != len2 )
            return false;

        final byte v1[] = _bytes;
        final byte v2[] = other.getBytes();

        int idx  = _offset + _len;
        int idx2 = other.getOffset() + _len;

        for ( int i = _len; i > 0; i-- ) {
            if ( v1[ --idx ] != v2[ --idx2 ] )
                return false;
        }

        return true;
    }

    @Override
    public final boolean equalsIgnoreCase( final ZString other ) {
        if ( this == other )
            return true;

        if ( other == null ) return false;

        final int len2 = other.length();
        if ( _len != len2 )
            return false;

        final byte v1[] = _bytes;
        final byte v2[] = other.getBytes();

        int idx  = _offset + _len;
        int idx2 = other.getOffset() + _len;

        for ( int i = _len; i > 0; i-- ) {
            byte b1 = v1[ --idx ];
            byte b2 = v2[ --idx2 ];

            if ( b1 != b2 ) {
                if ( b1 >= 'A' && b1 <= 'Z' ) {
                    b1 += 0x20; // convert to lowercase
                    if ( b1 != b2 ) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public final boolean equalsIgnoreCase( final String other ) {

        if ( other == null ) return false;

        final int n = _len;
        if ( n != other.length() ) {
            return false;
        }

        final byte v1[] = _bytes;

        int idx = _offset;

        for ( int i = 0; i < n; i++ ) {
            byte b1 = v1[ idx++ ];
            byte b2 = (byte) other.charAt( i );

            if ( b1 != b2 ) {
                if ( b1 >= 'A' && b1 <= 'Z' ) {
                    b1 += 0x20; // convert to lowercase
                    if ( b1 != b2 ) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public final byte getByte( final int idx ) {
        return _bytes[ _offset + idx ];
    }

    @Override
    public void getBytes( final byte[] target, final int targetStart ) {
        if ( _len != 0 ) {
            final int len = _len;

            if ( len < SizeConstants.MIN_MEMCPY_LENGTH ) {
                int       srcIdx  = _offset;
                int       destIdx = targetStart;
                final int max     = srcIdx + len;
                while( srcIdx < max ) {
                    target[ destIdx++ ] = _bytes[ srcIdx++ ];
                }
            } else {
                System.arraycopy( _bytes, _offset, target, targetStart, len );
            }
        }
    }

    @Override
    public void getBytes( final byte[] target, final int srcStart, final int targetStart, final int len ) {
        if ( _len != 0 ) {

            if ( len < SizeConstants.MIN_MEMCPY_LENGTH ) {
                int       srcIdx  = _offset + srcStart;
                int       destIdx = targetStart;
                final int max     = srcIdx + len;
                while( srcIdx < max ) {
                    target[ destIdx++ ] = _bytes[ srcIdx++ ];
                }
            } else {
                System.arraycopy( _bytes, srcStart + _offset, target, targetStart, len );
            }
        }
    }

    @Override
    public final byte[] getBytes() {
        return _bytes;
    }

    @Override
    public int getOffset() {
        return _offset;
    }

    @Override
    public int indexOf( final int c ) {

        return indexOf( c, 0 );
    }

    @Override
    public int indexOf( final int c, final int startIdx ) {

        if ( startIdx < 0 ) return -1;

        final int max = _offset + _len;

        for ( int i = _offset + startIdx; i < max; i++ ) {
            if ( _bytes[ i ] == c ) {
                return i - _offset;
            }
        }

        return -1;
    }

    @Override public int indexOf( String s ) {
        return indexOf( s, 0 );
    }

    @Override public int indexOf( String s, int fromIdx ) {
        return indexOf( getBytes(), _offset, length(), s.getBytes(), 0, s.length(), fromIdx );
    }

    @Override public int indexOf( ZString s ) {
        return indexOf( s, 0 );
    }

    @Override public int indexOf( ZString s, int fromIdx ) {
        return indexOf( getBytes(), _offset, length(), s.getBytes(), s.getOffset(), s.length(), fromIdx );
    }

    @Override
    public int lastIndexOf( final int c ) {

        return lastIndexOf( c, _len - 1 );
    }

    @Override
    public int lastIndexOf( final int c, final int fromIdx ) {

        for ( int i = fromIdx + _offset; i >= _offset; i-- ) {
            if ( _bytes[ i ] == c ) {
                return i - _offset;
            }
        }

        return -1;
    }

    @Override
    public int length() {
        return _len;
    }

    @Override public boolean startsWith( String prefix )  { return startsWith( prefix, getOffset() ); }

    @Override public boolean startsWith( ZString prefix ) { return startsWith( prefix, getOffset() ); }

    @Override public boolean startsWith( String prefix, int toffset ) {

        byte ta[] = getBytes();
        int  to   = toffset;
        int  po   = getOffset();
        int  pc   = prefix.length();

        if ( (toffset < 0) || (toffset > _len - pc) ) {
            return false;
        }

        while( --pc >= 0 ) {
            if ( ta[ to++ ] != prefix.charAt( po++ ) ) {
                return false;
            }
        }

        return true;
    }

    @Override public boolean startsWith( ZString prefix, int toffset ) {

        byte pa[] = prefix.getBytes();
        byte ta[] = getBytes();
        int  to   = toffset;
        int  po   = getOffset();
        int  pc   = prefix.length();

        if ( (toffset < 0) || (toffset > _len - pc) ) {
            return false;
        }

        while( --pc >= 0 ) {
            if ( ta[ to++ ] != pa[ po++ ] ) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void substring( ReusableString dest, int startIdx ) {
        if ( startIdx < 0 ) {
            throw new StringIndexOutOfBoundsException( startIdx );
        }
        int subLen = _len - startIdx;
        if ( subLen < 0 ) {
            throw new StringIndexOutOfBoundsException( subLen );
        }

        dest.ensureCapacity( subLen );
        getBytes( dest.getBytes(), startIdx, 0, subLen );
        dest.setLength( subLen );
    }

    @Override
    public void substring( ReusableString dest, int startIdx, int endIdx ) {
        if ( startIdx < 0 ) throw new StringIndexOutOfBoundsException( startIdx );

        if ( endIdx > _len ) {
            throw new StringIndexOutOfBoundsException( endIdx );
        }

        int subLen = endIdx - startIdx;

        if ( subLen < 0 ) {
            throw new StringIndexOutOfBoundsException( subLen );
        }

        dest.ensureCapacity( subLen );
        getBytes( dest.getBytes(), startIdx + _offset, 0, subLen );
        dest.setLength( subLen );
    }

    @Override
    public String substring( int startIdx ) {
        if ( startIdx < 0 ) {
            throw new StringIndexOutOfBoundsException( startIdx );
        }
        int subLen = _len - startIdx;
        if ( subLen < 0 ) {
            throw new StringIndexOutOfBoundsException( subLen );
        }

        return new String( getBytes(), startIdx + _offset, subLen );
    }

    @Override
    public String substring( int startIdx, int endIdx ) {
        if ( startIdx < 0 ) throw new StringIndexOutOfBoundsException( startIdx );

        if ( endIdx > _len ) {
            throw new StringIndexOutOfBoundsException( endIdx );
        }

        int subLen = endIdx - startIdx;

        if ( subLen < 0 ) {
            throw new StringIndexOutOfBoundsException( subLen );
        }

        return new String( getBytes(), startIdx + _offset, subLen );
    }

    @Override
    public void write( final ByteBuffer bb ) {

        bb.put( _bytes, _offset, _len );
    }

    @Override
    public int hashCode() {
        int h = _hash;
        if ( h == 0 ) {
            final int max = _offset + _len;
            for ( int i = _offset; i < max; i++ ) {
                h = 31 * h + _bytes[ i ];
            }
            _hash = h;
        }
        return h;
    }

    @Override public final boolean equals( final Object other ) {
        if ( this == other )
            return true;

        if ( other == null ) return false;

        Class<?> otherClass = other.getClass();

        // ideally should use instanceOf ZString ... but dont want hit
        if ( otherClass == ViewString.class || otherClass == ReusableString.class ) {
            return equals( (ViewString) other );
        }

        if ( other.getClass() == String.class ) {
            return equals( (String) other );
        }

        return false;
    }

    @Override
    public String toString() {
        return new String( _bytes, _offset, _len );
    }

    @Override
    public void setValue( final byte[] buf, final int offset, final int len ) {
        _bytes  = buf;
        _len    = len;
        _offset = offset;
        _hash   = 0;
    }

    @Override
    public void setValue( ZString from ) {
        throw new RuntimeException( "Cant assign a ZString to a ViewString : cant set to " + from );
    }

    /**
     * @return copy of viewed bytes
     * @NOTE CREATES NEW ARRAY AVOID FOR ANYTHING OTHER THAN TEST CODE
     */
    public byte[] cloneBytes() {
        byte[] dest = new byte[ _len ];

        System.arraycopy( _bytes, _offset, dest, 0, _len );
        return dest;
    }

    public void ensureCapacity( int minimumCapacity ) {
        throw new RuntimeException( "Invalid operation. " + ViewString.class.getName() + " instances cannot be modified" );
    }

    public final boolean equals( final ViewString other ) {
        if ( this == other )
            return true;

        if ( other == null ) return false;

        final int len2 = other._len;
        if ( _len != len2 )
            return false;

        final byte v1[] = _bytes;
        final byte v2[] = other._bytes;

        int idx  = _offset + _len;
        int idx2 = other._offset + _len;

        for ( int i = _len; i > 0; i-- ) {
            if ( v1[ --idx ] != v2[ --idx2 ] )
                return false;
        }

        return true;
    }

    public final boolean equals( final String other ) {

        if ( other == null ) return false;

        final int n = _len;
        if ( n != other.length() ) {
            return false;
        }

        final byte v1[] = _bytes;

        int idx = _offset;

        for ( int i = 0; i < n; i++ ) {
            if ( v1[ idx++ ] != other.charAt( i ) )
                return false;
        }

        return true;

    }

    public final boolean equals( final byte[] other ) {

        if ( other == null ) return false;

        final int n = _len;
        if ( n != other.length ) {
            return false;
        }

        final byte v1[] = _bytes;

        int idx = _offset;

        for ( int i = 0; i < n; i++ ) {
            if ( v1[ idx++ ] != other[ i ] )
                return false;
        }

        return true;
    }

    public int getCapacity() {
        return _bytes.length;
    }

    public void reset() {
        _len    = 0;
        _offset = 0;
    }

    /**
     * @param length
     * @NOTE no range checking so caller must check
     */
    public void setLength( int length ) {
        _len = length;
    }

    public void setValue( final byte[] buf ) {
        _len    = 0;
        _offset = 0;
        _bytes  = buf;
        _hash   = 0;
    }

    public void setValue( final int offset, final int len ) {
        _len    = len;
        _offset = offset;
        _hash   = 0;
    }
}
