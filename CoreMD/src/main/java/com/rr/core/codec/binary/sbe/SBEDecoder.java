/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.sbe;

import com.rr.core.codec.BinaryDecoder;

public interface SBEDecoder extends BinaryDecoder {

    void decodeStartPacket( byte[] msg, int offset, int maxIdx, SBEPacketHeader h );

    int getCurrentOffset();

    void logLastMsg();

}
