/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

public class DisconnectedException extends Exception {

    private static final long serialVersionUID = 1L;

    public DisconnectedException( String msg ) {
        super( msg );
    }
}
