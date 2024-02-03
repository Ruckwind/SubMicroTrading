/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

public class FileException extends SMTException {

    private static final long serialVersionUID = 1L;

    public FileException( String message, Throwable cause ) {
        super( message, cause );
    }

    public FileException( String message ) {
        super( message );
    }
}
