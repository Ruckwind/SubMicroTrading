/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

import com.rr.core.utils.SMTRuntimeException;

public class RuntimeEncodingException extends SMTRuntimeException {

    private static final long serialVersionUID = 1L;

    public RuntimeEncodingException( String message, Throwable cause ) {
        super( message, cause );
    }

    public RuntimeEncodingException( String message ) {
        super( message );
    }
}
