/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.ets;

public enum ETSMessageId {

    SessionLogon( (byte) 5 );

    private final byte _code;

    ETSMessageId( byte code ) {
        _code = code;
    }

    public byte getCode() {
        return _code;
    }
}
