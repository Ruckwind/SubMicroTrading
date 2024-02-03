/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.Env;
import com.rr.core.lang.Procedure;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.time.BackTestClock;

/**
 * should only be constructed by calling the ThreadUtilsFactory or BackTestThreadUtilsWrapper
 * this ensures only one instance per thread
 */
public class BackTestThreadUtils implements ThreadUtils {

    private static final int MAX_WAIT = 1000;

    private static ThreadLocal<BackTestClock> _threadLocalClock = ThreadLocal.withInitial( () -> (BackTestClock) ClockFactory.get() );

    public static void reset() {
        _threadLocalClock.remove();
    }

    public BackTestThreadUtils() {
        if ( Env.BACKTEST != AppProps.instance().getProperty( CoreProps.RUN_ENV, Env.class ) ) {
            throw new RuntimeException( "BackTestThreadUtilsWrapper must be used with RUN_ENV set to BACKTEST" );
        }
    }

    public BackTestClock getClock() {
        return _threadLocalClock.get();
    }

    @Override public void init( final String fileName ) {
        // nothing
    }

    @Override public void init( final String fileName, final boolean isDebug ) {
        // nothing
    }

    @Override public void setPriority( final Thread thread, final ThreadPriority priority ) {
        // nothing
    }

    @Override public void setPriority( final Thread thread, final int priority, final int mask ) {
        // nothing
    }

    @Override public void sleep( final int ms ) {
        if ( ms <= 0 ) return;
        long now = _threadLocalClock.get().currentTimeMillis();
        _threadLocalClock.get().setCurrentTimeMillis( now + ms );
    }

    @Override public void sleepMicros( final int micros ) {
        long now = _threadLocalClock.get().currentTimeMillis();
        _threadLocalClock.get().setCurrentTimeMillis( now + (micros * 1000) );
    }

    @Override public void waitFor( final Object delayLock, final int delayIntervalMS ) {
        long now = _threadLocalClock.get().currentTimeMillis();
        int delay = delayIntervalMS;

        if ( delay > MAX_WAIT ) delay = MAX_WAIT;

        _threadLocalClock.get().setCurrentTimeMillis( now + delay );
    }

    @Override public void delayedRun( final String id, final int millisToDelay, final Procedure procToRun ) {
        Thread t = new Thread( new Runnable() {

            @Override public void run() {
                ThreadUtilsFactory.get().sleep( millisToDelay );
                procToRun.invoke();
            }

        }, id );

        t.setDaemon( true );
        t.start();
    }
}
