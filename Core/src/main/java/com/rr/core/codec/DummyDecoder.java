/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

import com.rr.core.lang.TimeUtils;
import com.rr.core.model.ClientProfile;
import com.rr.core.model.Event;
import com.rr.core.model.InstrumentLocator;

public class DummyDecoder implements Decoder {

    @Override public String getComponentId() { return "DummyDecoder"; }

    @Override public void setClientProfile( ClientProfile client )                    { /* nothing */ }    @Override public void setInstrumentLocator( InstrumentLocator instrumentLocator ) { /* nothing */ }

    @Override public InstrumentLocator getInstrumentLocator() {
        return null;
    }

    @Override public Event decode( byte[] msg, int offset, int maxIdx ) {
        return null;
    }

    @Override public void setReceived( long nanos ) { /* nothing */ }

    @Override public long getReceived() {
        return 0;
    }

    @Override public int parseHeader( byte[] inBuffer, int inHdrLen, int bytesRead ) {
        return 0;
    }

    @Override public ResyncCode resync( byte[] fixMsg, int offset, int maxIdx ) {
        return null;
    }

    @Override public int getSkipBytes() {
        return 0;
    }

    @Override public void setTimeUtils( TimeUtils calc ) { /* nothing */ }

    @Override public Event postHeaderDecode() {
        return null;
    }

    @Override public void setNanoStats( boolean nanoTiming ) { /* nothing */ }

    @Override public int getLength() {
        return 0;
    }


}
