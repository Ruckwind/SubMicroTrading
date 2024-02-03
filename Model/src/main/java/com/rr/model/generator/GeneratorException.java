/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator;

public class GeneratorException extends Exception {

    private static final long serialVersionUID = 1L;

    public GeneratorException( String message, Throwable cause ) {
        super( message, cause );
    }

    public GeneratorException( String message ) {
        super( message );
    }

}
