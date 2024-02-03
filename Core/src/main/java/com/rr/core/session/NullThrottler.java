/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.model.Event;

public final class NullThrottler implements Throttler {

    @Override
    public void setThrottleNoMsgs( int throttleNoMsgs ) {
        // nothing
    }

    @Override
    public void setThrottleTimeIntervalMS( long throttleTimeIntervalMS ) {
        // nothing
    }

    @Override
    public void setDisconnectLimit( int disconnectLimit ) {
        // nothing
    }

    @Override
    public void checkThrottle( Event msg ) throws ThrottleException {
        // nothing
    }

    @Override
    public boolean throttled( long now ) {
        return false;
    }
}
