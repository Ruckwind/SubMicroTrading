/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary;

/**
 * helper for binary decoder protocols
 * <p>
 * can throw RuntimeEncodingException
 */
public class BinaryLittleEndianDecoderUtils extends AbstractBinaryDecoderUtils {

    @Override
    public int decodeInt() {

        return ((_buffer[ _idx++ ] & 0xFF) |
                ((_buffer[ _idx++ ] & 0xFF) << 8) |
                ((_buffer[ _idx++ ] & 0xFF) << 16) |
                ((_buffer[ _idx++ ] & 0xFF) << 24));
    }

    @Override
    public long decodeLong() {

        return (((long) _buffer[ _idx++ ] & 0xFF) |
                (((long) _buffer[ _idx++ ] & 0xFF) << 8) |
                (((long) _buffer[ _idx++ ] & 0xFF) << 16) |
                (((long) _buffer[ _idx++ ] & 0xFF) << 24) |
                (((long) _buffer[ _idx++ ] & 0xFF) << 32) |
                (((long) _buffer[ _idx++ ] & 0xFF) << 40) |
                (((long) _buffer[ _idx++ ] & 0xFF) << 48) |
                (((long) _buffer[ _idx++ ] & 0xFF) << 56));
    }

    @Override
    public short decodeShort() {

        return (short) ((_buffer[ _idx++ ] & 0xFF) | ((_buffer[ _idx++ ] & 0xFF) << 8));
    }

}
