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
public class BinaryBigEndianEncoderUtils extends AbstractBinaryEncoderUtils {

    public static void encodeLong( byte[] buffer, int idx, long value ) {
        buffer[ idx++ ] = (byte) (value >> 56);
        buffer[ idx++ ] = (byte) (value >> 48);
        buffer[ idx++ ] = (byte) (value >> 40);
        buffer[ idx++ ] = (byte) (value >> 32);
        buffer[ idx++ ] = (byte) (value >> 24);
        buffer[ idx++ ] = (byte) (value >> 16);
        buffer[ idx++ ] = (byte) (value >> 8);
        buffer[ idx ]   = (byte) (value);
    }

    public BinaryBigEndianEncoderUtils( byte[] buffer, int offset ) {
        super( buffer, offset );
    }

    @Override
    public final void encodeLong( final long value ) {

        if ( value == Constants.UNSET_LONG ) {
            _buffer[ _idx++ ] = 0x00;
            _buffer[ _idx++ ] = 0x00;
            _buffer[ _idx++ ] = 0x00;
            _buffer[ _idx++ ] = 0x00;
            _buffer[ _idx++ ] = 0x00;
            _buffer[ _idx++ ] = 0x00;
            _buffer[ _idx++ ] = 0x00;
            _buffer[ _idx++ ] = 0x00;
        } else {
            _buffer[ _idx++ ] = (byte) (value >> 56);
            _buffer[ _idx++ ] = (byte) (value >> 48);
            _buffer[ _idx++ ] = (byte) (value >> 40);
            _buffer[ _idx++ ] = (byte) (value >> 32);
            _buffer[ _idx++ ] = (byte) (value >> 24);
            _buffer[ _idx++ ] = (byte) (value >> 16);
            _buffer[ _idx++ ] = (byte) (value >> 8);
            _buffer[ _idx++ ] = (byte) (value);
        }
    }

    @Override
    public final void encodeInt( final int value ) {

        if ( value == Constants.UNSET_INT ) {
            _buffer[ _idx++ ] = 0x00;
            _buffer[ _idx++ ] = 0x00;
            _buffer[ _idx++ ] = 0x00;
            _buffer[ _idx++ ] = 0x00;
        } else {
            _buffer[ _idx++ ] = (byte) (value >> 24);
            _buffer[ _idx++ ] = (byte) (value >> 16);
            _buffer[ _idx++ ] = (byte) (value >> 8);
            _buffer[ _idx++ ] = (byte) (value);
        }
    }

    @Override
    public final void encodeShort( int val ) {
        final byte upper = (byte) (val >>> 8);
        final byte lower = (byte) (val & 0xFF);

        if ( val == Constants.UNSET_SHORT ) {
            _buffer[ _idx++ ] = 0x00;
            _buffer[ _idx++ ] = 0x00;
        } else {
            _buffer[ _idx++ ] = upper;
            _buffer[ _idx++ ] = lower;
        }
    }
}
