/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.thread;

import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.ThreadUtilsFactory;

/**
 * a control thread which slices time across three executable streams
 */
public class SlowTriElementControlThread extends AbstractControlThread {

    private static final int THREAD_THROTTLE_MS = 1;
    private static final int THROTTLE_BATCH     = 1000;
    private final Logger _log = LoggerFactory.create( SlowTriElementControlThread.class );
    private int  _delay         = THREAD_THROTTLE_MS;
    private int  _throttleBatch = THROTTLE_BATCH;
    private long _cnt;
    private ExecutableElement _workerA = null;
    private ExecutableElement _workerB = null;
    private ExecutableElement _workerC = null;

    public SlowTriElementControlThread( String name, ThreadPriority priority ) {

        super( name, priority );
    }

    @Override
    public void register( ExecutableElement ex ) {

        _log.info( "TriElementControlThread : registered " + ex.getComponentId() + " with " + getName() );

        if ( _workerA == null ) {
            _workerA = ex;
        } else if ( _workerB == null ) {
            _workerB = ex;
        } else if ( _workerC == null ) {
            _workerC = ex;
        } else {
            if ( _workerA != ex && _workerB != ex && _workerC != ex ) {
                throw new SMTRuntimeException( "TriElementControlThread cannot register >3 execElems, failed with " + ex.getComponentId() );
            }
        }
    }

    @Override
    protected void runControlLoop() {

        if ( _workerA == null || _workerB == null || _workerC == null ) {
            abortMissingWorker();
        }

        ExecutableElement cur = _workerC;

        try {
            _workerA.threadedInit();
            _workerB.threadedInit();
            _workerC.threadedInit();
        } catch( Exception e ) {
            abortThreadedInit( e );
        }

        boolean readyA;
        boolean readyB;
        boolean readyC;

        while( !isStopping() ) {
            readyA = _workerA.checkReady();
            readyB = _workerB.checkReady();
            readyC = _workerC.checkReady();

            if ( readyA || readyB || readyC ) {

                try {

                    while( !isStopping() ) {
                        if ( cur == _workerC ) { // catch case where workerA/B previously threw exception ie its B/C's turn next
                            cur = _workerA;
                            _workerA.execute();
                        }
                        if ( cur == _workerA ) {
                            cur = _workerB;
                            _workerB.execute();
                        }
                        cur = _workerC;
                        _workerC.execute();

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
        _workerC.stop();

        _log.info( "TRI-CONTROL LOOP FINISHED " + getName() );
    }
}
