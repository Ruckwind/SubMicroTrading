package com.rr.core.thread;

import com.rr.core.utils.SMTRuntimeException;

public class AllDisconnectedException extends SMTRuntimeException {

    private static final long serialVersionUID = 1L;

    public AllDisconnectedException( String msg ) {
        super( msg );
    }
}

