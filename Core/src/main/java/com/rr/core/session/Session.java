/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.component.SMTInitialisableComponent;
import com.rr.core.lang.ZString;
import com.rr.core.model.EventHandler;
import com.rr.core.utils.SMTRuntimeException;

public interface Session extends EventHandler, SMTInitialisableComponent {

    /**
     * return a string identifier for the connection or null if none
     */
    ZString getConnectionId();

    /**
     * @return unique integer assigned session key starting at 1
     */
    int getIntId();

    boolean isConnected();

    default void requestConnect() { throw new SMTRuntimeException( "requestConnect not implemented" ); }

    void stop();

    ;
}
