/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session.socket;

import com.rr.core.session.SessionException;

public class SessionStateException extends SessionException {

    private static final long serialVersionUID = 1L;

    public SessionStateException( String msg ) {
        this( msg, false );
    }

    public SessionStateException( String msg, boolean forcedLogout ) {
        super( msg, forcedLogout );
    }
}
