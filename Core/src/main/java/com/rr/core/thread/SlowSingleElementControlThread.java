/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.thread;

import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ReusableString;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.core.utils.Utils;

public class SlowSingleElementControlThread extends AbstractControlThread {

    private static final int THREAD_THROTTLE_MS = 1;
    private static final int THROTTLE_BATCH     = 1000;

    private final Logger _log = ConsoleFactory.console( SlowSingleElementControlThread.class );

    private int  _delay         = THREAD_THROTTLE_MS;
    private int  _throttleBatch = THROTTLE_BATCH;
    private long _cnt;

    private ExecutableElement _worker;
    private long              _last   = ClockFactory.getLiveClock().currentTimeMillis();
    private ReusableString    _logMsg = new ReusableString();

    private double _logDurationMoreThanSecs = SlowNonBlockingWorkerMultiplexor.LOG_BLOCK_DURATION_MORE_THAN_SECS;

    public SlowSingleElementControlThread( String name, ThreadPriority priority ) {

        super( name, priority );
    }

    @Override
    public void register( ExecutableElement ex ) {
        _log.info( "SingleElementControlThread : registered " + ex.getComponentId() + " with " + getName() );

        _worker = ex;
    }

    @Override
    protected void runControlLoop() {
        if ( _worker == null ) {
            abortMissingWorker();
        }

        _log.info( "SlowSingleElementControlThread : runControlLoop " + getComponentId() + " / " + getName() + " starting, throttleMod=" + _throttleBatch + ", delay=" + _delay );

        try {
            _worker.threadedInit();
        } catch( Exception e ) {
            abortThreadedInit( e );
        }

        while( !isStopping() ) {

            if ( _worker.checkReady() ) {
                try {

                    _last = ClockFactory.getLiveClock().currentTimeMillis();

                    while( !isStopping() ) {
                        _worker.execute();

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

                } catch( AllDisconnectedException e ) { // worker is complete

                    setStopping( true );

                } catch( Exception e ) {

                    _worker.handleExecutionException( e );
                }
            } else {
                _worker.notReady();

                goPassive();
            }
        }

        _worker.stop();

        _log.info( "SlowSingleElementControlThread : runControlLoop " + getComponentId() + " / " + getName() + " end" );
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
