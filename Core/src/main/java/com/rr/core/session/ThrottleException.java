/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.utils.SMTRuntimeException;

public class ThrottleException extends SMTRuntimeException {

    private static final long serialVersionUID = 1L;

    public ThrottleException( String msg ) {
        super( msg );
    }
}
