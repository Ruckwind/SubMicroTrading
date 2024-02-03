/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

import com.rr.core.lang.ZString;

public interface FixEncodeBuilder {

    /**
     * special tag for stats
     */
    void encodeAckStats( long orderIn, long orderOut, long ackIn, long ackOut );

    void encodeBool( int tag, boolean isOn );

    void encodeByte( int tag, byte code );

    void encodeBytes( int tag, byte[] code );

    void encodeDate( int tag, int yyyymmdd );

    /**
     * write the fix version and len tags and appends checksum
     */
    void encodeEnvelope();

    void encodeInt( int tag, int val );

    void encodeLong( int tag, long val );

    void encodePrice( int tag, double price );

    void encodeString( int tag, ZString str );

    void encodeString( int tag, byte[] buf, int offset, int len );

    void encodeTwoByte( int tag, byte[] code );

    void encodeUTCTimestamp( int tag, long msFromStartOfDayUTC );

    /**
     * @return number of bytes in the message
     */
    int getLength();

    int getNextFreeIdx();

    /**
     * @return offset in buffer for start of message
     */
    int getOffset();

    void start();
}
