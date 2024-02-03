/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.processor.states;

public class StateException extends Exception {

    private static final long serialVersionUID = 1L;

    public StateException() {
        super();
    }

    public StateException( String message, Throwable cause ) {
        super( message, cause );
    }

    public StateException( String message ) {
        super( message );
    }

    public StateException( Throwable cause ) {
        super( cause );
    }
}
