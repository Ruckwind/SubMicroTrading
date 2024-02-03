/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.asia.bse.fastfix;

/**
 * structure representing BSE packet header
 */

// @TODO move to model.xml

public class BSEPacketHeader {

    public int  _packetSeqNum; // contiguous per senderCompId PER PORT !
    public int  _partitionId;
    public long _perfIndicator;
    public int  _senderCompId;
    public long _sendingTime;

    @Override
    public String toString() {
        return "BSEPacketHeader [_partitionId=" + _partitionId + ", _senderCompId=" + _senderCompId + ", _packetSeqNum=" + _packetSeqNum + ", _sendingTime="
               + _sendingTime + ", _perfIndicator=" + _perfIndicator + "]";
    }
}
