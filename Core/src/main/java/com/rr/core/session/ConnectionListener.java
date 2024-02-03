/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

public interface ConnectionListener {

    void connected( RecoverableSession session );

    void disconnected( RecoverableSession session );
}
