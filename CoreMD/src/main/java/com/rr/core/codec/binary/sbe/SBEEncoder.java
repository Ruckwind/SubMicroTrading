/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.sbe;

import com.rr.core.codec.BinaryEncoder;

public interface SBEEncoder extends BinaryEncoder {

    void encodeStartPacket( SBEPacketHeader h );

    void logLastMsg();

    void logStats();

}
