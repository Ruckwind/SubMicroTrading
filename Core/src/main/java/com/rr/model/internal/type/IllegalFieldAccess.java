/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.internal.type;

public class IllegalFieldAccess extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IllegalFieldAccess( String message, Throwable cause ) {
        super( message, cause );
    }

    public IllegalFieldAccess( String message ) {
        super( message );
    }
}
