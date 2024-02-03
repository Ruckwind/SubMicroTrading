/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TimeUtils;

public interface BinaryDecodeBuilder {

    void clear();

    /**
     * decode a long from a base36 encoding of fixed width set of bytes ... zero padded on left
     *
     * @param len
     */
    long decodeBase36Number( int len );

    boolean decodeBool();

    byte decodeByte();

    byte decodeChar();

    /**
     * decode a fixed width set of bytes
     *
     * @param dest
     * @param len
     */
    void decodeData( ReusableString dest, int len );

    int decodeDate();

    double decodeDecimal();

    int decodeInt();

    void decodeIntToString( ReusableString dest );

    long decodeLong();

    void decodeLongToString( ReusableString dest );

    double decodePrice();

    /**
     * decode a price
     */
    double decodePrice( int wholeDigits, int decimalPlaces );

    int decodeQty();

    /**
     * decode a fixed width quantity with total length of len
     */
    int decodeQty( int len );

    short decodeShort();

    void decodeString( ReusableString dest );

    /**
     * decode a fixed width set of bytes, strips null padding
     *
     * @param dest
     * @param len
     */
    void decodeStringFixedWidth( ReusableString dest, int len );

    long decodeTimeLocal();

    long decodeTimeUTC();

    long decodeTimestampLocal();

    long decodeTimestampUTC();

    byte decodeUByte();

    int decodeUInt();

    long decodeULong();

    short decodeUShort();

    /**
     * decode a null terminated String, first 0x00 to be treated as null terminator
     */
    void decodeZStringFixedWidth( ReusableString dest, int len );

    void end();

    byte[] getBuffer();

    int getCurrentIndex();

    int getLength();

    int getMaxIdx();

    void setMaxIdx( int maxIdx );

    int getNextFreeIdx();

    int getOffset();

    void setTimeUtils( TimeUtils calc );

    void skip( int size );

    void start( byte[] msg, int offset, int maxIdx );
}
