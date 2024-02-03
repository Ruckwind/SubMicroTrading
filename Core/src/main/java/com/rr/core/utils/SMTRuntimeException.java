/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

public class SMTRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SMTRuntimeException( String msg ) {
        super( msg );
    }

    public SMTRuntimeException( String msg, Throwable e ) {
        super( msg, e );
    }
}

