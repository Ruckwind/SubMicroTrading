/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.model.Event;

public interface Throttler {

    /**
     * @return true if passed throttle check, false if exceeded limit
     */
    void checkThrottle( Event msg ) throws ThrottleException;

    void setDisconnectLimit( int disconnectLimit );

    void setThrottleNoMsgs( int throttleNoMsgs );

    void setThrottleTimeIntervalMS( long throttleTimeIntervalMS );

    /**
     * optional throttle check method
     * <p>
     * will throw runtime exception if method not supported by implementation
     *
     * @return true if throttled .... can be used as alternative to checkThrottle
     */
    boolean throttled( long now );
}
