/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary;

/**
 * helper for binary decoder protocols
 * <p>
 * can throw RuntimeEncodingException
 */
public class BinaryBigEndianDecoderUtils extends AbstractBinaryDecoderUtils {

    public static long decodeLong( byte[] buffer, int idx ) {
        return ((((long) buffer[ idx++ ] & 0xFF) << 56) |
                (((long) buffer[ idx++ ] & 0xFF) << 48) |
                (((long) buffer[ idx++ ] & 0xFF) << 40) |
                (((long) buffer[ idx++ ] & 0xFF) << 32) |
                (((long) buffer[ idx++ ] & 0xFF) << 24) |
                (((long) buffer[ idx++ ] & 0xFF) << 16) |
                (((long) buffer[ idx++ ] & 0xFF) << 8) |
                ((long) buffer[ idx ] & 0xFF));
    }

    public BinaryBigEndianDecoderUtils() {
        super();
    }

    @Override
    public final int decodeInt() {

        return (((_buffer[ _idx++ ] & 0xFF) << 24) |
                ((_buffer[ _idx++ ] & 0xFF) << 16) |
                ((_buffer[ _idx++ ] & 0xFF) << 8) |
                (_buffer[ _idx++ ] & 0xFF));
    }

    @Override
    public final long decodeLong() {

        return ((((long) _buffer[ _idx++ ] & 0xFF) << 56) |
                (((long) _buffer[ _idx++ ] & 0xFF) << 48) |
                (((long) _buffer[ _idx++ ] & 0xFF) << 40) |
                (((long) _buffer[ _idx++ ] & 0xFF) << 32) |
                (((long) _buffer[ _idx++ ] & 0xFF) << 24) |
                (((long) _buffer[ _idx++ ] & 0xFF) << 16) |
                (((long) _buffer[ _idx++ ] & 0xFF) << 8) |
                ((long) _buffer[ _idx++ ] & 0xFF));
    }

    @Override
    public final short decodeShort() {

        return (short) (((_buffer[ _idx++ ] & 0xFF) << 8) | (_buffer[ _idx++ ] & 0xFF));
    }
}
