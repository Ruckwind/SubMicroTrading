/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary;

import com.rr.core.lang.Constants;

/**
 * helper for binary protocols
 *
 * @NOTE DOESNT CHECK FOR BUFFER OVERRUN SO ENSURE BUF IS BIG ENOUGH
 * <p>
 * can throw RuntimeEncodingException
 */
public class BinaryLittleEndianEncoderUtils extends AbstractBinaryEncoderUtils {

    public BinaryLittleEndianEncoderUtils( byte[] buffer, int offset ) {
        super( buffer, offset );
    }

    @Override
    public final void encodeLong( final long value ) {

        if ( value == Constants.UNSET_LONG ) {
            doEncodeSignedLongNull();
        } else {
            doEncodeLong( value );
        }
    }

    @Override
    public void encodeInt( final int value ) {

        if ( value == Constants.UNSET_INT ) {
            doEncodeSignedIntNull();
        } else {
            doEncodeInt( value );
        }
    }

    @Override
    public void encodeShort( int val ) {
        if ( val == Constants.UNSET_SHORT ) {
            doEncodeSignedShortNull();
        } else {
            doEncodeShort( val );
        }
    }

    protected void doEncodeInt( final int value ) {
        _buffer[ _idx++ ] = (byte) (value);
        _buffer[ _idx++ ] = (byte) (value >> 8);
        _buffer[ _idx++ ] = (byte) (value >> 16);
        _buffer[ _idx++ ] = (byte) (value >> 24);
    }

    protected void doEncodeLong( final long value ) {
        _buffer[ _idx++ ] = (byte) (value);
        _buffer[ _idx++ ] = (byte) (value >> 8);
        _buffer[ _idx++ ] = (byte) (value >> 16);
        _buffer[ _idx++ ] = (byte) (value >> 24);
        _buffer[ _idx++ ] = (byte) (value >> 32);
        _buffer[ _idx++ ] = (byte) (value >> 40);
        _buffer[ _idx++ ] = (byte) (value >> 48);
        _buffer[ _idx++ ] = (byte) (value >> 56);
    }

    protected void doEncodeShort( int val ) {
        final byte upper = (byte) (val >>> 8);
        final byte lower = (byte) (val & 0xFF);

        _buffer[ _idx++ ] = lower;
        _buffer[ _idx++ ] = upper;
    }

    protected void doEncodeSignedIntNull() {
        _buffer[ _idx++ ] = 0x00;
        _buffer[ _idx++ ] = 0x00;
        _buffer[ _idx++ ] = 0x00;
        _buffer[ _idx++ ] = 0x00;
    }

    protected void doEncodeSignedLongNull() {
        _buffer[ _idx++ ] = 0x00;
        _buffer[ _idx++ ] = 0x00;
        _buffer[ _idx++ ] = 0x00;
        _buffer[ _idx++ ] = 0x00;
        _buffer[ _idx++ ] = 0x00;
        _buffer[ _idx++ ] = 0x00;
        _buffer[ _idx++ ] = 0x00;
        _buffer[ _idx++ ] = 0x00;
    }

    protected void doEncodeSignedShortNull() {
        _buffer[ _idx++ ] = 0x00;
        _buffer[ _idx++ ] = 0x00;
    }
}
