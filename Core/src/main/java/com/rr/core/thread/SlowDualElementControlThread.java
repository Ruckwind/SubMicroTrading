/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.thread;

import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.ThreadUtilsFactory;

public final class SlowDualElementControlThread extends AbstractControlThread {

    private static final int THREAD_THROTTLE_MS = 1;
    private static final int THROTTLE_BATCH     = 1000;
    private final Logger _log = LoggerFactory.create( SlowDualElementControlThread.class );
    private int  _delay         = THREAD_THROTTLE_MS;
    private int  _throttleBatch = THROTTLE_BATCH;
    private long _cnt;
    private ExecutableElement _workerA = null;
    private ExecutableElement _workerB = null;

    public SlowDualElementControlThread( String name, ThreadPriority priority ) {

        super( name, priority );
    }

    @Override
    public void register( ExecutableElement ex ) {

        _log.info( "SlowDualElementControlThread : registered " + ex.getComponentId() + " with " + getName() );

        if ( _workerA == null ) {
            _workerA = ex;
        } else if ( _workerB == null ) {
            _workerB = ex;
        } else {
            if ( _workerA != ex && _workerB != ex ) {
                throw new SMTRuntimeException( "SlowDualElementControlThread cannot register 3 execElems, failed with " + ex.getComponentId() );
            }
        }
    }

    @Override
    protected void runControlLoop() {

        _log.info( "SlowDualElementControlThread : runControlLoop " + getComponentId() + " / " + getName() + " starting, throttleMod=" + _throttleBatch + ", delay=" + _delay );

        if ( _workerA == null || _workerB == null ) {
            abortMissingWorker();
        }

        ExecutableElement cur = _workerB;

        try {
            _workerA.threadedInit();
            _workerB.threadedInit();
        } catch( Exception e ) {
            abortThreadedInit( e );
        }

        boolean readyA;
        boolean readyB;

        while( !isStopping() ) {
            readyA = _workerA.checkReady();
            readyB = _workerB.checkReady();

            if ( readyA || readyB ) {
                try {
                    while( !isStopping() ) {
                        if ( cur == _workerB ) { // catch case where workerA previously threw exception ie its B's turn next 
                            cur = _workerA;
                            _workerA.execute();
                        }
                        cur = _workerB;
                        _workerB.execute();

                        if ( ++_cnt % _throttleBatch == 0 ) {
                            ThreadUtilsFactory.get().sleep( _delay );
                        }
                    }
                } catch( Exception e ) {
                    cur.handleExecutionException( e );
                }
            } else {
                goPassive();
            }
        }

        _workerA.stop();
        _workerB.stop();

        _log.info( "SlowDualElementControlThread : runControlLoop " + getComponentId() + " / " + getName() + " end" );
    }
}
