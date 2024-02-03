/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

public class RTStartupException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RTStartupException( String msg ) {
        super( msg );
    }

    public RTStartupException( Exception e ) {
        super( e );
    }

    public RTStartupException( String msg, Exception e ) {
        super( msg, e );
    }
}
