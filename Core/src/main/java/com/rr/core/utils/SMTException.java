/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

public class SMTException extends Exception {

    private static final long serialVersionUID = 1L;

    public SMTException( String msg ) {
        super( msg );
    }

    public SMTException( String msg, Throwable e ) {
        super( msg, e );
    }
}

