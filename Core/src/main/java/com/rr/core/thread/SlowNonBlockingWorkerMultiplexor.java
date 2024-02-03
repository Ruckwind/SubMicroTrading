/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.thread;

import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ReusableString;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.core.utils.Utils;

/**
 * slow version of NonBlockingWorkerMultiplexor useful for Low end PC's
 */
public final class SlowNonBlockingWorkerMultiplexor extends BaseNonBlockingWorkerMultiplexor {

    public static final int    THREAD_THROTTLE_MS                = 10;
    public static final int    THROTTLE_BATCH                    = 10000;
    public static final double LOG_BLOCK_DURATION_MORE_THAN_SECS = 30.0;
    private static final Logger _log = LoggerFactory.create( SlowNonBlockingWorkerMultiplexor.class );
    private int _delay         = THREAD_THROTTLE_MS;
    private int _throttleBatch = THROTTLE_BATCH;

    private ReusableString _logMsg = new ReusableString();

    private double _logDurationMoreThanSecs = SlowNonBlockingWorkerMultiplexor.LOG_BLOCK_DURATION_MORE_THAN_SECS;

    private long _last = ClockFactory.getLiveClock().currentTimeMillis();

    private long _cnt;

    public SlowNonBlockingWorkerMultiplexor( String id, ControlThread ctl ) {
        super( id, ctl );
    }

    @Override
    public void execute() throws Exception {

        super.execute();

        if ( ++_cnt % _throttleBatch == 0 ) {
            long   now           = ClockFactory.getLiveClock().currentTimeMillis();
            double duractionSecs = Math.abs( now - _last ) / 1000.0;

            if ( duractionSecs > 0 ) {
                double ratePerSec = _throttleBatch / duractionSecs;

                if ( Utils.hasNonZeroVal( _logDurationMoreThanSecs ) && duractionSecs > _logDurationMoreThanSecs ) {
                    if ( _log.isEnabledFor( Level.debug ) ) {
                        _logMsg.copy( id() )
                               .append( " slow batch, batchSize=" ).append( _throttleBatch )
                               .append( ", durationSec=" ).append( duractionSecs )
                               .append( ", rate=" ).append( ratePerSec );

                        _log.log( Level.debug, _logMsg );
                    }
                }
            }

            ThreadUtilsFactory.get().sleep( _delay );

            _last = ClockFactory.getLiveClock().currentTimeMillis();
        }
    }

    public void setDelay( final int delay ) {
        _delay = delay;
    }

    public void setLogDurationMoreThanSecs( final double logDurationMoreThanSecs ) {
        _logDurationMoreThanSecs = logDurationMoreThanSecs;
    }

    public void setThrottleBatch( final int throttleBatch ) {
        _throttleBatch = throttleBatch;
    }
}
