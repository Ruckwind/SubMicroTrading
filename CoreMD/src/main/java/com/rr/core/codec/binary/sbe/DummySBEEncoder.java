/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.sbe;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TimeUtils;
import com.rr.core.lang.ZString;
import com.rr.core.model.Event;

public class DummySBEEncoder implements SBEEncoder {

    private byte[] _buf = new byte[ 100 ];

    @Override
    public void addStats( ReusableString outBuf, Event msg, long time ) {
        // nothing
    }

    @Override
    public void encode( Event msg ) {
        // nothing
    }

    @Override
    public byte[] getBytes() {
        return _buf;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public int getOffset() {
        return 0;
    }

    @Override
    public void setNanoStats( boolean nanoTiming ) {
        // nothing
    }

    @Override
    public void setTimeUtils( TimeUtils calc ) {
        // nothing
    }

    @Override
    public Event unableToSend( Event msg, ZString errMsg ) {
        return null;
    }

    @Override public String getComponentId() { return "DummySBEEncoder"; }

    @Override
    public boolean isDebug() {
        return false;
    }

    @Override
    public void setDebug( boolean debug ) {
        // nothing
    }

    @Override
    public void logStats() {
        // nothing
    }

    @Override
    public void logLastMsg() {
        // nothing
    }

    @Override
    public void encodeStartPacket( SBEPacketHeader h ) {
        // nothing
    }
}
