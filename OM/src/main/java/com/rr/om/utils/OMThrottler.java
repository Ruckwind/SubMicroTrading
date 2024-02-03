/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.utils;

import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.model.MsgFlag;
import com.rr.core.session.AbstractSession;
import com.rr.core.session.ThrottleException;
import com.rr.core.session.Throttler;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.model.generated.internal.core.EventIds;

/**
 * A  throttler for OM
 * <p>
 * throw exceptions for NOS/AMEND when exceed rate UNLESS PosDup is true when sleep
 * sleep on cancel requests
 * <p>
 * NOT threadsafe, should only be used by the dispatch thread
 * <p>
 * Each dispatcher should own its own throttler
 * <p>
 * Very simple, uses fact that default value in timestamp array will be zero
 */
public class OMThrottler implements Throttler {

    private static final int  DEFAULT_SIZE = 1000;
    private static final int  EXTRA_MS     = 10;
    private static final long MAX_DELAY    = 1000;

    private static final int DEFAULT_THROTTLE_MS = 1000;

    private static final ZString TO = new ViewString( " to " );
    private final Logger _log = LoggerFactory.create( AbstractSession.class );
    private final ReusableString _logMsg               = new ReusableString( 100 );
    private final ReusableString _throttleBaseOverride = new ReusableString( "Throttle back, override calc'd delay " );
    private final ReusableString _throttleBase         = new ReusableString( "Throttle back " );
    private long _timestamps[];
    private long _timePeriodMS = 1000;
    private int  _throttleNoMsgs;
    private int  _idx          = -1;
    private       String         _errMsg               = "";
    private       ZString        _id                   = null;

    public OMThrottler() {
        size( DEFAULT_SIZE );
    }

    public OMThrottler( ZString id ) {
        this();

        _id = id;

        _throttleBaseOverride.append( id );
        _throttleBaseOverride.append( " " );

        _throttleBase.append( id );
        _throttleBase.append( " " );
    }

    @Override
    public void setThrottleNoMsgs( int throttleNoMsgs ) {
        size( throttleNoMsgs );

        _log.info( "ThrottlerWithSleep for " + ((_id != null) ? _id : "unnamed") + ", rate=" + throttleNoMsgs + " per second" );
    }

    @Override
    public void setThrottleTimeIntervalMS( long throttleTimeIntervalMS ) {
        _timePeriodMS = throttleTimeIntervalMS;

        if ( throttleTimeIntervalMS != 1000 ) {
            _log.info( "ThrottlerWithSleep for set interval to  " + throttleTimeIntervalMS );
        }
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
            throttle( msg, time, period );
            time = ClockFactory.get().currentTimeMillis();
        }

        _timestamps[ nextIdx ] = time;
                                 _idx = nextIdx;
    }

    @Override
    public boolean throttled( long now ) {
        throw new SMTRuntimeException( "mustThrottle not supported by this implementation" );
    }

    private void delay( long time, long period ) {
        int msDelay = (int) ((_timePeriodMS - period) + EXTRA_MS);

        if ( msDelay < 0 || msDelay > MAX_DELAY ) {
            _logMsg.copy( _throttleBaseOverride );
            _logMsg.append( msDelay );
            _logMsg.append( TO );
            _logMsg.append( DEFAULT_THROTTLE_MS );

            msDelay = DEFAULT_THROTTLE_MS;
        } else {
            _logMsg.copy( _throttleBase );
            _logMsg.append( msDelay );
        }

        _log.info( _logMsg );

        ThreadUtilsFactory.get().sleep( msDelay );
    }

    private void setErrorMsg() {
        _errMsg = "Exceeded throttle rate of " + _throttleNoMsgs + " messages per " + _timePeriodMS + " ms";
    }

    private void size( int throttleNoMsgs ) {
        _throttleNoMsgs = throttleNoMsgs;

        _timestamps = new long[ _throttleNoMsgs ];

        setErrorMsg();
    }

    private void throttle( Event msg, long time, long period ) {

        if ( msg.isFlagSet( MsgFlag.PossDupFlag ) || msg.getReusableType().getSubId() == EventIds.ID_CANCELREQUEST ) {

            // DONT REJECT BACK ReplayRequests ... DELAY THEM

            delay( time, period );
        } else {
            throw new ThrottleException( _errMsg );
        }
    }
}
