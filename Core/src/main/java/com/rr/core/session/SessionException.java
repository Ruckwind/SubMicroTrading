/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

public class SessionException extends Exception {

    private static final long serialVersionUID = 1L;

    private final boolean _forcedLogout;

    public SessionException( String msg ) {
        this( msg, false );
    }

    public SessionException( String msg, boolean forcedLogout ) {
        super( msg );

        _forcedLogout = forcedLogout;
    }

    public SessionException( String msg, Exception e ) {
        super( msg, e );

        _forcedLogout = false;
    }

    public boolean isForcedLogout() {
        return _forcedLogout;
    }
}
