/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

public class BadMessageSize extends SessionException {

    private static final long serialVersionUID = 1L;

    public BadMessageSize( String msg ) {
        super( msg );
    }
}
