/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

import com.rr.core.lang.ZString;

public class RuntimeDecodingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private ZString _fixMsg = null;

    public RuntimeDecodingException( String message, Throwable cause ) {
        super( message, cause );
    }

    public RuntimeDecodingException( String message ) {
        super( message );
    }

    public RuntimeDecodingException( String message, ZString fixMsg ) {
        super( message );

        _fixMsg = fixMsg;
    }

    // @TODO remove GC from exceptions
    public RuntimeDecodingException( ZString errMsg ) {
        super( errMsg.toString() );
    }

    public ZString getFixMsg() {
        return _fixMsg;
    }
}
