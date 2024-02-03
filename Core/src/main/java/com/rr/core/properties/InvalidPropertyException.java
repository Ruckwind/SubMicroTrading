/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.properties;

/**
 * property is not valid tag entry see CoreProps
 */
public class InvalidPropertyException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidPropertyException( String msg, Exception e ) {
        super( msg, e );
    }

    public InvalidPropertyException( String msg ) {
        super( msg );
    }

}
