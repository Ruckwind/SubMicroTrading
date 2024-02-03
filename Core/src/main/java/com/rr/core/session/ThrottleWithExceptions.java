/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.lang.ClockFactory;
import com.rr.core.model.Event;

/**
 * A simple throttler with no concept of which messages can and cant be rejected
 * <p>
 * NOT threadsafe, should only be used by the dispatch thread
 * <p>
 * Each dispatcher should own its own throttler
 * <p>
 * Very simple, uses fact that default value in timestamp array will be zero
 */
public class ThrottleWithExceptions implements Throttler {

    private static final int DEFAULT_SIZE = 1000;

    private long   _timestamps[];
    private long   _timePeriodMS = 1000;
    private int    _throttleNoMsgs;
    private int    _idx          = -1;
    private String _errMsg       = "";

    public ThrottleWithExceptions() {
        size( DEFAULT_SIZE );
    }

    @Override
    public void setThrottleNoMsgs( int throttleNoMsgs ) {
        size( throttleNoMsgs );
    }

    @Override
    public void setThrottleTimeIntervalMS( long throttleTimeIntervalMS ) {
        _timePeriodMS = throttleTimeIntervalMS;
        setErrorMsg();
    }

    @Override
    public void setDisconnectLimit( int disconnectLimit ) {
        // not used 
    }

    @Override
    public void checkThrottle( Event msg ) throws ThrottleException {

        long time = ClockFactory.get().currentTimeMillis();

        int nextIdx = _idx + 1;

        if ( nextIdx == _timestamps.length ) {
            nextIdx = 0;
        }

        long oldestTime = _timestamps[ nextIdx ];

        long period = Math.abs( time - oldestTime );

        if ( period < _timePeriodMS ) {
            throw new ThrottleException( _errMsg );
        }

        _timestamps[ nextIdx ] = time;
                                 _idx = nextIdx;
    }

    @Override
    public boolean throttled( long now ) {
        int nextIdx = _idx + 1;

        if ( nextIdx == _timestamps.length ) {
            nextIdx = 0;
        }

        long oldestTime = _timestamps[ nextIdx ];

        long period = Math.abs( now - oldestTime );

        if ( period < _timePeriodMS ) {
            return true;
        }

        _timestamps[ nextIdx ] = now;
                                 _idx = nextIdx;

        return false;
    }

    private void setErrorMsg() {
        _errMsg = "Exceeded throttle rate of " + _throttleNoMsgs + " messages per " + _timePeriodMS + " ms";
    }

    private void size( int throttleNoMsgs ) {
        _throttleNoMsgs = throttleNoMsgs;

        _timestamps = new long[ _throttleNoMsgs ];

        setErrorMsg();
    }
}
