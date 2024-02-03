/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.pool;

public class RuntimeAllocException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RuntimeAllocException( String message, Throwable cause ) {
        super( message, cause );
    }

    public RuntimeAllocException( String message ) {
        super( message );
    }
}
