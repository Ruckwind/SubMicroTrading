/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary;

import com.rr.core.lang.TimeUtils;
import com.rr.core.lang.ZString;

public interface BinaryEncodeBuilder {

    void clear();

    /**
     * encode a long with a base36 encoding of [A-Z][0-9] fixed width set of bytes ... zero padded on left
     *
     * @param len number of chars for the base 36 value, zero padded to left
     */
    void encodeBase36Number( long value, int len );

    void encodeBool( final boolean isOn );

    void encodeByte( final byte code );

    void encodeBytes( final byte[] buf );

    void encodeChar( final byte code );

    void encodeData( ZString str, int len );

    /**
     * encodeDate - most encoders dont need this as date generally encoded as int or long
     *
     * @param yyyymmdd
     */
    void encodeDate( final int yyyymmdd );

    void encodeDecimal( final double price );

    void encodeFiller( int len );

    void encodeInt( final int value );

    void encodeLong( final long value );

    void encodePrice( final double price );

    void encodeQty( final int value );

    void encodeShort( final int val );

    void encodeString( final ZString str );

    void encodeString( final ZString str, int maxLen );

    void encodeString( final byte[] buf, final int offset, int len );

    void encodeStringAsInt( ZString intLong );

    // iunternal String to external logger/int
    void encodeStringAsLong( ZString strLong );

    void encodeStringFixedWidth( ZString str, int len );

    void encodeStringFixedWidth( byte[] value, int offset, int fixedDataSize );

    void encodeTimeLocal( long internalTime );

    void encodeTimeUTC( long internalTime );

    void encodeTimestampLocal( long internalTime );

    void encodeTimestampUTC( long internalTime );

    void encodeUByte( final byte code );

    void encodeUInt( final int value );

    // unsigned encoders ... required by ETI as uses different NULL for unsigned and signed
    void encodeULong( final long value );

    void encodeUShort( final short val );

    // other string methods for fixed widths
    void encodeZStringFixedWidth( ZString str, int len );

    void encodeZStringFixedWidth( byte[] value, int offset, int fixedDataSize );

    int end();

    byte[] getBuffer();

    /**
     * @return current length of message (as message is encoded this will change)
     */
    int getCurLength();

    int getCurrentIndex();

    /**
     * @return actual length of full message ... must call end() first to set this value
     */
    int getLength();

    int getNextFreeIdx();

    int getOffset();

    void setTimeUtils( TimeUtils calc );

    void start();

    /**
     * start new messaqe, encode msgType as first byte of message
     *
     * @param msgType
     */
    void start( int msgType );

}
