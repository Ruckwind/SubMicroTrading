/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

public class CoreErrorCode extends ErrorCode {

    public static final ErrorCode ERR_RETRY = new ErrorCode( "FAP200", "FileAppender: failed on log retry, origException : " );

    private CoreErrorCode( String code, String msg ) {
        super( code, msg );
    }
}
