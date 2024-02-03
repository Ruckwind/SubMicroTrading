/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.persister;

public class PersisterException extends Exception {

    private static final long serialVersionUID = 1L;

    public PersisterException( String msg, Exception e ) {
        super( msg, e );
    }

    public PersisterException( String msg ) {
        super( msg );
    }
}
