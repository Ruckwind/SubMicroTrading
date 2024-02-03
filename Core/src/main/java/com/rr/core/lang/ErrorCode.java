/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;

import java.util.HashMap;
import java.util.Map;

public class ErrorCode {

    private static Logger              _log  = ConsoleFactory.console( ErrorCode.class, Level.info );
    private static Map<String, String> _msgs = new HashMap<>( 256 );

    private ZString _msg;

    public ErrorCode( String code, String msg ) {
        if ( _msgs.containsKey( code ) ) {
            _log.warn( "Duplicate error message code=" + code );
        }

        _msgs.put( code, msg );

        _msg = new ViewString( "[" + code + "] " + msg );
    }

    public ZString getError() {
        return _msg;
    }
}
