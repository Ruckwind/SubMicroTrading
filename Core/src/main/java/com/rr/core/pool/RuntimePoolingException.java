/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.pool;

public class RuntimePoolingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RuntimePoolingException( String message, Throwable cause ) {
        super( message, cause );
    }

    public RuntimePoolingException( String message ) {
        super( message );
    }
}
